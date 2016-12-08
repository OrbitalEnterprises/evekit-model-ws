package enterprises.orbital.evekit.ws.server;

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
import enterprises.orbital.evekit.model.server.ServerStatus;
import enterprises.orbital.evekit.ws.ServiceError;
import enterprises.orbital.evekit.ws.ServiceUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/ws/v1/server")
@Consumes({
    "application/json"
})
@Produces({
    "application/json"
})
@Api(
    tags = {
        "Server"
    },
    produces = "application/json",
    consumes = "application/json")
public class ModelServerWS {

  @Path("/server_status")
  @GET
  @ApiOperation(
      value = "Get server status")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested server statuses",
              response = ServerStatus.class,
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
  public Response getServerStatus(
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
                                  @QueryParam("onlinePlayers") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "onlinePlayers",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Online players selector") AttributeSelector onlinePlayers,
                                  @QueryParam("serverOpen") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "serverOpen",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Server open selector") AttributeSelector serverOpen) {
    ServiceUtil.sanitizeAttributeSelector(at, onlinePlayers, serverOpen);
    maxresults = Math.min(1000, maxresults);
    try {
      List<ServerStatus> result = ServerStatus.accessQuery(contid, maxresults, reverse, at, onlinePlayers, serverOpen);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getServerStatusExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

}
