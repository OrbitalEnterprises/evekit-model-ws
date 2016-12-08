package enterprises.orbital.evekit.ws.calls;

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

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;
import enterprises.orbital.evekit.model.RefData;
import enterprises.orbital.evekit.model.calls.Call;
import enterprises.orbital.evekit.model.calls.CallGroup;
import enterprises.orbital.evekit.ws.ServiceError;
import enterprises.orbital.evekit.ws.ServiceUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/ws/v1/calls")
@Consumes({
    "application/json"
})
@Produces({
    "application/json"
})
@Api(
    tags = {
        "Calls"
    },
    produces = "application/json",
    consumes = "application/json")
public class ModelCallsWS {

  @Path("/calls")
  @GET
  @ApiOperation(
      value = "Get EVE API calls")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested API calls",
              response = Call.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getCalls(
                           @Context HttpServletRequest request,
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
                           @QueryParam("accessMask") @DefaultValue(
                               value = "{ any: true }") @ApiParam(
                                   name = "accessMask",
                                   required = false,
                                   defaultValue = "{ any: true }",
                                   value = "Call access mask selector") AttributeSelector accessMask,
                           @QueryParam("type") @DefaultValue(
                               value = "{ any: true }") @ApiParam(
                                   name = "type",
                                   required = false,
                                   defaultValue = "{ any: true }",
                                   value = "Call type selector") AttributeSelector type,
                           @QueryParam("name") @DefaultValue(
                               value = "{ any: true }") @ApiParam(
                                   name = "name",
                                   required = false,
                                   defaultValue = "{ any: true }",
                                   value = "Call name selector") AttributeSelector name,
                           @QueryParam("groupID") @DefaultValue(
                               value = "{ any: true }") @ApiParam(
                                   name = "groupID",
                                   required = false,
                                   defaultValue = "{ any: true }",
                                   value = "Call group ID selector") AttributeSelector groupID,
                           @QueryParam("description") @DefaultValue(
                               value = "{ any: true }") @ApiParam(
                                   name = "description",
                                   required = false,
                                   defaultValue = "{ any: true }",
                                   value = "Call description selector") AttributeSelector description) {
    ServiceUtil.sanitizeAttributeSelector(at, accessMask, type, name, groupID, description);
    maxresults = Math.min(1000, maxresults);
    try {
      List<Call> result = Call.accessQuery(contid, maxresults, reverse, at, accessMask, type, name, groupID, description);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getCallListExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/call_groups")
  @GET
  @ApiOperation(
      value = "Get call groups status")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested API call groups",
              response = CallGroup.class,
              responseContainer = "array"),
          @ApiResponse(
              code = 400,
              message = "invalid attribute selector",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getCallGroups(
                                @Context HttpServletRequest request,
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
                                @QueryParam("groupID") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "groupID",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Call group ID selector") AttributeSelector groupID,
                                @QueryParam("name") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "name",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Call group name selector") AttributeSelector name,
                                @QueryParam("description") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "description",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Call group description selector") AttributeSelector description) {
    ServiceUtil.sanitizeAttributeSelector(at, groupID, name, description);
    maxresults = Math.min(1000, maxresults);
    try {
      List<CallGroup> result = CallGroup.accessQuery(contid, maxresults, reverse, at, groupID, name, description);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getCallListExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

}
