package enterprises.orbital.evekit.ws;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountHandlerUtil {
  private static final Logger log = Logger.getLogger(AccountHandlerUtil.class.getName());

  // Default max result size
  private static final String PROP_RESULT_LIMIT = "enterprises.orbital.evekit.sync_model_ws.max_results";
  private static final int DEF_RESULT_LIMIT = 1000;

  // Endpoint expiry map
  @SuppressWarnings("JavacQuirks")
  private static final Map<ESISyncEndpoint, Long> modelExpiry = Stream.of(
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_WALLET_BALANCE, TimeUnit.MILLISECONDS.convert(120, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_WALLET_BALANCE, TimeUnit.MILLISECONDS.convert(300, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_AGENTS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_WALLET_JOURNAL, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_WALLET_JOURNAL, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_WALLET_TRANSACTIONS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_WALLET_TRANSACTIONS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_ASSETS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_ASSETS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_BLUEPRINTS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_BLUEPRINTS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_MARKET, TimeUnit.MILLISECONDS.convert(1200, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_MARKET, TimeUnit.MILLISECONDS.convert(1200, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_STANDINGS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_STANDINGS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_CONTRACTS, TimeUnit.MILLISECONDS.convert(300, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_CONTRACTS, TimeUnit.MILLISECONDS.convert(300, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_INDUSTRY, TimeUnit.MILLISECONDS.convert(300, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_INDUSTRY, TimeUnit.MILLISECONDS.convert(300, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_CONTAINER_LOGS, TimeUnit.MILLISECONDS.convert(600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_LOCATION, TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_SHIP_TYPE, TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_ONLINE, TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_BOOKMARKS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_BOOKMARKS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_KILL_MAIL, TimeUnit.MILLISECONDS.convert(120, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_KILL_MAIL, TimeUnit.MILLISECONDS.convert(300, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_SHEET, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_SKILLS, TimeUnit.MILLISECONDS.convert(120, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_CLONES, TimeUnit.MILLISECONDS.convert(120, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_FATIGUE, TimeUnit.MILLISECONDS.convert(300, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_SKILL_QUEUE, TimeUnit.MILLISECONDS.convert(120, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_IMPLANTS, TimeUnit.MILLISECONDS.convert(300, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_MAIL, TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_CONTACTS, TimeUnit.MILLISECONDS.convert(300, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_CONTACTS, TimeUnit.MILLISECONDS.convert(300, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_FACTION_WAR, TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_FACTION_WAR, TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_PLANETS, TimeUnit.MILLISECONDS.convert(600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_CALENDAR, TimeUnit.MILLISECONDS.convert(600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_CORP_ROLES, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_NOTIFICATIONS, TimeUnit.MILLISECONDS.convert(600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_MEDALS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_TITLES, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_MEDALS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_SHEET, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_TRACK_MEMBERS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_SHAREHOLDERS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_DIVISIONS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_TITLES, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_MEMBERSHIP, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_CUSTOMS, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_FACILITIES, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_STARBASES, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_FITTINGS, TimeUnit.MILLISECONDS.convert(300, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_LOYALTY, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_MINING, TimeUnit.MILLISECONDS.convert(600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_MINING, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_FLEETS, TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CHAR_OPPORTUNITIES, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS)),
      new AbstractMap.SimpleEntry<>(ESISyncEndpoint.CORP_STRUCTURES, TimeUnit.MILLISECONDS.convert(3600, TimeUnit.SECONDS))
                                                                         )
                                                                      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

  // Standard handling for an illegal selector value
  private static Response handleIllegalSelector() {
    ServiceError errMsg = new ServiceError(Response.Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
    return Response.status(Response.Status.BAD_REQUEST)
                   .entity(errMsg)
                   .build();
  }

  // Standard handling for an IO error while querying data
  private static Response handleIOError(IOException e) {
    log.log(Level.WARNING, "Error retrieving values", e);
    ServiceError errMsg = new ServiceError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Internal error retrieving value.  If this error persists, please contact the site administrator");
    return Response.status(Response.Status.BAD_REQUEST)
                   .entity(errMsg)
                   .build();
  }

  // Standard interface for retrieving a list of results
  public interface QueryCaller<A extends CachedData> {
    List<A> getList(SynchronizedEveAccount acct, long contid, int maxresults, boolean reverse, AttributeSelector at,
                    AttributeSelector... others) throws IOException;

    long getExpiry(SynchronizedEveAccount acct);
  }

  // Standard determination of result expiry
  public static long handleStandardExpiry(ESISyncEndpoint ep, SynchronizedEveAccount acct) {
    long shift = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
    long def = OrbitalProperties.getCurrentTime() + shift;
    try {
      return Math.max(def, ESIEndpointSyncTracker.getLatestFinishedTracker(acct, ep)
                                                 .getSyncEnd() + modelExpiry.get(ep) + shift);
    } catch (IOException | TrackerNotFoundException e) {
      // Log and return current time plus a small delta
      log.log(Level.WARNING, "Error retrieving last tracker finish time", e);
      return def;
    }
  }

  // Standard handler for a list retriever with one or more attribute selectors
  public static <A extends CachedData> Response handleStandardListRequest(int accessKey,
                                                                          String accessCred,
                                                                          AccountAccessMask mask,
                                                                          AttributeSelector at,
                                                                          long contid,
                                                                          int maxresults,
                                                                          boolean reverse,
                                                                          QueryCaller<A> query,
                                                                          HttpServletRequest request,
                                                                          AttributeSelector... sels) {
    ServiceUtil.sanitizeAttributeSelector(at);
    ServiceUtil.sanitizeAttributeSelector(sels);
    maxresults = Math.min(PersistentProperty.getIntegerPropertyWithFallback(PROP_RESULT_LIMIT, DEF_RESULT_LIMIT), maxresults);
    ServiceUtil.AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, mask);
    if (cfg.fail) return cfg.response;
    try {
      List<A> results = query.getList(cfg.owner, contid, maxresults, reverse, at, sels);
      for (CachedData next : results) next.prepareTransient();
      cfg.presetExpiry = query.getExpiry(cfg.owner);
      return ServiceUtil.finish(cfg, results, request);
    } catch (NumberFormatException e) {
      return handleIllegalSelector();
    } catch (IOException e) {
      return handleIOError(e);
    }
  }


}
