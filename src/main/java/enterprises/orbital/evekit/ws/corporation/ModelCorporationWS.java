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
import enterprises.orbital.evekit.model.corporation.MemberTracking;
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

  // TODO
  // -rwx------+ 1 mark_000 mark_000 2845 Sep 20 14:10 ContainerLogResource.java
  // -rwx------+ 1 mark_000 mark_000 1240 Sep 20 14:10 CorporationMedalsResource.java
  // -rwx------+ 1 mark_000 mark_000 2779 Sep 20 14:10 CorporationSheetResource.java
  // -rwx------+ 1 mark_000 mark_000 1240 Sep 20 14:10 CorporationTitlesResource.java
  // -rwx------+ 1 mark_000 mark_000 1231 Sep 20 14:10 CustomsOfficesResource.java
  // -rwx------+ 1 mark_000 mark_000 1204 Sep 20 14:10 FacilitiesResource.java
  // -rwx------+ 1 mark_000 mark_000 2923 Sep 20 14:10 MemberMedalsResource.java
  // -rwx------+ 1 mark_000 mark_000 4135 Sep 20 14:10 MemberSecurityResource.java
  // -rwx------+ 1 mark_000 mark_000 1202 Sep 20 14:10 OutpostResource.java
  // -rwx------+ 1 mark_000 mark_000 1227 Sep 20 14:10 ShareholderResource.java
  // -rwx------+ 1 mark_000 mark_000 1206 Sep 20 14:10 StarbaseResource.java

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
                                            value = "member location ID selector") AttributeSelector locationID,
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
    ServiceUtil.sanitizeAttributeSelector(at);
    ServiceUtil.sanitizeAttributeSelector(characterID);
    ServiceUtil.sanitizeAttributeSelector(base);
    ServiceUtil.sanitizeAttributeSelector(baseID);
    ServiceUtil.sanitizeAttributeSelector(grantableRoles);
    ServiceUtil.sanitizeAttributeSelector(location);
    ServiceUtil.sanitizeAttributeSelector(locationID);
    ServiceUtil.sanitizeAttributeSelector(logoffDateTime);
    ServiceUtil.sanitizeAttributeSelector(logonDateTime);
    ServiceUtil.sanitizeAttributeSelector(name);
    ServiceUtil.sanitizeAttributeSelector(roles);
    ServiceUtil.sanitizeAttributeSelector(shipType);
    ServiceUtil.sanitizeAttributeSelector(shipTypeID);
    ServiceUtil.sanitizeAttributeSelector(startDateTime);
    ServiceUtil.sanitizeAttributeSelector(title);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_MEMBER_TRACKING);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve
      List<MemberTracking> result = MemberTracking.accessQuery(cfg.owner, contid, maxresults, at, characterID, base, baseID, grantableRoles, location,
                                                               locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID, startDateTime,
                                                               title);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

}
