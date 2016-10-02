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
import enterprises.orbital.evekit.model.common.Asset;
import enterprises.orbital.evekit.model.common.Blueprint;
import enterprises.orbital.evekit.model.common.Bookmark;
import enterprises.orbital.evekit.model.common.Contact;
import enterprises.orbital.evekit.model.common.ContactLabel;
import enterprises.orbital.evekit.model.common.Contract;
import enterprises.orbital.evekit.model.common.ContractBid;
import enterprises.orbital.evekit.model.common.ContractItem;
import enterprises.orbital.evekit.model.common.FacWarStats;
import enterprises.orbital.evekit.model.common.IndustryJob;
import enterprises.orbital.evekit.model.common.Kill;
import enterprises.orbital.evekit.model.common.KillAttacker;
import enterprises.orbital.evekit.model.common.KillItem;
import enterprises.orbital.evekit.model.common.KillVictim;
import enterprises.orbital.evekit.model.common.Location;
import enterprises.orbital.evekit.model.common.MarketOrder;
import enterprises.orbital.evekit.model.common.Standing;
import enterprises.orbital.evekit.model.common.WalletJournal;
import enterprises.orbital.evekit.model.common.WalletTransaction;
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
                                    @QueryParam("reverse") @DefaultValue("false") @ApiParam(
                                        name = "reverse",
                                        required = false,
                                        defaultValue = "false",
                                        value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
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
    ServiceUtil.sanitizeAttributeSelector(at, accountID, accountKey);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_ACCOUNT_BALANCE);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<AccountBalance> result = AccountBalance.accessQuery(cfg.owner, contid, maxresults, reverse, at, accountID, accountKey);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/asset")
  @GET
  @ApiOperation(
      value = "Get assets")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested assets",
              response = Asset.class,
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
  public Response getAssets(
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
                                    value = "Asset item ID selector") AttributeSelector itemID,
                            @QueryParam("locationID") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "locationID",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Asset location ID selector") AttributeSelector locationID,
                            @QueryParam("typeID") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "typeID",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Asset type ID selector") AttributeSelector typeID,
                            @QueryParam("quantity") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "quantity",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Asset quantity selector") AttributeSelector quantity,
                            @QueryParam("flag") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "flag",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Asset flag selector") AttributeSelector flag,
                            @QueryParam("singleton") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "singleton",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Asset is singleton selector") AttributeSelector singleton,
                            @QueryParam("rawQuantity") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "rawQuantity",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Asset raw quantity selector") AttributeSelector rawQuantity,
                            @QueryParam("container") @DefaultValue(
                                value = "{ any: true }") @ApiParam(
                                    name = "container",
                                    required = false,
                                    defaultValue = "{ any: true }",
                                    value = "Asset container selector") AttributeSelector container) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, itemID, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_ASSETS);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<Asset> result = Asset.accessQuery(cfg.owner, contid, maxresults, reverse, at, itemID, locationID, typeID, quantity, flag, singleton, rawQuantity,
                                             container);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/blueprint")
  @GET
  @ApiOperation(
      value = "Get blueprints")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested blueprints",
              response = Blueprint.class,
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
  public Response getBlueprints(
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
                                        value = "Blueprint item ID selector") AttributeSelector itemID,
                                @QueryParam("locationID") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "locationID",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Blueprint location ID selector") AttributeSelector locationID,
                                @QueryParam("typeID") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "typeID",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Blueprint type ID selector") AttributeSelector typeID,
                                @QueryParam("typeName") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "typeName",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Blueprint type name selector") AttributeSelector typeName,
                                @QueryParam("flagID") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "flagID",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Blueprint flag ID selector") AttributeSelector flagID,
                                @QueryParam("quantity") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "quantity",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Blueprint quantity selector") AttributeSelector quantity,
                                @QueryParam("timeEfficiency") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "timeEfficiency",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Blueprint time efficiency selector") AttributeSelector timeEfficiency,
                                @QueryParam("materialEfficiency") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "materialEfficiency",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Blueprint material efficiency selector") AttributeSelector materialEfficiency,
                                @QueryParam("runs") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "runs",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Blueprint runs selector") AttributeSelector runs) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, itemID, locationID, typeID, typeName, flagID, quantity, timeEfficiency, materialEfficiency, runs);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_BLUEPRINTS);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<Blueprint> result = Blueprint.accessQuery(cfg.owner, contid, maxresults, reverse, at, itemID, locationID, typeID, typeName, flagID, quantity,
                                                     timeEfficiency, materialEfficiency, runs);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/bookmark")
  @GET
  @ApiOperation(
      value = "Get bookmarks")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested bookmarks",
              response = Bookmark.class,
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
  public Response getBookmarks(
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
                               @QueryParam("folderID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "folderID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Bookmark folder ID selector") AttributeSelector folderID,
                               @QueryParam("folderName") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "folderName",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Bookmark folder name selector") AttributeSelector folderName,
                               @QueryParam("folderCreatorID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "folderCreatorID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Bookmark folder creator ID selector") AttributeSelector folderCreatorID,
                               @QueryParam("bookmarkID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "bookmarkID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Bookmark ID selector") AttributeSelector bookmarkID,
                               @QueryParam("bookmarkCreatorID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "bookmarkCreatorID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Bookmark creator ID selector") AttributeSelector bookmarkCreatorID,
                               @QueryParam("created") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "created",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Bookmark created selector") AttributeSelector created,
                               @QueryParam("itemID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "itemID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Bookmark item ID selector") AttributeSelector itemID,
                               @QueryParam("typeID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "typeID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Bookmark type ID selector") AttributeSelector typeID,
                               @QueryParam("locationID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "locationID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Bookmark location ID selector") AttributeSelector locationID,
                               @QueryParam("x") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "x",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Bookmark x coordinate selector") AttributeSelector x,
                               @QueryParam("y") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "y",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Bookmark y coordinate selector") AttributeSelector y,
                               @QueryParam("z") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "z",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Bookmark z coordinate selector") AttributeSelector z,
                               @QueryParam("memo") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "memo",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Bookmark memo selector") AttributeSelector memo,
                               @QueryParam("note") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "note",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Bookmark note selector") AttributeSelector note) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID, x, y,
                                          z, memo, note);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_BOOKMARKS);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<Bookmark> result = Bookmark.accessQuery(cfg.owner, contid, maxresults, reverse, at, folderID, folderName, folderCreatorID, bookmarkID,
                                                   bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/contact")
  @GET
  @ApiOperation(
      value = "Get contacts")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested contacts",
              response = Contact.class,
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
  public Response getContacts(
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
                              @QueryParam("list") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "list",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Contact list selector") AttributeSelector list,
                              @QueryParam("contactID") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "contactID",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Contact ID selector") AttributeSelector contactID,
                              @QueryParam("contactName") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "contactName",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Contact name selector") AttributeSelector contactName,
                              @QueryParam("standing") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "standing",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Contact standing selector") AttributeSelector standing,
                              @QueryParam("contactTypeID") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "contactTypeID",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Contact type ID selector") AttributeSelector contactTypeID,
                              @QueryParam("inWatchlist") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "inWatchlist",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Contact in watch list selector") AttributeSelector inWatchlist,
                              @QueryParam("labelMask") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "labelMask",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Contact label mask selector") AttributeSelector labelMask) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, list, contactID, contactName, standing, contactTypeID, inWatchlist, labelMask);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CONTACT_LIST);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<Contact> result = Contact.accessQuery(cfg.owner, contid, maxresults, reverse, at, list, contactID, contactName, standing, contactTypeID, inWatchlist,
                                                 labelMask);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/contact_label")
  @GET
  @ApiOperation(
      value = "Get contact labels")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested contact labels",
              response = ContactLabel.class,
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
  public Response getContactLabels(
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
                                   @QueryParam("list") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "list",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Contact list selector") AttributeSelector list,
                                   @QueryParam("labelID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "labelID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Contact label ID selector") AttributeSelector labelID,
                                   @QueryParam("name") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "name",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Contact label name selector") AttributeSelector name) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, list, labelID, name);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CONTACT_LIST);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<ContactLabel> result = ContactLabel.accessQuery(cfg.owner, contid, maxresults, reverse, at, list, labelID, name);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/contract")
  @GET
  @ApiOperation(
      value = "Get contracts")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested contracts",
              response = Contract.class,
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
  public Response getContracts(
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
                               @QueryParam("contractID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "contractID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract ID selector") AttributeSelector contractID,
                               @QueryParam("issuerID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "issuerID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract issuer ID selector") AttributeSelector issuerID,
                               @QueryParam("issuerCorpID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "issuerCorpID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract issuer corporation ID selector") AttributeSelector issuerCorpID,
                               @QueryParam("assigneeID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "assigneeID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract assignee ID selector") AttributeSelector assigneeID,
                               @QueryParam("acceptorID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "acceptorID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract acceptor ID selector") AttributeSelector acceptorID,
                               @QueryParam("startStationID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "startStationID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract start station ID selector") AttributeSelector startStationID,
                               @QueryParam("endStationID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "endStationID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract end station ID selector") AttributeSelector endStationID,
                               @QueryParam("type") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "type",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract type selector") AttributeSelector type,
                               @QueryParam("status") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "status",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract status selector") AttributeSelector status,
                               @QueryParam("title") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "title",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract title selector") AttributeSelector title,
                               @QueryParam("forCorp") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "forCorp",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract for corporation selector") AttributeSelector forCorp,
                               @QueryParam("availability") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "availability",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract availability selector") AttributeSelector availability,
                               @QueryParam("dateIssued") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "dateIssued",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract date issued selector") AttributeSelector dateIssued,
                               @QueryParam("dateExpired") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "dateExpired",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract date expired selector") AttributeSelector dateExpired,
                               @QueryParam("dateAccepted") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "dateAccepted",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract date accepted selector") AttributeSelector dateAccepted,
                               @QueryParam("numDays") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "numDays",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract duration (days) selector") AttributeSelector numDays,
                               @QueryParam("dateCompleted") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "dateCompleted",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract date completed selector") AttributeSelector dateCompleted,
                               @QueryParam("price") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "price",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract price selector") AttributeSelector price,
                               @QueryParam("reward") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "reward",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract reward value selector") AttributeSelector reward,
                               @QueryParam("collateral") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "collateral",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract collateral value selector") AttributeSelector collateral,
                               @QueryParam("buyout") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "buyout",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract buyout price selector") AttributeSelector buyout,
                               @QueryParam("volume") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "volume",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Contract volume selector") AttributeSelector volume) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title,
                                          forCorp, availability, dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral,
                                          buyout, volume);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CONTRACTS);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<Contract> result = Contract.accessQuery(cfg.owner, contid, maxresults, reverse, at, contractID, issuerID, issuerCorpID, assigneeID, acceptorID,
                                                   startStationID, endStationID, type, status, title, forCorp, availability, dateIssued, dateExpired,
                                                   dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/contract_bid")
  @GET
  @ApiOperation(
      value = "Get contract bids")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested contract bids",
              response = ContractBid.class,
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
  public Response getContractBids(
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
                                  @QueryParam("bidID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "bidID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Contract bid ID selector") AttributeSelector bidID,
                                  @QueryParam("contractID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "contractID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Contract ID selector") AttributeSelector contractID,
                                  @QueryParam("bidderID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "bidderID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Contract bid bidder ID selector") AttributeSelector bidderID,
                                  @QueryParam("dateBid") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "dateBid",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Contract bid date selector") AttributeSelector dateBid,
                                  @QueryParam("amount") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "amount",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Contract bid amount selector") AttributeSelector amount) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, bidID, contractID, bidderID, dateBid, amount);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CONTRACTS);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<ContractBid> result = ContractBid.accessQuery(cfg.owner, contid, maxresults, reverse, at, bidID, contractID, bidderID, dateBid, amount);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/contract_item")
  @GET
  @ApiOperation(
      value = "Get contract items")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested contract items",
              response = ContractItem.class,
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
  public Response getContractItems(
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
                                   @QueryParam("contractID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "contractID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Contract ID selector") AttributeSelector contractID,
                                   @QueryParam("recordID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "recordID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Contract item record ID selector") AttributeSelector recordID,
                                   @QueryParam("typeID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "typeID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Contract item type ID selector") AttributeSelector typeID,
                                   @QueryParam("quantity") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "quantity",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Contract item quantity selector") AttributeSelector quantity,
                                   @QueryParam("rawQuantity") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "rawQuantity",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Contract item raw quantity selector") AttributeSelector rawQuantity,
                                   @QueryParam("singleton") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "singleton",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Contract item singleton selector") AttributeSelector singleton,
                                   @QueryParam("included") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "included",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Contract item included selector") AttributeSelector included) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, contractID, recordID, typeID, quantity, rawQuantity, singleton, included);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_CONTRACTS);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<ContractItem> result = ContractItem.accessQuery(cfg.owner, contid, maxresults, reverse, at, contractID, recordID, typeID, quantity, rawQuantity,
                                                           singleton, included);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/fac_war_stats")
  @GET
  @ApiOperation(
      value = "Get faction war statistics")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested faction war statistics",
              response = FacWarStats.class,
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
  public Response getFacWarStats(
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
                                 @QueryParam("currentRank") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "currentRank",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Faction war statistics current rank selector") AttributeSelector currentRank,
                                 @QueryParam("enlisted") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "enlisted",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Faction war statistics enlisted indicator selector") AttributeSelector enlisted,
                                 @QueryParam("factionID") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "factionID",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Faction war statistics faction ID selector") AttributeSelector factionID,
                                 @QueryParam("factionName") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "factionName",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Faction war statistics faction name selector") AttributeSelector factionName,
                                 @QueryParam("highestRank") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "highestRank",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Faction war statistics highest rank selector") AttributeSelector highestRank,
                                 @QueryParam("killsLastWeek") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "killsLastWeek",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Faction war statistics kill last week selector") AttributeSelector killsLastWeek,
                                 @QueryParam("killsTotal") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "killsTotal",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Faction war statistics total kills selector") AttributeSelector killsTotal,
                                 @QueryParam("killsYesterday") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "killsYesterday",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Faction war statistics kills yesterday selector") AttributeSelector killsYesterday,
                                 @QueryParam("pilots") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "pilots",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Faction war statistics pilot count selector") AttributeSelector pilots,
                                 @QueryParam("victoryPointsLastWeek") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "victoryPointsLastWeek",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Faction war statistics victory points last week selector") AttributeSelector victoryPointsLastWeek,
                                 @QueryParam("victoryPointsTotal") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "victoryPointsTotal",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Faction war statistics victory points total selector") AttributeSelector victoryPointsTotal,
                                 @QueryParam("victoryPointsYesterday") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "victoryPointsYesterday",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Faction war statistics victory points yesterday selector") AttributeSelector victoryPointsYesterday) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, currentRank, enlisted, factionID, factionName, highestRank, killsLastWeek, killsTotal, killsYesterday, pilots,
                                          victoryPointsLastWeek, victoryPointsTotal, victoryPointsYesterday);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_FAC_WAR_STATS);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<FacWarStats> result = FacWarStats.accessQuery(cfg.owner, contid, maxresults, reverse, at, currentRank, enlisted, factionID, factionName, highestRank,
                                                         killsLastWeek, killsTotal, killsYesterday, pilots, victoryPointsLastWeek, victoryPointsTotal,
                                                         victoryPointsYesterday);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/industry_job")
  @GET
  @ApiOperation(
      value = "Get industry jobs")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested industry jobs",
              response = IndustryJob.class,
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
  public Response getIndustryJobs(
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
                                  @QueryParam("jobID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "jobID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job ID selector") AttributeSelector jobID,
                                  @QueryParam("installerID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "installerID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job installer ID selector") AttributeSelector installerID,
                                  @QueryParam("installerName") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "installerName",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job installer name selector") AttributeSelector installerName,
                                  @QueryParam("facilityID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "facilityID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job facility ID selector") AttributeSelector facilityID,
                                  @QueryParam("solarSystemID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "solarSystemID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job solar system ID selector") AttributeSelector solarSystemID,
                                  @QueryParam("solarSystemName") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "solarSystemName",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job solar system name selector") AttributeSelector solarSystemName,
                                  @QueryParam("stationID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "stationID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job station ID selector") AttributeSelector stationID,
                                  @QueryParam("activityID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "activityID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job activity ID selector") AttributeSelector activityID,
                                  @QueryParam("blueprintID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "blueprintID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job blueprint ID selector") AttributeSelector blueprintID,
                                  @QueryParam("blueprintTypeID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "blueprintTypeID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job blueprint type ID selector") AttributeSelector blueprintTypeID,
                                  @QueryParam("blueprintTypeName") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "blueprintTypeName",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job blueprint type name selector") AttributeSelector blueprintTypeName,
                                  @QueryParam("blueprintLocationID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "blueprintLocationID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job blueprint location ID selector") AttributeSelector blueprintLocationID,
                                  @QueryParam("outputLocationID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "outputLocationID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job output location ID selector") AttributeSelector outputLocationID,
                                  @QueryParam("runs") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "runs",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job runs selector") AttributeSelector runs,
                                  @QueryParam("cost") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "cost",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job cost selector") AttributeSelector cost,
                                  @QueryParam("teamID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "teamID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job team ID selector") AttributeSelector teamID,
                                  @QueryParam("licensedRuns") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "licensedRuns",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job licensed runs selector") AttributeSelector licensedRuns,
                                  @QueryParam("probability") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "probability",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job probability selector") AttributeSelector probability,
                                  @QueryParam("productTypeID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "productTypeID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job product type ID selector") AttributeSelector productTypeID,
                                  @QueryParam("productTypeName") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "productTypeName",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job product type name selector") AttributeSelector productTypeName,
                                  @QueryParam("status") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "status",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job status selector") AttributeSelector status,
                                  @QueryParam("timeInSeconds") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "timeInSeconds",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job time in seconds selector") AttributeSelector timeInSeconds,
                                  @QueryParam("startDate") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "startDate",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job start date selector") AttributeSelector startDate,
                                  @QueryParam("endDate") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "endDate",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job end date selector") AttributeSelector endDate,
                                  @QueryParam("pauseDate") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "pauseDate",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job pause date selector") AttributeSelector pauseDate,
                                  @QueryParam("completedDate") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "completedDate",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job completed date selector") AttributeSelector completedDate,
                                  @QueryParam("completedCharacterID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "completedCharacterID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job completed character ID selector") AttributeSelector completedCharacterID,
                                  @QueryParam("successfulRuns") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "successfulRuns",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Industry job successful runs selector") AttributeSelector successfulRuns) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID,
                                          blueprintTypeID, blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns,
                                          probability, productTypeID, productTypeName, status, timeInSeconds, startDate, endDate, pauseDate, completedDate,
                                          completedCharacterID, successfulRuns);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_INDUSTRY_JOBS);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<IndustryJob> result = IndustryJob.accessQuery(cfg.owner, contid, maxresults, reverse, at, jobID, installerID, installerName, facilityID,
                                                         solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID, blueprintTypeName,
                                                         blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID,
                                                         productTypeName, status, timeInSeconds, startDate, endDate, pauseDate, completedDate,
                                                         completedCharacterID, successfulRuns);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/kill")
  @GET
  @ApiOperation(
      value = "Get kills")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested kills",
              response = Kill.class,
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
  public Response getKills(
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
                           @QueryParam("killID") @DefaultValue(
                               value = "{ any: true }") @ApiParam(
                                   name = "killID",
                                   required = false,
                                   defaultValue = "{ any: true }",
                                   value = "Kill ID selector") AttributeSelector killID,
                           @QueryParam("killTime") @DefaultValue(
                               value = "{ any: true }") @ApiParam(
                                   name = "killTime",
                                   required = false,
                                   defaultValue = "{ any: true }",
                                   value = "Kill time selector") AttributeSelector killTime,
                           @QueryParam("moonID") @DefaultValue(
                               value = "{ any: true }") @ApiParam(
                                   name = "moonID",
                                   required = false,
                                   defaultValue = "{ any: true }",
                                   value = "Kill moon ID selector") AttributeSelector moonID,
                           @QueryParam("solarSystemID") @DefaultValue(
                               value = "{ any: true }") @ApiParam(
                                   name = "solarSystemID",
                                   required = false,
                                   defaultValue = "{ any: true }",
                                   value = "Kill solar system ID selector") AttributeSelector solarSystemID) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, killID, killTime, moonID, solarSystemID);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_KILL_LOG);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<Kill> result = Kill.accessQuery(cfg.owner, contid, maxresults, reverse, at, killID, killTime, moonID, solarSystemID);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/kill_attacker")
  @GET
  @ApiOperation(
      value = "Get kill attackers")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested kill attackers",
              response = KillAttacker.class,
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
  public Response getKillAttackers(
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
                                   @QueryParam("killID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "killID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Kill ID selector") AttributeSelector killID,
                                   @QueryParam("attackerCharacterID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "attackerCharacterID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Kill attacker character ID selector") AttributeSelector attackerCharacterID,
                                   @QueryParam("allianceID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "allianceID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Kill attacker alliance ID selector") AttributeSelector allianceID,
                                   @QueryParam("allianceName") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "allianceName",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Kill attacker alliance name selector") AttributeSelector allianceName,
                                   @QueryParam("attackerCharacterName") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "attackerCharacterName",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Kill attacker character name selector") AttributeSelector attackerCharacterName,
                                   @QueryParam("attackerCorporationID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "attackerCorporationID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Kill attacker corporation ID selector") AttributeSelector attackerCorporationID,
                                   @QueryParam("attackerCorporationName") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "attackerCorporationName",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Kill attacker corporation name selector") AttributeSelector attackerCorporationName,
                                   @QueryParam("damageDone") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "damageDone",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Kill attacker damage done selector") AttributeSelector damageDone,
                                   @QueryParam("factionID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "factionID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Kill attacker faction ID selector") AttributeSelector factionID,
                                   @QueryParam("factionName") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "factionName",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Kill attacker faction name selector") AttributeSelector factionName,
                                   @QueryParam("securityStatus") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "securityStatus",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Kill attacker security status selector") AttributeSelector securityStatus,
                                   @QueryParam("shipTypeID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "shipTypeID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Kill attacker ship type ID selector") AttributeSelector shipTypeID,
                                   @QueryParam("weaponTypeID") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "weaponTypeID",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Kill attacker weapon type ID selector") AttributeSelector weaponTypeID,
                                   @QueryParam("finalBlow") @DefaultValue(
                                       value = "{ any: true }") @ApiParam(
                                           name = "finalBlow",
                                           required = false,
                                           defaultValue = "{ any: true }",
                                           value = "Kill attacker final blow selector") AttributeSelector finalBlow) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, killID, attackerCharacterID, allianceID, allianceName, attackerCharacterName, attackerCorporationID,
                                          attackerCorporationName, damageDone, factionID, factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_KILL_LOG);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<KillAttacker> result = KillAttacker.accessQuery(cfg.owner, contid, maxresults, reverse, at, killID, attackerCharacterID, allianceID, allianceName,
                                                           attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone, factionID,
                                                           factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/kill_item")
  @GET
  @ApiOperation(
      value = "Get kill items")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested kill items",
              response = KillItem.class,
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
  public Response getKillItems(
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
                               @QueryParam("killID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "killID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Kill ID selector") AttributeSelector killID,
                               @QueryParam("typeID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "typeID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Kill item type ID selector") AttributeSelector typeID,
                               @QueryParam("flag") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "flag",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Kill item flag selector") AttributeSelector flag,
                               @QueryParam("qtyDestroyed") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "qtyDestroyed",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Kill item quantity destroyed selector") AttributeSelector qtyDestroyed,
                               @QueryParam("qtyDropped") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "qtyDropped",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Kill item quantity dropped selector") AttributeSelector qtyDropped,
                               @QueryParam("singleton") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "singleton",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Kill item singleton selector") AttributeSelector singleton,
                               @QueryParam("sequence") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "sequence",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Kill item sequence selector") AttributeSelector sequence,
                               @QueryParam("containerSequence") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "containerSequence",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Kill item container sequence selector") AttributeSelector containerSequence) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_KILL_LOG);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<KillItem> result = KillItem.accessQuery(cfg.owner, contid, maxresults, reverse, at, killID, typeID, flag, qtyDestroyed, qtyDropped, singleton,
                                                   sequence, containerSequence);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/kill_victim")
  @GET
  @ApiOperation(
      value = "Get kill victims")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested kill victims",
              response = KillVictim.class,
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
  public Response getKillVictims(
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
                                 @QueryParam("killID") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "killID",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Kill ID selector") AttributeSelector killID,
                                 @QueryParam("allianceID") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "allianceID",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Kill victim alliance ID selector") AttributeSelector allianceID,
                                 @QueryParam("allianceName") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "allianceName",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Kill victim alliance name selector") AttributeSelector allianceName,
                                 @QueryParam("killCharacterID") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "killCharacterID",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Kill victim character ID selector") AttributeSelector killCharacterID,
                                 @QueryParam("killCharacterName") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "killCharacterName",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Kill victim character name selector") AttributeSelector killCharacterName,
                                 @QueryParam("killCorporationID") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "killCorporationID",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Kill victim corporation ID selector") AttributeSelector killCorporationID,
                                 @QueryParam("killCorporationName") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "killCorporationName",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Kill victim corporation name selector") AttributeSelector killCorporationName,
                                 @QueryParam("damageTaken") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "damageTaken",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Kill victim damage taken selector") AttributeSelector damageTaken,
                                 @QueryParam("factionID") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "factionID",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Kill victim faction ID selector") AttributeSelector factionID,
                                 @QueryParam("factionName") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "factionName",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Kill victim faction name selector") AttributeSelector factionName,
                                 @QueryParam("shipTypeID") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "shipTypeID",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Kill victim ship type ID selector") AttributeSelector shipTypeID) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, killID, allianceID, allianceName, killCharacterID, killCharacterName, killCorporationID, killCorporationName,
                                          damageTaken, factionID, factionName, shipTypeID);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_KILL_LOG);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<KillVictim> result = KillVictim.accessQuery(cfg.owner, contid, maxresults, reverse, at, killID, allianceID, allianceName, killCharacterID,
                                                       killCharacterName, killCorporationID, killCorporationName, damageTaken, factionID, factionName,
                                                       shipTypeID);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/location")
  @GET
  @ApiOperation(
      value = "Get locations")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested locations",
              response = Location.class,
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
  public Response getLocations(
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
                                       value = "Location item ID selector") AttributeSelector itemID,
                               @QueryParam("itemName") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "itemName",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Location item name selector") AttributeSelector itemName,
                               @QueryParam("x") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "x",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Location X position selector") AttributeSelector x,
                               @QueryParam("y") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "y",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Location Y position selector") AttributeSelector y,
                               @QueryParam("z") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "z",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Location Z position selector") AttributeSelector z) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, itemID, itemName, x, y, z);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_LOCATIONS);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<Location> result = Location.accessQuery(cfg.owner, contid, maxresults, reverse, at, itemID, itemName, x, y, z);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/market_order")
  @GET
  @ApiOperation(
      value = "Get market orders")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested market orders",
              response = MarketOrder.class,
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
  public Response getMarketOrders(
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
                                  @QueryParam("orderID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "orderID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Market order ID selector") AttributeSelector orderID,
                                  @QueryParam("accountKey") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "accountKey",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Market order account key selector") AttributeSelector accountKey,
                                  @QueryParam("bid") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "bid",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Market order bid indicator selector") AttributeSelector bid,
                                  @QueryParam("charID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "charID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Market order character ID selector") AttributeSelector charID,
                                  @QueryParam("duration") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "duration",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Market order duration selector") AttributeSelector duration,
                                  @QueryParam("escrow") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "escrow",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Market order escrow selector") AttributeSelector escrow,
                                  @QueryParam("issued") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "issued",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Market order issue date selector") AttributeSelector issued,
                                  @QueryParam("minVolume") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "minVolume",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Market order min volume selector") AttributeSelector minVolume,
                                  @QueryParam("orderState") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "orderState",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Market order state selector") AttributeSelector orderState,
                                  @QueryParam("price") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "price",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Market order price selector") AttributeSelector price,
                                  @QueryParam("orderRange") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "orderRange",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Market order range selector") AttributeSelector orderRange,
                                  @QueryParam("stationID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "stationID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Market order station ID selector") AttributeSelector stationID,
                                  @QueryParam("typeID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "typeID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Market order type ID selector") AttributeSelector typeID,
                                  @QueryParam("volEntered") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "volEntered",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Market order volume entered selector") AttributeSelector volEntered,
                                  @QueryParam("volRemaining") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "volRemaining",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Market order volume remaining selector") AttributeSelector volRemaining) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, orderID, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID,
                                          typeID, volEntered, volRemaining);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_MARKET_ORDERS);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<MarketOrder> result = MarketOrder.accessQuery(cfg.owner, contid, maxresults, reverse, at, orderID, accountKey, bid, charID, duration, escrow, issued,
                                                         minVolume, orderState, price, orderRange, stationID, typeID, volEntered, volRemaining);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/standing")
  @GET
  @ApiOperation(
      value = "Get standings)")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested standings",
              response = Standing.class,
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
  public Response getStandings(
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
                               @QueryParam("standingEntity") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "standingEntity",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Standing entity selector") AttributeSelector standingEntity,
                               @QueryParam("fromID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "fromID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Standing from ID selector") AttributeSelector fromID,
                               @QueryParam("fromName") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "fromName",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Standing from name selector") AttributeSelector fromName,
                               @QueryParam("standing") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "standing",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Standing value selector") AttributeSelector standing) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, standingEntity, fromID, fromName, standing);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_STANDINGS);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<Standing> result = Standing.accessQuery(cfg.owner, contid, maxresults, reverse, at, standingEntity, fromID, fromName, standing);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/wallet_journal")
  @GET
  @ApiOperation(
      value = "Get wallet journal entries")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested wallet journal entries",
              response = WalletJournal.class,
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
  public Response getJournalEntries(
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
                                    @QueryParam("accountKey") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "accountKey",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Wallet journal account key selector") AttributeSelector accountKey,
                                    @QueryParam("refID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "refID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Journal entry ref ID selector") AttributeSelector refID,
                                    @QueryParam("date") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "date",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Journal entry date selector") AttributeSelector date,
                                    @QueryParam("refTypeID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "refTypeID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Journal entry ref type ID selector") AttributeSelector refTypeID,
                                    @QueryParam("ownerName1") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "ownerName1",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Journal entry first owner name selector") AttributeSelector ownerName1,
                                    @QueryParam("ownerID1") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "ownerID1",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Journal entry first owner ID selector") AttributeSelector ownerID1,
                                    @QueryParam("ownerName2") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "ownerName2",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Journal entry second owner name selector") AttributeSelector ownerName2,
                                    @QueryParam("ownerID2") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "ownerID2",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Journal entry second owner ID selector") AttributeSelector ownerID2,
                                    @QueryParam("argName1") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "argName1",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Journal entry argument name selector") AttributeSelector argName1,
                                    @QueryParam("argID1") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "argID1",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Journal entry argument ID selector") AttributeSelector argID1,
                                    @QueryParam("amount") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "amount",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Journal entry amount selector") AttributeSelector amount,
                                    @QueryParam("balance") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "balance",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Journal entry balance selector") AttributeSelector balance,
                                    @QueryParam("reason") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "reason",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Journal entry reason selector") AttributeSelector reason,
                                    @QueryParam("taxReceiverID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "taxReceiverID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Journal entry tax receiver ID selector") AttributeSelector taxReceiverID,
                                    @QueryParam("taxAmount") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "taxAmount",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Journal entry tax amount selector") AttributeSelector taxAmount) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance,
                                          reason, taxReceiverID, taxAmount);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_WALLET_JOURNAL);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<WalletJournal> result = WalletJournal.accessQuery(cfg.owner, contid, maxresults, reverse, at, accountKey, refID, date, refTypeID, ownerName1,
                                                             ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
                                                             taxAmount);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/wallet_transaction")
  @GET
  @ApiOperation(
      value = "Get wallet transactions")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested wallet transactions",
              response = WalletTransaction.class,
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
  public Response getWalletTransactions(
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
                                        @QueryParam("accountKey") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "accountKey",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Wallet account key selector") AttributeSelector accountKey,
                                        @QueryParam("transactionID") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "transactionID",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Transaction ID selector") AttributeSelector transactionID,
                                        @QueryParam("date") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "date",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Transaction date selector") AttributeSelector date,
                                        @QueryParam("quantity") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "quantity",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Transaction quantity selector") AttributeSelector quantity,
                                        @QueryParam("typeName") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "typeName",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Transaction type name selector") AttributeSelector typeName,
                                        @QueryParam("typeID") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "typeID",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Transaction type ID selector") AttributeSelector typeID,
                                        @QueryParam("price") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "price",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Transaction price selector") AttributeSelector price,
                                        @QueryParam("clientID") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "clientID",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Transaction client ID selector") AttributeSelector clientID,
                                        @QueryParam("clientName") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "clientName",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Transaction client name selector") AttributeSelector clientName,
                                        @QueryParam("stationID") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "stationID",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Transaction station ID selector") AttributeSelector stationID,
                                        @QueryParam("stationName") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "stationName",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Transaction station name selector") AttributeSelector stationName,
                                        @QueryParam("transactionType") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "transactionType",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Transaction type selector") AttributeSelector transactionType,
                                        @QueryParam("transactionFor") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "transactionFor",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Transaction for selector") AttributeSelector transactionFor,
                                        @QueryParam("journalTransactionID") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "journalTransactionID",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Journal transaction ID selector") AttributeSelector journalTransactionID,
                                        @QueryParam("clientTypeID") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "clientTypeID",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Client type ID selector") AttributeSelector clientTypeID,
                                        @QueryParam("characterID") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "characterID",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Character ID selector") AttributeSelector characterID,
                                        @QueryParam("characterName") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "characterName",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Character name selector") AttributeSelector characterName) {
    // Verify access key and authorization for requested data
    ServiceUtil.sanitizeAttributeSelector(at, accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName,
                                          transactionType, transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    maxresults = Math.min(1000, maxresults);
    AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, at, AccountAccessMask.ACCESS_WALLET_TRANSACTIONS);
    if (cfg.fail) return cfg.response;
    // Retrieve requested balance
    try {
      List<WalletTransaction> result = WalletTransaction.accessQuery(cfg.owner, contid, maxresults, reverse, at, accountKey, transactionID, date, quantity,
                                                                     typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
                                                                     transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
      // Finish
      return ServiceUtil.finish(cfg, result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

}
