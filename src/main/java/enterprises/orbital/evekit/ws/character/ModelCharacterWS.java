package enterprises.orbital.evekit.ws.character;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import enterprises.orbital.evekit.model.ESISyncEndpoint;
import enterprises.orbital.evekit.model.character.*;
import enterprises.orbital.evekit.ws.AccountHandlerUtil;
import enterprises.orbital.evekit.ws.ServiceError;
import enterprises.orbital.evekit.ws.ServiceUtil;
import enterprises.orbital.evekit.ws.ServiceUtil.AccessConfig;
import io.swagger.annotations.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
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
                                                    required = false,
                                                    defaultValue = "{ values: [ \"9223372036854775806\" ] }",
                                                    value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
                                            @QueryParam("contid") @DefaultValue("-1") @ApiParam(
                                                name = "contid",
                                                required = false,
                                                defaultValue = "-1",
                                                value = "Continuation ID for paged results") long contid,
                                            @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
                                                name = "maxresults",
                                                required = false,
                                                defaultValue = "1000",
                                                value = "Maximum number of results to retrieve") int maxresults,
                                            @QueryParam("reverse") @DefaultValue("false") @ApiParam(
                                                name = "reverse",
                                                required = false,
                                                defaultValue = "false",
                                                value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
                                            @QueryParam("duration") @DefaultValue(
                                                value = "{ any: true }") @ApiParam(
                                                    name = "duration",
                                                    required = false,
                                                    defaultValue = "{ any: true }",
                                                    value = "Event duration selector") AttributeSelector duration,
                                            @QueryParam("eventDate") @DefaultValue(
                                                value = "{ any: true }") @ApiParam(
                                                    name = "eventDate",
                                                    required = false,
                                                    defaultValue = "{ any: true }",
                                                    value = "Event date selector (milliseconds UTC)") AttributeSelector eventDate,
                                            @QueryParam("eventID") @DefaultValue(
                                                value = "{ any: true }") @ApiParam(
                                                    name = "eventID",
                                                    required = false,
                                                    defaultValue = "{ any: true }",
                                                    value = "Event ID selector") AttributeSelector eventID,
                                            @QueryParam("eventText") @DefaultValue(
                                                value = "{ any: true }") @ApiParam(
                                                    name = "eventText",
                                                    required = false,
                                                    defaultValue = "{ any: true }",
                                                    value = "Event text selector") AttributeSelector eventText,
                                            @QueryParam("eventTitle") @DefaultValue(
                                                value = "{ any: true }") @ApiParam(
                                                    name = "eventTitle",
                                                    required = false,
                                                    defaultValue = "{ any: true }",
                                                    value = "Event title selector") AttributeSelector eventTitle,
                                            @QueryParam("ownerID") @DefaultValue(
                                                value = "{ any: true }") @ApiParam(
                                                    name = "ownerID",
                                                    required = false,
                                                    defaultValue = "{ any: true }",
                                                    value = "Owner ID selector") AttributeSelector ownerID,
                                            @QueryParam("ownerName") @DefaultValue(
                                                value = "{ any: true }") @ApiParam(
                                                    name = "ownerName",
                                                    required = false,
                                                    defaultValue = "{ any: true }",
                                                    value = "Owner name selector") AttributeSelector ownerName,
                                            @QueryParam("response") @DefaultValue(
                                                value = "{ any: true }") @ApiParam(
                                                    name = "response",
                                                    required = false,
                                                    defaultValue = "{ any: true }",
                                                    value = "Response text selector") AttributeSelector response,
                                            @QueryParam("important") @DefaultValue(
                                                value = "{ any: true }") @ApiParam(
                                                    name = "important",
                                                    required = false,
                                                    defaultValue = "{ any: true }",
                                                    value = "Important flag selector") AttributeSelector important,
                                            @QueryParam("ownerTypeID") @DefaultValue(
                                                value = "{ any: true }") @ApiParam(
                                                    name = "ownerTypeID",
                                                    required = false,
                                                    defaultValue = "{ any: true }",
                                                    value = "Event owner type ID selector") AttributeSelector ownerTypeID) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName, response, important, ownerTypeID);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_UPCOMING_CALENDAR_EVENTS);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<UpcomingCalendarEvent> result = UpcomingCalendarEvent.accessQuery(cfg.owner, contid, maxresults, reverse, at, duration, eventDate, eventID,
                                                                             eventText, eventTitle, ownerID, ownerName, response, important, ownerTypeID);
      for (CachedData next : result) {
        next.prepareTransient();
      }
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                                    required = false,
                                                    defaultValue = "{ values: [ \"9223372036854775806\" ] }",
                                                    value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
                                            @QueryParam("contid") @DefaultValue("-1") @ApiParam(
                                                name = "contid",
                                                required = false,
                                                defaultValue = "-1",
                                                value = "Continuation ID for paged results") long contid,
                                            @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
                                                name = "maxresults",
                                                required = false,
                                                defaultValue = "1000",
                                                value = "Maximum number of results to retrieve") int maxresults,
                                            @QueryParam("reverse") @DefaultValue("false") @ApiParam(
                                                name = "reverse",
                                                required = false,
                                                defaultValue = "false",
                                                value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
                                            @QueryParam("eventID") @DefaultValue(
                                                value = "{ any: true }") @ApiParam(
                                                    name = "eventID",
                                                    required = false,
                                                    defaultValue = "{ any: true }",
                                                    value = "Calendar event ID selector") AttributeSelector eventID,
                                            @QueryParam("characterID") @DefaultValue(
                                                value = "{ any: true }") @ApiParam(
                                                    name = "characterID",
                                                    required = false,
                                                    defaultValue = "{ any: true }",
                                                    value = "Attending character ID selector") AttributeSelector characterID,
                                            @QueryParam("characterName") @DefaultValue(
                                                value = "{ any: true }") @ApiParam(
                                                    name = "characterName",
                                                    required = false,
                                                    defaultValue = "{ any: true }",
                                                    value = "Attending character name selector") AttributeSelector characterName,
                                            @QueryParam("response") @DefaultValue(
                                                value = "{ any: true }") @ApiParam(
                                                    name = "response",
                                                    required = false,
                                                    defaultValue = "{ any: true }",
                                                    value = "Attendee response selector") AttributeSelector response) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, eventID, characterID, characterName, response);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CALENDAR_EVENT_ATTENDEES);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CalendarEventAttendee> result = CalendarEventAttendee.accessQuery(cfg.owner, contid, maxresults, reverse, at, eventID, characterID, characterName,
                                                                             response);
      for (CachedData next : result) {
        next.prepareTransient();
      }
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                   required = false,
                                   defaultValue = "{ values: [ \"9223372036854775806\" ] }",
                                   value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
                           @QueryParam("contid") @DefaultValue("-1") @ApiParam(
                               name = "contid",
                               required = false,
                               defaultValue = "-1",
                               value = "Continuation ID for paged results") long contid,
                           @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
                               name = "maxresults",
                               required = false,
                               defaultValue = "1000",
                               value = "Maximum number of results to retrieve") int maxresults,
                           @QueryParam("reverse") @DefaultValue("false") @ApiParam(
                               name = "reverse",
                               required = false,
                               defaultValue = "false",
                               value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
                           @QueryParam("roleCategory") @DefaultValue(
                               value = "{ any: true }") @ApiParam(
                                   name = "roleCategory",
                                   required = false,
                                   defaultValue = "{ any: true }",
                                   value = "Role category selector") AttributeSelector roleCategory,
                           @QueryParam("roleID") @DefaultValue(
                               value = "{ any: true }") @ApiParam(
                                   name = "roleID",
                                   required = false,
                                   defaultValue = "{ any: true }",
                                   value = "Role ID selector") AttributeSelector roleID,
                           @QueryParam("roleName") @DefaultValue(
                               value = "{ any: true }") @ApiParam(
                                   name = "roleName",
                                   required = false,
                                   defaultValue = "{ any: true }",
                                   value = "Role name selector") AttributeSelector roleName) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, roleCategory, roleID, roleName);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CHARACTER_SHEET);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CharacterRole> result = CharacterRole.accessQuery(cfg.owner, contid, maxresults, reverse, at, roleCategory, roleID, roleName);
      for (CachedData next : result) {
        next.prepareTransient();
      }
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                                            return CharacterSheetAttributes.accessQuery(acct, contid, maxresults,
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
                                                        willpower, bonusRemaps, lastRemapDate, accruedRemapCooldownDate);
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
                                                            return CharacterSheetClone.accessQuery(acct, contid, maxresults,
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
                                                        }, request, cloneJumpDate, homeStationID, homeStationType, lastStationChangeDate);
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
                                                            return CharacterSheetJump.accessQuery(acct, contid, maxresults,
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
                                                            return CharacterSheetSkillPoints.accessQuery(acct, contid, maxresults,
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
                                                        }, request, typeID, trainedSkillLevel, skillpoints, activeSkillLevel);
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
                                    required = false,
                                    defaultValue = "{ values: [ \"9223372036854775806\" ] }",
                                    value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
                            @QueryParam("contid") @DefaultValue("-1") @ApiParam(
                                name = "contid",
                                required = false,
                                defaultValue = "-1",
                                value = "Continuation ID for paged results") long contid,
                            @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
                                name = "maxresults",
                                required = false,
                                defaultValue = "1000",
                                value = "Maximum number of results to retrieve") int maxresults,
                            @QueryParam("reverse") @DefaultValue("false") @ApiParam(
                                name = "reverse",
                                required = false,
                                defaultValue = "false",
                                value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
                            @QueryParam("titleID") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "titleID",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Character title ID selector") AttributeSelector titleID,
                            @QueryParam("titleName") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "titleName",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Character title name selector") AttributeSelector titleName) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, titleID, titleName);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CHARACTER_SHEET);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CharacterTitle> result = CharacterTitle.accessQuery(cfg.owner, contid, maxresults, reverse, at, titleID, titleName);
      for (CachedData next : result) {
        next.prepareTransient();
      }
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                                            return JumpCloneImplant.accessQuery(acct, contid, maxresults,
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
                                    required = false,
                                    defaultValue = "{ values: [ \"9223372036854775806\" ] }",
                                    value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
                            @QueryParam("contid") @DefaultValue("-1") @ApiParam(
                                name = "contid",
                                required = false,
                                defaultValue = "-1",
                                value = "Continuation ID for paged results") long contid,
                            @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
                                name = "maxresults",
                                required = false,
                                defaultValue = "1000",
                                value = "Maximum number of results to retrieve") int maxresults,
                            @QueryParam("reverse") @DefaultValue("false") @ApiParam(
                                name = "reverse",
                                required = false,
                                defaultValue = "false",
                                value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
                            @QueryParam("description") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "description",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Medal description selector") AttributeSelector description,
                            @QueryParam("medalID") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "medalID",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Medal ID selector") AttributeSelector medalID,
                            @QueryParam("title") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "title",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Medal title selector") AttributeSelector title,
                            @QueryParam("corporationID") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "corporationID",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Awarding corporation ID selector") AttributeSelector corporationID,
                            @QueryParam("issued") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "issued",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Issue date selector") AttributeSelector issued,
                            @QueryParam("issuerID") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "issuerID",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Issuer ID selector") AttributeSelector issuerID,
                            @QueryParam("reason") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "reason",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Medal award reason selector") AttributeSelector reason,
                            @QueryParam("status") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "status",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Medal status selector") AttributeSelector status) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, description, medalID, title, corporationID, issued, issuerID, reason, status);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_MEDALS);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CharacterMedal> result = CharacterMedal.accessQuery(cfg.owner, contid, maxresults, reverse, at, description, medalID, title, corporationID, issued,
                                                               issuerID, reason, status);
      for (CachedData next : result) {
        next.prepareTransient();
      }
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                           required = false,
                                           defaultValue = "{ values: [ \"9223372036854775806\" ] }",
                                           value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
                                   @QueryParam("contid") @DefaultValue("-1") @ApiParam(
                                       name = "contid",
                                       required = false,
                                       defaultValue = "-1",
                                       value = "Continuation ID for paged results") long contid,
                                   @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
                                       name = "maxresults",
                                       required = false,
                                       defaultValue = "1000",
                                       value = "Maximum number of results to retrieve") int maxresults,
                                   @QueryParam("reverse") @DefaultValue("false") @ApiParam(
                                       name = "reverse",
                                       required = false,
                                       defaultValue = "false",
                                       value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
                                   @QueryParam("notificationID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "notificationID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Notification ID selector") AttributeSelector notificationID,
                                   @QueryParam("typeID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "typeID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Notification type ID selector") AttributeSelector typeID,
                                   @QueryParam("senderID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "senderID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Notification sender ID selector") AttributeSelector senderID,
                                   @QueryParam("sentDate") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "sentDate",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Notification send date selector") AttributeSelector sentDate,
                                   @QueryParam("msgRead") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "msgRead",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Notification read selector") AttributeSelector msgRead) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, notificationID, typeID, senderID, sentDate, msgRead);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_NOTIFICATIONS);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CharacterNotification> result = CharacterNotification.accessQuery(cfg.owner, contid, maxresults, reverse, at, notificationID, typeID, senderID,
                                                                             sentDate, msgRead);
      for (CachedData next : result) {
        next.prepareTransient();
      }
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/notification_body")
  @GET
  @ApiOperation(
      value = "Get character notification bodies")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested notification bodies",
              response = CharacterNotificationBody.class,
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
  public Response getNotificationBodies(
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
                                                required = false,
                                                defaultValue = "{ values: [ \"9223372036854775806\" ] }",
                                                value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
                                        @QueryParam("contid") @DefaultValue("-1") @ApiParam(
                                            name = "contid",
                                            required = false,
                                            defaultValue = "-1",
                                            value = "Continuation ID for paged results") long contid,
                                        @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
                                            name = "maxresults",
                                            required = false,
                                            defaultValue = "1000",
                                            value = "Maximum number of results to retrieve") int maxresults,
                                        @QueryParam("reverse") @DefaultValue("false") @ApiParam(
                                            name = "reverse",
                                            required = false,
                                            defaultValue = "false",
                                            value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
                                        @QueryParam("notificationID") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "notificationID",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Notification ID selector") AttributeSelector notificationID,
                                        @QueryParam("retrieved") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "retrieved",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Body retrieved selector") AttributeSelector retrieved,
                                        @QueryParam("text") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "text",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Notification text selector") AttributeSelector text,
                                        @QueryParam("missing") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "missing",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Notification missing selector") AttributeSelector missing) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, notificationID, retrieved, text, missing);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_NOTIFICATIONS);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CharacterNotificationBody> result = CharacterNotificationBody.accessQuery(cfg.owner, contid, maxresults, reverse, at, notificationID, retrieved,
                                                                                     text, missing);
      for (CachedData next : result) {
        next.prepareTransient();
      }
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                      required = false,
                                      defaultValue = "{ values: [ \"9223372036854775806\" ] }",
                                      value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
                              @QueryParam("contid") @DefaultValue("-1") @ApiParam(
                                  name = "contid",
                                  required = false,
                                  defaultValue = "-1",
                                  value = "Continuation ID for paged results") long contid,
                              @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
                                  name = "maxresults",
                                  required = false,
                                  defaultValue = "1000",
                                  value = "Maximum number of results to retrieve") int maxresults,
                              @QueryParam("reverse") @DefaultValue("false") @ApiParam(
                                  name = "reverse",
                                  required = false,
                                  defaultValue = "false",
                                  value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
                              @QueryParam("channelID") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "channelID",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Channel ID selector") AttributeSelector channelID,
                              @QueryParam("ownerID") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "ownerID",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Channel owner ID selector") AttributeSelector ownerID,
                              @QueryParam("ownerName") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "ownerName",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Channel owner name selector") AttributeSelector ownerName,
                              @QueryParam("displayName") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "displayName",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Channel display name selector") AttributeSelector displayName,
                              @QueryParam("comparisonKey") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "comparisonKey",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Channel comparison key selector") AttributeSelector comparisonKey,
                              @QueryParam("hasPassword") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "hasPassword",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Channel has password selector") AttributeSelector hasPassword,
                              @QueryParam("motd") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "motd",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Channel Message of the Day selector") AttributeSelector motd) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, channelID, ownerID, ownerName, displayName, comparisonKey, hasPassword, motd);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CHAT_CHANNELS);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<ChatChannel> result = ChatChannel.accessQuery(cfg.owner, contid, maxresults, reverse, at, channelID, ownerID, ownerName, displayName, comparisonKey,
                                                         hasPassword, motd);
      for (CachedData next : result) {
        next.prepareTransient();
      }
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                            required = false,
                                            defaultValue = "{ values: [ \"9223372036854775806\" ] }",
                                            value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
                                    @QueryParam("contid") @DefaultValue("-1") @ApiParam(
                                        name = "contid",
                                        required = false,
                                        defaultValue = "-1",
                                        value = "Continuation ID for paged results") long contid,
                                    @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
                                        name = "maxresults",
                                        required = false,
                                        defaultValue = "1000",
                                        value = "Maximum number of results to retrieve") int maxresults,
                                    @QueryParam("reverse") @DefaultValue("false") @ApiParam(
                                        name = "reverse",
                                        required = false,
                                        defaultValue = "false",
                                        value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
                                    @QueryParam("channelID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "channelID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Channel ID selector") AttributeSelector channelID,
                                    @QueryParam("category") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "category",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member category selector") AttributeSelector category,
                                    @QueryParam("accessorID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "accessorID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member accessor ID selector") AttributeSelector accessorID,
                                    @QueryParam("accessorName") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "accessorName",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member accessor name selector") AttributeSelector accessorName,
                                    @QueryParam("untilWhen") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "untilWhen",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member restriction \"until when\" date selector") AttributeSelector untilWhen,
                                    @QueryParam("reason") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "reason",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member restriction reason selector") AttributeSelector reason) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, channelID, category, accessorID, accessorName, untilWhen, reason);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CHAT_CHANNELS);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<ChatChannelMember> result = ChatChannelMember.accessQuery(cfg.owner, contid, maxresults, reverse, at, channelID, category, accessorID, accessorName,
                                                                     untilWhen, reason);
      for (CachedData next : result) {
        next.prepareTransient();
      }
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                                  required = false,
                                                  defaultValue = "{ values: [ \"9223372036854775806\" ] }",
                                                  value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
                                          @QueryParam("contid") @DefaultValue("-1") @ApiParam(
                                              name = "contid",
                                              required = false,
                                              defaultValue = "-1",
                                              value = "Continuation ID for paged results") long contid,
                                          @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
                                              name = "maxresults",
                                              required = false,
                                              defaultValue = "1000",
                                              value = "Maximum number of results to retrieve") int maxresults,
                                          @QueryParam("reverse") @DefaultValue("false") @ApiParam(
                                              name = "reverse",
                                              required = false,
                                              defaultValue = "false",
                                              value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
                                          @QueryParam("notificationID") @DefaultValue(
                                              value = "{ any: true }") @ApiParam(
                                                  name = "notificationID",
                                                  required = false,
                                                  defaultValue = "{ any: true }",
                                                  value = "Notification ID selector") AttributeSelector notificationID,
                                          @QueryParam("senderID") @DefaultValue(
                                              value = "{ any: true }") @ApiParam(
                                                  name = "senderID",
                                                  required = false,
                                                  defaultValue = "{ any: true }",
                                                  value = "Contact notification sender ID selector") AttributeSelector senderID,
                                          @QueryParam("senderName") @DefaultValue(
                                              value = "{ any: true }") @ApiParam(
                                                  name = "senderName",
                                                  required = false,
                                                  defaultValue = "{ any: true }",
                                                  value = "Contact notification sender name selector") AttributeSelector senderName,
                                          @QueryParam("sentDate") @DefaultValue(
                                              value = "{ any: true }") @ApiParam(
                                                  name = "sentDate",
                                                  required = false,
                                                  defaultValue = "{ any: true }",
                                                  value = "Contact notification send date selector") AttributeSelector sentDate,
                                          @QueryParam("messageData") @DefaultValue(
                                              value = "{ any: true }") @ApiParam(
                                                  name = "messageData",
                                                  required = false,
                                                  defaultValue = "{ any: true }",
                                                  value = "Contact notification message data selector") AttributeSelector messageData) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, notificationID, senderID, senderName, sentDate, messageData);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CONTACT_NOTIFICATIONS);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CharacterContactNotification> result = CharacterContactNotification.accessQuery(cfg.owner, contid, maxresults, reverse, at, notificationID, senderID,
                                                                                           senderName, sentDate, messageData);
      for (CachedData next : result) {
        next.prepareTransient();
      }
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                                            return CharacterMailMessage.accessQuery(acct, contid, maxresults,
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
                                                        }, request, messageID, senderID, sentDate, title, msgRead, labelID, recipientType, recipientID, body);
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
                                               required = false,
                                               defaultValue = "{ values: [ \"9223372036854775806\" ] }",
                                               value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
                                       @QueryParam("contid") @DefaultValue("-1") @ApiParam(
                                           name = "contid",
                                           required = false,
                                           defaultValue = "-1",
                                           value = "Continuation ID for paged results") long contid,
                                       @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
                                           name = "maxresults",
                                           required = false,
                                           defaultValue = "1000",
                                           value = "Maximum number of results to retrieve") int maxresults,
                                       @QueryParam("reverse") @DefaultValue("false") @ApiParam(
                                           name = "reverse",
                                           required = false,
                                           defaultValue = "false",
                                           value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
                                       @QueryParam("planetID") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "planetID",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Planet ID selector") AttributeSelector planetID,
                                       @QueryParam("solarSystemID") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "solarSystemID",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Solar system ID selector") AttributeSelector solarSystemID,
                                       @QueryParam("solarSystemName") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "solarSystemName",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Solar system name selector") AttributeSelector solarSystemName,
                                       @QueryParam("planetName") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "planetName",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Planet name selector") AttributeSelector planetName,
                                       @QueryParam("planetTypeID") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "planetTypeID",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Planet type ID selector") AttributeSelector planetTypeID,
                                       @QueryParam("planetTypeName") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "planetTypeName",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Planet type name selector") AttributeSelector planetTypeName,
                                       @QueryParam("ownerID") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "ownerID",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Colony owner ID selector") AttributeSelector ownerID,
                                       @QueryParam("ownerName") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "ownerName",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Colony owner name selector") AttributeSelector ownerName,
                                       @QueryParam("lastUpdate") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "lastUpdate",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Colony last update selector") AttributeSelector lastUpdate,
                                       @QueryParam("upgradeLevel") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "upgradeLevel",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Colony upgrade level selector") AttributeSelector upgradeLevel,
                                       @QueryParam("numberOfPins") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "numberOfPins",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Colony number of pins selector") AttributeSelector numberOfPins) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, planetID, solarSystemID, solarSystemName, planetName, planetTypeID, planetTypeName, ownerID, ownerName,
                                          lastUpdate, upgradeLevel, numberOfPins);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_ASSETS);
    if (cfg.fail) return cfg.response;
    cfg.presetExpiry = Capsuleer.getCapsuleer(cfg.owner).getPlanetaryColoniesExpiry();
    try {
      // Retrieve
      List<PlanetaryColony> result = PlanetaryColony.accessQuery(cfg.owner, contid, maxresults, reverse, at, planetID, solarSystemID, solarSystemName,
                                                                 planetName, planetTypeID, planetTypeName, ownerID, ownerName, lastUpdate, upgradeLevel,
                                                                 numberOfPins);
      for (CachedData next : result) {
        next.prepareTransient();
      }
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                            required = false,
                                            defaultValue = "{ values: [ \"9223372036854775806\" ] }",
                                            value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
                                    @QueryParam("contid") @DefaultValue("-1") @ApiParam(
                                        name = "contid",
                                        required = false,
                                        defaultValue = "-1",
                                        value = "Continuation ID for paged results") long contid,
                                    @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
                                        name = "maxresults",
                                        required = false,
                                        defaultValue = "1000",
                                        value = "Maximum number of results to retrieve") int maxresults,
                                    @QueryParam("reverse") @DefaultValue("false") @ApiParam(
                                        name = "reverse",
                                        required = false,
                                        defaultValue = "false",
                                        value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
                                    @QueryParam("planetID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "planetID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Planet ID selector") AttributeSelector planetID,
                                    @QueryParam("sourcePinID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "sourcePinID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Link source pin ID selector") AttributeSelector sourcePinID,
                                    @QueryParam("destinationPinID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "destinationPinID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Link destination pin ID selector") AttributeSelector destinationPinID,
                                    @QueryParam("linkLevel") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "linkLevel",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Link level selector") AttributeSelector linkLevel) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, planetID, sourcePinID, destinationPinID, linkLevel);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_ASSETS);
    if (cfg.fail) return cfg.response;
    cfg.presetExpiry = Capsuleer.getCapsuleer(cfg.owner).getPlanetaryColoniesExpiry();
    try {
      // Retrieve
      List<PlanetaryLink> result = PlanetaryLink.accessQuery(cfg.owner, contid, maxresults, reverse, at, planetID, sourcePinID, destinationPinID, linkLevel);
      for (CachedData next : result) {
        next.prepareTransient();
      }
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                           required = false,
                                           defaultValue = "{ values: [ \"9223372036854775806\" ] }",
                                           value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
                                   @QueryParam("contid") @DefaultValue("-1") @ApiParam(
                                       name = "contid",
                                       required = false,
                                       defaultValue = "-1",
                                       value = "Continuation ID for paged results") long contid,
                                   @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
                                       name = "maxresults",
                                       required = false,
                                       defaultValue = "1000",
                                       value = "Maximum number of results to retrieve") int maxresults,
                                   @QueryParam("reverse") @DefaultValue("false") @ApiParam(
                                       name = "reverse",
                                       required = false,
                                       defaultValue = "false",
                                       value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
                                   @QueryParam("planetID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "planetID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Planet ID selector") AttributeSelector planetID,
                                   @QueryParam("pinID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "pinID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Pin ID selector") AttributeSelector pinID,
                                   @QueryParam("typeID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "typeID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Pin type ID selector") AttributeSelector typeID,
                                   @QueryParam("typeName") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "typeName",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Pin type name selector") AttributeSelector typeName,
                                   @QueryParam("schematicID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "schematicID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Pin schematic ID selector") AttributeSelector schematicID,
                                   @QueryParam("lastLaunchTime") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "lastLaunchTime",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Pin last launch time selector") AttributeSelector lastLaunchTime,
                                   @QueryParam("cycleTime") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "cycleTime",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Pin cycle time selector") AttributeSelector cycleTime,
                                   @QueryParam("quantityPerCycle") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "quantityPerCycle",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Pin quantity per cycle selector") AttributeSelector quantityPerCycle,
                                   @QueryParam("installTime") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "installTime",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Pin install time selector") AttributeSelector installTime,
                                   @QueryParam("expiryTime") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "expiryTime",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Pin expiry time selector") AttributeSelector expiryTime,
                                   @QueryParam("contentTypeID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "contentTypeID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Pin content type ID selector") AttributeSelector contentTypeID,
                                   @QueryParam("contentTypeName") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "contentTypeName",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Pin content type name selector") AttributeSelector contentTypeName,
                                   @QueryParam("contentQuantity") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "contentQuantity",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Pin content quantity selector") AttributeSelector contentQuantity,
                                   @QueryParam("longitude") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "longitude",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Pin longitude selector") AttributeSelector longitude,
                                   @QueryParam("latitude") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "latitude",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Pin latitude selector") AttributeSelector latitude) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime,
                                          expiryTime, contentTypeID, contentTypeName, contentQuantity, longitude, latitude);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_ASSETS);
    if (cfg.fail) return cfg.response;
    cfg.presetExpiry = Capsuleer.getCapsuleer(cfg.owner).getPlanetaryColoniesExpiry();
    try {
      // Retrieve
      List<PlanetaryPin> result = PlanetaryPin.accessQuery(cfg.owner, contid, maxresults, reverse, at, planetID, pinID, typeID, typeName, schematicID,
                                                           lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID, contentTypeName,
                                                           contentQuantity, longitude, latitude);
      for (CachedData next : result) {
        next.prepareTransient();
      }
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                             required = false,
                                             defaultValue = "{ values: [ \"9223372036854775806\" ] }",
                                             value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
                                     @QueryParam("contid") @DefaultValue("-1") @ApiParam(
                                         name = "contid",
                                         required = false,
                                         defaultValue = "-1",
                                         value = "Continuation ID for paged results") long contid,
                                     @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
                                         name = "maxresults",
                                         required = false,
                                         defaultValue = "1000",
                                         value = "Maximum number of results to retrieve") int maxresults,
                                     @QueryParam("reverse") @DefaultValue("false") @ApiParam(
                                         name = "reverse",
                                         required = false,
                                         defaultValue = "false",
                                         value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
                                     @QueryParam("planetID") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "planetID",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Planet ID selector") AttributeSelector planetID,
                                     @QueryParam("routeID") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "routeID",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Route ID selector") AttributeSelector routeID,
                                     @QueryParam("sourcePinID") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "sourcePinID",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Route source pin ID selector") AttributeSelector sourcePinID,
                                     @QueryParam("destinationPinID") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "destinationPinID",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Route destination pin ID selector") AttributeSelector destinationPinID,
                                     @QueryParam("contentTypeID") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "contentTypeID",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Route content type ID selector") AttributeSelector contentTypeID,
                                     @QueryParam("contentTypeName") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "contentTypeName",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Route content type name selector") AttributeSelector contentTypeName,
                                     @QueryParam("quantity") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "quantity",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Route quantity selector") AttributeSelector quantity,
                                     @QueryParam("waypoint1") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "waypoint1",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Route waypoint 1 selector") AttributeSelector waypoint1,
                                     @QueryParam("waypoint2") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "waypoint2",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Route waypoint 2 selector") AttributeSelector waypoint2,
                                     @QueryParam("waypoint3") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "waypoint3",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Route waypoint 3 selector") AttributeSelector waypoint3,
                                     @QueryParam("waypoint4") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "waypoint4",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Route waypoint 4 selector") AttributeSelector waypoint4,
                                     @QueryParam("waypoint5") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "waypoint5",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Route waypoint 5 selector") AttributeSelector waypoint5) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, planetID, routeID, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2,
                                          waypoint3, waypoint4, waypoint5);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_ASSETS);
    if (cfg.fail) return cfg.response;
    cfg.presetExpiry = Capsuleer.getCapsuleer(cfg.owner).getPlanetaryColoniesExpiry();
    try {
      // Retrieve
      List<PlanetaryRoute> result = PlanetaryRoute.accessQuery(cfg.owner, contid, maxresults, reverse, at, planetID, routeID, sourcePinID, destinationPinID,
                                                               contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4, waypoint5);
      for (CachedData next : result) {
        next.prepareTransient();
      }
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_SKILL_QUEUE,
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
                                                            return CharacterLocation.accessQuery(acct, contid, maxresults,
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
