package enterprises.orbital.evekit.ws.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedAccountAccessKey;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import enterprises.orbital.evekit.model.MetaDataCountException;
import enterprises.orbital.evekit.model.MetaDataLimitException;
import enterprises.orbital.evekit.ws.ServiceError;
import enterprises.orbital.evekit.ws.ServiceUtil;
import enterprises.orbital.evekit.ws.ServiceUtil.AccessConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/ws/v1/meta")
@Consumes({
    "application/json"
})
@Produces({
    "application/json"
})
@Api(
    tags = {
        "Meta"
},
    produces = "application/json",
    consumes = "application/json")
public class ModelMetaWS {
  private static final Logger log = Logger.getLogger(ModelMetaWS.class.getName());

  private static Response limitCheck(
      SynchronizedAccountAccessKey key,
      CachedData target) {
    if (key.getLimit() > 0) {
      long limit = key.getLimit();
      if (limit >= target.getLifeEnd()) {
        ServiceError errMsg = new ServiceError(
            Status.FORBIDDEN.getStatusCode(), "Access key not authorized to access the requested model object, contact key owner");
        return Response.status(Status.FORBIDDEN).entity(errMsg).build();
      }
    }
    return null;
  }

  @Path("/meta/{cid}/{key}")
  @GET
  @ApiOperation(
      value = "Get meta data value.  A value of null indicates key was not set on this object.")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "Meta data pair for the given model object with the given key",
              response = MetaData.class),
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
              message = "access key or model object not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
  })
  public Response getMetaDataValue(
                                   @Context HttpServletRequest request,
                                   @QueryParam("accessKey") @ApiParam(
                                       name = "accessKey",
                                       required = true,
                                       value = "Model access key") int accessKey,
                                   @QueryParam("accessCred") @ApiParam(
                                       name = "accessCred",
                                       required = true,
                                       value = "Model access credential") String accessCred,
                                   @PathParam("cid") @ApiParam(
                                       name = "cid",
                                       required = true,
                                       value = "Model object ID") long cid,
                                   @PathParam("key") @ApiParam(
                                       name = "key",
                                       required = true,
                                       value = "Meta-data key") String key) {
    // Meta-data steps:
    // 1. Find requested object if it exists - error if not
    CachedData target = CachedData.get(cid);
    if (target == null) {
      ServiceError errMsg = new ServiceError(Status.NOT_FOUND.getStatusCode(), "No model object found with the given ID");
      return Response.status(Status.NOT_FOUND).entity(errMsg).build();
    }
    // 2. Perform access check - error on failure
    Collection<AccountAccessMask> masks = AccountAccessMask.createMaskSet(target.getAccessMask());
    AttributeSelector at = new AttributeSelector("{ any: true }");
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, masks);
    if (cfg.fail) return cfg.response;
    // 3. Verify key limits against target object
    Response limitCheck = limitCheck(cfg.key, target);
    if (limitCheck != null) return limitCheck;
    // 4. Perform requested meta-data operation
    MetaData result = new MetaData(key, target.getMetaData(key));
    // 5. return result - meta-data operations are not cacheable
    cfg.presetExpiry = Long.MIN_VALUE;
    return ServiceUtil.finish(cfg, result, request);
  }

  @Path("/meta/{cid}")
  @GET
  @ApiOperation(
      value = "Get all meta data values for the given model object.")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of meta data values for the given model object",
              response = MetaData.class,
              responseContainer = "array"),
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
              message = "access key or model object not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
  })
  public Response getMetaDataValues(
                                    @Context HttpServletRequest request,
                                    @QueryParam("accessKey") @ApiParam(
                                        name = "accessKey",
                                        required = true,
                                        value = "Model access key") int accessKey,
                                    @QueryParam("accessCred") @ApiParam(
                                        name = "accessCred",
                                        required = true,
                                        value = "Model access credential") String accessCred,
                                    @PathParam("cid") @ApiParam(
                                        name = "cid",
                                        required = true,
                                        value = "Model object ID") long cid) {
    // Meta-data steps:
    // 1. Find requested object if it exists - error if not
    CachedData target = CachedData.get(cid);
    if (target == null) {
      ServiceError errMsg = new ServiceError(Status.NOT_FOUND.getStatusCode(), "No model object found with the given ID");
      return Response.status(Status.NOT_FOUND).entity(errMsg).build();
    }
    // 2. Perform access check - error on failure
    Collection<AccountAccessMask> masks = AccountAccessMask.createMaskSet(target.getAccessMask());
    AttributeSelector at = new AttributeSelector("{ any: true }");
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, masks);
    if (cfg.fail) return cfg.response;
    // 3. Verify key limits against target object
    Response limitCheck = limitCheck(cfg.key, target);
    if (limitCheck != null) return limitCheck;
    // 4. Perform requested meta-data operation
    List<MetaData> result = new ArrayList<>();
    for (Entry<String, String> datum : target.getAllMetaData()) {
      result.add(new MetaData(datum.getKey(), datum.getValue()));
    }
    // 5. return result - meta-data operations are not cacheable
    cfg.presetExpiry = Long.MIN_VALUE;
    return ServiceUtil.finish(cfg, result, request);
  }

  @SuppressWarnings("Duplicates")
  @Path("/meta/{cid}")
  @DELETE
  @ApiOperation(
      value = "Delete all meta data values for the given model object.")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "data deleted successfully"),
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
              message = "access key or model object not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
  })
  public Response removeMetaDataValues(
                                       @Context HttpServletRequest request,
                                       @QueryParam("accessKey") @ApiParam(
                                           name = "accessKey",
                                           required = true,
                                           value = "Model access key") int accessKey,
                                       @QueryParam("accessCred") @ApiParam(
                                           name = "accessCred",
                                           required = true,
                                           value = "Model access credential") String accessCred,
                                       @PathParam("cid") @ApiParam(
                                           name = "cid",
                                           required = true,
                                           value = "Model object ID") long cid) {
    // Meta-data steps:
    // 1. Find requested object if it exists - error if not
    CachedData target = CachedData.get(cid);
    if (target == null) {
      ServiceError errMsg = new ServiceError(Status.NOT_FOUND.getStatusCode(), "No model object found with the given ID");
      return Response.status(Status.NOT_FOUND).entity(errMsg).build();
    }
    // 2. Perform access check - error on failure
    Collection<AccountAccessMask> masks = AccountAccessMask.createMaskSet(target.getAccessMask());
    AttributeSelector at = new AttributeSelector("{ any: true }");
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, masks);
    if (cfg.fail) return cfg.response;
    // 3. Verify key limits against target object
    Response limitCheck = limitCheck(cfg.key, target);
    if (limitCheck != null) return limitCheck;
    // 4. Perform requested meta-data operation
    for (Entry<String, String> datum : target.getAllMetaData()) {
      target.deleteMetaData(datum.getKey());
    }
    try {
      CachedData.update(target);
    } catch (IOException e) {
      log.log(Level.WARNING, "query error", e);
      ServiceError errMsg = new ServiceError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Internal error removing meta data.  If this error persists, please contact the site administrator");
      return Response.status(Response.Status.BAD_REQUEST)
                     .entity(errMsg)
                     .build();
    }
    // 5. return result - meta-data operations are not cacheable
    cfg.presetExpiry = Long.MIN_VALUE;
    return ServiceUtil.finish(cfg, null, request);
  }

  @Path("/meta/{cid}/{key}/{value}")
  @PUT
  @ApiOperation(
      value = "Set meta data value.  If a value already exists for this key, then it is silently over-written.  Null keys or values are not allowed.")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "Meta data value set"),
          @ApiResponse(
              code = 400,
              message = "invalid key or value, or meta-data constraints exceeded",
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
              message = "access key or model object not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
  })
  public Response setMetaDataValue(
                                   @Context HttpServletRequest request,
                                   @QueryParam("accessKey") @ApiParam(
                                       name = "accessKey",
                                       required = true,
                                       value = "Model access key") int accessKey,
                                   @QueryParam("accessCred") @ApiParam(
                                       name = "accessCred",
                                       required = true,
                                       value = "Model access credential") String accessCred,
                                   @PathParam("cid") @ApiParam(
                                       name = "cid",
                                       required = true,
                                       value = "Model object ID") long cid,
                                   @PathParam("key") @ApiParam(
                                       name = "key",
                                       required = true,
                                       value = "Meta-data key") String key,
                                   @PathParam("value") @ApiParam(
                                       name = "value",
                                       required = true,
                                       value = "Meta-data value") String value) {
    // Meta-data steps:
    // 1. Find requested object if it exists - error if not
    CachedData target = CachedData.get(cid);
    if (target == null) {
      ServiceError errMsg = new ServiceError(Status.NOT_FOUND.getStatusCode(), "No model object found with the given ID");
      return Response.status(Status.NOT_FOUND).entity(errMsg).build();
    }
    // 2. Perform access check - error on failure
    Collection<AccountAccessMask> masks = AccountAccessMask.createMaskSet(target.getAccessMask());
    AttributeSelector at = new AttributeSelector("{ any: true }");
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, masks);
    if (cfg.fail) return cfg.response;
    // 3. Verify key limits against target object
    Response limitCheck = limitCheck(cfg.key, target);
    if (limitCheck != null) return limitCheck;
    // 4. Perform requested meta-data operation
    try {
      target.setMetaData(key, value);
      CachedData.update(target);
    } catch (MetaDataLimitException | MetaDataCountException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), e.getMessage());
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    } catch (IOException e) {
      log.log(Level.WARNING, "query error", e);
      ServiceError errMsg = new ServiceError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Internal error setting meta data.  If this error persists, please contact the site administrator");
      return Response.status(Response.Status.BAD_REQUEST)
                     .entity(errMsg)
                     .build();
    }
    // 5. return result - meta-data operations are not cacheable
    cfg.presetExpiry = Long.MIN_VALUE;
    return ServiceUtil.finish(cfg, null, request);
  }

  @SuppressWarnings("Duplicates")
  @Path("/meta/{cid}/{key}")
  @DELETE
  @ApiOperation(
      value = "Delete meta data value.")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "Meta value deleted"),
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
              message = "access key or model object not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
  })
  public Response removeMetaDataValue(
                                      @Context HttpServletRequest request,
                                      @QueryParam("accessKey") @ApiParam(
                                          name = "accessKey",
                                          required = true,
                                          value = "Model access key") int accessKey,
                                      @QueryParam("accessCred") @ApiParam(
                                          name = "accessCred",
                                          required = true,
                                          value = "Model access credential") String accessCred,
                                      @PathParam("cid") @ApiParam(
                                          name = "cid",
                                          required = true,
                                          value = "Model object ID") long cid,
                                      @PathParam("key") @ApiParam(
                                          name = "key",
                                          required = true,
                                          value = "Meta-data key") String key) {
    // Meta-data steps:
    // 1. Find requested object if it exists - error if not
    CachedData target = CachedData.get(cid);
    if (target == null) {
      ServiceError errMsg = new ServiceError(Status.NOT_FOUND.getStatusCode(), "No model object found with the given ID");
      return Response.status(Status.NOT_FOUND).entity(errMsg).build();
    }
    // 2. Perform access check - error on failure
    Collection<AccountAccessMask> masks = AccountAccessMask.createMaskSet(target.getAccessMask());
    AttributeSelector at = new AttributeSelector("{ any: true }");
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, masks);
    if (cfg.fail) return cfg.response;
    // 3. Verify key limits against target object
    Response limitCheck = limitCheck(cfg.key, target);
    if (limitCheck != null) return limitCheck;
    // 4. Perform requested meta-data operation
    target.deleteMetaData(key);
    try {
      CachedData.update(target);
    } catch (IOException e) {
      log.log(Level.WARNING, "query error", e);
      ServiceError errMsg = new ServiceError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Internal error removing meta data.  If this error persists, please contact the site administrator");
      return Response.status(Response.Status.BAD_REQUEST)
                     .entity(errMsg)
                     .build();
    }
    // 5. return result - meta-data operations are not cacheable
    cfg.presetExpiry = Long.MIN_VALUE;
    return ServiceUtil.finish(cfg, null, request);
  }

}
