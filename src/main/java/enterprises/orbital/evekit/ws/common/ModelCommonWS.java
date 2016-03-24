package enterprises.orbital.evekit.ws.common;

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
import enterprises.orbital.evekit.model.common.AccountBalance;
import enterprises.orbital.evekit.ws.ServiceError;
import enterprises.orbital.evekit.ws.ServiceUtil;
import enterprises.orbital.evekit.ws.ServiceUtil.AccessConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/ws/v1/common")
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
public class ModelCommonWS {

  // TODO
  // -rwx------+ 1 mark_000 mark_000 1262 Sep 20 14:10 AccountStatusResource.java
  // -rwx------+ 1 mark_000 mark_000 2507 Sep 20 14:10 AssetResource.java
  // -rwx------+ 1 mark_000 mark_000 2369 Sep 20 14:10 BlueprintResource.java
  // -rwx------+ 1 mark_000 mark_000 3099 Sep 20 14:10 BookmarkResource.java
  // -rwx------+ 1 mark_000 mark_000 6572 Sep 20 14:10 ContactResource.java
  // -rwx------+ 1 mark_000 mark_000 7965 Sep 20 14:10 ContractResource.java
  // -rwx------+ 1 mark_000 mark_000 1238 Sep 20 14:10 FactionWarResource.java
  // -rwx------+ 1 mark_000 mark_000 4794 Sep 20 14:10 IndustryJobResource.java
  // -rwx------+ 1 mark_000 mark_000 8879 Sep 20 14:10 KillResource.java
  // -rwx------+ 1 mark_000 mark_000 4833 Sep 20 14:10 MarketOrderResource.java
  // -rwx------+ 1 mark_000 mark_000 5130 Sep 20 14:10 MetaResource.java
  // -rwx------+ 1 mark_000 mark_000 3846 Sep 20 14:10 StandingResource.java
  // -rwx------+ 1 mark_000 mark_000 5248 Sep 20 14:10 WalletResource.java

  @Path("/account_balance")
  @GET
  @ApiOperation(
      value = "Get account balance(s)")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested account balances",
              response = AccountBalance.class,
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
  public Response getAccountBalance(
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
                                    @QueryParam("accountID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "accountID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Account ID selector") AttributeSelector accountID,
                                    @QueryParam("accountKey") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "accountKey",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Account key selector") AttributeSelector accountKey) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at);
    ServiceUtil.sanitizeAttributeSelector(accountID);
    ServiceUtil.sanitizeAttributeSelector(accountKey);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_ACCOUNT_BALANCE);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<AccountBalance> result = AccountBalance.accessQuery(cfg.owner, contid, maxresults, at, accountID, accountKey);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

}
