package enterprises.orbital.evekit.ws.corporation;

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
import enterprises.orbital.evekit.model.corporation.ContainerLog;
import enterprises.orbital.evekit.model.corporation.Corporation;
import enterprises.orbital.evekit.model.corporation.CorporationMedal;
import enterprises.orbital.evekit.model.corporation.CorporationMemberMedal;
import enterprises.orbital.evekit.model.corporation.CorporationSheet;
import enterprises.orbital.evekit.model.corporation.CorporationTitle;
import enterprises.orbital.evekit.model.corporation.CustomsOffice;
import enterprises.orbital.evekit.model.corporation.Division;
import enterprises.orbital.evekit.model.corporation.Facility;
import enterprises.orbital.evekit.model.corporation.Fuel;
import enterprises.orbital.evekit.model.corporation.MemberSecurity;
import enterprises.orbital.evekit.model.corporation.MemberSecurityLog;
import enterprises.orbital.evekit.model.corporation.MemberTracking;
import enterprises.orbital.evekit.model.corporation.Outpost;
import enterprises.orbital.evekit.model.corporation.OutpostServiceDetail;
import enterprises.orbital.evekit.model.corporation.Role;
import enterprises.orbital.evekit.model.corporation.SecurityRole;
import enterprises.orbital.evekit.model.corporation.SecurityTitle;
import enterprises.orbital.evekit.model.corporation.Shareholder;
import enterprises.orbital.evekit.model.corporation.Starbase;
import enterprises.orbital.evekit.model.corporation.StarbaseDetail;
import enterprises.orbital.evekit.ws.ServiceError;
import enterprises.orbital.evekit.ws.ServiceUtil;
import enterprises.orbital.evekit.ws.ServiceUtil.AccessConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/ws/v1/corp")
@Consumes({
    "application/json"
})
@Produces({
    "application/json"
})
@Api(
    tags = {
        "Corporation"
    },
    produces = "application/json",
    consumes = "application/json")
public class ModelCorporationWS {

  @Path("/container_log")
  @GET
  @ApiOperation(
      value = "Get container log records")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested container log records",
              response = ContainerLog.class,
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
  public Response getContainerLogs(
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
                                   @QueryParam("logTime") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "logTime",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Corporation container log time selector") AttributeSelector logTime,
                                   @QueryParam("action") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "action",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Corporation container log action selector") AttributeSelector action,
                                   @QueryParam("actorID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "actorID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Corporation container log actor ID selector") AttributeSelector actorID,
                                   @QueryParam("actorName") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "actorName",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Corporation container log actor name selector") AttributeSelector actorName,
                                   @QueryParam("flag") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "flag",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Corporation container log flag selector") AttributeSelector flag,
                                   @QueryParam("itemID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "itemID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Corporation container log item ID selector") AttributeSelector itemID,
                                   @QueryParam("itemTypeID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "itemTypeID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Corporation container log item type ID selector") AttributeSelector itemTypeID,
                                   @QueryParam("locationID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "locationID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Corporation container log location ID selector") AttributeSelector locationID,
                                   @QueryParam("newConfiguration") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "newConfiguration",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Corporation container log new configuration selector") AttributeSelector newConfiguration,
                                   @QueryParam("oldConfiguration") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "oldConfiguration",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Corporation container log old configuration selector") AttributeSelector oldConfiguration,
                                   @QueryParam("passwordType") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "passwordType",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Corporation container log password type selector") AttributeSelector passwordType,
                                   @QueryParam("quantity") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "quantity",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Corporation container log quantity selector") AttributeSelector quantity,
                                   @QueryParam("typeID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "typeID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Corporation container log type ID selector") AttributeSelector typeID) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, logTime, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration,
                                          passwordType, quantity, typeID);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CONTAINER_LOG);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<ContainerLog> result = ContainerLog.accessQuery(cfg.owner, contid, maxresults, reverse, at, logTime, action, actorID, actorName, flag, itemID,
                                                           itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID);
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
      value = "Get corporation medals")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation medals",
              response = CorporationMedal.class,
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
  public Response getCorporationMedals(
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
                                       @QueryParam("medalID") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "medalID",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Corporation medal ID selector") AttributeSelector medalID,
                                       @QueryParam("description") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "description",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Corporation medal description selector") AttributeSelector description,
                                       @QueryParam("title") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "title",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Corporation medal title selector") AttributeSelector title,
                                       @QueryParam("created") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "created",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Corporation medal created date selector") AttributeSelector created,
                                       @QueryParam("creatorID") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "creatorID",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Corporation medal creator ID selector") AttributeSelector creatorID) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, medalID, description, title, created, creatorID);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CORPORATION_MEDALS);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CorporationMedal> result = CorporationMedal.accessQuery(cfg.owner, contid, maxresults, reverse, at, medalID, description, title, created, creatorID);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/member_medal")
  @GET
  @ApiOperation(
      value = "Get medals awarded to corporation members")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested medals awarded to corporation members",
              response = CorporationMemberMedal.class,
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
  public Response getMemberMedals(
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
                                  @QueryParam("medalID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "medalID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Member medal ID selector") AttributeSelector medalID,
                                  @QueryParam("characterID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "characterID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Member medal character ID selector") AttributeSelector characterID,
                                  @QueryParam("issued") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "issued",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Member medal issued date selector") AttributeSelector issued,
                                  @QueryParam("issuerID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "issuerID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Member medal issuer ID selector") AttributeSelector issuerID,
                                  @QueryParam("reason") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "reason",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Member medal reason selector") AttributeSelector reason,
                                  @QueryParam("status") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "status",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Member medal status selector") AttributeSelector status) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, medalID, characterID, issued, issuerID, reason, status);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_MEMBER_MEDALS);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CorporationMemberMedal> result = CorporationMemberMedal.accessQuery(cfg.owner, contid, maxresults, reverse, at, medalID, characterID, issued,
                                                                               issuerID, reason, status);
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
      value = "Get corporation sheet")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation sheets",
              response = CorporationSheet.class,
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
  public Response getCorporationSheet(
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
                                      @QueryParam("allianceID") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "allianceID",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation alliance ID selector") AttributeSelector allianceID,
                                      @QueryParam("allianceName") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "allianceName",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation alliance name selector") AttributeSelector allianceName,
                                      @QueryParam("ceoID") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "ceoID",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation CEO ID selector") AttributeSelector ceoID,
                                      @QueryParam("ceoName") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "ceoName",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation CEO name selector") AttributeSelector ceoName,
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
                                      @QueryParam("description") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "description",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation description selector") AttributeSelector description,
                                      @QueryParam("logoColor1") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "logoColor1",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation first logo color selector") AttributeSelector logoColor1,
                                      @QueryParam("logoColor2") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "logoColor2",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation second logo color selector") AttributeSelector logoColor2,
                                      @QueryParam("logoColor3") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "logoColor3",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation third logo color selector") AttributeSelector logoColor3,
                                      @QueryParam("logoGraphicID") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "logoGraphicID",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation logo graphic ID selector") AttributeSelector logoGraphicID,
                                      @QueryParam("logoShape1") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "logoShape1",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation first logo shape selector") AttributeSelector logoShape1,
                                      @QueryParam("logoShape2") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "logoShape2",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation second logo shape selector") AttributeSelector logoShape2,
                                      @QueryParam("logoShape3") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "logoShape3",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation third logo shape selector") AttributeSelector logoShape3,
                                      @QueryParam("memberCount") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "memberCount",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation member count selector") AttributeSelector memberCount,
                                      @QueryParam("memberLimit") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "memberLimit",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation member limit selector") AttributeSelector memberLimit,
                                      @QueryParam("shares") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "shares",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation shares selector") AttributeSelector shares,
                                      @QueryParam("stationID") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "stationID",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation station ID selector") AttributeSelector stationID,
                                      @QueryParam("stationName") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "stationName",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation station name selector") AttributeSelector stationName,
                                      @QueryParam("taxRate") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "taxRate",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation tax rate selector") AttributeSelector taxRate,
                                      @QueryParam("ticker") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "ticker",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation ticker selector") AttributeSelector ticker,
                                      @QueryParam("url") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "url",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Corporation URL selector") AttributeSelector url) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2,
                                          logoColor3, logoGraphicID, logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID,
                                          stationName, taxRate, ticker, url);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CORPORATION_SHEET);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CorporationSheet> result = CorporationSheet.accessQuery(cfg.owner, contid, maxresults, reverse, at, allianceID, allianceName, ceoID, ceoName,
                                                                   corporationID, corporationName, description, logoColor1, logoColor2, logoColor3,
                                                                   logoGraphicID, logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares,
                                                                   stationID, stationName, taxRate, ticker, url);
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
      value = "Get corporation titles")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation titles",
              response = CorporationTitle.class,
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
  public Response getCorporationTitles(
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
                                               value = "Corporation title ID selector") AttributeSelector titleID,
                                       @QueryParam("titleName") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "titleName",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Corporation title name selector") AttributeSelector titleName,
                                       @QueryParam("grantableRoles") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "grantableRoles",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Corporation title grantable roles selector") AttributeSelector grantableRoles,
                                       @QueryParam("grantableRolesAtBase") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "grantableRolesAtBase",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Corporation title grantable roles at base selector") AttributeSelector grantableRolesAtBase,
                                       @QueryParam("grantableRolesAtHQ") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "grantableRolesAtHQ",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Corporation title grantable roles at HQ selector") AttributeSelector grantableRolesAtHQ,
                                       @QueryParam("grantableRolesAtOther") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "grantableRolesAtOther",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Corporation title grantable roles at other selector") AttributeSelector grantableRolesAtOther,
                                       @QueryParam("roles") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "roles",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Corporation title roles selector") AttributeSelector roles,
                                       @QueryParam("rolesAtBase") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "rolesAtBase",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Corporation title roles at base selector") AttributeSelector rolesAtBase,
                                       @QueryParam("rolesAtHQ") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "rolesAtHQ",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Corporation title roles at HQ selector") AttributeSelector rolesAtHQ,
                                       @QueryParam("rolesAtOther") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "rolesAtOther",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Corporation title roles at other selector") AttributeSelector rolesAtOther) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, titleID, titleName, grantableRoles, grantableRolesAtBase, grantableRolesAtHQ, grantableRolesAtOther, roles,
                                          rolesAtBase, rolesAtHQ, rolesAtOther);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CORPORATION_TITLES);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<CorporationTitle> result = CorporationTitle.accessQuery(cfg.owner, contid, maxresults, reverse, at, titleID, titleName, grantableRoles,
                                                                   grantableRolesAtBase, grantableRolesAtHQ, grantableRolesAtOther, roles, rolesAtBase,
                                                                   rolesAtHQ, rolesAtOther);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/customs_office")
  @GET
  @ApiOperation(
      value = "Get corporation customs offices")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested customs offices",
              response = CustomsOffice.class,
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
  public Response getCustomsOffices(
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
                                    @QueryParam("itemID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "itemID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Customs office item ID selector") AttributeSelector itemID,
                                    @QueryParam("solarSystemID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "solarSystemID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Customs office solar system ID selector") AttributeSelector solarSystemID,
                                    @QueryParam("solarSystemName") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "solarSystemName",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Customs office solar system name selector") AttributeSelector solarSystemName,
                                    @QueryParam("reinforceHour") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "reinforceHour",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Customs office reinforce hour selector") AttributeSelector reinforceHour,
                                    @QueryParam("allowAlliance") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "allowAlliance",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Customs office allow alliance selector") AttributeSelector allowAlliance,
                                    @QueryParam("allowStandings") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "allowStandings",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Customs office allow standings selector") AttributeSelector allowStandings,
                                    @QueryParam("standingLevel") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "standingLevel",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Customs office standing level selector") AttributeSelector standingLevel,
                                    @QueryParam("taxRateAlliance") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "taxRateAlliance",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Customs office tax rate alliance selector") AttributeSelector taxRateAlliance,
                                    @QueryParam("taxRateCorp") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "taxRateCorp",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Customs office tax rate corporation selector") AttributeSelector taxRateCorp,
                                    @QueryParam("taxRateStandingHigh") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "taxRateStandingHigh",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Customs office tax rate standing high selector") AttributeSelector taxRateStandingHigh,
                                    @QueryParam("taxRateStandingGood") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "taxRateStandingGood",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Customs office tax rate standing good selector") AttributeSelector taxRateStandingGood,
                                    @QueryParam("taxRateStandingNeutral") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "taxRateStandingNeutral",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Customs office tax rate standing neutral selector") AttributeSelector taxRateStandingNeutral,
                                    @QueryParam("taxRateStandingBad") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "taxRateStandingBad",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Customs office tax rate standing bad selector") AttributeSelector taxRateStandingBad,
                                    @QueryParam("taxRateStandingHorrible") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "taxRateStandingHorrible",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Customs office tax rate standing horrible selector") AttributeSelector taxRateStandingHorrible) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, itemID, solarSystemID, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel,
                                          taxRateAlliance, taxRateCorp, taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
                                          taxRateStandingHorrible);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_ASSETS);
    if (cfg.fail) return cfg.response;
    cfg.presetExpiry = Corporation.getCorporation(cfg.owner).getCustomsOfficeExpiry();
    try {
      // Retrieve
      List<CustomsOffice> result = CustomsOffice.accessQuery(cfg.owner, contid, maxresults, reverse, at, itemID, solarSystemID, solarSystemName, reinforceHour,
                                                             allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp, taxRateStandingHigh,
                                                             taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/division")
  @GET
  @ApiOperation(
      value = "Get corporation divisions")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation divisions",
              response = Division.class,
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
  public Response getDivisions(
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
                               @QueryParam("wallet") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "wallet",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Division wallet indicator selector") AttributeSelector wallet,
                               @QueryParam("accountKey") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "accountKey",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Division account key selector") AttributeSelector accountKey,
                               @QueryParam("description") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "description",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Division description selector") AttributeSelector description) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, wallet, accountKey, description);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CORPORATION_SHEET);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<Division> result = Division.accessQuery(cfg.owner, contid, maxresults, reverse, at, wallet, accountKey, description);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/facility")
  @GET
  @ApiOperation(
      value = "Get corporation facilities")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation facilities",
              response = Facility.class,
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
  public Response getFacilities(
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
                                @QueryParam("facilityID") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "facilityID",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Facility ID selector") AttributeSelector facilityID,
                                @QueryParam("typeID") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "typeID",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Facility type ID selector") AttributeSelector typeID,
                                @QueryParam("typeName") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "typeName",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Facility type name selector") AttributeSelector typeName,
                                @QueryParam("solarSystemID") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "solarSystemID",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Facility solar system ID selector") AttributeSelector solarSystemID,
                                @QueryParam("solarSystemName") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "solarSystemName",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Facility solar system name selector") AttributeSelector solarSystemName,
                                @QueryParam("regionID") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "regionID",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Facility region ID selector") AttributeSelector regionID,
                                @QueryParam("regionName") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "regionName",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Facility region name selector") AttributeSelector regionName,
                                @QueryParam("starbaseModifier") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "starbaseModifier",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Facility starbase modifier selector") AttributeSelector starbaseModifier,
                                @QueryParam("tax") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "tax",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Facility tax selector") AttributeSelector tax) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, facilityID, typeID, typeName, solarSystemID, solarSystemName, regionID, regionName, starbaseModifier, tax);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_INDUSTRY_JOBS);
    if (cfg.fail) return cfg.response;
    cfg.presetExpiry = Corporation.getCorporation(cfg.owner).getFacilitiesExpiry();
    try {
      // Retrieve
      List<Facility> result = Facility.accessQuery(cfg.owner, contid, maxresults, reverse, at, facilityID, typeID, typeName, solarSystemID, solarSystemName,
                                                   regionID, regionName, starbaseModifier, tax);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/fuel")
  @GET
  @ApiOperation(
      value = "Get corporation starbase fuel levels")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation starbase fuel levels",
              response = Fuel.class,
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
  public Response getFuel(
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
                          @QueryParam("itemID") @DefaultValue(
                              value = "{ any: true }") @ApiParam(
                                  name = "itemID",
                                  required = false,
                                  defaultValue = "{ any: true }",
                                  value = "Fuel item ID selector") AttributeSelector itemID,
                          @QueryParam("typeID") @DefaultValue(
                              value = "{ any: true }") @ApiParam(
                                  name = "typeID",
                                  required = false,
                                  defaultValue = "{ any: true }",
                                  value = "Fuel type ID selector") AttributeSelector typeID,
                          @QueryParam("quantity") @DefaultValue(
                              value = "{ any: true }") @ApiParam(
                                  name = "quantity",
                                  required = false,
                                  defaultValue = "{ any: true }",
                                  value = "Fuel quantity selector") AttributeSelector quantity) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, itemID, typeID, quantity);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_STARBASE_LIST);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<Fuel> result = Fuel.accessQuery(cfg.owner, contid, maxresults, reverse, at, itemID, typeID, quantity);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/member_security")
  @GET
  @ApiOperation(
      value = "Get corporation member security settings")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation member security settings",
              response = MemberSecurity.class,
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
  public Response getMemberSecurity(
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
                                    @QueryParam("characterID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "characterID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member security character ID selector") AttributeSelector characterID,
                                    @QueryParam("name") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "name",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member security character name selector") AttributeSelector name,
                                    @QueryParam("grantableRoles") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "grantableRoles",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member security grantable roles selector") AttributeSelector grantableRoles,
                                    @QueryParam("grantableRolesAtBase") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "grantableRolesAtBase",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member security grantable roles at base selector") AttributeSelector grantableRolesAtBase,
                                    @QueryParam("grantableRolesAtHQ") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "grantableRolesAtHQ",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member security grantable roles at HQ selector") AttributeSelector grantableRolesAtHQ,
                                    @QueryParam("grantableRolesAtOther") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "grantableRolesAtOther",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member security grantable roles at other selector") AttributeSelector grantableRolesAtOther,
                                    @QueryParam("roles") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "roles",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member security roles selector") AttributeSelector roles,
                                    @QueryParam("rolesAtBase") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "rolesAtBase",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member security roles at base selector") AttributeSelector rolesAtBase,
                                    @QueryParam("rolesAtHQ") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "rolesAtHQ",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member security roles at HQ selector") AttributeSelector rolesAtHQ,
                                    @QueryParam("rolesAtOther") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "rolesAtOther",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member security roles at other selector") AttributeSelector rolesAtOther,
                                    @QueryParam("titles") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "titles",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member security titles selector") AttributeSelector titles) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, characterID, name, grantableRoles, grantableRolesAtBase, grantableRolesAtHQ, grantableRolesAtOther, roles,
                                          rolesAtBase, rolesAtHQ, rolesAtOther, titles);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_MEMBER_SECURITY);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<MemberSecurity> result = MemberSecurity.accessQuery(cfg.owner, contid, maxresults, reverse, at, characterID, name, grantableRoles,
                                                               grantableRolesAtBase, grantableRolesAtHQ, grantableRolesAtOther, roles, rolesAtBase, rolesAtHQ,
                                                               rolesAtOther, titles);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/member_security_log")
  @GET
  @ApiOperation(
      value = "Get corporation member security log entries")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation member security log entries",
              response = MemberSecurityLog.class,
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
  public Response getMemberSecurityLog(
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
                                       @QueryParam("changeTime") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "changeTime",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Member security log change time selector") AttributeSelector changeTime,
                                       @QueryParam("changedCharacterID") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "changedCharacterID",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Member security log changed character ID selector") AttributeSelector changedCharacterID,
                                       @QueryParam("changedCharacterName") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "changedCharacterName",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Member security log changed character name selector") AttributeSelector changedCharacterName,
                                       @QueryParam("issuerID") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "issuerID",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Member security log issuer ID selector") AttributeSelector issuerID,
                                       @QueryParam("issuerName") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "issuerName",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Member security log issuer name selector") AttributeSelector issuerName,
                                       @QueryParam("roleLocationType") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "roleLocationType",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Member security log role location type selector") AttributeSelector roleLocationType,
                                       @QueryParam("oldRoles") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "oldRoles",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Member security log old roles selector") AttributeSelector oldRoles,
                                       @QueryParam("newRoles") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "newRoles",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Member security log new roles selector") AttributeSelector newRoles) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, changeTime, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType, oldRoles, newRoles);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_MEMBER_SECURITY_LOG);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<MemberSecurityLog> result = MemberSecurityLog.accessQuery(cfg.owner, contid, maxresults, reverse, at, changeTime, changedCharacterID,
                                                                     changedCharacterName, issuerID, issuerName, roleLocationType, oldRoles, newRoles);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/member_tracking")
  @GET
  @ApiOperation(
      value = "Get member tracking information")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of member tracking information",
              response = MemberTracking.class,
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
  public Response getMemberTracking(
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
                                    @QueryParam("characterID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "characterID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member character ID selector") AttributeSelector characterID,
                                    @QueryParam("base") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "base",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member base selector") AttributeSelector base,
                                    @QueryParam("baseID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "baseID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member base ID selector") AttributeSelector baseID,
                                    @QueryParam("grantableRoles") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "grantableRoles",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member grantable roles selector") AttributeSelector grantableRoles,
                                    @QueryParam("location") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "location",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member location selector") AttributeSelector location,
                                    @QueryParam("locationID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "locationID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member location ID selector") AttributeSelector locationID,
                                    @QueryParam("logoffDateTime") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "logoffDateTime",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member logoff time selector") AttributeSelector logoffDateTime,
                                    @QueryParam("logonDateTime") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "logonDateTime",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member logon time selector") AttributeSelector logonDateTime,
                                    @QueryParam("name") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "name",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member name selector") AttributeSelector name,
                                    @QueryParam("roles") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "roles",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member roles selector") AttributeSelector roles,
                                    @QueryParam("shipType") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "shipType",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member ship type selector") AttributeSelector shipType,
                                    @QueryParam("shipTypeID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "shipTypeID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member ship type ID selector") AttributeSelector shipTypeID,
                                    @QueryParam("startDateTime") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "startDateTime",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member start time selector") AttributeSelector startDateTime,
                                    @QueryParam("title") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "title",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Member title selector") AttributeSelector title) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, characterID, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles,
                                          shipType, shipTypeID, startDateTime, title);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_MEMBER_TRACKING);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<MemberTracking> result = MemberTracking.accessQuery(cfg.owner, contid, maxresults, reverse, at, characterID, base, baseID, grantableRoles, location,
                                                               locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID, startDateTime,
                                                               title);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/outpost")
  @GET
  @ApiOperation(
      value = "Get corporation outposts")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation outposts",
              response = Outpost.class,
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
  public Response getOutposts(
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
                              @QueryParam("stationID") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "stationID",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Outpost station ID selector") AttributeSelector stationID,
                              @QueryParam("ownerID") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "ownerID",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Outpost owner ID selector") AttributeSelector ownerID,
                              @QueryParam("stationName") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "stationName",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Outpost station name selector") AttributeSelector stationName,
                              @QueryParam("solarSystemID") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "solarSystemID",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Outpost solar system ID selector") AttributeSelector solarSystemID,
                              @QueryParam("dockingCostPerShipVolume") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "dockingCostPerShipVolume",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Outpost docking cost per ship volume selector") AttributeSelector dockingCostPerShipVolume,
                              @QueryParam("officeRentalCost") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "officeRentalCost",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Outpost office rental cost selector") AttributeSelector officeRentalCost,
                              @QueryParam("stationTypeID") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "stationTypeID",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Outpost station type ID selector") AttributeSelector stationTypeID,
                              @QueryParam("reprocessingEfficiency") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "reprocessingEfficiency",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Outpost reprocessing efficiency selector") AttributeSelector reprocessingEfficiency,
                              @QueryParam("reprocessingStationTake") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "reprocessingStationTake",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Outpost reprocessing station take selector") AttributeSelector reprocessingStationTake,
                              @QueryParam("standingOwnerID") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "standingOwnerID",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Outpost standing owner ID selector") AttributeSelector standingOwnerID,
                              @QueryParam("x") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "x",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Outpost x coordinate selector") AttributeSelector x,
                              @QueryParam("y") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "y",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Outpost y coordinate selector") AttributeSelector y,
                              @QueryParam("z") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "z",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Outpost z coordinate selector") AttributeSelector z) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, stationID, ownerID, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID,
                                          reprocessingEfficiency, reprocessingStationTake, standingOwnerID, x, y, z);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_OUTPOST_LIST);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<Outpost> result = Outpost.accessQuery(cfg.owner, contid, maxresults, reverse, at, stationID, ownerID, stationName, solarSystemID,
                                                 dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency, reprocessingStationTake,
                                                 standingOwnerID, x, y, z);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/outpost_service_detail")
  @GET
  @ApiOperation(
      value = "Get corporation outpost service detail")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation outpost service details",
              response = OutpostServiceDetail.class,
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
  public Response getOutpostServiceDetails(
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
                                           @QueryParam("stationID") @DefaultValue(
                                               value = "{ any: true }") @ApiParam(
                                                   name = "stationID",
                                                   required = false,
                                                   defaultValue = "{ any: true }",
                                                   value = "Outpost service detail station ID selector") AttributeSelector stationID,
                                           @QueryParam("serviceName") @DefaultValue(
                                               value = "{ any: true }") @ApiParam(
                                                   name = "serviceName",
                                                   required = false,
                                                   defaultValue = "{ any: true }",
                                                   value = "Outpost service detail service name selector") AttributeSelector serviceName,
                                           @QueryParam("ownerID") @DefaultValue(
                                               value = "{ any: true }") @ApiParam(
                                                   name = "ownerID",
                                                   required = false,
                                                   defaultValue = "{ any: true }",
                                                   value = "Outpost service owner ID selector") AttributeSelector ownerID,
                                           @QueryParam("minStanding") @DefaultValue(
                                               value = "{ any: true }") @ApiParam(
                                                   name = "minStanding",
                                                   required = false,
                                                   defaultValue = "{ any: true }",
                                                   value = "Outpost service minimum standing selector") AttributeSelector minStanding,
                                           @QueryParam("surchargePerBadStanding") @DefaultValue(
                                               value = "{ any: true }") @ApiParam(
                                                   name = "surchargePerBadStanding",
                                                   required = false,
                                                   defaultValue = "{ any: true }",
                                                   value = "Outpost service surcharge per bad standing selector") AttributeSelector surchargePerBadStanding,
                                           @QueryParam("discountPerGoodStanding") @DefaultValue(
                                               value = "{ any: true }") @ApiParam(
                                                   name = "discountPerGoodStanding",
                                                   required = false,
                                                   defaultValue = "{ any: true }",
                                                   value = "Outpost service discount per good standing selector") AttributeSelector discountPerGoodStanding) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, stationID, serviceName, ownerID, minStanding, surchargePerBadStanding, discountPerGoodStanding);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_OUTPOST_LIST);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<OutpostServiceDetail> result = OutpostServiceDetail.accessQuery(cfg.owner, contid, maxresults, reverse, at, stationID, serviceName, ownerID,
                                                                           minStanding, surchargePerBadStanding, discountPerGoodStanding);
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
      value = "Get corporation title roles")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation title roles",
              response = Role.class,
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
                           @QueryParam("roleID") @DefaultValue(
                               value = "{ any: true }") @ApiParam(
                                   name = "roleID",
                                   required = false,
                                   defaultValue = "{ any: true }",
                                   value = "Corporation tile role ID selector") AttributeSelector roleID,
                           @QueryParam("roleDescription") @DefaultValue(
                               value = "{ any: true }") @ApiParam(
                                   name = "roleDescription",
                                   required = false,
                                   defaultValue = "{ any: true }",
                                   value = "Corporation title role description selector") AttributeSelector roleDescription,
                           @QueryParam("roleName") @DefaultValue(
                               value = "{ any: true }") @ApiParam(
                                   name = "roleName",
                                   required = false,
                                   defaultValue = "{ any: true }",
                                   value = "Corporation title role name selector") AttributeSelector roleName) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, roleID, roleDescription, roleName);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CORPORATION_TITLES);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<Role> result = Role.accessQuery(cfg.owner, contid, maxresults, reverse, at, roleID, roleDescription, roleName);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/security_role")
  @GET
  @ApiOperation(
      value = "Get corporation security roles")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation security roles",
              response = SecurityRole.class,
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
  public Response getSecurityRoles(
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
                                   @QueryParam("roleID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "roleID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Security role ID selector") AttributeSelector roleID,
                                   @QueryParam("roleName") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "roleName",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Security role name selector") AttributeSelector roleName) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, roleID, roleName);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_MEMBER_SECURITY);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<SecurityRole> result = SecurityRole.accessQuery(cfg.owner, contid, maxresults, reverse, at, roleID, roleName);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/security_title")
  @GET
  @ApiOperation(
      value = "Get corporation security titles")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation security titles",
              response = SecurityTitle.class,
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
  public Response getSecurityTitles(
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
                                            value = "Security title ID selector") AttributeSelector titleID,
                                    @QueryParam("titleName") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "titleName",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Security title name selector") AttributeSelector titleName) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, titleID, titleName);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_MEMBER_SECURITY);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<SecurityTitle> result = SecurityTitle.accessQuery(cfg.owner, contid, maxresults, reverse, at, titleID, titleName);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/shareholder")
  @GET
  @ApiOperation(
      value = "Get corporation shareholders")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation shareholders",
              response = Shareholder.class,
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
  public Response getShareholders(
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
                                  @QueryParam("shareholderID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "shareholderID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Shareholder ID selector") AttributeSelector shareholderID,
                                  @QueryParam("isCorporation") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "isCorporation",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Shareholder is corporation selector") AttributeSelector isCorporation,
                                  @QueryParam("shareholderCorporationID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "shareholderCorporationID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Shareholder corporation ID selector") AttributeSelector shareholderCorporationID,
                                  @QueryParam("shareholderCorporationName") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "shareholderCorporationName",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Shareholder corporation name selector") AttributeSelector shareholderCorporationName,
                                  @QueryParam("shareholderName") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "shareholderName",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Shareholder name selector") AttributeSelector shareholderName,
                                  @QueryParam("shares") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "shares",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Shareholder shares selector") AttributeSelector shares) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, shareholderID, isCorporation, shareholderCorporationID, shareholderCorporationName, shareholderName, shares);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_SHAREHOLDERS);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<Shareholder> result = Shareholder.accessQuery(cfg.owner, contid, maxresults, reverse, at, shareholderID, isCorporation, shareholderCorporationID,
                                                         shareholderCorporationName, shareholderName, shares);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/starbase")
  @GET
  @ApiOperation(
      value = "Get corporation starbases")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation starbases",
              response = Starbase.class,
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
  public Response getStarbases(
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
                               @QueryParam("itemID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "itemID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Starbase item ID selector") AttributeSelector itemID,
                               @QueryParam("locationID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "locationID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Starbase location ID selector") AttributeSelector locationID,
                               @QueryParam("moonID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "moonID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Starbase moon ID selector") AttributeSelector moonID,
                               @QueryParam("onlineTimestamp") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "onlineTimestamp",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Starbase online timestamp selector") AttributeSelector onlineTimestamp,
                               @QueryParam("state") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "state",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Starbase state selector") AttributeSelector state,
                               @QueryParam("stateTimestamp") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "stateTimestamp",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Starbase state timestamp selector") AttributeSelector stateTimestamp,
                               @QueryParam("typeID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "typeID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Starbase type ID selector") AttributeSelector typeID,
                               @QueryParam("standingOwnerID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "standingOwnerID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Starbase standing owner ID selector") AttributeSelector standingOwnerID) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, itemID, locationID, moonID, onlineTimestamp, state, stateTimestamp, typeID, standingOwnerID);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_STARBASE_LIST);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<Starbase> result = Starbase.accessQuery(cfg.owner, contid, maxresults, reverse, at, itemID, locationID, moonID, onlineTimestamp, state,
                                                   stateTimestamp, typeID, standingOwnerID);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/starbase_detail")
  @GET
  @ApiOperation(
      value = "Get corporation starbase details")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation starbase details",
              response = StarbaseDetail.class,
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
  public Response getStarbaseDetails(
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
                                     @QueryParam("itemID") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "itemID",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase item ID selector") AttributeSelector itemID,
                                     @QueryParam("state") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "state",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details state selector") AttributeSelector state,
                                     @QueryParam("stateTimestamp") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "stateTimestamp",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details state timestamp selector") AttributeSelector stateTimestamp,
                                     @QueryParam("onlineTimestamp") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "onlineTimestamp",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details online timestamp selector") AttributeSelector onlineTimestamp,
                                     @QueryParam("usageFlags") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "usageFlags",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details usage flags selector") AttributeSelector usageFlags,
                                     @QueryParam("deployFlags") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "deployFlags",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details deploy flags selector") AttributeSelector deployFlags,
                                     @QueryParam("allowAllianceMembers") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "allowAllianceMembers",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details allow alliance members enabled selector") AttributeSelector allowAllianceMembers,
                                     @QueryParam("allowCorporationMembers") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "allowCorporationMembers",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details allow corporation members enabled selector") AttributeSelector allowCorporationMembers,
                                     @QueryParam("useStandingsFrom") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "useStandingsFrom",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details standings from selector") AttributeSelector useStandingsFrom,
                                     @QueryParam("onAggressionEnabled") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "onAggressionEnabled",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details on aggression enabled selector") AttributeSelector onAggressionEnabled,
                                     @QueryParam("onAggressionStanding") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "onAggressionStanding",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details standing for aggression selector") AttributeSelector onAggressionStanding,
                                     @QueryParam("onCorporationWarEnabled") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "onCorporationWarEnabled",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details on corporation war enabled selector") AttributeSelector onCorporationWarEnabled,
                                     @QueryParam("onCorporationWarStanding") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "onCorporationWarStanding",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details standing for corporation war selector") AttributeSelector onCorporationWarStanding,
                                     @QueryParam("onStandingDropEnabled") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "onStandingDropEnabled",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details on standing drop enabled selector") AttributeSelector onStandingDropEnabled,
                                     @QueryParam("onStandingDropStanding") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "onStandingDropStanding",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details standing for standing drop selector") AttributeSelector onStandingDropStanding,
                                     @QueryParam("onStatusDropEnabled") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "onStatusDropEnabled",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details on status drop enabled selector") AttributeSelector onStatusDropEnabled,
                                     @QueryParam("onStatusDropStanding") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "onStatusDropStanding",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Starbase details standing for status drop selector") AttributeSelector onStatusDropStanding) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, itemID, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers,
                                          allowCorporationMembers, useStandingsFrom, onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled,
                                          onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding, onStatusDropEnabled, onStatusDropStanding);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_STARBASE_LIST);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<StarbaseDetail> result = StarbaseDetail.accessQuery(cfg.owner, contid, maxresults, reverse, at, itemID, state, stateTimestamp, onlineTimestamp,
                                                               usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                                                               onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding,
                                                               onStandingDropEnabled, onStandingDropStanding, onStatusDropEnabled, onStatusDropStanding);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

}
