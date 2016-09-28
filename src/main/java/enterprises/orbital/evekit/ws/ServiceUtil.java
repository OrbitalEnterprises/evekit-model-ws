package enterprises.orbital.evekit.ws;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.NoSuchKeyException;
import enterprises.orbital.evekit.account.SynchronizedAccountAccessKey;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import enterprises.orbital.evekit.model.character.Capsuleer;
import enterprises.orbital.evekit.model.corporation.Corporation;

public class ServiceUtil {
  private static final Logger log = Logger.getLogger(ServiceUtil.class.getName());

  public static class AuthenticationResult {
    // The authenticated key as a result of a successful authentication
    public SynchronizedAccountAccessKey key;
    // An error response as a result of an unsuccessful authentication
    public Response                     response;

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
    public int                          accessKey;
    public String                       accessCred;
    public long                         when;
    public AttributeSelector            at;
    public AccountAccessMask            mask;
    public boolean                      fail         = false;
    public Response                     response;
    public SynchronizedEveAccount       owner;
    public SynchronizedAccountAccessKey key;
    public long                         presetExpiry = -1;

    public AccessConfig(int accessKey, String accessCred, AttributeSelector at, AccountAccessMask mask) {
      super();
      this.accessKey = accessKey;
      this.accessCred = accessCred;
      this.when = OrbitalProperties.getCurrentTime();
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
    AccessConfig cfg = new AccessConfig(accessKey, accessCred, at, mask.iterator().next());
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
        return new AuthenticationResult(Response.status(Status.UNAUTHORIZED).entity(errMsg).build());
      }
    } catch (NoSuchKeyException e) {
      ServiceError errMsg = new ServiceError(Status.NOT_FOUND.getStatusCode(), "Access key with the given ID not found");
      return new AuthenticationResult(Response.status(Status.NOT_FOUND).entity(errMsg).build());
    } catch (Exception e) {
      ServiceError errMsg = new ServiceError(
          Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Error verifying access key.  If this problem persists, please contact the site admin.");
      return new AuthenticationResult(Response.status(Status.INTERNAL_SERVER_ERROR).entity(errMsg).build());
    }
    if (key.getExpiry() > 0 && key.getExpiry() <= when) {
      ServiceError errMsg = new ServiceError(Status.FORBIDDEN.getStatusCode(), "Access key expired, contact key owner for renewal");
      return new AuthenticationResult(Response.status(Status.FORBIDDEN).entity(errMsg).build());
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
          Status.FORBIDDEN.getStatusCode(), "Access key not authorized to access the requested model type, contact key owner");
      return new AuthenticationResult(Response.status(Status.FORBIDDEN).entity(errMsg).build());
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
        Status.FORBIDDEN.getStatusCode(), "Access key not authorized to access the requested model object, contact key owner");
    return new AuthenticationResult(Response.status(Status.FORBIDDEN).entity(errMsg).build());
  }

  private static SimpleDateFormat dateFormat              = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
  public static final long        DEFAULT_EXPIRY_INTERVAL = TimeUnit.MILLISECONDS.convert(24, TimeUnit.HOURS);
  private static String           versionString           = "2.0";

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
    return result.header("Date", getServerTime(when)).header("EveKit-Version", versionString);
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
    log.fine(join(", ", "AUDIT", "RESOURCEACCESS", "FROM", src, "USER", key.getSyncAccount().getUserAccount().getUid(), "ACCT",
                  String.valueOf(key.getSyncAccount().getAid()), "KEY", String.valueOf(key.getAccessKey()), "OP", op == null ? "NA" : op.toString(), "PATH",
                  path));
  }

  public static void auditAccess(
                                 SynchronizedAccountAccessKey key,
                                 Collection<AccountAccessMask> op,
                                 String src,
                                 String path) {
    log.fine(join(", ", "AUDIT", "RESOURCEACCESS", "FROM", src, "USER", key.getSyncAccount().getUserAccount().getUid(), "ACCT",
                  String.valueOf(key.getSyncAccount().getAid()), "KEY", String.valueOf(key.getAccessKey()), "OP",
                  Arrays.toString(op.toArray()).replace(", ", "|"), "PATH", path));
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

  public static long computeExpiry(
                                   long when,
                                   SynchronizedEveAccount acct,
                                   AccountAccessMask type)
    throws IllegalArgumentException {
    Capsuleer caps = null;
    Corporation corp = null;
    boolean isCap = acct.isCharacterType();
    if (isCap) {
      caps = Capsuleer.getCapsuleer(acct);
      // This is normally an error, but can happen if access is attempted before
      // an account has sync'd for the first time.
      if (caps == null) return when;
    } else {
      corp = Corporation.getCorporation(acct);
      // This is normally an error, but can happen if access is attempted before
      // an account has sync'd for the first time.
      if (corp == null) return when;
    }
    switch (type) {
    case ACCESS_ACCOUNT_BALANCE:
      return Math.max(when, isCap ? caps.getAccountBalanceExpiry() : corp.getAccountBalanceExpiry());
    case ACCESS_ASSETS:
      return Math.max(when, isCap ? caps.getAssetListExpiry() : corp.getAssetListExpiry());
    case ACCESS_BLUEPRINTS:
      return Math.max(when, isCap ? caps.getBlueprintsExpiry() : corp.getBlueprintsExpiry());
    case ACCESS_BOOKMARKS:
      return Math.max(when, isCap ? caps.getBookmarksExpiry() : corp.getBookmarksExpiry());
    case ACCESS_CONTACT_LIST:
      return Math.max(when, isCap ? caps.getContactListExpiry() : corp.getContactListExpiry());
    case ACCESS_CONTRACTS:
      return Math.max(when, isCap ? caps.getContractsExpiry() : corp.getContractsExpiry());
    case ACCESS_FAC_WAR_STATS:
      return Math.max(when, isCap ? caps.getFacWarStatsExpiry() : corp.getFacWarStatsExpiry());
    case ACCESS_INDUSTRY_JOBS:
      return Math.max(when, isCap ? caps.getIndustryJobsExpiry() : corp.getIndustryJobsExpiry());
    case ACCESS_KILL_LOG:
      return Math.max(when, isCap ? caps.getKilllogExpiry() : corp.getKilllogExpiry());
    case ACCESS_MARKET_ORDERS:
      return Math.max(when, isCap ? caps.getMarketOrdersExpiry() : corp.getMarketOrdersExpiry());
    case ACCESS_STANDINGS:
      return Math.max(when, isCap ? caps.getStandingsExpiry() : corp.getStandingsExpiry());
    case ACCESS_WALLET_JOURNAL:
      return Math.max(when, isCap ? caps.getWalletJournalExpiry() : corp.getWalletJournalExpiry());
    case ACCESS_WALLET_TRANSACTIONS:
      return Math.max(when, isCap ? caps.getWalletTransactionsExpiry() : corp.getWalletTransactionsExpiry());
    case ALLOW_METADATA_CHANGES:
      // Character Specific Resources
    case ACCESS_ACCOUNT_STATUS:
      return Math.max(when, caps.getAccountStatusExpiry());
    case ACCESS_CALENDAR_EVENT_ATTENDEES:
      return Math.max(when, caps.getCalendarEventAttendeesExpiry());
    case ACCESS_CHARACTER_SHEET:
      return Math.max(when, caps.getCharacterSheetExpiry());
    case ACCESS_CHAT_CHANNELS:
      return Math.max(when, caps.getChatChannelsExpiry());
    case ACCESS_CONTACT_NOTIFICATIONS:
      return Math.max(when, caps.getContactNotificationsExpiry());
    case ACCESS_MAIL:
      return Math.max(when, caps.getMailMessagesExpiry());
    case ACCESS_MAILING_LISTS:
      return Math.max(when, caps.getMailingListsExpiry());
    case ACCESS_MEDALS:
      return Math.max(when, caps.getMedalsExpiry());
    case ACCESS_NOTIFICATIONS:
      return Math.max(when, caps.getNotificationsExpiry());
    case ACCESS_RESEARCH:
      return Math.max(when, caps.getResearchExpiry());
    case ACCESS_SKILL_IN_TRAINING:
      return Math.max(when, caps.getSkillInTrainingExpiry());
    case ACCESS_SKILL_QUEUE:
      return Math.max(when, caps.getSkillQueueExpiry());
    case ACCESS_UPCOMING_CALENDAR_EVENTS:
      return Math.max(when, caps.getUpcomingCalendarEventsExpiry());
    // Corporation Specific Resources
    case ACCESS_CONTAINER_LOG:
      return Math.max(when, corp.getContainerLogExpiry());
    case ACCESS_CORPORATION_MEDALS:
      return Math.max(when, corp.getMemberMedalsExpiry());
    case ACCESS_CORPORATION_SHEET:
      return Math.max(when, corp.getCorporationSheetExpiry());
    case ACCESS_CORPORATION_TITLES:
      return Math.max(when, corp.getTitlesExpiry());
    case ACCESS_MEMBER_MEDALS:
      return Math.max(when, corp.getMemberMedalsExpiry());
    case ACCESS_MEMBER_SECURITY:
      // Fall through
    case ACCESS_MEMBER_SECURITY_LOG:
      return Math.max(when, corp.getMemberSecurityExpiry());
    case ACCESS_MEMBER_TRACKING:
      return Math.max(when, corp.getMemberTrackingExpiry());
    case ACCESS_OUTPOST_LIST:
      return Math.max(when, corp.getOutpostListExpiry());
    case ACCESS_SHAREHOLDERS:
      return Math.max(when, corp.getShareholdersExpiry());
    case ACCESS_STARBASE_LIST:
      return Math.max(when, corp.getStarbaseListExpiry());
    default:
      return when;
    }
  }

  public static void sanitizeAttributeSelector(
                                               AttributeSelector as) {
    // restrict size of string parameters for all settings to less than 200 characters
    if (as.start != null && as.start.length() > 200) as.start = as.start.substring(0, 200);
    if (as.end != null && as.end.length() > 200) as.end = as.end.substring(0, 200);
    // allow at most 500 set members for set selectors and verify strings are not too long
    if (as.values.size() > 0) {
      Set<String> newSet = new HashSet<String>();
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
