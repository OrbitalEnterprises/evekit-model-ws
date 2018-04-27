package enterprises.orbital.evekit.ws;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.evekit.account.AccessKeyNotFoundException;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedAccountAccessKey;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import enterprises.orbital.evekit.model.ESISyncEndpoint;
import org.apache.commons.lang3.tuple.Pair;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ServiceUtil {
  private static final Logger log = Logger.getLogger(ServiceUtil.class.getName());
  private static final String PROP_MIN_CACHE_DELAY = "enterprises.orbital.evekit.model.min_cache_delay";
  private static final long DEF_MIN_CACHE_DELAY = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);

  public static class AuthenticationResult {
    // The authenticated key as a result of a successful authentication
    public SynchronizedAccountAccessKey key;
    // An error response as a result of an unsuccessful authentication
    public Response response;

    public boolean isFail() {
      return response != null;
    }

    public AuthenticationResult(SynchronizedAccountAccessKey k) {
      key = k;
    }

    public AuthenticationResult(Response r) {
      response = r;
    }
  }

  public static class AccessConfig {
    public int accessKey;
    public String accessCred;
    public long when;
    public AttributeSelector at;
    public AccountAccessMask mask;
    public boolean fail = false;
    public Response response;
    public SynchronizedEveAccount owner;
    public SynchronizedAccountAccessKey key;
    public long presetExpiry = -1;

    public AccessConfig(int accessKey, String accessCred, AttributeSelector at, AccountAccessMask mask) {
      super();
      this.accessKey = accessKey;
      this.accessCred = accessCred;
      this.when = OrbitalProperties.getCurrentTime() + OrbitalProperties.getLongGlobalProperty(PROP_MIN_CACHE_DELAY,
                                                                                               DEF_MIN_CACHE_DELAY);
      this.at = at;
      this.mask = mask;
    }

  }

  public static AccessConfig start(
      int accessKey,
      String accessCred,
      AttributeSelector at,
      AccountAccessMask mask) {
    AccessConfig cfg = new AccessConfig(accessKey, accessCred, at, mask);
    AuthenticationResult check = authenticate(cfg.accessKey, cfg.accessCred, cfg.when, cfg.at, cfg.mask);
    if (check.isFail()) {
      cfg.response = check.response;
      cfg.fail = true;
    } else {
      cfg.key = check.key;
      cfg.owner = check.key.getSyncAccount();
    }
    return cfg;
  }

  public static AccessConfig start(
      int accessKey,
      String accessCred,
      AttributeSelector at,
      Collection<AccountAccessMask> mask) {
    assert !mask.isEmpty();
    // Arbitrarily use the first mask (usually the only mask) for expiry settings
    AccessConfig cfg = new AccessConfig(accessKey, accessCred, at, mask.iterator()
                                                                       .next());
    AuthenticationResult check = authenticate(cfg.accessKey, cfg.accessCred, cfg.when, cfg.at, mask);
    if (check.isFail()) {
      cfg.response = check.response;
      cfg.fail = true;
    } else {
      cfg.key = check.key;
      cfg.owner = check.key.getSyncAccount();
    }
    return cfg;
  }

  public static <A extends CachedData> Response finishRef(
      long when,
      long expiry,
      Collection<A> result,
      HttpServletRequest request) {
    auditRefAccess(getSource(request), getRequestURI(request));
    ResponseBuilder rBuilder = Response.ok();
    if (result != null) rBuilder = rBuilder.entity(result);
    return stamp(rBuilder, when, expiry).build();
  }

  public static Response finish(
      AccessConfig cfg,
      Object result,
      HttpServletRequest request) {
    long expiry = cfg.presetExpiry == -1 ? computeExpiry(cfg.when, cfg.owner, cfg.mask) : cfg.presetExpiry;
    auditAccess(cfg.key, cfg.mask, getSource(request), getRequestURI(request));
    ResponseBuilder rBuilder = Response.ok();
    if (result != null) rBuilder = rBuilder.entity(result);
    return stamp(rBuilder, cfg.when, expiry).build();
  }

  public static <A extends CachedData> Response finish(
      AccessConfig cfg,
      Collection<A> result,
      HttpServletRequest request) {
    long expiry = cfg.presetExpiry == -1 ? computeExpiry(cfg.when, cfg.owner, cfg.mask) : cfg.presetExpiry;
    auditAccess(cfg.key, cfg.mask, getSource(request), getRequestURI(request));
    ResponseBuilder rBuilder = Response.ok();
    if (result != null) rBuilder = rBuilder.entity(result);
    return stamp(rBuilder, cfg.when, expiry).build();
  }

  public static String getSource(
      HttpServletRequest request) {
    return request == null ? "INTERNAL" : request.getRemoteAddr();
  }

  public static String getRequestURI(
      HttpServletRequest request) {
    return request == null ? "INTERNAL" : request.getRequestURI();
  }

  public static AuthenticationResult authenticate(
      int id,
      String hash,
      long when,
      AttributeSelector at) {
    SynchronizedAccountAccessKey key;
    try {
      key = SynchronizedAccountAccessKey.checkHash(id, hash);
      if (key == null) {
        ServiceError errMsg = new ServiceError(Status.UNAUTHORIZED.getStatusCode(), "Access credential invalid");
        return new AuthenticationResult(Response.status(Status.UNAUTHORIZED)
                                                .entity(errMsg)
                                                .build());
      }
    } catch (AccessKeyNotFoundException e) {
      ServiceError errMsg = new ServiceError(Status.NOT_FOUND.getStatusCode(),
                                             "Access key with the given ID not found");
      return new AuthenticationResult(Response.status(Status.NOT_FOUND)
                                              .entity(errMsg)
                                              .build());
    } catch (Exception e) {
      ServiceError errMsg = new ServiceError(
          Status.INTERNAL_SERVER_ERROR.getStatusCode(),
          "Error verifying access key.  If this problem persists, please contact the site admin.");
      return new AuthenticationResult(Response.status(Status.INTERNAL_SERVER_ERROR)
                                              .entity(errMsg)
                                              .build());
    }
    if (key.getExpiry() > 0 && key.getExpiry() <= when) {
      ServiceError errMsg = new ServiceError(Status.FORBIDDEN.getStatusCode(),
                                             "Access key expired, contact key owner for renewal");
      return new AuthenticationResult(Response.status(Status.FORBIDDEN)
                                              .entity(errMsg)
                                              .build());
    }
    // Rather than reject "at" times below the key limit, we convert the at filter to not allow queries below the key limit
    if (key.getLimit() > 0) {
      switch (at.type()) {
        case SET:
          // Filter out all values below the key limit
          Set<Long> start = at.getLongValues();
          Set<Long> keep = new HashSet<Long>();
          for (long next : start) {
            if (next > key.getLimit()) keep.add(next);
          }
          at.values.clear();
          for (long next : keep) {
            at.values.add(String.valueOf(next));
          }
          break;
        case RANGE:
          // Ensure the bottom of the range is above the limit
          if (at.getLongStart() <= key.getLimit()) {
            at.start = String.valueOf(key.getLimit() + 1);
          }
          break;
        case WILDCARD:
        default:
          // Convert to a range from one above the key limit to MAX_LONG
          at.any = false;
          at.values.clear();
          at.start = String.valueOf(key.getLimit() + 1);
          at.end = String.valueOf(Long.MAX_VALUE);
          break;
      }
    }
    return new AuthenticationResult(key);
  }

  public static AuthenticationResult authenticate(
      int id,
      String hash,
      long when,
      AttributeSelector at,
      AccountAccessMask desiredOp) {
    AuthenticationResult result = authenticate(id, hash, when, at);
    if (result.isFail()) return result;
    if (!desiredOp.checkAccess(result.key.getAccessMask())) {
      ServiceError errMsg = new ServiceError(
          Status.FORBIDDEN.getStatusCode(),
          "Access key not authorized to access the requested model type, contact key owner");
      return new AuthenticationResult(Response.status(Status.FORBIDDEN)
                                              .entity(errMsg)
                                              .build());
    }
    return result;
  }

  public static AuthenticationResult authenticate(
      int id,
      String hash,
      long when,
      AttributeSelector at,
      Collection<AccountAccessMask> desiredOp) {
    AuthenticationResult result = authenticate(id, hash, when, at);
    if (result.isFail()) return result;
    for (AccountAccessMask nextMask : desiredOp) {
      if (nextMask.checkAccess(result.key.getAccessMask())) return result;
    }
    ServiceError errMsg = new ServiceError(
        Status.FORBIDDEN.getStatusCode(),
        "Access key not authorized to access the requested model object, contact key owner");
    return new AuthenticationResult(Response.status(Status.FORBIDDEN)
                                            .entity(errMsg)
                                            .build());
  }

  private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
  public static final long DEFAULT_EXPIRY_INTERVAL = TimeUnit.MILLISECONDS.convert(24, TimeUnit.HOURS);
  private static String versionString = "2.0";

  protected static String getServerTime(
      long tm) {
    return dateFormat.format(new Date(tm));
  }

  public static ResponseBuilder stamp(
      ResponseBuilder result,
      long when,
      long expiry) {
    // expiry = MIN_VALUE is a tag for no-cache
    if (expiry <= 0 && expiry != Long.MIN_VALUE) expiry = when + DEFAULT_EXPIRY_INTERVAL;
    if (expiry > 0) {
      // valid expiry
      result = result.expires(new Date(expiry));
    } else {
      // if expiry is Long.MIN_VALUE then we interpret as not cacheable
      CacheControl noC = new CacheControl();
      noC.setNoCache(true);
      result = result.cacheControl(noC);
    }
    return result.header("Date", getServerTime(when))
                 .header("EveKit-Version", versionString);
  }

  protected static String join(
      String delim,
      String... args) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < args.length; i++) {
      builder.append(args[i]);
      if (i + 1 < args.length) builder.append(delim);
    }
    return builder.toString();
  }

  public static void auditAccess(
      SynchronizedAccountAccessKey key,
      AccountAccessMask op,
      String src,
      String path) {
    log.fine(join(", ", "AUDIT", "RESOURCEACCESS", "FROM", src, "USER", key.getSyncAccount()
                                                                           .getUserAccount()
                                                                           .getUid(), "ACCT",
                  String.valueOf(key.getSyncAccount()
                                    .getAid()), "KEY", String.valueOf(key.getAccessKey()), "OP",
                  op == null ? "NA" : op.toString(), "PATH",
                  path));
  }

  public static void auditAccess(
      SynchronizedAccountAccessKey key,
      Collection<AccountAccessMask> op,
      String src,
      String path) {
    log.fine(join(", ", "AUDIT", "RESOURCEACCESS", "FROM", src, "USER", key.getSyncAccount()
                                                                           .getUserAccount()
                                                                           .getUid(), "ACCT",
                  String.valueOf(key.getSyncAccount()
                                    .getAid()), "KEY", String.valueOf(key.getAccessKey()), "OP",
                  Arrays.toString(op.toArray())
                        .replace(", ", "|"), "PATH", path));
  }

  public static void auditRefAccess(
      String src,
      String path) {
    log.fine(join(", ", "AUDIT", "REFRESOURCEACCESS", "FROM", src, "PATH", path));
  }

  public static void updateLifeline(
      long start,
      CachedData... data) {
    long end = Long.MAX_VALUE;
    for (CachedData next : data) {
      end = Math.min(end, next.getLifeEnd());
    }
    for (CachedData next : data) {
      next.setLifeStart(start);
      next.setLifeEnd(end);
    }
  }

  public static <A extends CachedData> void updateLifeline(
      long start,
      Collection<A> data) {
    long end = Long.MAX_VALUE;
    for (A next : data) {
      end = Math.min(end, next.getLifeEnd());
    }
    for (A next : data) {
      next.setLifeStart(start);
      next.setLifeEnd(end);
    }
  }

  private static Set<ESISyncEndpoint> makeEPSet(ESISyncEndpoint... n) {
    return new HashSet<>(Arrays.asList(n));
  }

  // Map access mask and account type to appropriate endpoint for looking up tracking info
  private static final Map<Pair<AccountAccessMask, Boolean>, Set<ESISyncEndpoint>> accessMap;

  static {
    Map<Pair<AccountAccessMask, Boolean>, Set<ESISyncEndpoint>> map = new HashMap<>();
    map.put(Pair.of(AccountAccessMask.ACCESS_UPCOMING_CALENDAR_EVENTS, true), makeEPSet(ESISyncEndpoint.CHAR_CALENDAR));
    map.put(Pair.of(AccountAccessMask.ACCESS_CALENDAR_EVENT_ATTENDEES, true),
            makeEPSet(ESISyncEndpoint.CHAR_CALENDAR));
    map.put(Pair.of(AccountAccessMask.ACCESS_CHARACTER_SHEET, true),
            makeEPSet(ESISyncEndpoint.CHAR_CORP_ROLES, ESISyncEndpoint.CHAR_SHEET,
                      ESISyncEndpoint.CHAR_SKILLS, ESISyncEndpoint.CHAR_CLONES,
                      ESISyncEndpoint.CHAR_FATIGUE, ESISyncEndpoint.CHAR_TITLES,
                      ESISyncEndpoint.CHAR_IMPLANTS, ESISyncEndpoint.CHAR_CLONES));
    map.put(Pair.of(AccountAccessMask.ACCESS_FITTINGS, true),
            makeEPSet(ESISyncEndpoint.CHAR_FITTINGS));
    map.put(Pair.of(AccountAccessMask.ACCESS_MEDALS, true),
            makeEPSet(ESISyncEndpoint.CHAR_MEDALS));
    map.put(Pair.of(AccountAccessMask.ACCESS_NOTIFICATIONS, true),
            makeEPSet(ESISyncEndpoint.CHAR_NOTIFICATIONS));
    map.put(Pair.of(AccountAccessMask.ACCESS_CHAT_CHANNELS, true),
            makeEPSet(ESISyncEndpoint.CHAR_CHANNELS));
    map.put(Pair.of(AccountAccessMask.ACCESS_CONTACT_NOTIFICATIONS, true),
            makeEPSet(ESISyncEndpoint.CHAR_NOTIFICATIONS));
    map.put(Pair.of(AccountAccessMask.ACCESS_MAILING_LISTS, true),
            makeEPSet(ESISyncEndpoint.CHAR_MAIL));
    map.put(Pair.of(AccountAccessMask.ACCESS_MAIL, true),
            makeEPSet(ESISyncEndpoint.CHAR_MAIL));
    map.put(Pair.of(AccountAccessMask.ACCESS_RESEARCH, true),
            makeEPSet(ESISyncEndpoint.CHAR_AGENTS));
    map.put(Pair.of(AccountAccessMask.ACCESS_SKILL_QUEUE, true),
            makeEPSet(ESISyncEndpoint.CHAR_SKILL_QUEUE));
    map.put(Pair.of(AccountAccessMask.ACCESS_ACCOUNT_STATUS, true),
            makeEPSet(ESISyncEndpoint.CHAR_ONLINE));

    // Corporation specific
    map.put(Pair.of(AccountAccessMask.ACCESS_CONTAINER_LOG, false),
            makeEPSet(ESISyncEndpoint.CORP_CONTAINER_LOGS));
    map.put(Pair.of(AccountAccessMask.ACCESS_CORPORATION_MEDALS, false),
            makeEPSet(ESISyncEndpoint.CORP_MEDALS));
    map.put(Pair.of(AccountAccessMask.ACCESS_CORPORATION_SHEET, false),
            makeEPSet(ESISyncEndpoint.CORP_SHEET,
                      ESISyncEndpoint.CORP_DIVISIONS));
    map.put(Pair.of(AccountAccessMask.ACCESS_CORPORATION_TITLES, false),
            makeEPSet(ESISyncEndpoint.CORP_TITLES));
    map.put(Pair.of(AccountAccessMask.ACCESS_MEMBER_MEDALS, false),
            makeEPSet(ESISyncEndpoint.CORP_MEDALS));
    map.put(Pair.of(AccountAccessMask.ACCESS_MEMBER_SECURITY, false),
            makeEPSet(ESISyncEndpoint.CORP_MEMBERSHIP,
                      ESISyncEndpoint.CORP_TITLES));
    map.put(Pair.of(AccountAccessMask.ACCESS_MEMBER_SECURITY_LOG, false),
            makeEPSet(ESISyncEndpoint.CORP_MEMBERSHIP));
    map.put(Pair.of(AccountAccessMask.ACCESS_MEMBER_TRACKING, false),
            makeEPSet(ESISyncEndpoint.CORP_TRACK_MEMBERS));
    map.put(Pair.of(AccountAccessMask.ACCESS_SHAREHOLDERS, false),
            makeEPSet(ESISyncEndpoint.CORP_SHAREHOLDERS));
    map.put(Pair.of(AccountAccessMask.ACCESS_STARBASE_LIST, false),
            makeEPSet(ESISyncEndpoint.CORP_STARBASES));

    // Common
    map.put(Pair.of(AccountAccessMask.ACCESS_ACCOUNT_BALANCE, true),
            makeEPSet(ESISyncEndpoint.CHAR_WALLET_BALANCE));
    map.put(
        Pair.of(AccountAccessMask.ACCESS_ACCOUNT_BALANCE, false),
        makeEPSet(ESISyncEndpoint.CORP_WALLET_BALANCE));
    map.put(Pair.of(AccountAccessMask.ACCESS_ASSETS, true),
            makeEPSet(ESISyncEndpoint.CHAR_PLANETS,
                      ESISyncEndpoint.CHAR_ASSETS));
    map.put(Pair.of(AccountAccessMask.ACCESS_ASSETS, false),
            makeEPSet(ESISyncEndpoint.CORP_CUSTOMS,
                      ESISyncEndpoint.CORP_ASSETS));
    map.put(Pair.of(AccountAccessMask.ACCESS_BLUEPRINTS, true),
            makeEPSet(ESISyncEndpoint.CHAR_BLUEPRINTS));
    map.put(Pair.of(AccountAccessMask.ACCESS_BLUEPRINTS, false),
            makeEPSet(ESISyncEndpoint.CORP_BLUEPRINTS));
    map.put(Pair.of(AccountAccessMask.ACCESS_BOOKMARKS, true),
            makeEPSet(ESISyncEndpoint.CHAR_BOOKMARKS));
    map.put(Pair.of(AccountAccessMask.ACCESS_BOOKMARKS, false),
            makeEPSet(ESISyncEndpoint.CORP_BOOKMARKS));
    map.put(Pair.of(AccountAccessMask.ACCESS_CONTACT_LIST, true),
            makeEPSet(ESISyncEndpoint.CHAR_CONTACTS));
    map.put(Pair.of(AccountAccessMask.ACCESS_CONTACT_LIST, false),
            makeEPSet(ESISyncEndpoint.CORP_CONTACTS));
    map.put(Pair.of(AccountAccessMask.ACCESS_CONTRACTS, true),
            makeEPSet(ESISyncEndpoint.CHAR_CONTRACTS));
    map.put(Pair.of(AccountAccessMask.ACCESS_CONTRACTS, false),
            makeEPSet(ESISyncEndpoint.CORP_CONTRACTS));
    map.put(Pair.of(AccountAccessMask.ACCESS_FAC_WAR_STATS, true),
            makeEPSet(ESISyncEndpoint.CHAR_FACTION_WAR));
    map.put(
        Pair.of(AccountAccessMask.ACCESS_FAC_WAR_STATS, false),
        makeEPSet(ESISyncEndpoint.CORP_FACTION_WAR));
    map.put(Pair.of(AccountAccessMask.ACCESS_INDUSTRY_JOBS, true),
            makeEPSet(ESISyncEndpoint.CHAR_INDUSTRY));
    map.put(
        Pair.of(AccountAccessMask.ACCESS_INDUSTRY_JOBS, false),
        makeEPSet(ESISyncEndpoint.CORP_FACILITIES,
                  ESISyncEndpoint.CORP_INDUSTRY));
    map.put(Pair.of(AccountAccessMask.ACCESS_KILL_LOG, true),
            makeEPSet(ESISyncEndpoint.CHAR_KILL_MAIL));
    map.put(Pair.of(AccountAccessMask.ACCESS_KILL_LOG, false),
            makeEPSet(ESISyncEndpoint.CORP_KILL_MAIL));
    map.put(Pair.of(AccountAccessMask.ACCESS_LOCATIONS, true),
            makeEPSet(ESISyncEndpoint.CHAR_LOCATION,
                      ESISyncEndpoint.CHAR_SHIP_TYPE,
                      ESISyncEndpoint.CHAR_ASSETS));
    map.put(Pair.of(AccountAccessMask.ACCESS_LOCATIONS, false),
            makeEPSet(ESISyncEndpoint.CORP_ASSETS));
    map.put(Pair.of(AccountAccessMask.ACCESS_MARKET_ORDERS, true),
            makeEPSet(ESISyncEndpoint.CHAR_MARKET));
    map.put(
        Pair.of(AccountAccessMask.ACCESS_MARKET_ORDERS, false),
        makeEPSet(ESISyncEndpoint.CORP_MARKET));
    map.put(Pair.of(AccountAccessMask.ACCESS_STANDINGS, true),
            makeEPSet(ESISyncEndpoint.CHAR_STANDINGS));
    map.put(Pair.of(AccountAccessMask.ACCESS_STANDINGS, false),
            makeEPSet(ESISyncEndpoint.CORP_STANDINGS));
    map.put(
        Pair.of(AccountAccessMask.ACCESS_WALLET_JOURNAL, true),
        makeEPSet(ESISyncEndpoint.CHAR_WALLET_JOURNAL));
    map.put(
        Pair.of(AccountAccessMask.ACCESS_WALLET_JOURNAL, false),
        makeEPSet(ESISyncEndpoint.CORP_WALLET_JOURNAL));
    map.put(
        Pair.of(AccountAccessMask.ACCESS_WALLET_TRANSACTIONS, true),
        makeEPSet(ESISyncEndpoint.CHAR_WALLET_TRANSACTIONS));
    map.put(
        Pair.of(AccountAccessMask.ACCESS_WALLET_TRANSACTIONS, false),
        makeEPSet(ESISyncEndpoint.CORP_WALLET_TRANSACTIONS));
    accessMap = map;
  }


  public static long computeExpiry(
      long when,
      SynchronizedEveAccount acct,
      AccountAccessMask type)
      throws IllegalArgumentException {
    Set<ESISyncEndpoint> endpoints = accessMap.get(Pair.of(type, acct.isCharacterType()));
    if (endpoints == null || endpoints.isEmpty()) return when;
    return endpoints.stream()
                    .map(x -> AccountHandlerUtil.handleStandardExpiry(x, acct))
                    .min(Long::compare)
                    .get();
  }

  public static void sanitizeAttributeSelector(
      AttributeSelector as) {
    // restrict size of string parameters for all settings to less than 200 characters
    if (as.start != null && as.start.length() > 200) as.start = as.start.substring(0, 200);
    if (as.end != null && as.end.length() > 200) as.end = as.end.substring(0, 200);
    // allow at most 500 set members for set selectors and verify strings are not too long
    if (as.values.size() > 0) {
      Set<String> newSet = new HashSet<>();
      Iterator<String> i = as.values.iterator();
      for (int j = 0; j < 500 && i.hasNext(); j++) {
        String next = i.next();
        if (next.length() > 200) next = next.substring(0, 200);
        newSet.add(next);
      }
      as.values = newSet;
    }
  }

  public static void sanitizeAttributeSelector(
      AttributeSelector... as) {
    for (AttributeSelector next : as)
      sanitizeAttributeSelector(next);
  }

}
