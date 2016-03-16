package enterprises.orbital.evekit.ws.common;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.NoSuchKeyException;
import enterprises.orbital.evekit.account.SynchronizedAccountAccessKey;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
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
    public long                         at;
    public AccountAccessMask            mask;
    public boolean                      fail = false;
    public Response                     response;
    public SynchronizedEveAccount       owner;
    public SynchronizedAccountAccessKey key;

    public AccessConfig(int accessKey, String accessMask, long at, AccountAccessMask mask) {
      super();
      this.accessKey = accessKey;
      this.accessCred = accessMask;
      this.when = OrbitalProperties.getCurrentTime();
      this.at = at < 0 ? when : at;
      this.mask = mask;
    }

  }

  public static AccessConfig start(
                                   int accessKey,
                                   String accessCred,
                                   long at,
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

  public static Response finish(
                                AccessConfig cfg,
                                CachedData result,
                                HttpServletRequest request) {
    updateLifeline(cfg.at, result);
    long expiry = computeExpiry(cfg.when, cfg.owner, cfg.mask);
    auditAccess(cfg.key, cfg.mask, getSource(request), getRequestURI(request));
    return stamp(Response.ok().entity(result), cfg.when, expiry).build();
  }

  public static <A extends CachedData> Response finish(
                                                       AccessConfig cfg,
                                                       Collection<A> result,
                                                       HttpServletRequest request) {
    updateLifeline(cfg.at, result);
    long expiry = computeExpiry(cfg.when, cfg.owner, cfg.mask);
    auditAccess(cfg.key, cfg.mask, getSource(request), getRequestURI(request));
    return stamp(Response.ok().entity(result), cfg.when, expiry).build();
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
                                                  long at) {
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
    if (key.getLimit() > 0 && key.getLimit() > at) {
      ServiceError errMsg = new ServiceError(
          Status.FORBIDDEN.getStatusCode(), "Access key not authorized to access the model at the requested time in the lifeline, contact key owner");
      return new AuthenticationResult(Response.status(Status.FORBIDDEN).entity(errMsg).build());
    }
    return new AuthenticationResult(key);
  }

  public static AuthenticationResult authenticate(
                                                  int id,
                                                  String hash,
                                                  long when,
                                                  long at,
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
    if (expiry <= 0) expiry = when + DEFAULT_EXPIRY_INTERVAL;
    return result.header("Date", getServerTime(when)).expires(new Date(expiry)).header("EveKit-Version", versionString);
  }

  public static void auditAccess(
                                 SynchronizedAccountAccessKey key,
                                 AccountAccessMask op,
                                 String src,
                                 String path) {
    log.fine(String.join(", ", "AUDIT", "RESOURCEACCESS", "FROM", src, "USER", key.getSyncAccount().getUserAccount().getUid(), "ACCT",
                         String.valueOf(key.getSyncAccount().getAid()), "KEY", String.valueOf(key.getAccessKey()), "OP", op.toString(), "PATH", path));
  }

  public static void auditAccess(
                                 SynchronizedAccountAccessKey key,
                                 Collection<AccountAccessMask> op,
                                 String src,
                                 String path) {
    log.fine(String.join(", ", "AUDIT", "RESOURCEACCESS", "FROM", src, "USER", key.getSyncAccount().getUserAccount().getUid(), "ACCT",
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
                                   AccountAccessMask type) throws IllegalArgumentException {
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
    case ACCESS_ACCOUNT_STATUS:
    case ACCESS_ACCOUNT_BALANCE:
      return Math.max(when, isCap ? caps.getAccountBalanceExpiry() : corp.getAccountBalanceExpiry());
    case ACCESS_ASSETS:
    case ACCESS_CONTACT_LIST:
    case ACCESS_BLUEPRINTS:
    case ACCESS_BOOKMARKS:
    case ACCESS_CONTRACTS:
    case ACCESS_FAC_WAR_STATS:
    case ACCESS_INDUSTRY_JOBS:
    case ACCESS_KILL_LOG:
    case ACCESS_MARKET_ORDERS:
    case ACCESS_STANDINGS:
    case ACCESS_WALLET_JOURNAL:
    case ACCESS_WALLET_TRANSACTIONS:
    case ALLOW_METADATA_CHANGES:
      // Character Specific Resources
    case ACCESS_CALENDAR_EVENT_ATTENDEES:
    case ACCESS_CHARACTER_SHEET:
    case ACCESS_CHAT_CHANNELS:
    case ACCESS_CONTACT_NOTIFICATIONS:
    case ACCESS_MAIL:
    case ACCESS_MAILING_LISTS:
    case ACCESS_MEDALS:
    case ACCESS_NOTIFICATIONS:
    case ACCESS_RESEARCH:
    case ACCESS_SKILL_IN_TRAINING:
    case ACCESS_SKILL_QUEUE:
    case ACCESS_UPCOMING_CALENDAR_EVENTS:
      // Corporation Specific Resources
    case ACCESS_CONTAINER_LOG:
    case ACCESS_CORPORATION_SHEET:
    case ACCESS_CORPORATION_MEDALS:
    case ACCESS_MEMBER_MEDALS:
    case ACCESS_MEMBER_SECURITY:
    case ACCESS_MEMBER_SECURITY_LOG:
    case ACCESS_MEMBER_TRACKING:
    case ACCESS_OUTPOST_LIST:
    case ACCESS_SHAREHOLDERS:
    case ACCESS_STARBASE_LIST:
    case ACCESS_CORPORATION_TITLES:
    default:
      return when;
    }
  }
}
