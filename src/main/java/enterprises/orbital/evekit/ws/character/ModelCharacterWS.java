package enterprises.orbital.evekit.ws.character;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.character.CalendarEventAttendee;
import enterprises.orbital.evekit.model.character.Capsuleer;
import enterprises.orbital.evekit.model.character.CharacterContactNotification;
import enterprises.orbital.evekit.model.character.CharacterMailMessage;
import enterprises.orbital.evekit.model.character.CharacterMailMessageBody;
import enterprises.orbital.evekit.model.character.CharacterMedal;
import enterprises.orbital.evekit.model.character.CharacterNotification;
import enterprises.orbital.evekit.model.character.CharacterNotificationBody;
import enterprises.orbital.evekit.model.character.CharacterRole;
import enterprises.orbital.evekit.model.character.CharacterSheet;
import enterprises.orbital.evekit.model.character.CharacterSheetBalance;
import enterprises.orbital.evekit.model.character.CharacterSheetClone;
import enterprises.orbital.evekit.model.character.CharacterSheetJump;
import enterprises.orbital.evekit.model.character.CharacterSkill;
import enterprises.orbital.evekit.model.character.CharacterSkillInTraining;
import enterprises.orbital.evekit.model.character.CharacterTitle;
import enterprises.orbital.evekit.model.character.ChatChannel;
import enterprises.orbital.evekit.model.character.ChatChannelMember;
import enterprises.orbital.evekit.model.character.Implant;
import enterprises.orbital.evekit.model.character.JumpClone;
import enterprises.orbital.evekit.model.character.JumpCloneImplant;
import enterprises.orbital.evekit.model.character.MailingList;
import enterprises.orbital.evekit.model.character.PlanetaryColony;
import enterprises.orbital.evekit.model.character.PlanetaryLink;
import enterprises.orbital.evekit.model.character.PlanetaryPin;
import enterprises.orbital.evekit.model.character.PlanetaryRoute;
import enterprises.orbital.evekit.model.character.ResearchAgent;
import enterprises.orbital.evekit.model.character.SkillInQueue;
import enterprises.orbital.evekit.model.character.UpcomingCalendarEvent;
import enterprises.orbital.evekit.model.common.AccountStatus;
import enterprises.orbital.evekit.ws.ServiceError;
import enterprises.orbital.evekit.ws.ServiceUtil;
import enterprises.orbital.evekit.ws.ServiceUtil.AccessConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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

  @Path("/account_status")
  @GET
  @ApiOperation(
      value = "Get account status(es)")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested account statuses",
              response = AccountStatus.class,
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
  public Response getAccountStatus(
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
                                   @QueryParam("paidUntil") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "paidUntil",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Account status paid until selector") AttributeSelector paidUntil,
                                   @QueryParam("createDate") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "createDate",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Account status create date selector") AttributeSelector createDate,
                                   @QueryParam("logonCount") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "logonCount",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Account status logon count selector") AttributeSelector logonCount,
                                   @QueryParam("logonMinutes") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "logonMinutes",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Account status logon minutes selector") AttributeSelector logonMinutes,
                                   @QueryParam("multiCharacterTraining") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "multiCharacterTraining",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Account status multi-character training selector") AttributeSelector multiCharacterTraining) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, paidUntil, createDate, logonCount, logonMinutes, multiCharacterTraining);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_ACCOUNT_STATUS);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<AccountStatus> result = AccountStatus.accessQuery(cfg.owner, contid, maxresults, at, paidUntil, createDate, logonCount, logonMinutes,
                                                             multiCharacterTraining);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

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
                                                    value = "Important flag selector") AttributeSelector important) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName, response, important);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_UPCOMING_CALENDAR_EVENTS);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<UpcomingCalendarEvent> result = UpcomingCalendarEvent.accessQuery(cfg.owner, contid, maxresults, at, duration, eventDate, eventID, eventText,
                                                                             eventTitle, ownerID, ownerName, response, important);
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
      List<CalendarEventAttendee> result = CalendarEventAttendee.accessQuery(cfg.owner, contid, maxresults, at, eventID, characterID, characterName, response);
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
      List<CharacterRole> result = CharacterRole.accessQuery(cfg.owner, contid, maxresults, at, roleCategory, roleID, roleName);
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
                                     @QueryParam("characterID") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "characterID",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Character ID selector") AttributeSelector characterID,
                                     @QueryParam("name") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "name",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Name selector") AttributeSelector name,
                                     @QueryParam("corporationID") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "corporationID",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Corporation ID selector") AttributeSelector corporationID,
                                     @QueryParam("corporationName") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "corporationName",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Corporation name selector") AttributeSelector corporationName,
                                     @QueryParam("race") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "race",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Race selector") AttributeSelector race,
                                     @QueryParam("doB") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "doB",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Date of birth selector") AttributeSelector doB,
                                     @QueryParam("bloodlineID") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "bloodlineID",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Bloodline ID selector") AttributeSelector bloodlineID,
                                     @QueryParam("bloodline") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "bloodline",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Bloodline selector") AttributeSelector bloodline,
                                     @QueryParam("ancestryID") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "ancestryID",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Ancestry ID selector") AttributeSelector ancestryID,
                                     @QueryParam("ancestry") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "ancestry",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Ancestry selector") AttributeSelector ancestry,
                                     @QueryParam("gender") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "gender",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Gender selector") AttributeSelector gender,
                                     @QueryParam("allianceName") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "allianceName",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Alliance name selector") AttributeSelector allianceName,
                                     @QueryParam("allianceID") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "allianceID",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Alliance ID selector") AttributeSelector allianceID,
                                     @QueryParam("factionName") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "factionName",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Faction name selector") AttributeSelector factionName,
                                     @QueryParam("factionID") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "factionID",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Faction ID selector") AttributeSelector factionID,
                                     @QueryParam("intelligence") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "intelligence",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Intelligence selector") AttributeSelector intelligence,
                                     @QueryParam("memory") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "memory",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Memory selector") AttributeSelector memory,
                                     @QueryParam("charisma") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "charisma",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Charisma selector") AttributeSelector charisma,
                                     @QueryParam("perception") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "perception",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Perception selector") AttributeSelector perception,
                                     @QueryParam("willpower") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "willpower",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Willpower selector") AttributeSelector willpower,
                                     @QueryParam("homeStationID") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "homeStationID",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Home station ID selector") AttributeSelector homeStationID,
                                     @QueryParam("lastRespecDate") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "lastRespecDate",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Last respec date selector") AttributeSelector lastRespecDate,
                                     @QueryParam("lastTimedRespec") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "lastTimedRespec",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Last timed respec selector") AttributeSelector lastTimedRespec,
                                     @QueryParam("freeRespecs") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "freeRespecs",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Free respecs selector") AttributeSelector freeRespecs,
                                     @QueryParam("freeSkillPoints") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "freeSkillPoints",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Free skill points selector") AttributeSelector freeSkillPoints,
                                     @QueryParam("remoteStationDate") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "remoteStationDate",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Remote station date selector") AttributeSelector remoteStationDate) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry,
                                          gender, allianceName, allianceID, factionName, factionID, intelligence, memory, charisma, perception, willpower,
                                          homeStationID, lastRespecDate, lastTimedRespec, freeRespecs, freeSkillPoints, remoteStationDate);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CHARACTER_SHEET);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CharacterSheet> result = CharacterSheet.accessQuery(cfg.owner, contid, maxresults, at, characterID, name, corporationID, corporationName, race, doB,
                                                               bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName, allianceID, factionName,
                                                               factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate,
                                                               lastTimedRespec, freeRespecs, freeSkillPoints, remoteStationDate);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/balance")
  @GET
  @ApiOperation(
      value = "Get character sheet balance")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of character sheet balances",
              response = CharacterSheetBalance.class,
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
  public Response getBalances(
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
                              @QueryParam("balance") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "balance",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Balance selector") AttributeSelector balance) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, balance);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CHARACTER_SHEET);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CharacterSheetBalance> result = CharacterSheetBalance.accessQuery(cfg.owner, contid, maxresults, at, balance);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                     @QueryParam("cloneJumpDate") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "cloneJumpDate",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Clone jump date selector") AttributeSelector cloneJumpDate) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, cloneJumpDate);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CHARACTER_SHEET);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CharacterSheetClone> result = CharacterSheetClone.accessQuery(cfg.owner, contid, maxresults, at, cloneJumpDate);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                @QueryParam("jumpActivation") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "jumpActivation",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Jump activation selector") AttributeSelector jumpActivation,
                                @QueryParam("jumpFatigue") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "jumpFatigue",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Jump fatigue selector") AttributeSelector jumpFatigue,
                                @QueryParam("jumpLastUpdate") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "jumpLastUpdate",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Jump last update time selector") AttributeSelector jumpLastUpdate) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, jumpActivation, jumpFatigue, jumpLastUpdate);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CHARACTER_SHEET);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CharacterSheetJump> result = CharacterSheetJump.accessQuery(cfg.owner, contid, maxresults, at, jumpActivation, jumpFatigue, jumpLastUpdate);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                            @QueryParam("typeID") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "typeID",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Skill type ID selector") AttributeSelector typeID,
                            @QueryParam("level") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "level",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Skill level selector") AttributeSelector level,
                            @QueryParam("skillpoints") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "skillpoints",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Skill points selector") AttributeSelector skillpoints,
                            @QueryParam("published") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "published",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Published skill selector") AttributeSelector published) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, typeID, level, skillpoints, published);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CHARACTER_SHEET);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CharacterSkill> result = CharacterSkill.accessQuery(cfg.owner, contid, maxresults, at, typeID, level, skillpoints, published);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
      List<CharacterTitle> result = CharacterTitle.accessQuery(cfg.owner, contid, maxresults, at, titleID, titleName);
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
                              @QueryParam("typeID") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "typeID",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Implant type ID selector") AttributeSelector typeID,
                              @QueryParam("typeName") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "typeName",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Implant type name selector") AttributeSelector typeName) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, typeID, typeName);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CHARACTER_SHEET);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<Implant> result = Implant.accessQuery(cfg.owner, contid, maxresults, at, typeID, typeName);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                @QueryParam("jumpCloneID") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "jumpCloneID",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Jump clone ID selector") AttributeSelector jumpCloneID,
                                @QueryParam("typeID") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "typeID",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Jump clone type ID selector") AttributeSelector typeID,
                                @QueryParam("locationID") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "locationID",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Jump clone location ID selector") AttributeSelector locationID,
                                @QueryParam("cloneName") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "cloneName",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Clone name selector selector") AttributeSelector cloneName) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, jumpCloneID, typeID, locationID, cloneName);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CHARACTER_SHEET);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<JumpClone> result = JumpClone.accessQuery(cfg.owner, contid, maxresults, at, jumpCloneID, typeID, locationID, cloneName);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                       @QueryParam("jumpCloneID") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "jumpCloneID",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Jump clone ID selector") AttributeSelector jumpCloneID,
                                       @QueryParam("typeID") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "typeID",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Implant type ID selector") AttributeSelector typeID,
                                       @QueryParam("typeName") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "typeName",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Implant type name selector") AttributeSelector typeName) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, jumpCloneID, typeID, typeName);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CHARACTER_SHEET);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<JumpCloneImplant> result = JumpCloneImplant.accessQuery(cfg.owner, contid, maxresults, at, jumpCloneID, typeID, typeName);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
      List<CharacterMedal> result = CharacterMedal.accessQuery(cfg.owner, contid, maxresults, at, description, medalID, title, corporationID, issued, issuerID,
                                                               reason, status);
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
      List<CharacterNotification> result = CharacterNotification.accessQuery(cfg.owner, contid, maxresults, at, notificationID, typeID, senderID, sentDate,
                                                                             msgRead);
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
      List<CharacterNotificationBody> result = CharacterNotificationBody.accessQuery(cfg.owner, contid, maxresults, at, notificationID, retrieved, text,
                                                                                     missing);
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
      List<ChatChannel> result = ChatChannel.accessQuery(cfg.owner, contid, maxresults, at, channelID, ownerID, ownerName, displayName, comparisonKey,
                                                         hasPassword, motd);
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
      List<ChatChannelMember> result = ChatChannelMember.accessQuery(cfg.owner, contid, maxresults, at, channelID, category, accessorID, accessorName,
                                                                     untilWhen, reason);
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
      List<CharacterContactNotification> result = CharacterContactNotification.accessQuery(cfg.owner, contid, maxresults, at, notificationID, senderID,
                                                                                           senderName, sentDate, messageData);
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
                                  @QueryParam("displayName") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "displayName",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Mailing list display name selector") AttributeSelector displayName,
                                  @QueryParam("listID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "listID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Mailing list ID selector") AttributeSelector listID) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, displayName, listID);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_MAILING_LISTS);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<MailingList> result = MailingList.accessQuery(cfg.owner, contid, maxresults, at, displayName, listID);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/mail_message")
  @GET
  @ApiOperation(
      value = "Get character mail messages (not bodies)")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character mail messages (not bodies)",
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
                                  @QueryParam("messageID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "messageID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Message ID selector") AttributeSelector messageID,
                                  @QueryParam("senderID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "senderID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Message sender ID selector") AttributeSelector senderID,
                                  @QueryParam("senderName") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "senderName",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Message sender name selector") AttributeSelector senderName,
                                  @QueryParam("toCharacterID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "toCharacterID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Message destination character ID selector") AttributeSelector toCharacterID,
                                  @QueryParam("sentDate") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "sentDate",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Message send date selector") AttributeSelector sentDate,
                                  @QueryParam("title") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "title",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Message title selector") AttributeSelector title,
                                  @QueryParam("corpOrAllianceID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "corpOrAllianceID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Message corporation or alliance ID selector") AttributeSelector corpOrAllianceID,
                                  @QueryParam("toListID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "toListID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Message destination list ID selector") AttributeSelector toListID,
                                  @QueryParam("msgRead") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "msgRead",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Message read selector") AttributeSelector msgRead,
                                  @QueryParam("senderTypeID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "senderTypeID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Message sender type ID selector") AttributeSelector senderTypeID) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, messageID, senderID, senderName, toCharacterID, sentDate, title, corpOrAllianceID, toListID, msgRead,
                                          senderTypeID);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_MAIL);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CharacterMailMessage> result = CharacterMailMessage.accessQuery(cfg.owner, contid, maxresults, at, messageID, senderID, senderName, toCharacterID,
                                                                           sentDate, title, corpOrAllianceID, toListID, msgRead, senderTypeID);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/mail_message_body")
  @GET
  @ApiOperation(
      value = "Get character mail message bodies")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character mail message bodies",
              response = CharacterMailMessageBody.class,
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
  public Response getMailMessageBodies(
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
                                       @QueryParam("messageID") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "messageID",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Mail message ID selector") AttributeSelector messageID,
                                       @QueryParam("retrieved") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "retrieved",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Mail message body retrieved selector") AttributeSelector retrieved,
                                       @QueryParam("body") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "body",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Mail message body selector") AttributeSelector body) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, messageID, retrieved, body);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_MAIL);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CharacterMailMessageBody> result = CharacterMailMessageBody.accessQuery(cfg.owner, contid, maxresults, at, messageID, retrieved, body);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
      List<PlanetaryColony> result = PlanetaryColony.accessQuery(cfg.owner, contid, maxresults, at, planetID, solarSystemID, solarSystemName, planetName,
                                                                 planetTypeID, planetTypeName, ownerID, ownerName, lastUpdate, upgradeLevel, numberOfPins);
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
      List<PlanetaryLink> result = PlanetaryLink.accessQuery(cfg.owner, contid, maxresults, at, planetID, sourcePinID, destinationPinID, linkLevel);
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
      List<PlanetaryPin> result = PlanetaryPin.accessQuery(cfg.owner, contid, maxresults, at, planetID, pinID, typeID, typeName, schematicID, lastLaunchTime,
                                                           cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID, contentTypeName,
                                                           contentQuantity, longitude, latitude);
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
      List<PlanetaryRoute> result = PlanetaryRoute.accessQuery(cfg.owner, contid, maxresults, at, planetID, routeID, sourcePinID, destinationPinID,
                                                               contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4, waypoint5);
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
                                    @QueryParam("agentID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "agentID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Research agent ID selector") AttributeSelector agentID,
                                    @QueryParam("currentPoints") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "currentPoints",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Agent current points selector") AttributeSelector currentPoints,
                                    @QueryParam("pointsPerDay") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "pointsPerDay",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Agent points per day selector") AttributeSelector pointsPerDay,
                                    @QueryParam("remainderPoints") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "remainderPoints",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Agent remainder points selector") AttributeSelector remainderPoints,
                                    @QueryParam("researchStartDate") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "researchStartDate",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Agent research start date selector") AttributeSelector researchStartDate,
                                    @QueryParam("skillTypeID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "skillTypeID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Agent skill type ID selector") AttributeSelector skillTypeID) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, agentID, currentPoints, pointsPerDay, remainderPoints, researchStartDate, skillTypeID);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_RESEARCH);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<ResearchAgent> result = ResearchAgent.accessQuery(cfg.owner, contid, maxresults, at, agentID, currentPoints, pointsPerDay, remainderPoints,
                                                             researchStartDate, skillTypeID);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/skill_in_training")
  @GET
  @ApiOperation(
      value = "Get character skill in training")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested skills in training",
              response = CharacterSkillInTraining.class,
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
  public Response getSkillsInTraining(
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
                                      @QueryParam("skillInTraining") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "skillInTraining",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Skill in training selector") AttributeSelector skillInTraining,
                                      @QueryParam("currentTrainingQueueTime") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "currentTrainingQueueTime",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Current taining queue time selector") AttributeSelector currentTrainingQueueTime,
                                      @QueryParam("trainingStartTime") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "trainingStartTime",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Training start time selector") AttributeSelector trainingStartTime,
                                      @QueryParam("trainingEndTime") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "trainingEndTime",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Training end time selector") AttributeSelector trainingEndTime,
                                      @QueryParam("trainingStartSP") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "trainingStartSP",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Training start skill points selector") AttributeSelector trainingStartSP,
                                      @QueryParam("trainingDestinationSP") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "trainingDestinationSP",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Training destination skill points selector") AttributeSelector trainingDestinationSP,
                                      @QueryParam("trainingToLevel") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "trainingToLevel",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Training to level selector") AttributeSelector trainingToLevel,
                                      @QueryParam("skillTypeID") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "skillTypeID",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Skill type ID selector") AttributeSelector skillTypeID) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, skillInTraining, currentTrainingQueueTime, trainingStartTime, trainingEndTime, trainingStartSP,
                                          trainingDestinationSP, trainingToLevel, skillTypeID);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_SKILL_IN_TRAINING);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CharacterSkillInTraining> result = CharacterSkillInTraining.accessQuery(cfg.owner, contid, maxresults, at, skillInTraining, currentTrainingQueueTime,
                                                                                   trainingStartTime, trainingEndTime, trainingStartSP, trainingDestinationSP,
                                                                                   trainingToLevel, skillTypeID);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
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
                                   @QueryParam("endSP") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "endSP",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Skill ending skill points selector") AttributeSelector endSP,
                                   @QueryParam("endTime") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "endTime",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Skill training end time selector") AttributeSelector endTime,
                                   @QueryParam("level") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "level",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Skill training to level selector") AttributeSelector level,
                                   @QueryParam("queuePosition") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "queuePosition",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Queue position selector") AttributeSelector queuePosition,
                                   @QueryParam("startSP") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "startSP",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Starting skill points selector") AttributeSelector startSP,
                                   @QueryParam("startTime") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "startTime",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Training start time selector") AttributeSelector startTime,
                                   @QueryParam("typeID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "typeID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Skill type ID selector") AttributeSelector typeID) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, endSP, endTime, level, queuePosition, startSP, startTime, typeID);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_SKILL_QUEUE);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<SkillInQueue> result = SkillInQueue.accessQuery(cfg.owner, contid, maxresults, at, endSP, endTime, level, queuePosition, startSP, startTime, typeID);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

}
