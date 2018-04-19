package enterprises.orbital.evekit.ws.common;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.ws.ServiceError;
import enterprises.orbital.evekit.ws.ServiceUtil;
import enterprises.orbital.evekit.ws.ServiceUtil.AccessConfig;
import enterprises.orbital.evekit.ws.ServiceUtil.AuthenticationResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/ws/v1/accesskey")
@Consumes({
    "application/json"
})
@Produces({
    "application/json"
})
@Api(
    tags = {
        "AccessKey"
    },
    produces = "application/json",
    consumes = "application/json")
public class ModelAccessKeyWS {

  @Path("/key_info")
  @GET
  @ApiOperation(
      value = "Get information about an access key")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "access key info",
              response = KeyInfo.class),
          @ApiResponse(
              code = 401,
              message = "access key credential is invalid",
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
  public Response getKeyInfo(
                             @Context HttpServletRequest request,
                             @QueryParam("accessKey") @ApiParam(
                                 name = "accessKey",
                                 required = true,
                                 value = "Model access key") int accessKey,
                             @QueryParam("accessCred") @ApiParam(
                                 name = "accessCred",
                                 required = true,
                                 value = "Model access credential") String accessCred) {
    long now = OrbitalProperties.getCurrentTime();
    AttributeSelector at = new AttributeSelector("{values:[" + now + "]}");
    AuthenticationResult result = ServiceUtil.authenticate(accessKey, accessCred, now, at);
    if (result.isFail()) return result.response;
    AccessConfig cfg = new AccessConfig(accessKey, accessCred, at, null);
    cfg.key = result.key;
    cfg.key.generateMaskValue();
    cfg.owner = result.key.getSyncAccount();
    cfg.owner.updateValid();
    cfg.presetExpiry = now + TimeUnit.MINUTES.toMillis(5);
    KeyInfo ki = new KeyInfo(
        cfg.owner.isCharacterType() ? "character" : "corporation",
        cfg.owner.isCharacterType() ? cfg.owner.getEveCharacterName() : cfg.owner.getEveCorporationName(),
        cfg.owner.isCharacterType() ? cfg.owner.getEveCharacterID() : cfg.owner.getEveCorporationID(), cfg.key.getMaskValue().longValue(), cfg.key.getExpiry(),
        cfg.key.getLimit(), cfg.owner.isValid());
    return ServiceUtil.finish(cfg, ki, request);
  }

  @Path("/mask_list")
  @GET
  @ApiOperation(
      value = "Get access key mask list constants")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "access key mask list",
              response = MaskList.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
      })
  public Response getMaskList(
                              @Context HttpServletRequest request) {
    return Response.status(Status.OK).entity(new MaskList()).build();
  }

}
