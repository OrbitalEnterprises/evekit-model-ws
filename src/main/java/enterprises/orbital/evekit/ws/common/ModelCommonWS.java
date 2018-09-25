package enterprises.orbital.evekit.ws.common;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.ESISyncEndpoint;
import enterprises.orbital.evekit.model.common.*;
import enterprises.orbital.evekit.model.common.Contact;
import enterprises.orbital.evekit.ws.AccountHandlerUtil;
import enterprises.orbital.evekit.ws.ServiceError;
import io.swagger.annotations.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

import static enterprises.orbital.evekit.ws.AccountHandlerUtil.handleStandardExpiry;

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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("division") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "division",
          defaultValue = "{ any: true }",
          value = "Division selector") AttributeSelector division,
      @QueryParam("balance") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "balance",
          defaultValue = "{ any: true }",
          value = "Balance selector") AttributeSelector balance) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_ACCOUNT_BALANCE,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<AccountBalance>() {

                                                          @Override
                                                          public List<AccountBalance> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int DIVISION = 0;
                                                            final int BALANCE = 1;
                                                            return AccountBalance.accessQuery(acct, contid, maxresults,
                                                                                              reverse, at,
                                                                                              others[DIVISION],
                                                                                              others[BALANCE]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_WALLET_BALANCE : ESISyncEndpoint.CORP_WALLET_BALANCE,
                                                                acct);
                                                          }
                                                        }, request, division, balance);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("itemID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "itemID",
          defaultValue = "{ any: true }",
          value = "Asset item ID selector") AttributeSelector itemID,
      @QueryParam("locationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "locationID",
          defaultValue = "{ any: true }",
          value = "Asset location ID selector") AttributeSelector locationID,
      @QueryParam("locationType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "locationType",
          defaultValue = "{ any: true }",
          value = "Asset location type selector") AttributeSelector locationType,
      @QueryParam("locationFlag") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "locationFlag",
          defaultValue = "{ any: true }",
          value = "Asset location flag selector") AttributeSelector locationFlag,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Asset type ID selector") AttributeSelector typeID,
      @QueryParam("quantity") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "quantity",
          defaultValue = "{ any: true }",
          value = "Asset quantity selector") AttributeSelector quantity,
      @QueryParam("singleton") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "singleton",
          defaultValue = "{ any: true }",
          value = "Asset is singleton selector") AttributeSelector singleton,
      @QueryParam("blueprintType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "blueprintType",
          defaultValue = "{ any: true }",
          value = "Asset blueprint type selector") AttributeSelector blueprintType,
      @QueryParam("blueprintCopy") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "blueprintCopy",
          defaultValue = "{ any: true }",
          value = "Asset blueprint copy selector") AttributeSelector blueprintCopy) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_ASSETS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Asset>() {

                                                          @Override
                                                          public List<Asset> getList(SynchronizedEveAccount acct,
                                                                                     long contid, int maxresults,
                                                                                     boolean reverse,
                                                                                     AttributeSelector at,
                                                                                     AttributeSelector... others) throws IOException {
                                                            final int ITEM_ID = 0;
                                                            final int LOCATION_ID = 1;
                                                            final int LOCATION_TYPE = 2;
                                                            final int LOCATION_FLAG = 3;
                                                            final int TYPE_ID = 4;
                                                            final int QUANTITY = 5;
                                                            final int SINGLETON = 6;
                                                            final int BLUEPRINT_TYPE = 7;
                                                            final int BLUEPRINT_COPY = 8;

                                                            return Asset.accessQuery(acct, contid, maxresults, reverse,
                                                                                     at,
                                                                                     others[ITEM_ID],
                                                                                     others[LOCATION_ID],
                                                                                     others[LOCATION_TYPE],
                                                                                     others[LOCATION_FLAG],
                                                                                     others[TYPE_ID],
                                                                                     others[QUANTITY],
                                                                                     others[SINGLETON],
                                                                                     others[BLUEPRINT_TYPE],
                                                                                     others[BLUEPRINT_COPY]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_ASSETS : ESISyncEndpoint.CORP_ASSETS,
                                                                acct);
                                                          }
                                                        }, request, itemID, locationID, locationType, locationFlag,
                                                        typeID, quantity, singleton, blueprintType, blueprintCopy);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("itemID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "itemID",
          defaultValue = "{ any: true }",
          value = "Blueprint item ID selector") AttributeSelector itemID,
      @QueryParam("locationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "locationID",
          defaultValue = "{ any: true }",
          value = "Blueprint location ID selector") AttributeSelector locationID,
      @QueryParam("locationFlag") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "locationFlag",
          defaultValue = "{ any: true }",
          value = "Blueprint location flag selector") AttributeSelector locationFlag,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Blueprint type ID selector") AttributeSelector typeID,
      @QueryParam("quantity") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "quantity",
          defaultValue = "{ any: true }",
          value = "Blueprint quantity selector") AttributeSelector quantity,
      @QueryParam("timeEfficiency") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "timeEfficiency",
          defaultValue = "{ any: true }",
          value = "Blueprint time efficiency selector") AttributeSelector timeEfficiency,
      @QueryParam("materialEfficiency") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "materialEfficiency",
          defaultValue = "{ any: true }",
          value = "Blueprint material efficiency selector") AttributeSelector materialEfficiency,
      @QueryParam("runs") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "runs",
          defaultValue = "{ any: true }",
          value = "Blueprint runs selector") AttributeSelector runs) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_BLUEPRINTS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Blueprint>() {

                                                          @Override
                                                          public List<Blueprint> getList(SynchronizedEveAccount acct,
                                                                                         long contid, int maxresults,
                                                                                         boolean reverse,
                                                                                         AttributeSelector at,
                                                                                         AttributeSelector... others) throws IOException {
                                                            final int ITEM_ID = 0;
                                                            final int LOCATION_ID = 1;
                                                            final int LOCATION_FLAG = 2;
                                                            final int TYPE_ID = 3;
                                                            final int QUANTITY = 4;
                                                            final int TIME_EFFICIENCY = 5;
                                                            final int MATERIAL_EFFICIENCY = 6;
                                                            final int RUNS = 7;

                                                            return Blueprint.accessQuery(acct, contid, maxresults,
                                                                                         reverse, at,
                                                                                         others[ITEM_ID],
                                                                                         others[LOCATION_ID],
                                                                                         others[LOCATION_FLAG],
                                                                                         others[TYPE_ID],
                                                                                         others[QUANTITY],
                                                                                         others[TIME_EFFICIENCY],
                                                                                         others[MATERIAL_EFFICIENCY],
                                                                                         others[RUNS]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_BLUEPRINTS : ESISyncEndpoint.CORP_BLUEPRINTS,
                                                                acct);
                                                          }
                                                        }, request, itemID, locationID, locationFlag, typeID, quantity,
                                                        timeEfficiency, materialEfficiency, runs);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("folderID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "folderID",
          defaultValue = "{ any: true }",
          value = "Bookmark folder ID selector") AttributeSelector folderID,
      @QueryParam("folderName") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "folderName",
          defaultValue = "{ any: true }",
          value = "Bookmark folder name selector") AttributeSelector folderName,
      @QueryParam("folderCreatorID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "folderCreatorID",
          defaultValue = "{ any: true }",
          value = "Bookmark folder creator ID selector") AttributeSelector folderCreatorID,
      @QueryParam("bookmarkID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "bookmarkID",
          defaultValue = "{ any: true }",
          value = "Bookmark ID selector") AttributeSelector bookmarkID,
      @QueryParam("bookmarkCreatorID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "bookmarkCreatorID",
          defaultValue = "{ any: true }",
          value = "Bookmark creator ID selector") AttributeSelector bookmarkCreatorID,
      @QueryParam("created") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "created",
          defaultValue = "{ any: true }",
          value = "Bookmark created selector") AttributeSelector created,
      @QueryParam("itemID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "itemID",
          defaultValue = "{ any: true }",
          value = "Bookmark item ID selector") AttributeSelector itemID,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Bookmark type ID selector") AttributeSelector typeID,
      @QueryParam("locationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "locationID",
          defaultValue = "{ any: true }",
          value = "Bookmark location ID selector") AttributeSelector locationID,
      @QueryParam("x") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "x",
          defaultValue = "{ any: true }",
          value = "Bookmark x coordinate selector") AttributeSelector x,
      @QueryParam("y") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "y",
          defaultValue = "{ any: true }",
          value = "Bookmark y coordinate selector") AttributeSelector y,
      @QueryParam("z") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "z",
          defaultValue = "{ any: true }",
          value = "Bookmark z coordinate selector") AttributeSelector z,
      @QueryParam("memo") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "memo",
          defaultValue = "{ any: true }",
          value = "Bookmark memo selector") AttributeSelector memo,
      @QueryParam("note") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "note",
          defaultValue = "{ any: true }",
          value = "Bookmark note selector") AttributeSelector note) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_BOOKMARKS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Bookmark>() {

                                                          @Override
                                                          public List<Bookmark> getList(SynchronizedEveAccount acct,
                                                                                        long contid, int maxresults,
                                                                                        boolean reverse,
                                                                                        AttributeSelector at,
                                                                                        AttributeSelector... others) throws IOException {
                                                            final int FOLDER_ID = 0;
                                                            final int FOLDER_NAME = 1;
                                                            final int FOLDER_CREATOR_ID = 2;
                                                            final int BOOKMARK_ID = 3;
                                                            final int BOOKMARK_CREATOR_ID = 4;
                                                            final int CREATED = 5;
                                                            final int ITEM_ID = 6;
                                                            final int TYPE_ID = 7;
                                                            final int LOCATION_ID = 8;
                                                            final int X = 9;
                                                            final int Y = 10;
                                                            final int Z = 11;
                                                            final int MEMO = 12;
                                                            final int NOTE = 13;

                                                            return Bookmark.accessQuery(acct, contid, maxresults,
                                                                                        reverse, at,
                                                                                        others[FOLDER_ID],
                                                                                        others[FOLDER_NAME],
                                                                                        others[FOLDER_CREATOR_ID],
                                                                                        others[BOOKMARK_ID],
                                                                                        others[BOOKMARK_CREATOR_ID],
                                                                                        others[CREATED],
                                                                                        others[ITEM_ID],
                                                                                        others[TYPE_ID],
                                                                                        others[LOCATION_ID],
                                                                                        others[X],
                                                                                        others[Y],
                                                                                        others[Z],
                                                                                        others[MEMO],
                                                                                        others[NOTE]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_BOOKMARKS : ESISyncEndpoint.CORP_BOOKMARKS,
                                                                acct);
                                                          }
                                                        }, request, folderID, folderName, folderCreatorID, bookmarkID,
                                                        bookmarkCreatorID, created,
                                                        itemID, typeID, locationID, x, y, z,
                                                        memo, note);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("list") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "list",
          defaultValue = "{ any: true }",
          value = "Contact list selector") AttributeSelector list,
      @QueryParam("contactID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "contactID",
          defaultValue = "{ any: true }",
          value = "Contact ID selector") AttributeSelector contactID,
      @QueryParam("standing") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "standing",
          defaultValue = "{ any: true }",
          value = "Contact standing selector") AttributeSelector standing,
      @QueryParam("contactType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "contactType",
          defaultValue = "{ any: true }",
          value = "Contact type selector") AttributeSelector contactType,
      @QueryParam("inWatchlist") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "inWatchlist",
          defaultValue = "{ any: true }",
          value = "Contact in watch list selector") AttributeSelector inWatchlist,
      @QueryParam("isBlocked") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "isBlocked",
          defaultValue = "{ any: true }",
          value = "Contact is blocked selector") AttributeSelector isBlocked,
      @QueryParam("labelID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "labelID",
          defaultValue = "{ any: true }",
          value = "Contact label selector") AttributeSelector labelID) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CONTACT_LIST,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Contact>() {

                                                          @Override
                                                          public List<Contact> getList(SynchronizedEveAccount acct,
                                                                                       long contid, int maxresults,
                                                                                       boolean reverse,
                                                                                       AttributeSelector at,
                                                                                       AttributeSelector... others) throws IOException {
                                                            final int LIST = 0;
                                                            final int CONTACT_ID = 1;
                                                            final int STANDING = 2;
                                                            final int CONTACT_TYPE = 3;
                                                            final int IN_WATCH_LIST = 4;
                                                            final int IS_BLOCKED = 5;
                                                            final int LABEL_ID = 6;

                                                            return Contact.accessQuery(acct, contid, maxresults,
                                                                                       reverse, at,
                                                                                       others[LIST],
                                                                                       others[CONTACT_ID],
                                                                                       others[STANDING],
                                                                                       others[CONTACT_TYPE],
                                                                                       others[IN_WATCH_LIST],
                                                                                       others[IS_BLOCKED],
                                                                                       others[LABEL_ID]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_CONTACTS : ESISyncEndpoint.CORP_CONTACTS,
                                                                acct);
                                                          }
                                                        }, request, list, contactID, standing, contactType, inWatchlist,
                                                        isBlocked, labelID);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("list") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "list",
          defaultValue = "{ any: true }",
          value = "Contact list selector") AttributeSelector list,
      @QueryParam("labelID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "labelID",
          defaultValue = "{ any: true }",
          value = "Contact label ID selector") AttributeSelector labelID,
      @QueryParam("name") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "name",
          defaultValue = "{ any: true }",
          value = "Contact label name selector") AttributeSelector name) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CONTACT_LIST,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<ContactLabel>() {

                                                          @Override
                                                          public List<ContactLabel> getList(SynchronizedEveAccount acct,
                                                                                            long contid, int maxresults,
                                                                                            boolean reverse,
                                                                                            AttributeSelector at,
                                                                                            AttributeSelector... others) throws IOException {
                                                            final int LIST = 0;
                                                            final int LABEL_ID = 1;
                                                            final int NAME = 2;

                                                            return ContactLabel.accessQuery(acct, contid, maxresults,
                                                                                            reverse, at,
                                                                                            others[LIST],
                                                                                            others[LABEL_ID],
                                                                                            others[NAME]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CHAR_CONTACTS,
                                                                                        acct);
                                                          }
                                                        }, request, list, labelID, name);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("contractID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "contractID",
          defaultValue = "{ any: true }",
          value = "Contract ID selector") AttributeSelector contractID,
      @QueryParam("issuerID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "issuerID",
          defaultValue = "{ any: true }",
          value = "Contract issuer ID selector") AttributeSelector issuerID,
      @QueryParam("issuerCorpID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "issuerCorpID",
          defaultValue = "{ any: true }",
          value = "Contract issuer corporation ID selector") AttributeSelector issuerCorpID,
      @QueryParam("assigneeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "assigneeID",
          defaultValue = "{ any: true }",
          value = "Contract assignee ID selector") AttributeSelector assigneeID,
      @QueryParam("acceptorID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "acceptorID",
          defaultValue = "{ any: true }",
          value = "Contract acceptor ID selector") AttributeSelector acceptorID,
      @QueryParam("startStationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "startStationID",
          defaultValue = "{ any: true }",
          value = "Contract start station ID selector") AttributeSelector startStationID,
      @QueryParam("endStationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "endStationID",
          defaultValue = "{ any: true }",
          value = "Contract end station ID selector") AttributeSelector endStationID,
      @QueryParam("type") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "type",
          defaultValue = "{ any: true }",
          value = "Contract type selector") AttributeSelector type,
      @QueryParam("status") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "status",
          defaultValue = "{ any: true }",
          value = "Contract status selector") AttributeSelector status,
      @QueryParam("title") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "title",
          defaultValue = "{ any: true }",
          value = "Contract title selector") AttributeSelector title,
      @QueryParam("forCorp") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "forCorp",
          defaultValue = "{ any: true }",
          value = "Contract for corporation selector") AttributeSelector forCorp,
      @QueryParam("availability") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "availability",
          defaultValue = "{ any: true }",
          value = "Contract availability selector") AttributeSelector availability,
      @QueryParam("dateIssued") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "dateIssued",
          defaultValue = "{ any: true }",
          value = "Contract date issued selector") AttributeSelector dateIssued,
      @QueryParam("dateExpired") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "dateExpired",
          defaultValue = "{ any: true }",
          value = "Contract date expired selector") AttributeSelector dateExpired,
      @QueryParam("dateAccepted") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "dateAccepted",
          defaultValue = "{ any: true }",
          value = "Contract date accepted selector") AttributeSelector dateAccepted,
      @QueryParam("numDays") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "numDays",
          defaultValue = "{ any: true }",
          value = "Contract duration (days) selector") AttributeSelector numDays,
      @QueryParam("dateCompleted") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "dateCompleted",
          defaultValue = "{ any: true }",
          value = "Contract date completed selector") AttributeSelector dateCompleted,
      @QueryParam("price") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "price",
          defaultValue = "{ any: true }",
          value = "Contract price selector") AttributeSelector price,
      @QueryParam("reward") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "reward",
          defaultValue = "{ any: true }",
          value = "Contract reward value selector") AttributeSelector reward,
      @QueryParam("collateral") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "collateral",
          defaultValue = "{ any: true }",
          value = "Contract collateral value selector") AttributeSelector collateral,
      @QueryParam("buyout") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "buyout",
          defaultValue = "{ any: true }",
          value = "Contract buyout price selector") AttributeSelector buyout,
      @QueryParam("volume") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "volume",
          defaultValue = "{ any: true }",
          value = "Contract volume selector") AttributeSelector volume) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CONTRACTS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Contract>() {

                                                          @Override
                                                          public List<Contract> getList(SynchronizedEveAccount acct,
                                                                                        long contid, int maxresults,
                                                                                        boolean reverse,
                                                                                        AttributeSelector at,
                                                                                        AttributeSelector... others) throws IOException {
                                                            final int CONTRACT_ID = 0;
                                                            final int ISSUER_ID = 1;
                                                            final int ISSUER_CORP_ID = 2;
                                                            final int ASSIGNEE_ID = 3;
                                                            final int ACCEPTOR_ID = 4;
                                                            final int START_STATION_ID = 5;
                                                            final int END_STATION_ID = 6;
                                                            final int TYPE = 7;
                                                            final int STATUS = 8;
                                                            final int TITLE = 9;
                                                            final int FOR_CORP = 10;
                                                            final int AVAILABILITY = 11;
                                                            final int DATE_ISSUED = 12;
                                                            final int DATE_EXPIRED = 13;
                                                            final int DATE_ACCEPTED = 14;
                                                            final int NUM_DAYS = 15;
                                                            final int DATE_COMPLETED = 16;
                                                            final int PRICE = 17;
                                                            final int REWARD = 18;
                                                            final int COLLATERAL = 19;
                                                            final int BUYOUT = 20;
                                                            final int VOLUME = 21;

                                                            return Contract.accessQuery(acct, contid, maxresults,
                                                                                        reverse, at,
                                                                                        others[CONTRACT_ID],
                                                                                        others[ISSUER_ID],
                                                                                        others[ISSUER_CORP_ID],
                                                                                        others[ASSIGNEE_ID],
                                                                                        others[ACCEPTOR_ID],
                                                                                        others[START_STATION_ID],
                                                                                        others[END_STATION_ID],
                                                                                        others[TYPE],
                                                                                        others[STATUS],
                                                                                        others[TITLE],
                                                                                        others[FOR_CORP],
                                                                                        others[AVAILABILITY],
                                                                                        others[DATE_ISSUED],
                                                                                        others[DATE_EXPIRED],
                                                                                        others[DATE_ACCEPTED],
                                                                                        others[NUM_DAYS],
                                                                                        others[DATE_COMPLETED],
                                                                                        others[PRICE],
                                                                                        others[REWARD],
                                                                                        others[COLLATERAL],
                                                                                        others[BUYOUT],
                                                                                        others[VOLUME]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_CONTRACTS : ESISyncEndpoint.CORP_CONTRACTS,
                                                                acct);
                                                          }
                                                        }, request, contractID, issuerID, issuerCorpID, assigneeID,
                                                        acceptorID, startStationID, endStationID,
                                                        type, status, title, forCorp, availability, dateIssued,
                                                        dateExpired, dateAccepted, numDays, dateCompleted,
                                                        price, reward, collateral, buyout, volume);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("bidID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "bidID",
          defaultValue = "{ any: true }",
          value = "Contract bid ID selector") AttributeSelector bidID,
      @QueryParam("contractID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "contractID",
          defaultValue = "{ any: true }",
          value = "Contract ID selector") AttributeSelector contractID,
      @QueryParam("bidderID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "bidderID",
          defaultValue = "{ any: true }",
          value = "Contract bid bidder ID selector") AttributeSelector bidderID,
      @QueryParam("dateBid") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "dateBid",
          defaultValue = "{ any: true }",
          value = "Contract bid date selector") AttributeSelector dateBid,
      @QueryParam("amount") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "amount",
          defaultValue = "{ any: true }",
          value = "Contract bid amount selector") AttributeSelector amount) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CONTRACTS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<ContractBid>() {

                                                          @Override
                                                          public List<ContractBid> getList(SynchronizedEveAccount acct,
                                                                                           long contid, int maxresults,
                                                                                           boolean reverse,
                                                                                           AttributeSelector at,
                                                                                           AttributeSelector... others) throws IOException {
                                                            final int BID_ID = 0;
                                                            final int CONTRACT_ID = 1;
                                                            final int BIDDER_ID = 2;
                                                            final int DATE_BID = 3;
                                                            final int AMOUNT = 4;

                                                            return ContractBid.accessQuery(acct, contid, maxresults,
                                                                                           reverse, at,
                                                                                           others[BID_ID],
                                                                                           others[CONTRACT_ID],
                                                                                           others[BIDDER_ID],
                                                                                           others[DATE_BID],
                                                                                           others[AMOUNT]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_CONTRACTS : ESISyncEndpoint.CORP_CONTRACTS,
                                                                acct);
                                                          }
                                                        }, request, bidID, contractID, bidderID, dateBid, amount);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("contractID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "contractID",
          defaultValue = "{ any: true }",
          value = "Contract ID selector") AttributeSelector contractID,
      @QueryParam("recordID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "recordID",
          defaultValue = "{ any: true }",
          value = "Contract item record ID selector") AttributeSelector recordID,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Contract item type ID selector") AttributeSelector typeID,
      @QueryParam("quantity") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "quantity",
          defaultValue = "{ any: true }",
          value = "Contract item quantity selector") AttributeSelector quantity,
      @QueryParam("rawQuantity") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "rawQuantity",
          defaultValue = "{ any: true }",
          value = "Contract item raw quantity selector") AttributeSelector rawQuantity,
      @QueryParam("singleton") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "singleton",
          defaultValue = "{ any: true }",
          value = "Contract item singleton selector") AttributeSelector singleton,
      @QueryParam("included") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "included",
          defaultValue = "{ any: true }",
          value = "Contract item included selector") AttributeSelector included) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CONTRACTS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<ContractItem>() {

                                                          @Override
                                                          public List<ContractItem> getList(SynchronizedEveAccount acct,
                                                                                            long contid, int maxresults,
                                                                                            boolean reverse,
                                                                                            AttributeSelector at,
                                                                                            AttributeSelector... others) throws IOException {
                                                            final int CONTRACT_ID = 0;
                                                            final int RECORD_ID = 1;
                                                            final int TYPE_ID = 2;
                                                            final int QUANTITY = 3;
                                                            final int RAW_QUANTITY = 4;
                                                            final int SINGLETON = 5;
                                                            final int INCLUDED = 6;

                                                            return ContractItem.accessQuery(acct, contid, maxresults,
                                                                                            reverse, at,
                                                                                            others[CONTRACT_ID],
                                                                                            others[RECORD_ID],
                                                                                            others[TYPE_ID],
                                                                                            others[QUANTITY],
                                                                                            others[RAW_QUANTITY],
                                                                                            others[SINGLETON],
                                                                                            others[INCLUDED]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_CONTRACTS : ESISyncEndpoint.CORP_CONTRACTS,
                                                                acct);
                                                          }
                                                        }, request, contractID, recordID, typeID, quantity, rawQuantity,
                                                        singleton, included);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("currentRank") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "currentRank",
          defaultValue = "{ any: true }",
          value = "Faction war statistics current rank selector") AttributeSelector currentRank,
      @QueryParam("enlisted") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "enlisted",
          defaultValue = "{ any: true }",
          value = "Faction war statistics enlisted indicator selector") AttributeSelector enlisted,
      @QueryParam("factionID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "factionID",
          defaultValue = "{ any: true }",
          value = "Faction war statistics faction ID selector") AttributeSelector factionID,
      @QueryParam("highestRank") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "highestRank",
          defaultValue = "{ any: true }",
          value = "Faction war statistics highest rank selector") AttributeSelector highestRank,
      @QueryParam("killsLastWeek") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "killsLastWeek",
          defaultValue = "{ any: true }",
          value = "Faction war statistics kill last week selector") AttributeSelector killsLastWeek,
      @QueryParam("killsTotal") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "killsTotal",
          defaultValue = "{ any: true }",
          value = "Faction war statistics total kills selector") AttributeSelector killsTotal,
      @QueryParam("killsYesterday") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "killsYesterday",
          defaultValue = "{ any: true }",
          value = "Faction war statistics kills yesterday selector") AttributeSelector killsYesterday,
      @QueryParam("pilots") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "pilots",
          defaultValue = "{ any: true }",
          value = "Faction war statistics pilot count selector") AttributeSelector pilots,
      @QueryParam("victoryPointsLastWeek") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "victoryPointsLastWeek",
          defaultValue = "{ any: true }",
          value = "Faction war statistics victory points last week selector") AttributeSelector victoryPointsLastWeek,
      @QueryParam("victoryPointsTotal") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "victoryPointsTotal",
          defaultValue = "{ any: true }",
          value = "Faction war statistics victory points total selector") AttributeSelector victoryPointsTotal,
      @QueryParam("victoryPointsYesterday") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "victoryPointsYesterday",
          defaultValue = "{ any: true }",
          value = "Faction war statistics victory points yesterday selector") AttributeSelector victoryPointsYesterday) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_FAC_WAR_STATS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<FacWarStats>() {

                                                          @Override
                                                          public List<FacWarStats> getList(SynchronizedEveAccount acct,
                                                                                           long contid, int maxresults,
                                                                                           boolean reverse,
                                                                                           AttributeSelector at,
                                                                                           AttributeSelector... others) throws IOException {
                                                            final int CURRENT_RANK = 0;
                                                            final int ENLISTED = 1;
                                                            final int FACTION_ID = 2;
                                                            final int HIGHEST_RANK = 3;
                                                            final int KILLS_LAST_WEEK = 4;
                                                            final int KILLS_TOTAL = 5;
                                                            final int KILLS_YESTERDAY = 6;
                                                            final int PILOTS = 7;
                                                            final int VICTORY_POINTS_LAST_WEEK = 8;
                                                            final int VICTORY_POINTS_TOTAL = 9;
                                                            final int VICTORY_POINTS_YESTERDAY = 10;

                                                            return FacWarStats.accessQuery(acct, contid, maxresults,
                                                                                           reverse, at,
                                                                                           others[CURRENT_RANK],
                                                                                           others[ENLISTED],
                                                                                           others[FACTION_ID],
                                                                                           others[HIGHEST_RANK],
                                                                                           others[KILLS_LAST_WEEK],
                                                                                           others[KILLS_TOTAL],
                                                                                           others[KILLS_YESTERDAY],
                                                                                           others[PILOTS],
                                                                                           others[VICTORY_POINTS_LAST_WEEK],
                                                                                           others[VICTORY_POINTS_TOTAL],
                                                                                           others[VICTORY_POINTS_YESTERDAY]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_FACTION_WAR : ESISyncEndpoint.CORP_FACTION_WAR,
                                                                acct);
                                                          }
                                                        }, request, currentRank, enlisted, factionID, highestRank,
                                                        killsLastWeek, killsTotal, killsYesterday,
                                                        pilots, victoryPointsLastWeek, victoryPointsTotal,
                                                        victoryPointsYesterday);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("jobID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "jobID",
          defaultValue = "{ any: true }",
          value = "Industry job ID selector") AttributeSelector jobID,
      @QueryParam("installerID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "installerID",
          defaultValue = "{ any: true }",
          value = "Industry job installer ID selector") AttributeSelector installerID,
      @QueryParam("facilityID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "facilityID",
          defaultValue = "{ any: true }",
          value = "Industry job facility ID selector") AttributeSelector facilityID,
      @QueryParam("stationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "stationID",
          defaultValue = "{ any: true }",
          value = "Industry job station ID selector") AttributeSelector stationID,
      @QueryParam("activityID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "activityID",
          defaultValue = "{ any: true }",
          value = "Industry job activity ID selector") AttributeSelector activityID,
      @QueryParam("blueprintID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "blueprintID",
          defaultValue = "{ any: true }",
          value = "Industry job blueprint ID selector") AttributeSelector blueprintID,
      @QueryParam("blueprintTypeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "blueprintTypeID",
          defaultValue = "{ any: true }",
          value = "Industry job blueprint type ID selector") AttributeSelector blueprintTypeID,
      @QueryParam("blueprintLocationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "blueprintLocationID",
          defaultValue = "{ any: true }",
          value = "Industry job blueprint location ID selector") AttributeSelector blueprintLocationID,
      @QueryParam("outputLocationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "outputLocationID",
          defaultValue = "{ any: true }",
          value = "Industry job output location ID selector") AttributeSelector outputLocationID,
      @QueryParam("runs") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "runs",
          defaultValue = "{ any: true }",
          value = "Industry job runs selector") AttributeSelector runs,
      @QueryParam("cost") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "cost",
          defaultValue = "{ any: true }",
          value = "Industry job cost selector") AttributeSelector cost,
      @QueryParam("licensedRuns") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "licensedRuns",
          defaultValue = "{ any: true }",
          value = "Industry job licensed runs selector") AttributeSelector licensedRuns,
      @QueryParam("probability") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "probability",
          defaultValue = "{ any: true }",
          value = "Industry job probability selector") AttributeSelector probability,
      @QueryParam("productTypeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "productTypeID",
          defaultValue = "{ any: true }",
          value = "Industry job product type ID selector") AttributeSelector productTypeID,
      @QueryParam("status") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "status",
          defaultValue = "{ any: true }",
          value = "Industry job status selector") AttributeSelector status,
      @QueryParam("timeInSeconds") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "timeInSeconds",
          defaultValue = "{ any: true }",
          value = "Industry job time in seconds selector") AttributeSelector timeInSeconds,
      @QueryParam("startDate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "startDate",
          defaultValue = "{ any: true }",
          value = "Industry job start date selector") AttributeSelector startDate,
      @QueryParam("endDate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "endDate",
          defaultValue = "{ any: true }",
          value = "Industry job end date selector") AttributeSelector endDate,
      @QueryParam("pauseDate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "pauseDate",
          defaultValue = "{ any: true }",
          value = "Industry job pause date selector") AttributeSelector pauseDate,
      @QueryParam("completedDate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "completedDate",
          defaultValue = "{ any: true }",
          value = "Industry job completed date selector") AttributeSelector completedDate,
      @QueryParam("completedCharacterID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "completedCharacterID",
          defaultValue = "{ any: true }",
          value = "Industry job completed character ID selector") AttributeSelector completedCharacterID,
      @QueryParam("successfulRuns") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "successfulRuns",
          defaultValue = "{ any: true }",
          value = "Industry job successful runs selector") AttributeSelector successfulRuns) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_INDUSTRY_JOBS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<IndustryJob>() {

                                                          @Override
                                                          public List<IndustryJob> getList(SynchronizedEveAccount acct,
                                                                                           long contid, int maxresults,
                                                                                           boolean reverse,
                                                                                           AttributeSelector at,
                                                                                           AttributeSelector... others) throws IOException {
                                                            final int JOB_ID = 0;
                                                            final int INSTALLER_ID = 1;
                                                            final int FACILITY_ID = 2;
                                                            final int STATION_ID = 3;
                                                            final int ACTIVITY_ID = 4;
                                                            final int BLUEPRINT_ID = 5;
                                                            final int BLUEPRINT_TYPE_ID = 6;
                                                            final int BLUEPRINT_LOCATION_ID = 7;
                                                            final int OUTPUT_LOCATION_ID = 8;
                                                            final int RUNS = 9;
                                                            final int COST = 10;
                                                            final int LICENSED_RUNS = 11;
                                                            final int PROBABILITY = 12;
                                                            final int PRODUCT_TYPE_ID = 13;
                                                            final int STATUS = 14;
                                                            final int TIME_IN_SECONDS = 15;
                                                            final int START_DATE = 16;
                                                            final int END_DATE = 17;
                                                            final int PAUSE_DATE = 18;
                                                            final int COMPLETED_DATE = 19;
                                                            final int COMPLETED_CHARACTER_ID = 20;
                                                            final int SUCCESSFUL_RUNS = 21;

                                                            return IndustryJob.accessQuery(acct, contid, maxresults,
                                                                                           reverse, at,
                                                                                           others[JOB_ID],
                                                                                           others[INSTALLER_ID],
                                                                                           others[FACILITY_ID],
                                                                                           others[STATION_ID],
                                                                                           others[ACTIVITY_ID],
                                                                                           others[BLUEPRINT_ID],
                                                                                           others[BLUEPRINT_TYPE_ID],
                                                                                           others[BLUEPRINT_LOCATION_ID],
                                                                                           others[OUTPUT_LOCATION_ID],
                                                                                           others[RUNS],
                                                                                           others[COST],
                                                                                           others[LICENSED_RUNS],
                                                                                           others[PROBABILITY],
                                                                                           others[PRODUCT_TYPE_ID],
                                                                                           others[STATUS],
                                                                                           others[TIME_IN_SECONDS],
                                                                                           others[START_DATE],
                                                                                           others[END_DATE],
                                                                                           others[PAUSE_DATE],
                                                                                           others[COMPLETED_DATE],
                                                                                           others[COMPLETED_CHARACTER_ID],
                                                                                           others[SUCCESSFUL_RUNS]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_INDUSTRY : ESISyncEndpoint.CORP_INDUSTRY,
                                                                acct);
                                                          }
                                                        }, request, jobID, installerID, facilityID, stationID,
                                                        activityID, blueprintID, blueprintTypeID, blueprintLocationID,
                                                        outputLocationID, runs, cost, licensedRuns, probability,
                                                        productTypeID,
                                                        status, timeInSeconds, startDate, endDate, pauseDate,
                                                        completedDate,
                                                        completedCharacterID, successfulRuns);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("killID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "killID",
          defaultValue = "{ any: true }",
          value = "Kill ID selector") AttributeSelector killID,
      @QueryParam("killTime") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "killTime",
          defaultValue = "{ any: true }",
          value = "Kill time selector") AttributeSelector killTime,
      @QueryParam("moonID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "moonID",
          defaultValue = "{ any: true }",
          value = "Kill moon ID selector") AttributeSelector moonID,
      @QueryParam("solarSystemID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "solarSystemID",
          defaultValue = "{ any: true }",
          value = "Kill solar system ID selector") AttributeSelector solarSystemID,
      @QueryParam("warID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "warID",
          defaultValue = "{ any: true }",
          value = "War ID selector") AttributeSelector warID) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_KILL_LOG,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Kill>() {

                                                          @Override
                                                          public List<Kill> getList(SynchronizedEveAccount acct,
                                                                                    long contid, int maxresults,
                                                                                    boolean reverse,
                                                                                    AttributeSelector at,
                                                                                    AttributeSelector... others) throws IOException {
                                                            final int KILL_ID = 0;
                                                            final int KILL_TIME = 1;
                                                            final int MOON_ID = 2;
                                                            final int SOLAR_SYSTEM_ID = 3;
                                                            final int WAR_ID = 4;

                                                            return Kill.accessQuery(acct, contid, maxresults, reverse,
                                                                                    at,
                                                                                    others[KILL_ID],
                                                                                    others[KILL_TIME],
                                                                                    others[MOON_ID],
                                                                                    others[SOLAR_SYSTEM_ID],
                                                                                    others[WAR_ID]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_KILL_MAIL : ESISyncEndpoint.CORP_KILL_MAIL,
                                                                acct);
                                                          }
                                                        }, request, killID, killTime, moonID, solarSystemID, warID);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("killID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "killID",
          defaultValue = "{ any: true }",
          value = "Kill ID selector") AttributeSelector killID,
      @QueryParam("attackerCharacterID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "attackerCharacterID",
          defaultValue = "{ any: true }",
          value = "Kill attacker character ID selector") AttributeSelector attackerCharacterID,
      @QueryParam("allianceID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "allianceID",
          defaultValue = "{ any: true }",
          value = "Kill attacker alliance ID selector") AttributeSelector allianceID,
      @QueryParam("attackerCorporationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "attackerCorporationID",
          defaultValue = "{ any: true }",
          value = "Kill attacker corporation ID selector") AttributeSelector attackerCorporationID,
      @QueryParam("damageDone") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "damageDone",
          defaultValue = "{ any: true }",
          value = "Kill attacker damage done selector") AttributeSelector damageDone,
      @QueryParam("factionID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "factionID",
          defaultValue = "{ any: true }",
          value = "Kill attacker faction ID selector") AttributeSelector factionID,
      @QueryParam("securityStatus") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "securityStatus",
          defaultValue = "{ any: true }",
          value = "Kill attacker security status selector") AttributeSelector securityStatus,
      @QueryParam("shipTypeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "shipTypeID",
          defaultValue = "{ any: true }",
          value = "Kill attacker ship type ID selector") AttributeSelector shipTypeID,
      @QueryParam("weaponTypeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "weaponTypeID",
          defaultValue = "{ any: true }",
          value = "Kill attacker weapon type ID selector") AttributeSelector weaponTypeID,
      @QueryParam("finalBlow") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "finalBlow",
          defaultValue = "{ any: true }",
          value = "Kill attacker final blow selector") AttributeSelector finalBlow) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_KILL_LOG,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<KillAttacker>() {

                                                          @Override
                                                          public List<KillAttacker> getList(SynchronizedEveAccount acct,
                                                                                            long contid, int maxresults,
                                                                                            boolean reverse,
                                                                                            AttributeSelector at,
                                                                                            AttributeSelector... others) throws IOException {
                                                            final int KILL_ID = 0;
                                                            final int ATTACKER_CHARACTER_ID = 1;
                                                            final int ALLIANCE_ID = 2;
                                                            final int ATTACKER_CORPORATION_ID = 3;
                                                            final int DAMAGE_DONE = 4;
                                                            final int FACTION_ID = 5;
                                                            final int SECURITY_STATUS = 6;
                                                            final int SHIP_TYPE_ID = 7;
                                                            final int WEAPON_TYPE_ID = 8;
                                                            final int FINAL_BLOW = 9;

                                                            return KillAttacker.accessQuery(acct, contid, maxresults,
                                                                                            reverse, at,
                                                                                            others[KILL_ID],
                                                                                            others[ATTACKER_CHARACTER_ID],
                                                                                            others[ALLIANCE_ID],
                                                                                            others[ATTACKER_CORPORATION_ID],
                                                                                            others[DAMAGE_DONE],
                                                                                            others[FACTION_ID],
                                                                                            others[SECURITY_STATUS],
                                                                                            others[SHIP_TYPE_ID],
                                                                                            others[WEAPON_TYPE_ID],
                                                                                            others[FINAL_BLOW]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_KILL_MAIL : ESISyncEndpoint.CORP_KILL_MAIL,
                                                                acct);
                                                          }
                                                        }, request, killID, attackerCharacterID, allianceID,
                                                        attackerCorporationID, damageDone, factionID,
                                                        securityStatus, shipTypeID, weaponTypeID, finalBlow);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("killID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "killID",
          defaultValue = "{ any: true }",
          value = "Kill ID selector") AttributeSelector killID,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Kill item type ID selector") AttributeSelector typeID,
      @QueryParam("flag") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "flag",
          defaultValue = "{ any: true }",
          value = "Kill item flag selector") AttributeSelector flag,
      @QueryParam("qtyDestroyed") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "qtyDestroyed",
          defaultValue = "{ any: true }",
          value = "Kill item quantity destroyed selector") AttributeSelector qtyDestroyed,
      @QueryParam("qtyDropped") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "qtyDropped",
          defaultValue = "{ any: true }",
          value = "Kill item quantity dropped selector") AttributeSelector qtyDropped,
      @QueryParam("singleton") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "singleton",
          defaultValue = "{ any: true }",
          value = "Kill item singleton selector") AttributeSelector singleton,
      @QueryParam("sequence") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "sequence",
          defaultValue = "{ any: true }",
          value = "Kill item sequence selector") AttributeSelector sequence,
      @QueryParam("containerSequence") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "containerSequence",
          defaultValue = "{ any: true }",
          value = "Kill item container sequence selector") AttributeSelector containerSequence) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_KILL_LOG,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<KillItem>() {

                                                          @Override
                                                          public List<KillItem> getList(SynchronizedEveAccount acct,
                                                                                        long contid, int maxresults,
                                                                                        boolean reverse,
                                                                                        AttributeSelector at,
                                                                                        AttributeSelector... others) throws IOException {
                                                            final int KILL_ID = 0;
                                                            final int TYPE_ID = 1;
                                                            final int FLAG = 2;
                                                            final int QTY_DESTROYED = 3;
                                                            final int QTY_DROPPED = 4;
                                                            final int SINGLETON = 5;
                                                            final int SEQUENCE = 6;
                                                            final int CONTAINER_SEQUENCE = 7;

                                                            return KillItem.accessQuery(acct, contid, maxresults,
                                                                                        reverse, at,
                                                                                        others[KILL_ID],
                                                                                        others[TYPE_ID],
                                                                                        others[FLAG],
                                                                                        others[QTY_DESTROYED],
                                                                                        others[QTY_DROPPED],
                                                                                        others[SINGLETON],
                                                                                        others[SEQUENCE],
                                                                                        others[CONTAINER_SEQUENCE]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_KILL_MAIL : ESISyncEndpoint.CORP_KILL_MAIL,
                                                                acct);
                                                          }
                                                        }, request, killID, typeID, flag, qtyDestroyed, qtyDropped,
                                                        singleton, sequence, containerSequence);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("killID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "killID",
          defaultValue = "{ any: true }",
          value = "Kill ID selector") AttributeSelector killID,
      @QueryParam("allianceID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "allianceID",
          defaultValue = "{ any: true }",
          value = "Kill victim alliance ID selector") AttributeSelector allianceID,
      @QueryParam("killCharacterID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "killCharacterID",
          defaultValue = "{ any: true }",
          value = "Kill victim character ID selector") AttributeSelector killCharacterID,
      @QueryParam("killCorporationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "killCorporationID",
          defaultValue = "{ any: true }",
          value = "Kill victim corporation ID selector") AttributeSelector killCorporationID,
      @QueryParam("damageTaken") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "damageTaken",
          defaultValue = "{ any: true }",
          value = "Kill victim damage taken selector") AttributeSelector damageTaken,
      @QueryParam("factionID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "factionID",
          defaultValue = "{ any: true }",
          value = "Kill victim faction ID selector") AttributeSelector factionID,
      @QueryParam("shipTypeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "shipTypeID",
          defaultValue = "{ any: true }",
          value = "Kill victim ship type ID selector") AttributeSelector shipTypeID,
      @QueryParam("x") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "x",
          defaultValue = "{ any: true }",
          value = "Kill X position selector") AttributeSelector x,
      @QueryParam("y") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "y",
          defaultValue = "{ any: true }",
          value = "Kill Y position selector") AttributeSelector y,
      @QueryParam("z") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "z",
          defaultValue = "{ any: true }",
          value = "Kill Z position selector") AttributeSelector z) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_KILL_LOG,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<KillVictim>() {

                                                          @Override
                                                          public List<KillVictim> getList(SynchronizedEveAccount acct,
                                                                                          long contid, int maxresults,
                                                                                          boolean reverse,
                                                                                          AttributeSelector at,
                                                                                          AttributeSelector... others) throws IOException {
                                                            final int KILL_ID = 0;
                                                            final int ALLIANCE_ID = 1;
                                                            final int KILL_CHARACTER_ID = 2;
                                                            final int KILL_CORPORATION_ID = 3;
                                                            final int DAMAGE_TAKEN = 4;
                                                            final int FACTION_ID = 5;
                                                            final int SHIP_TYPE_ID = 6;
                                                            final int X = 7;
                                                            final int Y = 8;
                                                            final int Z = 9;

                                                            return KillVictim.accessQuery(acct, contid, maxresults,
                                                                                          reverse, at,
                                                                                          others[KILL_ID],
                                                                                          others[ALLIANCE_ID],
                                                                                          others[KILL_CHARACTER_ID],
                                                                                          others[KILL_CORPORATION_ID],
                                                                                          others[DAMAGE_TAKEN],
                                                                                          others[FACTION_ID],
                                                                                          others[SHIP_TYPE_ID],
                                                                                          others[X],
                                                                                          others[Y],
                                                                                          others[Z]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_KILL_MAIL : ESISyncEndpoint.CORP_KILL_MAIL,
                                                                acct);
                                                          }
                                                        }, request, killID, allianceID, killCharacterID,
                                                        killCorporationID, damageTaken, factionID, shipTypeID, x, y, z);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("itemID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "itemID",
          defaultValue = "{ any: true }",
          value = "Location item ID selector") AttributeSelector itemID,
      @QueryParam("itemName") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "itemName",
          defaultValue = "{ any: true }",
          value = "Location item name selector") AttributeSelector itemName,
      @QueryParam("x") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "x",
          defaultValue = "{ any: true }",
          value = "Location X position selector") AttributeSelector x,
      @QueryParam("y") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "y",
          defaultValue = "{ any: true }",
          value = "Location Y position selector") AttributeSelector y,
      @QueryParam("z") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "z",
          defaultValue = "{ any: true }",
          value = "Location Z position selector") AttributeSelector z) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_LOCATIONS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Location>() {

                                                          @Override
                                                          public List<Location> getList(SynchronizedEveAccount acct,
                                                                                        long contid, int maxresults,
                                                                                        boolean reverse,
                                                                                        AttributeSelector at,
                                                                                        AttributeSelector... others) throws IOException {
                                                            final int ITEM_ID = 0;
                                                            final int ITEM_NAME = 1;
                                                            final int X = 2;
                                                            final int Y = 3;
                                                            final int Z = 4;

                                                            return Location.accessQuery(acct, contid, maxresults,
                                                                                        reverse, at,
                                                                                        others[ITEM_ID],
                                                                                        others[ITEM_NAME],
                                                                                        others[X],
                                                                                        others[Y],
                                                                                        others[Z]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_ASSETS : ESISyncEndpoint.CORP_ASSETS,
                                                                acct);
                                                          }
                                                        }, request, itemID, itemName, x, y, z);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("orderID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "orderID",
          defaultValue = "{ any: true }",
          value = "Market order ID selector") AttributeSelector orderID,
      @QueryParam("walletDivision") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "walletDivision",
          defaultValue = "{ any: true }",
          value = "Market order wallet division selector") AttributeSelector walletDivision,
      @QueryParam("bid") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "bid",
          defaultValue = "{ any: true }",
          value = "Market order bid indicator selector") AttributeSelector bid,
      @QueryParam("charID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "charID",
          defaultValue = "{ any: true }",
          value = "Market order character ID selector") AttributeSelector charID,
      @QueryParam("duration") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "duration",
          defaultValue = "{ any: true }",
          value = "Market order duration selector") AttributeSelector duration,
      @QueryParam("escrow") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "escrow",
          defaultValue = "{ any: true }",
          value = "Market order escrow selector") AttributeSelector escrow,
      @QueryParam("issued") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "issued",
          defaultValue = "{ any: true }",
          value = "Market order issue date selector") AttributeSelector issued,
      @QueryParam("issuedBy") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "issuedBy",
          defaultValue = "{ any: true }",
          value = "Market order issued by selector") AttributeSelector issuedBy,
      @QueryParam("minVolume") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "minVolume",
          defaultValue = "{ any: true }",
          value = "Market order min volume selector") AttributeSelector minVolume,
      @QueryParam("orderState") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "orderState",
          defaultValue = "{ any: true }",
          value = "Market order state selector") AttributeSelector orderState,
      @QueryParam("price") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "price",
          defaultValue = "{ any: true }",
          value = "Market order price selector") AttributeSelector price,
      @QueryParam("orderRange") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "orderRange",
          defaultValue = "{ any: true }",
          value = "Market order range selector") AttributeSelector orderRange,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Market order type ID selector") AttributeSelector typeID,
      @QueryParam("volEntered") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "volEntered",
          defaultValue = "{ any: true }",
          value = "Market order volume entered selector") AttributeSelector volEntered,
      @QueryParam("volRemaining") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "volRemaining",
          defaultValue = "{ any: true }",
          value = "Market order volume remaining selector") AttributeSelector volRemaining,
      @QueryParam("regionID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "regionID",
          defaultValue = "{ any: true }",
          value = "Market order region ID selector") AttributeSelector regionID,
      @QueryParam("locationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "locationID",
          defaultValue = "{ any: true }",
          value = "Market order location ID selector") AttributeSelector locationID,
      @QueryParam("isCorp") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "isCorp",
          defaultValue = "{ any: true }",
          value = "Market order is corporation selector") AttributeSelector isCorp) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_MARKET_ORDERS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<MarketOrder>() {

                                                          @Override
                                                          public List<MarketOrder> getList(SynchronizedEveAccount acct,
                                                                                           long contid, int maxresults,
                                                                                           boolean reverse,
                                                                                           AttributeSelector at,
                                                                                           AttributeSelector... others) throws IOException {
                                                            final int ORDER_ID = 0;
                                                            final int WALLET_DIVISION = 1;
                                                            final int BID = 2;
                                                            final int CHAR_ID = 3;
                                                            final int DURATION = 4;
                                                            final int ESCROW = 5;
                                                            final int ISSUED = 6;
                                                            final int ISSUED_BY = 7;
                                                            final int MIN_VOLUME = 8;
                                                            final int ORDER_STATE = 9;
                                                            final int PRICE = 10;
                                                            final int ORDER_RANGE = 11;
                                                            final int TYPE_ID = 12;
                                                            final int VOL_ENTERED = 13;
                                                            final int VOL_REMAINING = 14;
                                                            final int REGION_ID = 15;
                                                            final int LOCATION_ID = 16;
                                                            final int IS_CORP = 17;

                                                            return MarketOrder.accessQuery(acct, contid, maxresults,
                                                                                           reverse, at,
                                                                                           others[ORDER_ID],
                                                                                           others[WALLET_DIVISION],
                                                                                           others[BID],
                                                                                           others[CHAR_ID],
                                                                                           others[DURATION],
                                                                                           others[ESCROW],
                                                                                           others[ISSUED],
                                                                                           others[ISSUED_BY],
                                                                                           others[MIN_VOLUME],
                                                                                           others[ORDER_STATE],
                                                                                           others[PRICE],
                                                                                           others[ORDER_RANGE],
                                                                                           others[TYPE_ID],
                                                                                           others[VOL_ENTERED],
                                                                                           others[VOL_REMAINING],
                                                                                           others[REGION_ID],
                                                                                           others[LOCATION_ID],
                                                                                           others[IS_CORP]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_MARKET : ESISyncEndpoint.CORP_MARKET,
                                                                acct);
                                                          }
                                                        }, request, orderID, walletDivision, bid, charID, duration,
                                                        escrow, issued, issuedBy, minVolume, orderState, price, orderRange,
                                                        typeID, volEntered, volRemaining, regionID, locationID, isCorp);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("standingEntity") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "standingEntity",
          defaultValue = "{ any: true }",
          value = "Standing entity selector") AttributeSelector standingEntity,
      @QueryParam("fromID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "fromID",
          defaultValue = "{ any: true }",
          value = "Standing from ID selector") AttributeSelector fromID,
      @QueryParam("standing") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "standing",
          defaultValue = "{ any: true }",
          value = "Standing value selector") AttributeSelector standing) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_STANDINGS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Standing>() {

                                                          @Override
                                                          public List<Standing> getList(SynchronizedEveAccount acct,
                                                                                        long contid, int maxresults,
                                                                                        boolean reverse,
                                                                                        AttributeSelector at,
                                                                                        AttributeSelector... others) throws IOException {
                                                            final int STANDING_ENTITY = 0;
                                                            final int FROM_ID = 1;
                                                            final int STANDING = 2;

                                                            return Standing.accessQuery(acct, contid, maxresults,
                                                                                        reverse, at,
                                                                                        others[STANDING_ENTITY],
                                                                                        others[FROM_ID],
                                                                                        others[STANDING]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_STANDINGS : ESISyncEndpoint.CORP_STANDINGS,
                                                                acct);
                                                          }
                                                        }, request, standingEntity, fromID, standing);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("division") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "division",
          defaultValue = "{ any: true }",
          value = "Wallet journal division selector") AttributeSelector division,
      @QueryParam("refID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "refID",
          defaultValue = "{ any: true }",
          value = "Journal entry ref ID selector") AttributeSelector refID,
      @QueryParam("date") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "date",
          defaultValue = "{ any: true }",
          value = "Journal entry date selector") AttributeSelector date,
      @QueryParam("refType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "refType",
          defaultValue = "{ any: true }",
          value = "Journal entry ref type selector") AttributeSelector refType,
      @QueryParam("firstPartyID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "firstPartyID",
          defaultValue = "{ any: true }",
          value = "Journal entry first party ID selector") AttributeSelector firstPartyID,
      @QueryParam("secondPartyID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "secondPartyID",
          defaultValue = "{ any: true }",
          value = "Journal entry second party ID selector") AttributeSelector secondPartyID,
      @QueryParam("argName1") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "argName1",
          defaultValue = "{ any: true }",
          value = "Journal entry argument name selector") AttributeSelector argName1,
      @QueryParam("argID1") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "argID1",
          defaultValue = "{ any: true }",
          value = "Journal entry argument ID selector") AttributeSelector argID1,
      @QueryParam("amount") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "amount",
          defaultValue = "{ any: true }",
          value = "Journal entry amount selector") AttributeSelector amount,
      @QueryParam("balance") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "balance",
          defaultValue = "{ any: true }",
          value = "Journal entry balance selector") AttributeSelector balance,
      @QueryParam("reason") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "reason",
          defaultValue = "{ any: true }",
          value = "Journal entry reason selector") AttributeSelector reason,
      @QueryParam("taxReceiverID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "taxReceiverID",
          defaultValue = "{ any: true }",
          value = "Journal entry tax receiver ID selector") AttributeSelector taxReceiverID,
      @QueryParam("taxAmount") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "taxAmount",
          defaultValue = "{ any: true }",
          value = "Journal entry tax amount selector") AttributeSelector taxAmount,
      @QueryParam("contextID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "contextID",
          defaultValue = "{ any: true }",
          value = "Context ID selector") AttributeSelector contextID,
      @QueryParam("contextType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "contextType",
          defaultValue = "{ any: true }",
          value = "Context type selector") AttributeSelector contextType,
      @QueryParam("description") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "description",
          defaultValue = "{ any: true }",
          value = "Description selector") AttributeSelector description) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_WALLET_JOURNAL,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<WalletJournal>() {

                                                          @Override
                                                          public List<WalletJournal> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int DIVISION = 0;
                                                            final int REF_ID = 1;
                                                            final int DATE = 2;
                                                            final int REF_TYPE = 3;
                                                            final int FIRST_PARTY_ID = 4;
                                                            final int SECOND_PARTY_ID = 5;
                                                            final int ARG_NAME_1 = 6;
                                                            final int ARG_ID_1 = 7;
                                                            final int AMOUNT = 8;
                                                            final int BALANCE = 9;
                                                            final int REASON = 10;
                                                            final int TAX_RECEIVER_ID = 11;
                                                            final int TAX_AMOUNT = 12;
                                                            final int CONTEXT_ID = 13;
                                                            final int CONTEXT_TYPE = 14;
                                                            final int DESCRIPTION = 15;
                                                            return WalletJournal.accessQuery(acct, contid,
                                                                                             maxresults, reverse,
                                                                                             at,
                                                                                             others[DIVISION],
                                                                                             others[REF_ID],
                                                                                             others[DATE],
                                                                                             others[REF_TYPE],
                                                                                             others[FIRST_PARTY_ID],
                                                                                             others[SECOND_PARTY_ID],
                                                                                             others[ARG_NAME_1],
                                                                                             others[ARG_ID_1],
                                                                                             others[AMOUNT],
                                                                                             others[BALANCE],
                                                                                             others[REASON],
                                                                                             others[TAX_RECEIVER_ID],
                                                                                             others[TAX_AMOUNT],
                                                                                             others[CONTEXT_ID],
                                                                                             others[CONTEXT_TYPE],
                                                                                             others[DESCRIPTION]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_WALLET_JOURNAL : ESISyncEndpoint.CORP_WALLET_JOURNAL,
                                                                acct);
                                                          }
                                                        }, request, division, refID, date, refType, firstPartyID,
                                                        secondPartyID, argName1, argID1, amount, balance, reason,
                                                        taxReceiverID, taxAmount, contextID, contextType,
                                                        description);
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
          defaultValue = "{ values: [ \"9223372036854775806\" ] }",
          value = "Model lifeline selector (defaults to current live data)") AttributeSelector at,
      @QueryParam("contid") @DefaultValue("-1") @ApiParam(
          name = "contid",
          defaultValue = "-1",
          value = "Continuation ID for paged results") long contid,
      @QueryParam("maxresults") @DefaultValue("1000") @ApiParam(
          name = "maxresults",
          defaultValue = "1000",
          value = "Maximum number of results to retrieve") int maxresults,
      @QueryParam("reverse") @DefaultValue("false") @ApiParam(
          name = "reverse",
          defaultValue = "false",
          value = "If true, page backwards (results less than contid) with results in descending order (by cid)") boolean reverse,
      @QueryParam("division") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "division",
          defaultValue = "{ any: true }",
          value = "Wallet division selector") AttributeSelector division,
      @QueryParam("transactionID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "transactionID",
          defaultValue = "{ any: true }",
          value = "Transaction ID selector") AttributeSelector transactionID,
      @QueryParam("date") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "date",
          defaultValue = "{ any: true }",
          value = "Transaction date selector") AttributeSelector date,
      @QueryParam("quantity") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "quantity",
          defaultValue = "{ any: true }",
          value = "Transaction quantity selector") AttributeSelector quantity,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Transaction type ID selector") AttributeSelector typeID,
      @QueryParam("price") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "price",
          defaultValue = "{ any: true }",
          value = "Transaction price selector") AttributeSelector price,
      @QueryParam("clientID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "clientID",
          defaultValue = "{ any: true }",
          value = "Transaction client ID selector") AttributeSelector clientID,
      @QueryParam("locationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "locationID",
          defaultValue = "{ any: true }",
          value = "Transaction location ID selector") AttributeSelector locationID,
      @QueryParam("isBuy") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "isBuy",
          defaultValue = "{ any: true }",
          value = "Transaction isBuy selector") AttributeSelector isBuy,
      @QueryParam("isPersonal") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "isPersonal",
          defaultValue = "{ any: true }",
          value = "Transaction isPersonal selector") AttributeSelector isPersonal,
      @QueryParam("journalTransactionID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "journalTransactionID",
          defaultValue = "{ any: true }",
          value = "Journal transaction ID selector") AttributeSelector journalTransactionID) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_WALLET_TRANSACTIONS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<WalletTransaction>() {

                                                          @Override
                                                          public List<WalletTransaction> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int DIVISION = 0;
                                                            final int TRANSACTION_ID = 1;
                                                            final int DATE = 2;
                                                            final int QUANTITY = 3;
                                                            final int TYPE_ID = 4;
                                                            final int PRICE = 5;
                                                            final int CLIENT_ID = 6;
                                                            final int LOCATION_ID = 7;
                                                            final int IS_BUY = 8;
                                                            final int IS_PERSONAL = 9;
                                                            final int JOURNAL_TRANSACTION_ID = 10;
                                                            return WalletTransaction.accessQuery(acct, contid,
                                                                                                 maxresults, reverse,
                                                                                                 at, others[DIVISION],
                                                                                                 others[TRANSACTION_ID],
                                                                                                 others[DATE],
                                                                                                 others[QUANTITY],
                                                                                                 others[TYPE_ID],
                                                                                                 others[PRICE],
                                                                                                 others[CLIENT_ID],
                                                                                                 others[LOCATION_ID],
                                                                                                 others[IS_BUY],
                                                                                                 others[IS_PERSONAL],
                                                                                                 others[JOURNAL_TRANSACTION_ID]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                acct.isCharacterType() ? ESISyncEndpoint.CHAR_WALLET_TRANSACTIONS : ESISyncEndpoint.CORP_WALLET_TRANSACTIONS,
                                                                acct);
                                                          }
                                                        }, request, division, transactionID, date, quantity, typeID,
                                                        price, clientID, locationID, isBuy,
                                                        isPersonal, journalTransactionID);
  }

}
