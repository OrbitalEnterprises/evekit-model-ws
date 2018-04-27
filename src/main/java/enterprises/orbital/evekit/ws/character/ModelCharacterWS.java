package enterprises.orbital.evekit.ws.character;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.ESISyncEndpoint;
import enterprises.orbital.evekit.model.character.*;
import enterprises.orbital.evekit.ws.AccountHandlerUtil;
import enterprises.orbital.evekit.ws.ServiceError;
import io.swagger.annotations.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

import static enterprises.orbital.evekit.ws.AccountHandlerUtil.handleStandardExpiry;

@Path("/ws/v1/char")
@Consumes({
    "application/json"
})
@Produces({
    "application/json"
})
@Api(
    tags = {
        "Character"
    },
    produces = "application/json",
    consumes = "application/json")
public class ModelCharacterWS {

  @Path("/calendar_events")
  @GET
  @ApiOperation(
      value = "Get upcoming calendar events")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested upcoming calendar events",
              response = UpcomingCalendarEvent.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getUpcomingCalendarEvents(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("duration") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "duration",
          defaultValue = "{ any: true }",
          value = "Event duration selector") AttributeSelector duration,
      @QueryParam("eventDate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "eventDate",
          defaultValue = "{ any: true }",
          value = "Event date selector (milliseconds UTC)") AttributeSelector eventDate,
      @QueryParam("eventID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "eventID",
          defaultValue = "{ any: true }",
          value = "Event ID selector") AttributeSelector eventID,
      @QueryParam("eventText") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "eventText",
          defaultValue = "{ any: true }",
          value = "Event text selector") AttributeSelector eventText,
      @QueryParam("eventTitle") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "eventTitle",
          defaultValue = "{ any: true }",
          value = "Event title selector") AttributeSelector eventTitle,
      @QueryParam("ownerID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "ownerID",
          defaultValue = "{ any: true }",
          value = "Owner ID selector") AttributeSelector ownerID,
      @QueryParam("ownerName") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "ownerName",
          defaultValue = "{ any: true }",
          value = "Owner name selector") AttributeSelector ownerName,
      @QueryParam("response") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "response",
          defaultValue = "{ any: true }",
          value = "Response text selector") AttributeSelector response,
      @QueryParam("importance") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "importance",
          defaultValue = "{ any: true }",
          value = "Importance selector") AttributeSelector importance,
      @QueryParam("ownerType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "ownerType",
          defaultValue = "{ any: true }",
          value = "Event owner type selector") AttributeSelector ownerType) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_UPCOMING_CALENDAR_EVENTS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<UpcomingCalendarEvent>() {

                                                          @Override
                                                          public List<UpcomingCalendarEvent> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int DURATION = 0;
                                                            final int EVENT_DATE = 1;
                                                            final int EVENT_ID = 2;
                                                            final int EVENT_TEXT = 3;
                                                            final int EVENT_TITLE = 4;
                                                            final int OWNER_ID = 5;
                                                            final int OWNER_NAME = 6;
                                                            final int RESPONSE = 7;
                                                            final int IMPORTANCE = 8;
                                                            final int OWNER_TYPE = 9;
                                                            return UpcomingCalendarEvent.accessQuery(acct, contid,
                                                                                                     maxresults,
                                                                                                     reverse, at,
                                                                                                     others[DURATION],
                                                                                                     others[EVENT_DATE],
                                                                                                     others[EVENT_ID],
                                                                                                     others[EVENT_TEXT],
                                                                                                     others[EVENT_TITLE],
                                                                                                     others[OWNER_ID],
                                                                                                     others[OWNER_NAME],
                                                                                                     others[RESPONSE],
                                                                                                     others[IMPORTANCE],
                                                                                                     others[OWNER_TYPE]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_CALENDAR,
                                                                                        acct);
                                                          }
                                                        }, request, duration, eventDate, eventID, eventText, eventTitle,
                                                        ownerID, ownerName, response, importance, ownerType);
  }

  @Path("/calendar_event_attendees")
  @GET
  @ApiOperation(
      value = "Get calendar event attendees")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested calendar event attendees",
              response = CalendarEventAttendee.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getCalendarEventAttendees(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("eventID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "eventID",
          defaultValue = "{ any: true }",
          value = "Calendar event ID selector") AttributeSelector eventID,
      @QueryParam("characterID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "characterID",
          defaultValue = "{ any: true }",
          value = "Attending character ID selector") AttributeSelector characterID,
      @QueryParam("response") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "response",
          defaultValue = "{ any: true }",
          value = "Attendee response selector") AttributeSelector response) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_CALENDAR_EVENT_ATTENDEES,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CalendarEventAttendee>() {

                                                          @Override
                                                          public List<CalendarEventAttendee> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int EVENT_ID = 0;
                                                            final int CHARACTER_ID = 1;
                                                            final int RESPONSE = 2;
                                                            return CalendarEventAttendee.accessQuery(acct, contid,
                                                                                                     maxresults,
                                                                                                     reverse, at,
                                                                                                     others[EVENT_ID],
                                                                                                     others[CHARACTER_ID],
                                                                                                     others[RESPONSE]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_CALENDAR,
                                                                                        acct);
                                                          }
                                                        }, request, eventID, characterID, response);
  }

  @Path("/role")
  @GET
  @ApiOperation(
      value = "Get character roles")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character roles",
              response = CharacterRole.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getRoles(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("roleCategory") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "roleCategory",
          defaultValue = "{ any: true }",
          value = "Role category selector") AttributeSelector roleCategory,
      @QueryParam("roleName") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "roleName",
          defaultValue = "{ any: true }",
          value = "Role name selector") AttributeSelector roleName) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CHARACTER_SHEET,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterRole>() {

                                                          @Override
                                                          public List<CharacterRole> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int ROLE_CATEGORY = 0;
                                                            final int ROLE_NAME = 1;
                                                            return CharacterRole.accessQuery(acct, contid, maxresults,
                                                                                             reverse, at,
                                                                                             others[ROLE_CATEGORY],
                                                                                             others[ROLE_NAME]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_CORP_ROLES,
                                                                                        acct);
                                                          }
                                                        }, request, roleCategory, roleName);
  }

  @Path("/sheet")
  @GET
  @ApiOperation(
      value = "Get character sheet")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character sheets",
              response = CharacterSheet.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getCharacterSheets(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("characterID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "characterID",
          defaultValue = "{ any: true }",
          value = "Character ID selector") AttributeSelector characterID,
      @QueryParam("name") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "name",
          defaultValue = "{ any: true }",
          value = "Name selector") AttributeSelector name,
      @QueryParam("corporationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "corporationID",
          defaultValue = "{ any: true }",
          value = "Corporation ID selector") AttributeSelector corporationID,
      @QueryParam("raceID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "raceID",
          defaultValue = "{ any: true }",
          value = "Race selector") AttributeSelector raceID,
      @QueryParam("doB") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "doB",
          defaultValue = "{ any: true }",
          value = "Date of birth selector") AttributeSelector doB,
      @QueryParam("bloodlineID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "bloodlineID",
          defaultValue = "{ any: true }",
          value = "Bloodline ID selector") AttributeSelector bloodlineID,
      @QueryParam("ancestryID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "ancestryID",
          defaultValue = "{ any: true }",
          value = "Ancestry ID selector") AttributeSelector ancestryID,
      @QueryParam("gender") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "gender",
          defaultValue = "{ any: true }",
          value = "Gender selector") AttributeSelector gender,
      @QueryParam("allianceID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "allianceID",
          defaultValue = "{ any: true }",
          value = "Alliance ID selector") AttributeSelector allianceID,
      @QueryParam("factionID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "factionID",
          defaultValue = "{ any: true }",
          value = "Faction ID selector") AttributeSelector factionID,
      @QueryParam("description") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "description",
          defaultValue = "{ any: true }",
          value = "Description selector") AttributeSelector description,
      @QueryParam("securityStatus") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "securityStatus",
          defaultValue = "{ any: true }",
          value = "Security status selector") AttributeSelector securityStatus) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CHARACTER_SHEET,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterSheet>() {

                                                          @Override
                                                          public List<CharacterSheet> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int CHARACTER_ID = 0;
                                                            final int NAME = 1;
                                                            final int CORPORATION_ID = 2;
                                                            final int RACE_ID = 3;
                                                            final int DOB = 4;
                                                            final int BLOODLINE_ID = 5;
                                                            final int ANCESTRY_ID = 6;
                                                            final int GENDER = 7;
                                                            final int ALLIANCE_ID = 8;
                                                            final int FACTION_ID = 9;
                                                            final int DESCRIPTION = 10;
                                                            final int SECURITY_STATUS = 11;
                                                            return CharacterSheet.accessQuery(acct, contid, maxresults,
                                                                                              reverse, at,
                                                                                              others[CHARACTER_ID],
                                                                                              others[NAME],
                                                                                              others[CORPORATION_ID],
                                                                                              others[RACE_ID],
                                                                                              others[DOB],
                                                                                              others[BLOODLINE_ID],
                                                                                              others[ANCESTRY_ID],
                                                                                              others[GENDER],
                                                                                              others[ALLIANCE_ID],
                                                                                              others[FACTION_ID],
                                                                                              others[DESCRIPTION],
                                                                                              others[SECURITY_STATUS]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_SHEET,
                                                                                        acct);
                                                          }
                                                        }, request, characterID, name, corporationID, raceID, doB,
                                                        bloodlineID, ancestryID, gender, allianceID, factionID,
                                                        description, securityStatus);
  }

  @Path("/sheet_attributes")
  @GET
  @ApiOperation(
      value = "Get character sheet attributes")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character sheet attributes",
              response = CharacterSheetAttributes.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getCharacterSheetAttributes(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("intelligence") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "intelligence",
          defaultValue = "{ any: true }",
          value = "Intelligence selector") AttributeSelector intelligence,
      @QueryParam("memory") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "memory",
          defaultValue = "{ any: true }",
          value = "Memory selector") AttributeSelector memory,
      @QueryParam("charisma") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "charisma",
          defaultValue = "{ any: true }",
          value = "Charisma selector") AttributeSelector charisma,
      @QueryParam("perception") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "perception",
          defaultValue = "{ any: true }",
          value = "Perception selector") AttributeSelector perception,
      @QueryParam("willpower") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "willpower",
          defaultValue = "{ any: true }",
          value = "Willpower selector") AttributeSelector willpower,
      @QueryParam("lastRemapDate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "lastRemapDate",
          defaultValue = "{ any: true }",
          value = "Last remap date selector") AttributeSelector lastRemapDate,
      @QueryParam("accruedRemapCooldownDate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "accruedRemapCooldownDate",
          defaultValue = "{ any: true }",
          value = "Accrued remap cooldown date selector") AttributeSelector accruedRemapCooldownDate,
      @QueryParam("bonusRemaps") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "bonusRemaps",
          defaultValue = "{ any: true }",
          value = "Bonus remaps selector") AttributeSelector bonusRemaps) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CHARACTER_SHEET,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterSheetAttributes>() {

                                                          @Override
                                                          public List<CharacterSheetAttributes> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int INTELLIGENCE = 0;
                                                            final int MEMORY = 1;
                                                            final int CHARISMA = 2;
                                                            final int PERCEPTION = 3;
                                                            final int WILLPOWER = 4;
                                                            final int BONUS_REMAPS = 5;
                                                            final int LAST_REMAP_DATE = 6;
                                                            final int ACCRUED_REMAP_COOLDOWN_DATE = 7;
                                                            return CharacterSheetAttributes.accessQuery(acct, contid,
                                                                                                        maxresults,
                                                                                                        reverse, at,
                                                                                                        others[INTELLIGENCE],
                                                                                                        others[MEMORY],
                                                                                                        others[CHARISMA],
                                                                                                        others[PERCEPTION],
                                                                                                        others[WILLPOWER],
                                                                                                        others[BONUS_REMAPS],
                                                                                                        others[LAST_REMAP_DATE],
                                                                                                        others[ACCRUED_REMAP_COOLDOWN_DATE]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_SKILLS,
                                                                                        acct);
                                                          }
                                                        }, request, intelligence, memory, charisma, perception,
                                                        willpower, bonusRemaps, lastRemapDate,
                                                        accruedRemapCooldownDate);
  }

  @Path("/clone_jump_timer")
  @GET
  @ApiOperation(
      value = "Get character clone jump timers")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested clone jump timers",
              response = CharacterSheetClone.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getCloneJumpTimers(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("cloneJumpDate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "cloneJumpDate",
          defaultValue = "{ any: true }",
          value = "Clone jump date selector") AttributeSelector cloneJumpDate,
      @QueryParam("homeStationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "homeStationID",
          defaultValue = "{ any: true }",
          value = "Home station ID selector") AttributeSelector homeStationID,
      @QueryParam("homeStationType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "homeStationType",
          defaultValue = "{ any: true }",
          value = "Home station type selector") AttributeSelector homeStationType,
      @QueryParam("lastStationChangeDate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "lastStationChangeDate",
          defaultValue = "{ any: true }",
          value = "Last station change date selector") AttributeSelector lastStationChangeDate) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CHARACTER_SHEET,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterSheetClone>() {

                                                          @Override
                                                          public List<CharacterSheetClone> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int CLONE_JUMP_DATE = 0;
                                                            final int HOME_STATION_ID = 1;
                                                            final int HOME_STATION_TYPE = 2;
                                                            final int LAST_STATION_CHANGE_DATE = 3;
                                                            return CharacterSheetClone.accessQuery(acct, contid,
                                                                                                   maxresults,
                                                                                                   reverse, at,
                                                                                                   others[CLONE_JUMP_DATE],
                                                                                                   others[HOME_STATION_ID],
                                                                                                   others[HOME_STATION_TYPE],
                                                                                                   others[LAST_STATION_CHANGE_DATE]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_CLONES,
                                                                                        acct);
                                                          }
                                                        }, request, cloneJumpDate, homeStationID, homeStationType,
                                                        lastStationChangeDate);
  }

  @Path("/jump_timer")
  @GET
  @ApiOperation(
      value = "Get character jump timers")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character jump timers",
              response = CharacterSheetJump.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getJumpTimers(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("jumpActivation") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "jumpActivation",
          defaultValue = "{ any: true }",
          value = "Jump activation selector") AttributeSelector jumpActivation,
      @QueryParam("jumpFatigue") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "jumpFatigue",
          defaultValue = "{ any: true }",
          value = "Jump fatigue selector") AttributeSelector jumpFatigue,
      @QueryParam("jumpLastUpdate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "jumpLastUpdate",
          defaultValue = "{ any: true }",
          value = "Jump last update time selector") AttributeSelector jumpLastUpdate) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CHARACTER_SHEET,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterSheetJump>() {

                                                          @Override
                                                          public List<CharacterSheetJump> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int JUMP_ACTIVATION = 0;
                                                            final int JUMP_FATIGUE = 1;
                                                            final int JUMP_LAST_UPDATE = 2;
                                                            return CharacterSheetJump.accessQuery(acct, contid,
                                                                                                  maxresults,
                                                                                                  reverse, at,
                                                                                                  others[JUMP_ACTIVATION],
                                                                                                  others[JUMP_FATIGUE],
                                                                                                  others[JUMP_LAST_UPDATE]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_FATIGUE,
                                                                                        acct);
                                                          }
                                                        }, request, jumpActivation, jumpFatigue, jumpLastUpdate);
  }

  @Path("/skill_points")
  @GET
  @ApiOperation(
      value = "Get character skill points")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character skill points",
              response = CharacterSheetSkillPoints.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getSkillPoints(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("totalSkillPoints") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "totalSkillPoints",
          defaultValue = "{ any: true }",
          value = "Total skill points selector") AttributeSelector totalSkillPoints,
      @QueryParam("unallocatedSkillPoints") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "unallocatedSkillPoints",
          defaultValue = "{ any: true }",
          value = "Unallocated skill points selector") AttributeSelector unallocatedSkillPoints) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CHARACTER_SHEET,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterSheetSkillPoints>() {

                                                          @Override
                                                          public List<CharacterSheetSkillPoints> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int TOTAL_SKILL_POINTS = 0;
                                                            final int UNALLOCATED_SKILL_POINTS = 1;
                                                            return CharacterSheetSkillPoints.accessQuery(acct, contid,
                                                                                                         maxresults,
                                                                                                         reverse, at,
                                                                                                         others[TOTAL_SKILL_POINTS],
                                                                                                         others[UNALLOCATED_SKILL_POINTS]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_SKILLS,
                                                                                        acct);
                                                          }
                                                        }, request, totalSkillPoints, unallocatedSkillPoints);
  }

  @Path("/skill")
  @GET
  @ApiOperation(
      value = "Get character skills")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character skills",
              response = CharacterSkill.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getSkills(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Skill type ID selector") AttributeSelector typeID,
      @QueryParam("trainedSkillLevel") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "trainedSkillLevel",
          defaultValue = "{ any: true }",
          value = "Trained skill level selector") AttributeSelector trainedSkillLevel,
      @QueryParam("skillpoints") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "skillpoints",
          defaultValue = "{ any: true }",
          value = "Skill points selector") AttributeSelector skillpoints,
      @QueryParam("activeSkillLevel") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "activeSkillLevel",
          defaultValue = "{ any: true }",
          value = "Active skill level selector") AttributeSelector activeSkillLevel) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CHARACTER_SHEET,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterSkill>() {

                                                          @Override
                                                          public List<CharacterSkill> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int TYPE_ID = 0;
                                                            final int TRAINED_SKILL_LEVEL = 1;
                                                            final int SKILLPOINTS = 1;
                                                            final int ACTIVE_SKILL_LEVEL = 1;
                                                            return CharacterSkill.accessQuery(acct, contid, maxresults,
                                                                                              reverse, at,
                                                                                              others[TYPE_ID],
                                                                                              others[TRAINED_SKILL_LEVEL],
                                                                                              others[SKILLPOINTS],
                                                                                              others[ACTIVE_SKILL_LEVEL]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_SKILLS,
                                                                                        acct);
                                                          }
                                                        }, request, typeID, trainedSkillLevel, skillpoints,
                                                        activeSkillLevel);
  }

  @Path("/title")
  @GET
  @ApiOperation(
      value = "Get character titles")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character titles",
              response = CharacterTitle.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getTitles(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("titleID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "titleID",
          defaultValue = "{ any: true }",
          value = "Character title ID selector") AttributeSelector titleID,
      @QueryParam("titleName") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "titleName",
          defaultValue = "{ any: true }",
          value = "Character title name selector") AttributeSelector titleName) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_CHARACTER_SHEET,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterTitle>() {

                                                          @Override
                                                          public List<CharacterTitle> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int TITLE_ID = 0;
                                                            final int TITLE_NAME = 1;
                                                            return CharacterTitle.accessQuery(acct,
                                                                                              contid,
                                                                                              maxresults,
                                                                                              reverse, at,
                                                                                              others[TITLE_ID],
                                                                                              others[TITLE_NAME]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                ESISyncEndpoint.CHAR_TITLES,
                                                                acct);
                                                          }
                                                        }, request, titleID, titleName);
  }

  @Path("/fittings")
  @GET
  @ApiOperation(
      value = "Get character ship fittings")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested ship fittings",
              response = Fitting.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getFittings(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("fittingID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "fittingID",
          defaultValue = "{ any: true }",
          value = "Ship fitting ID selector") AttributeSelector fittingID,
      @QueryParam("name") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "name",
          defaultValue = "{ any: true }",
          value = "Ship fitting name selector") AttributeSelector name,
      @QueryParam("description") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "description",
          defaultValue = "{ any: true }",
          value = "Ship fitting description selector") AttributeSelector description,
      @QueryParam("shipTypeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "shipTypeID",
          defaultValue = "{ any: true }",
          value = "Ship fitting ship type ID selector") AttributeSelector shipTypeID) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_FITTINGS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Fitting>() {

                                                          @Override
                                                          public List<Fitting> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int FITTING_ID = 0;
                                                            final int NAME = 1;
                                                            final int DESCRIPTION = 2;
                                                            final int SHIP_TYPE_ID = 3;
                                                            return Fitting.accessQuery(acct, contid, maxresults,
                                                                                       reverse, at,
                                                                                       others[FITTING_ID],
                                                                                       others[NAME],
                                                                                       others[DESCRIPTION],
                                                                                       others[SHIP_TYPE_ID]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_FITTINGS,
                                                                                        acct);
                                                          }
                                                        }, request, fittingID, name, description, shipTypeID);
  }

  @Path("/fitting_items")
  @GET
  @ApiOperation(
      value = "Get character ship fitting items")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested ship fitting items",
              response = FittingItem.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getFittingItems(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("fittingID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "fittingID",
          defaultValue = "{ any: true }",
          value = "Ship fitting item fitting ID selector") AttributeSelector fittingID,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Ship fitting item type ID selector") AttributeSelector typeID,
      @QueryParam("flag") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "flag",
          defaultValue = "{ any: true }",
          value = "Ship fitting item flag selector") AttributeSelector flag,
      @QueryParam("quantity") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "quantity",
          defaultValue = "{ any: true }",
          value = "Ship fitting item quantity selector") AttributeSelector quantity) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_FITTINGS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<FittingItem>() {

                                                          @Override
                                                          public List<FittingItem> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int FITTING_ID = 0;
                                                            final int TYPE_ID = 1;
                                                            final int FLAG = 2;
                                                            final int QUANTITY = 3;
                                                            return FittingItem.accessQuery(acct, contid, maxresults,
                                                                                           reverse, at,
                                                                                           others[FITTING_ID],
                                                                                           others[TYPE_ID],
                                                                                           others[FLAG],
                                                                                           others[QUANTITY]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_FITTINGS,
                                                                                        acct);
                                                          }
                                                        }, request, fittingID, typeID, flag, quantity);
  }

  @Path("/implant")
  @GET
  @ApiOperation(
      value = "Get character implants")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested implants",
              response = Implant.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getImplants(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Implant type ID selector") AttributeSelector typeID) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CHARACTER_SHEET,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Implant>() {

                                                          @Override
                                                          public List<Implant> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int TYPE_ID = 0;
                                                            return Implant.accessQuery(acct, contid, maxresults,
                                                                                       reverse, at,
                                                                                       others[TYPE_ID]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_IMPLANTS,
                                                                                        acct);
                                                          }
                                                        }, request, typeID);
  }

  @Path("/jump_clone")
  @GET
  @ApiOperation(
      value = "Get character jump clones")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested jump clones",
              response = JumpClone.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getJumpClones(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("jumpCloneID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "jumpCloneID",
          defaultValue = "{ any: true }",
          value = "Jump clone ID selector") AttributeSelector jumpCloneID,
      @QueryParam("locationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "locationID",
          defaultValue = "{ any: true }",
          value = "Jump clone location ID selector") AttributeSelector locationID,
      @QueryParam("cloneName") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "cloneName",
          defaultValue = "{ any: true }",
          value = "Clone name selector selector") AttributeSelector cloneName,
      @QueryParam("locationType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "locationType",
          defaultValue = "{ any: true }",
          value = "Location type selector") AttributeSelector locationType) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CHARACTER_SHEET,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<JumpClone>() {

                                                          @Override
                                                          public List<JumpClone> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int JUMP_CLONE_ID = 0;
                                                            final int LOCATION_ID = 1;
                                                            final int CLONE_NAME = 2;
                                                            final int LOCATION_TYPE = 3;
                                                            return JumpClone.accessQuery(acct, contid, maxresults,
                                                                                         reverse, at,
                                                                                         others[JUMP_CLONE_ID],
                                                                                         others[LOCATION_ID],
                                                                                         others[CLONE_NAME],
                                                                                         others[LOCATION_TYPE]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_CLONES,
                                                                                        acct);
                                                          }
                                                        }, request, jumpCloneID, locationID, cloneName, locationType);
  }

  @Path("/jump_clone_implant")
  @GET
  @ApiOperation(
      value = "Get character jump clone implants")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested jump clone implants",
              response = JumpCloneImplant.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getJumpCloneImplants(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("jumpCloneID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "jumpCloneID",
          defaultValue = "{ any: true }",
          value = "Jump clone ID selector") AttributeSelector jumpCloneID,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Implant type ID selector") AttributeSelector typeID) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CHARACTER_SHEET,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<JumpCloneImplant>() {

                                                          @Override
                                                          public List<JumpCloneImplant> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int JUMP_CLONE_ID = 0;
                                                            final int TYPE_ID = 1;
                                                            return JumpCloneImplant.accessQuery(acct, contid,
                                                                                                maxresults,
                                                                                                reverse, at,
                                                                                                others[JUMP_CLONE_ID],
                                                                                                others[TYPE_ID]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_CLONES,
                                                                                        acct);
                                                          }
                                                        }, request, jumpCloneID, typeID);
  }

  @Path("/medal")
  @GET
  @ApiOperation(
      value = "Get character medals")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character medals",
              response = CharacterMedal.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getMedals(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("description") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "description",
          defaultValue = "{ any: true }",
          value = "Medal description selector") AttributeSelector description,
      @QueryParam("medalID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "medalID",
          defaultValue = "{ any: true }",
          value = "Medal ID selector") AttributeSelector medalID,
      @QueryParam("title") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "title",
          defaultValue = "{ any: true }",
          value = "Medal title selector") AttributeSelector title,
      @QueryParam("corporationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "corporationID",
          defaultValue = "{ any: true }",
          value = "Awarding corporation ID selector") AttributeSelector corporationID,
      @QueryParam("issued") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "issued",
          defaultValue = "{ any: true }",
          value = "Issue date selector") AttributeSelector issued,
      @QueryParam("issuerID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "issuerID",
          defaultValue = "{ any: true }",
          value = "Issuer ID selector") AttributeSelector issuerID,
      @QueryParam("reason") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "reason",
          defaultValue = "{ any: true }",
          value = "Medal award reason selector") AttributeSelector reason,
      @QueryParam("status") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "status",
          defaultValue = "{ any: true }",
          value = "Medal status selector") AttributeSelector status) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_MEDALS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterMedal>() {

                                                          @Override
                                                          public List<CharacterMedal> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int DESCRIPTION = 0;
                                                            final int MEDAL_ID = 1;
                                                            final int TITLE = 2;
                                                            final int CORPORATION_ID = 3;
                                                            final int ISSUED = 4;
                                                            final int ISSUER_ID = 5;
                                                            final int REASON = 6;
                                                            final int STATUS = 7;
                                                            return CharacterMedal.accessQuery(acct,
                                                                                              contid,
                                                                                              maxresults,
                                                                                              reverse, at,
                                                                                              others[DESCRIPTION],
                                                                                              others[MEDAL_ID],
                                                                                              others[TITLE],
                                                                                              others[CORPORATION_ID],
                                                                                              others[ISSUED],
                                                                                              others[ISSUER_ID],
                                                                                              others[REASON],
                                                                                              others[STATUS]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                ESISyncEndpoint.CHAR_MEDALS,
                                                                acct);
                                                          }
                                                        }, request, description, medalID, title, corporationID,
                                                        issued, issuerID, reason, status);
  }

  @Path("/medal_graphic")
  @GET
  @ApiOperation(
      value = "Get character medal graphics")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character medal graphics",
              response = CharacterMedalGraphic.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getMedalGraphics(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("medalID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "medalID",
          defaultValue = "{ any: true }",
          value = "Medal ID selector") AttributeSelector medalID,
      @QueryParam("issued") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "issued",
          defaultValue = "{ any: true }",
          value = "Medal issued selector") AttributeSelector issued,
      @QueryParam("part") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "part",
          defaultValue = "{ any: true }",
          value = "Medal graphic part selector") AttributeSelector part,
      @QueryParam("layer") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "layer",
          defaultValue = "{ any: true }",
          value = "Medal graphic layer selector") AttributeSelector layer,
      @QueryParam("graphic") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "graphic",
          defaultValue = "{ any: true }",
          value = "Medal graphic name selector") AttributeSelector graphic,
      @QueryParam("color") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "color",
          defaultValue = "{ any: true }",
          value = "Medal graphic color selector") AttributeSelector color) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_MEDALS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterMedalGraphic>() {

                                                          @Override
                                                          public List<CharacterMedalGraphic> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int MEDAL_ID = 0;
                                                            final int ISSUED = 1;
                                                            final int PART = 2;
                                                            final int LAYER = 3;
                                                            final int GRAPHIC = 4;
                                                            final int COLOR = 5;
                                                            return CharacterMedalGraphic.accessQuery(acct,
                                                                                                     contid,
                                                                                                     maxresults,
                                                                                                     reverse, at,
                                                                                                     others[MEDAL_ID],
                                                                                                     others[ISSUED],
                                                                                                     others[PART],
                                                                                                     others[LAYER],
                                                                                                     others[GRAPHIC],
                                                                                                     others[COLOR]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                ESISyncEndpoint.CHAR_MEDALS,
                                                                acct);
                                                          }
                                                        }, request, medalID, issued, part, layer, graphic, color);
  }

  @Path("/notification")
  @GET
  @ApiOperation(
      value = "Get character notifications (not bodies)")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested notifications (excludes bodies)",
              response = CharacterNotification.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getNotifications(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("notificationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "notificationID",
          defaultValue = "{ any: true }",
          value = "Notification ID selector") AttributeSelector notificationID,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "type",
          defaultValue = "{ any: true }",
          value = "Notification type selector") AttributeSelector type,
      @QueryParam("senderID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "senderID",
          defaultValue = "{ any: true }",
          value = "Notification sender ID selector") AttributeSelector senderID,
      @QueryParam("senderType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "senderType",
          defaultValue = "{ any: true }",
          value = "Notification sender type selector") AttributeSelector senderType,
      @QueryParam("sentDate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "sentDate",
          defaultValue = "{ any: true }",
          value = "Notification send date selector") AttributeSelector sentDate,
      @QueryParam("msgRead") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "msgRead",
          defaultValue = "{ any: true }",
          value = "Notification read selector") AttributeSelector msgRead,
      @QueryParam("text") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "text",
          defaultValue = "{ any: true }",
          value = "Notification text selector") AttributeSelector text) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_NOTIFICATIONS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterNotification>() {

                                                          @Override
                                                          public List<CharacterNotification> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int NOTIFICATION_ID = 0;
                                                            final int TYPE = 1;
                                                            final int SENDER_ID = 2;
                                                            final int SENDER_TYPE = 3;
                                                            final int SENT_DATE = 4;
                                                            final int MSG_READ = 5;
                                                            final int TEXT = 6;
                                                            return CharacterNotification.accessQuery(acct, contid,
                                                                                                     maxresults,
                                                                                                     reverse, at,
                                                                                                     others[NOTIFICATION_ID],
                                                                                                     others[TYPE],
                                                                                                     others[SENDER_ID],
                                                                                                     others[SENDER_TYPE],
                                                                                                     others[SENT_DATE],
                                                                                                     others[MSG_READ],
                                                                                                     others[TEXT]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                ESISyncEndpoint.CHAR_NOTIFICATIONS,
                                                                acct);
                                                          }
                                                        }, request, notificationID, type, senderID, senderType,
                                                        sentDate,
                                                        msgRead, text);
  }

  @Path("/channel")
  @GET
  @ApiOperation(
      value = "Get character chat channels")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested chat channels",
              response = ChatChannel.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getChannels(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("channelID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "channelID",
          defaultValue = "{ any: true }",
          value = "Channel ID selector") AttributeSelector channelID,
      @QueryParam("ownerID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "ownerID",
          defaultValue = "{ any: true }",
          value = "Channel owner ID selector") AttributeSelector ownerID,
      @QueryParam("displayName") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "displayName",
          defaultValue = "{ any: true }",
          value = "Channel display name selector") AttributeSelector displayName,
      @QueryParam("comparisonKey") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "comparisonKey",
          defaultValue = "{ any: true }",
          value = "Channel comparison key selector") AttributeSelector comparisonKey,
      @QueryParam("hasPassword") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "hasPassword",
          defaultValue = "{ any: true }",
          value = "Channel has password selector") AttributeSelector hasPassword,
      @QueryParam("motd") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "motd",
          defaultValue = "{ any: true }",
          value = "Channel Message of the Day selector") AttributeSelector motd) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CHAT_CHANNELS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<ChatChannel>() {

                                                          @Override
                                                          public List<ChatChannel> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int CHANNEL_ID = 0;
                                                            final int OWNER_ID = 1;
                                                            final int DISPLAY_NAME = 2;
                                                            final int COMPARISON_KEY = 3;
                                                            final int HAS_PASSWORD = 4;
                                                            final int MOTD = 5;
                                                            return ChatChannel.accessQuery(acct, contid, maxresults,
                                                                                           reverse, at,
                                                                                           others[CHANNEL_ID],
                                                                                           others[OWNER_ID],
                                                                                           others[DISPLAY_NAME],
                                                                                           others[COMPARISON_KEY],
                                                                                           others[HAS_PASSWORD],
                                                                                           others[MOTD]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_CHANNELS,
                                                                                        acct);
                                                          }
                                                        }, request, channelID, ownerID, displayName, comparisonKey,
                                                        hasPassword, motd);
  }

  @Path("/channel_member")
  @GET
  @ApiOperation(
      value = "Get chat channel members")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested chat channel members",
              response = ChatChannelMember.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getChannelMembers(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("channelID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "channelID",
          defaultValue = "{ any: true }",
          value = "Channel ID selector") AttributeSelector channelID,
      @QueryParam("category") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "category",
          defaultValue = "{ any: true }",
          value = "Member category selector") AttributeSelector category,
      @QueryParam("accessorID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "accessorID",
          defaultValue = "{ any: true }",
          value = "Member accessor ID selector") AttributeSelector accessorID,
      @QueryParam("accessorType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "accessorType",
          defaultValue = "{ any: true }",
          value = "Member accessor type selector") AttributeSelector accessorType,
      @QueryParam("untilWhen") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "untilWhen",
          defaultValue = "{ any: true }",
          value = "Member restriction \"until when\" date selector") AttributeSelector untilWhen,
      @QueryParam("reason") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "reason",
          defaultValue = "{ any: true }",
          value = "Member restriction reason selector") AttributeSelector reason) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CHAT_CHANNELS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<ChatChannelMember>() {

                                                          @Override
                                                          public List<ChatChannelMember> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int CHANNEL_ID = 0;
                                                            final int CATEGORY = 1;
                                                            final int ACCESSOR_ID = 2;
                                                            final int ACCESSOR_TYPE = 3;
                                                            final int UNTIL_WHEN = 4;
                                                            final int REASON = 5;
                                                            return ChatChannelMember.accessQuery(acct, contid,
                                                                                                 maxresults,
                                                                                                 reverse, at,
                                                                                                 others[CHANNEL_ID],
                                                                                                 others[CATEGORY],
                                                                                                 others[ACCESSOR_ID],
                                                                                                 others[ACCESSOR_TYPE],
                                                                                                 others[UNTIL_WHEN],
                                                                                                 others[REASON]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_CHANNELS,
                                                                                        acct);
                                                          }
                                                        }, request, channelID, category, accessorID, accessorType,
                                                        untilWhen, reason);
  }

  @Path("/contact_notification")
  @GET
  @ApiOperation(
      value = "Get character contact notifications")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character contact notifications",
              response = CharacterContactNotification.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getContactNotifications(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("notificationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "notificationID",
          defaultValue = "{ any: true }",
          value = "Notification ID selector") AttributeSelector notificationID,
      @QueryParam("senderID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "senderID",
          defaultValue = "{ any: true }",
          value = "Contact notification sender ID selector") AttributeSelector senderID,
      @QueryParam("sentDate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "sentDate",
          defaultValue = "{ any: true }",
          value = "Contact notification send date selector") AttributeSelector sentDate,
      @QueryParam("standingLevel") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "standingLevel",
          defaultValue = "{ any: true }",
          value = "Contact notification standing level selector") AttributeSelector standingLevel,
      @QueryParam("messageData") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "messageData",
          defaultValue = "{ any: true }",
          value = "Contact notification message data selector") AttributeSelector messageData) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_CONTACT_NOTIFICATIONS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterContactNotification>() {

                                                          @Override
                                                          public List<CharacterContactNotification> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int NOTIFICATION_ID = 0;
                                                            final int SENDER_ID = 1;
                                                            final int SENT_DATE = 2;
                                                            final int STANDING_LEVEL = 3;
                                                            final int MESSAGE_DATA = 4;
                                                            return CharacterContactNotification.accessQuery(acct,
                                                                                                            contid,
                                                                                                            maxresults,
                                                                                                            reverse, at,
                                                                                                            others[NOTIFICATION_ID],
                                                                                                            others[SENDER_ID],
                                                                                                            others[SENT_DATE],
                                                                                                            others[STANDING_LEVEL],
                                                                                                            others[MESSAGE_DATA]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                ESISyncEndpoint.CHAR_NOTIFICATIONS,
                                                                acct);
                                                          }
                                                        }, request, notificationID, senderID, sentDate, standingLevel,
                                                        messageData);
  }

  @Path("/mailing_list")
  @GET
  @ApiOperation(
      value = "Get character mailing lists")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character mailing lists",
              response = MailingList.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getMailingLists(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("displayName") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "displayName",
          defaultValue = "{ any: true }",
          value = "Mailing list display name selector") AttributeSelector displayName,
      @QueryParam("listID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "listID",
          defaultValue = "{ any: true }",
          value = "Mailing list ID selector") AttributeSelector listID) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_MAILING_LISTS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<MailingList>() {

                                                          @Override
                                                          public List<MailingList> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int DISPLAY_NAME = 0;
                                                            final int LIST_ID = 1;
                                                            return MailingList.accessQuery(acct, contid, maxresults,
                                                                                           reverse, at,
                                                                                           others[DISPLAY_NAME],
                                                                                           others[LIST_ID]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_MAIL,
                                                                                        acct);
                                                          }
                                                        }, request, displayName, listID);
  }

  @Path("/mail_label")
  @GET
  @ApiOperation(
      value = "Get character mail labels")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character mail labels",
              response = MailLabel.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getMailLabels(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("labelID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "labelID",
          defaultValue = "{ any: true }",
          value = "Mail label ID selector") AttributeSelector labelID,
      @QueryParam("unreadCount") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "unreadCount",
          defaultValue = "{ any: true }",
          value = "Mail label unread count selector") AttributeSelector unreadCount,
      @QueryParam("name") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "name",
          defaultValue = "{ any: true }",
          value = "Mail label name selector") AttributeSelector name,
      @QueryParam("color") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "color",
          defaultValue = "{ any: true }",
          value = "Mail label color selector") AttributeSelector color) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_MAIL,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<MailLabel>() {

                                                          @Override
                                                          public List<MailLabel> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int LABEL_ID = 0;
                                                            final int UNREAD_COUNT = 1;
                                                            final int NAME = 1;
                                                            final int COLOR = 1;
                                                            return MailLabel.accessQuery(acct, contid, maxresults,
                                                                                         reverse, at,
                                                                                         others[LABEL_ID],
                                                                                         others[UNREAD_COUNT],
                                                                                         others[NAME],
                                                                                         others[COLOR]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_MAIL,
                                                                                        acct);
                                                          }
                                                        }, request, labelID, unreadCount, name, color);
  }

  @Path("/mail_message")
  @GET
  @ApiOperation(
      value = "Get character mail messages")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character mail messages",
              response = CharacterMailMessage.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getMailMessages(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("messageID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "messageID",
          defaultValue = "{ any: true }",
          value = "Message ID selector") AttributeSelector messageID,
      @QueryParam("senderID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "senderID",
          defaultValue = "{ any: true }",
          value = "Message sender ID selector") AttributeSelector senderID,
      @QueryParam("sentDate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "sentDate",
          defaultValue = "{ any: true }",
          value = "Message send date selector") AttributeSelector sentDate,
      @QueryParam("title") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "title",
          defaultValue = "{ any: true }",
          value = "Message title selector") AttributeSelector title,
      @QueryParam("msgRead") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "msgRead",
          defaultValue = "{ any: true }",
          value = "Message read selector") AttributeSelector msgRead,
      @QueryParam("labelID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "labelID",
          defaultValue = "{ any: true }",
          value = "Message label ID selector") AttributeSelector labelID,
      @QueryParam("recipientType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "recipientType",
          defaultValue = "{ any: true }",
          value = "Message recipient type selector") AttributeSelector recipientType,
      @QueryParam("recipientID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "recipientID",
          defaultValue = "{ any: true }",
          value = "Message recipient ID selector") AttributeSelector recipientID,
      @QueryParam("body") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "body",
          defaultValue = "{ any: true }",
          value = "Message body selector") AttributeSelector body) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_MAIL,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterMailMessage>() {

                                                          @Override
                                                          public List<CharacterMailMessage> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int MESSAGE_ID = 0;
                                                            final int SENDER_ID = 1;
                                                            final int SENT_DATE = 2;
                                                            final int TITLE = 3;
                                                            final int MSG_READ = 4;
                                                            final int LABEL_ID = 5;
                                                            final int RECIPIENT_TYPE = 6;
                                                            final int RECIPIENT_ID = 7;
                                                            final int BODY = 8;
                                                            return CharacterMailMessage.accessQuery(acct, contid,
                                                                                                    maxresults,
                                                                                                    reverse, at,
                                                                                                    others[MESSAGE_ID],
                                                                                                    others[SENDER_ID],
                                                                                                    others[SENT_DATE],
                                                                                                    others[TITLE],
                                                                                                    others[MSG_READ],
                                                                                                    others[LABEL_ID],
                                                                                                    others[RECIPIENT_TYPE],
                                                                                                    others[RECIPIENT_ID],
                                                                                                    others[BODY]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_MAIL,
                                                                                        acct);
                                                          }
                                                        }, request, messageID, senderID, sentDate, title, msgRead,
                                                        labelID, recipientType, recipientID, body);
  }

  @Path("/planetary_colony")
  @GET
  @ApiOperation(
      value = "Get planetary colonies")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested planetary colonies",
              response = PlanetaryColony.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getPlanetaryColonies(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("planetID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "planetID",
          defaultValue = "{ any: true }",
          value = "Planet ID selector") AttributeSelector planetID,
      @QueryParam("solarSystemID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "solarSystemID",
          defaultValue = "{ any: true }",
          value = "Solar system ID selector") AttributeSelector solarSystemID,
      @QueryParam("planetType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "planetType",
          defaultValue = "{ any: true }",
          value = "Planet type selector") AttributeSelector planetType,
      @QueryParam("ownerID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "ownerID",
          defaultValue = "{ any: true }",
          value = "Colony owner ID selector") AttributeSelector ownerID,
      @QueryParam("lastUpdate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "lastUpdate",
          defaultValue = "{ any: true }",
          value = "Colony last update selector") AttributeSelector lastUpdate,
      @QueryParam("upgradeLevel") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "upgradeLevel",
          defaultValue = "{ any: true }",
          value = "Colony upgrade level selector") AttributeSelector upgradeLevel,
      @QueryParam("numberOfPins") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "numberOfPins",
          defaultValue = "{ any: true }",
          value = "Colony number of pins selector") AttributeSelector numberOfPins) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_ASSETS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<PlanetaryColony>() {

                                                          @Override
                                                          public List<PlanetaryColony> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int PLANET_ID = 0;
                                                            final int SOLAR_SYSTEM_ID = 1;
                                                            final int PLANET_TYPE = 2;
                                                            final int OWNER_ID = 3;
                                                            final int LAST_UPDATE = 4;
                                                            final int UPGRADE_LEVEL = 5;
                                                            final int NUMBER_OF_PINS = 6;
                                                            return PlanetaryColony.accessQuery(acct, contid, maxresults,
                                                                                               reverse, at,
                                                                                               others[PLANET_ID],
                                                                                               others[SOLAR_SYSTEM_ID],
                                                                                               others[PLANET_TYPE],
                                                                                               others[OWNER_ID],
                                                                                               others[LAST_UPDATE],
                                                                                               others[UPGRADE_LEVEL],
                                                                                               others[NUMBER_OF_PINS]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_PLANETS,
                                                                                        acct);
                                                          }
                                                        }, request, planetID, solarSystemID, planetType, ownerID,
                                                        lastUpdate, upgradeLevel, numberOfPins);
  }

  @Path("/planetary_link")
  @GET
  @ApiOperation(
      value = "Get planetary links")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested planetary links",
              response = PlanetaryLink.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getPlanetaryLinks(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("planetID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "planetID",
          defaultValue = "{ any: true }",
          value = "Planet ID selector") AttributeSelector planetID,
      @QueryParam("sourcePinID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "sourcePinID",
          defaultValue = "{ any: true }",
          value = "Link source pin ID selector") AttributeSelector sourcePinID,
      @QueryParam("destinationPinID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "destinationPinID",
          defaultValue = "{ any: true }",
          value = "Link destination pin ID selector") AttributeSelector destinationPinID,
      @QueryParam("linkLevel") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "linkLevel",
          defaultValue = "{ any: true }",
          value = "Link level selector") AttributeSelector linkLevel) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_ASSETS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<PlanetaryLink>() {

                                                          @Override
                                                          public List<PlanetaryLink> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int PLANET_ID = 0;
                                                            final int SOURCE_PIN_ID = 1;
                                                            final int DESTINATION_PIN_ID = 2;
                                                            final int LINK_LEVEL = 3;
                                                            return PlanetaryLink.accessQuery(acct, contid, maxresults,
                                                                                             reverse, at,
                                                                                             others[PLANET_ID],
                                                                                             others[SOURCE_PIN_ID],
                                                                                             others[DESTINATION_PIN_ID],
                                                                                             others[LINK_LEVEL]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_PLANETS,
                                                                                        acct);
                                                          }
                                                        }, request, planetID, sourcePinID, destinationPinID, linkLevel);
  }

  @Path("/planetary_pin")
  @GET
  @ApiOperation(
      value = "Get planetary pins")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested planetary pins",
              response = PlanetaryPin.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getPlanetaryPins(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("planetID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "planetID",
          defaultValue = "{ any: true }",
          value = "Planet ID selector") AttributeSelector planetID,
      @QueryParam("pinID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "pinID",
          defaultValue = "{ any: true }",
          value = "Pin ID selector") AttributeSelector pinID,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Pin type ID selector") AttributeSelector typeID,
      @QueryParam("schematicID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "schematicID",
          defaultValue = "{ any: true }",
          value = "Pin schematic ID selector") AttributeSelector schematicID,
      @QueryParam("lastCycleStart") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "lastCycleStart",
          defaultValue = "{ any: true }",
          value = "Pin last cycle start selector") AttributeSelector lastCycleStart,
      @QueryParam("cycleTime") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "cycleTime",
          defaultValue = "{ any: true }",
          value = "Pin cycle time selector") AttributeSelector cycleTime,
      @QueryParam("quantityPerCycle") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "quantityPerCycle",
          defaultValue = "{ any: true }",
          value = "Pin quantity per cycle selector") AttributeSelector quantityPerCycle,
      @QueryParam("installTime") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "installTime",
          defaultValue = "{ any: true }",
          value = "Pin install time selector") AttributeSelector installTime,
      @QueryParam("expiryTime") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "expiryTime",
          defaultValue = "{ any: true }",
          value = "Pin expiry time selector") AttributeSelector expiryTime,
      @QueryParam("productTypeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "productTypeID",
          defaultValue = "{ any: true }",
          value = "Pin product type ID selector") AttributeSelector productTypeID,
      @QueryParam("longitude") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "longitude",
          defaultValue = "{ any: true }",
          value = "Pin longitude selector") AttributeSelector longitude,
      @QueryParam("latitude") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "latitude",
          defaultValue = "{ any: true }",
          value = "Pin latitude selector") AttributeSelector latitude,
      @QueryParam("headRadius") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "headRadius",
          defaultValue = "{ any: true }",
          value = "Pin head radius selector") AttributeSelector headRadius,
      @QueryParam("headID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "headID",
          defaultValue = "{ any: true }",
          value = "Pin head ID selector") AttributeSelector headID,
      @QueryParam("headLongitude") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "headLongitude",
          defaultValue = "{ any: true }",
          value = "Pin head longitude selector") AttributeSelector headLongitude,
      @QueryParam("headLatitude") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "headLatitude",
          defaultValue = "{ any: true }",
          value = "Pin head latitude selector") AttributeSelector headLatitude,
      @QueryParam("contentTypeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "contentTypeID",
          defaultValue = "{ any: true }",
          value = "Pin content type ID selector") AttributeSelector contentTypeID,
      @QueryParam("contentAmount") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "contentAmount",
          defaultValue = "{ any: true }",
          value = "Pin content amount selector") AttributeSelector contentAmount) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_ASSETS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<PlanetaryPin>() {

                                                          @Override
                                                          public List<PlanetaryPin> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int PLANET_ID = 0;
                                                            final int PIN_ID = 1;
                                                            final int TYPE_ID = 2;
                                                            final int SCHEMATIC_ID = 3;
                                                            final int LAST_CYCLE_START = 4;
                                                            final int CYCLE_TIME = 5;
                                                            final int QUANTITY_PER_CYCLE = 6;
                                                            final int INSTALL_TIME = 7;
                                                            final int EXPIRY_TIME = 8;
                                                            final int PRODUCT_TYPE_ID = 9;
                                                            final int LONGITUDE = 10;
                                                            final int LATITUDE = 11;
                                                            final int HEAD_RADIUS = 12;
                                                            final int HEAD_ID = 13;
                                                            final int HEAD_LONGITUDE = 14;
                                                            final int HEAD_LATITUDE = 15;
                                                            final int CONTENT_TYPE_ID = 16;
                                                            final int CONTENT_AMOUNT = 17;
                                                            return PlanetaryPin.accessQuery(acct, contid, maxresults,
                                                                                            reverse, at,
                                                                                            others[PLANET_ID],
                                                                                            others[PIN_ID],
                                                                                            others[TYPE_ID],
                                                                                            others[SCHEMATIC_ID],
                                                                                            others[LAST_CYCLE_START],
                                                                                            others[CYCLE_TIME],
                                                                                            others[QUANTITY_PER_CYCLE],
                                                                                            others[INSTALL_TIME],
                                                                                            others[EXPIRY_TIME],
                                                                                            others[PRODUCT_TYPE_ID],
                                                                                            others[LONGITUDE],
                                                                                            others[LATITUDE],
                                                                                            others[HEAD_RADIUS],
                                                                                            others[HEAD_ID],
                                                                                            others[HEAD_LONGITUDE],
                                                                                            others[HEAD_LATITUDE],
                                                                                            others[CONTENT_TYPE_ID],
                                                                                            others[CONTENT_AMOUNT]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_PLANETS,
                                                                                        acct);
                                                          }
                                                        }, request, planetID, pinID, typeID, schematicID,
                                                        lastCycleStart,
                                                        cycleTime, quantityPerCycle, installTime, expiryTime,
                                                        productTypeID, longitude, latitude, headRadius, headID,
                                                        headLongitude, headLatitude, contentTypeID, contentAmount);
  }

  @Path("/planetary_route")
  @GET
  @ApiOperation(
      value = "Get planetary routes")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested planetary routes",
              response = PlanetaryRoute.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getPlanetaryRoutes(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("planetID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "planetID",
          defaultValue = "{ any: true }",
          value = "Planet ID selector") AttributeSelector planetID,
      @QueryParam("routeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "routeID",
          defaultValue = "{ any: true }",
          value = "Route ID selector") AttributeSelector routeID,
      @QueryParam("sourcePinID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "sourcePinID",
          defaultValue = "{ any: true }",
          value = "Route source pin ID selector") AttributeSelector sourcePinID,
      @QueryParam("destinationPinID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "destinationPinID",
          defaultValue = "{ any: true }",
          value = "Route destination pin ID selector") AttributeSelector destinationPinID,
      @QueryParam("contentTypeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "contentTypeID",
          defaultValue = "{ any: true }",
          value = "Route content type ID selector") AttributeSelector contentTypeID,
      @QueryParam("quantity") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "quantity",
          defaultValue = "{ any: true }",
          value = "Route quantity selector") AttributeSelector quantity,
      @QueryParam("waypoint") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "waypoint",
          defaultValue = "{ any: true }",
          value = "Route waypoint selector") AttributeSelector waypoint) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_ASSETS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<PlanetaryRoute>() {

                                                          @Override
                                                          public List<PlanetaryRoute> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int PLANET_ID = 0;
                                                            final int ROUTE_ID = 1;
                                                            final int SOURCE_PIN_ID = 2;
                                                            final int DESTINATION_PIN_ID = 3;
                                                            final int CONTENT_TYPE_ID = 4;
                                                            final int QUANTITY = 5;
                                                            final int WAYPOINT = 6;
                                                            return PlanetaryRoute.accessQuery(acct, contid, maxresults,
                                                                                              reverse, at,
                                                                                              others[PLANET_ID],
                                                                                              others[ROUTE_ID],
                                                                                              others[SOURCE_PIN_ID],
                                                                                              others[DESTINATION_PIN_ID],
                                                                                              others[CONTENT_TYPE_ID],
                                                                                              others[QUANTITY],
                                                                                              others[WAYPOINT]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_PLANETS,
                                                                                        acct);
                                                          }
                                                        }, request, planetID, routeID, sourcePinID, destinationPinID,
                                                        contentTypeID, quantity, waypoint);
  }

  @Path("/research_agent")
  @GET
  @ApiOperation(
      value = "Get character research agents")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested research agents",
              response = ResearchAgent.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getResearchAgents(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("agentID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "agentID",
          defaultValue = "{ any: true }",
          value = "Research agent ID selector") AttributeSelector agentID,
      @QueryParam("pointsPerDay") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "pointsPerDay",
          defaultValue = "{ any: true }",
          value = "Agent points per day selector") AttributeSelector pointsPerDay,
      @QueryParam("remainderPoints") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "remainderPoints",
          defaultValue = "{ any: true }",
          value = "Agent remainder points selector") AttributeSelector remainderPoints,
      @QueryParam("researchStartDate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "researchStartDate",
          defaultValue = "{ any: true }",
          value = "Agent research start date selector") AttributeSelector researchStartDate,
      @QueryParam("skillTypeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "skillTypeID",
          defaultValue = "{ any: true }",
          value = "Agent skill type ID selector") AttributeSelector skillTypeID) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_RESEARCH,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<ResearchAgent>() {

                                                          @Override
                                                          public List<ResearchAgent> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int AGENT_ID = 0;
                                                            final int POINTS_PER_DAY = 1;
                                                            final int REMAINDER_POINTS = 2;
                                                            final int RESEARCH_START_DATE = 3;
                                                            final int SKILL_TYPE_ID = 4;
                                                            return ResearchAgent.accessQuery(acct, contid, maxresults,
                                                                                             reverse, at,
                                                                                             others[AGENT_ID],
                                                                                             others[POINTS_PER_DAY],
                                                                                             others[REMAINDER_POINTS],
                                                                                             others[RESEARCH_START_DATE],
                                                                                             others[SKILL_TYPE_ID]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_AGENTS,
                                                                                        acct);
                                                          }
                                                        }, request, agentID, pointsPerDay, remainderPoints,
                                                        researchStartDate, skillTypeID);
  }

  @Path("/skill_queue")
  @GET
  @ApiOperation(
      value = "Get character skill queue")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character skill queue entries",
              response = SkillInQueue.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getSkillsInQueue(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("endSP") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "endSP",
          defaultValue = "{ any: true }",
          value = "Skill ending skill points selector") AttributeSelector endSP,
      @QueryParam("endTime") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "endTime",
          defaultValue = "{ any: true }",
          value = "Skill training end time selector") AttributeSelector endTime,
      @QueryParam("level") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "level",
          defaultValue = "{ any: true }",
          value = "Skill training to level selector") AttributeSelector level,
      @QueryParam("queuePosition") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "queuePosition",
          defaultValue = "{ any: true }",
          value = "Queue position selector") AttributeSelector queuePosition,
      @QueryParam("startSP") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "startSP",
          defaultValue = "{ any: true }",
          value = "Starting skill points selector") AttributeSelector startSP,
      @QueryParam("startTime") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "startTime",
          defaultValue = "{ any: true }",
          value = "Training start time selector") AttributeSelector startTime,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Skill type ID selector") AttributeSelector typeID,
      @QueryParam("trainingStartSP") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "trainingStartSP",
          defaultValue = "{ any: true }",
          value = "Training start skill point selector") AttributeSelector trainingStartSP) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_SKILL_QUEUE,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<SkillInQueue>() {

                                                          @Override
                                                          public List<SkillInQueue> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int END_SP = 0;
                                                            final int END_TIME = 1;
                                                            final int LEVEL = 2;
                                                            final int QUEUE_POSITION = 3;
                                                            final int START_SP = 4;
                                                            final int START_TIME = 5;
                                                            final int TYPE_ID = 6;
                                                            final int TRAINING_START_SP = 7;
                                                            return SkillInQueue.accessQuery(acct, contid, maxresults,
                                                                                            reverse, at,
                                                                                            others[END_SP],
                                                                                            others[END_TIME],
                                                                                            others[LEVEL],
                                                                                            others[QUEUE_POSITION],
                                                                                            others[START_SP],
                                                                                            others[START_TIME],
                                                                                            others[TYPE_ID],
                                                                                            others[TRAINING_START_SP]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                ESISyncEndpoint.CHAR_SKILL_QUEUE,
                                                                acct);
                                                          }
                                                        }, request, endSP, endTime, level, queuePosition, startSP,
                                                        startTime, typeID, trainingStartSP);
  }

  @Path("/location")
  @GET
  @ApiOperation(
      value = "Get character location")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested location data",
              response = CharacterLocation.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getLocation(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("solarSystemID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "solarSystemID",
          defaultValue = "{ any: true }",
          value = "Solar system ID selector") AttributeSelector solarSystemID,
      @QueryParam("stationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "stationID",
          defaultValue = "{ any: true }",
          value = "Station ID selector") AttributeSelector stationID,
      @QueryParam("structureID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "structureID",
          defaultValue = "{ any: true }",
          value = "Structure ID selector") AttributeSelector structureID) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_LOCATIONS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterLocation>() {

                                                          @Override
                                                          public List<CharacterLocation> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int SOLAR_SYSTEM_ID = 0;
                                                            final int STATION_ID = 1;
                                                            final int STRUCTURE_ID = 2;
                                                            return CharacterLocation.accessQuery(acct, contid,
                                                                                                 maxresults,
                                                                                                 reverse, at,
                                                                                                 others[SOLAR_SYSTEM_ID],
                                                                                                 others[STATION_ID],
                                                                                                 others[STRUCTURE_ID]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_LOCATION,
                                                                                        acct);
                                                          }
                                                        }, request, solarSystemID, stationID, structureID);
  }

  @Path("/ship_type")
  @GET
  @ApiOperation(
      value = "Get character ship type")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested ship type data",
              response = CharacterShip.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getShipType(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("shipTypeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "shipTypeID",
          defaultValue = "{ any: true }",
          value = "Ship type ID selector") AttributeSelector shipTypeID,
      @QueryParam("shipItemID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "shipItemID",
          defaultValue = "{ any: true }",
          value = "Ship item ID selector") AttributeSelector shipItemID,
      @QueryParam("shipName") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "shipName",
          defaultValue = "{ any: true }",
          value = "Ship name selector") AttributeSelector shipName) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_LOCATIONS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterShip>() {

                                                          @Override
                                                          public List<CharacterShip> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int SHIP_TYPE_ID = 0;
                                                            final int SHIP_ITEM_ID = 1;
                                                            final int SHIP_NAME = 2;
                                                            return CharacterShip.accessQuery(acct, contid, maxresults,
                                                                                             reverse, at,
                                                                                             others[SHIP_TYPE_ID],
                                                                                             others[SHIP_ITEM_ID],
                                                                                             others[SHIP_NAME]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_SHIP_TYPE,
                                                                                        acct);
                                                          }
                                                        }, request, shipTypeID, shipItemID, shipName);
  }

  @Path("/online")
  @GET
  @ApiOperation(
      value = "Get character online data")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested online data",
              response = CharacterOnline.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
              response = ServiceError.class),
          @ApiResponse(
              code = 403,
              message = "access key not permitted to access the requested data, or not permitted to access the requested time in the model lifeline",
              response = ServiceError.class),
          @ApiResponse(
              code = 404,
              message = "access key not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getOnline(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "Model access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "Model access credential") String accessCred,
      @QueryParam("at") @DefaultValue(
          value = "{ values: [ \"9223372036854775806\" ] }") @ApiParam(
          name = "at",
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("online") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "online",
          defaultValue = "{ any: true }",
          value = "Online selector") AttributeSelector online,
      @QueryParam("lastLogin") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "lastLogin",
          defaultValue = "{ any: true }",
          value = "Last login selector") AttributeSelector lastLogin,
      @QueryParam("lastLogout") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "lastLogout",
          defaultValue = "{ any: true }",
          value = "Last logout selector") AttributeSelector lastLogout,
      @QueryParam("logins") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "logins",
          defaultValue = "{ any: true }",
          value = "Logins selector") AttributeSelector logins) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_ACCOUNT_STATUS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CharacterOnline>() {

                                                          @Override
                                                          public List<CharacterOnline> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int ONLINE = 0;
                                                            final int LAST_LOGIN = 1;
                                                            final int LAST_LOGOUT = 2;
                                                            final int LOGINS = 3;
                                                            return CharacterOnline.accessQuery(acct, contid, maxresults,
                                                                                               reverse, at,
                                                                                               others[ONLINE],
                                                                                               others[LAST_LOGIN],
                                                                                               others[LAST_LOGOUT],
                                                                                               others[LOGINS]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_ONLINE,
                                                                                        acct);
                                                          }
                                                        }, request, online, lastLogin, lastLogout, logins);
  }

}
