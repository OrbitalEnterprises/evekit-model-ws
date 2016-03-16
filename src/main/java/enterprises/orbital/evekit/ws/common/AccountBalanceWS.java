package enterprises.orbital.evekit.ws.common;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.common.AccountBalance;
import enterprises.orbital.evekit.ws.common.ServiceUtil.AccessConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/ws/v1/common/account_balance")
@Consumes({
    "application/json"
})
@Produces({
    "application/json"
})
@Api(
    tags = {
        "Common"
},
    produces = "application/json",
    consumes = "application/json")
public class AccountBalanceWS {

  @Path("/key/{key}")
  @GET
  @ApiOperation(
      value = "Get account balance by account key, or all account balances if key = -1")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested account balances",
              response = AccountBalance.class,
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
              message = "access key not found or requested balance not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
  })
  public Response getAccountBalanceByKey(
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
                                             value = "-1") @ApiParam(
                                                 name = "at",
                                                 required = false,
                                                 defaultValue = "-1",
                                                 value = "Model lifeline time (milliseconds UTC) for request") long at,
                                         @PathParam("key") @ApiParam(
                                             name = "key",
                                             required = true,
                                             value = "Account key of requested balance.  Set to -1 to retrieve all balances.") int key) {
    // Verify access key and authorization for requested data
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_ACCOUNT_BALANCE);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    List<AccountBalance> result = new ArrayList<AccountBalance>();
    if (key != -1) {
      AccountBalance balance = AccountBalance.getByKey(cfg.owner, cfg.at, key);
      if (balance == null) {
        ServiceError errMsg = new ServiceError(Status.NOT_FOUND.getStatusCode(), "Balance with given key not found");
        return Response.status(Status.NOT_FOUND).entity(errMsg).build();
      }
      result.add(balance);
    } else {
      List<AccountBalance> balanceList = AccountBalance.getAll(cfg.owner, cfg.at);
      if (balanceList == null) {
        ServiceError errMsg = new ServiceError(Status.NOT_FOUND.getStatusCode(), "No balances found at the given time");
        return Response.status(Status.NOT_FOUND).entity(errMsg).build();
      }
      result.addAll(balanceList);
    }
    // Finish
    return ServiceUtil.finish(cfg, result, request);
  }

  @Path("/id/{id}")
  @GET
  @ApiOperation(
      value = "Get account balance by account ID, or all account balances if id = -1")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested account balances",
              response = AccountBalance.class,
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
              message = "access key not found or requested balance not found",
              response = ServiceError.class),
          @ApiResponse(
              code = 500,
              message = "internal service error",
              response = ServiceError.class),
  })
  public Response getAccountBalanceByID(
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
                                            value = "-1") @ApiParam(
                                                name = "at",
                                                required = false,
                                                defaultValue = "-1",
                                                value = "Model lifeline time (milliseconds UTC) for request") long at,
                                        @PathParam("id") @ApiParam(
                                            name = "id",
                                            required = true,
                                            value = "Account ID of requested balance.  Set to -1 to retrieve all balances.") int id) {
    // Verify access key and authorization for requested data
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_ACCOUNT_BALANCE);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    List<AccountBalance> result = new ArrayList<AccountBalance>();
    if (id != -1) {
      AccountBalance balance = AccountBalance.get(cfg.owner, cfg.at, id);
      if (balance == null) {
        ServiceError errMsg = new ServiceError(Status.NOT_FOUND.getStatusCode(), "Balance with given ID not found");
        return Response.status(Status.NOT_FOUND).entity(errMsg).build();
      }
      result.add(balance);
    } else {
      List<AccountBalance> balanceList = AccountBalance.getAll(cfg.owner, cfg.at);
      if (balanceList == null) {
        ServiceError errMsg = new ServiceError(Status.NOT_FOUND.getStatusCode(), "No balances found at the given time");
        return Response.status(Status.NOT_FOUND).entity(errMsg).build();
      }
      result.addAll(balanceList);
    }
    // Finish
    return ServiceUtil.finish(cfg, result, request);
  }

}
