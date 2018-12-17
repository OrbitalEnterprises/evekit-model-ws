package enterprises.orbital.evekit.ws.corporation;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.ESISyncEndpoint;
import enterprises.orbital.evekit.model.corporation.*;
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
      @QueryParam("logTime") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "logTime",
          defaultValue = "{ any: true }",
          value = "Corporation container log time selector") AttributeSelector logTime,
      @QueryParam("action") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "action",
          defaultValue = "{ any: true }",
          value = "Corporation container log action selector") AttributeSelector action,
      @QueryParam("characterID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "characterID",
          defaultValue = "{ any: true }",
          value = "Corporation container log character ID selector") AttributeSelector characterID,
      @QueryParam("locationFlag") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "locationFlag",
          defaultValue = "{ any: true }",
          value = "Corporation container log location flag selector") AttributeSelector locationFlag,
      @QueryParam("containerID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "containerID",
          defaultValue = "{ any: true }",
          value = "Corporation container log container ID selector") AttributeSelector containerID,
      @QueryParam("containerTypeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "containerTypeID",
          defaultValue = "{ any: true }",
          value = "Corporation container log container type ID selector") AttributeSelector containerTypeID,
      @QueryParam("locationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "locationID",
          defaultValue = "{ any: true }",
          value = "Corporation container log location ID selector") AttributeSelector locationID,
      @QueryParam("newConfiguration") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "newConfiguration",
          defaultValue = "{ any: true }",
          value = "Corporation container log new configuration selector") AttributeSelector newConfiguration,
      @QueryParam("oldConfiguration") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "oldConfiguration",
          defaultValue = "{ any: true }",
          value = "Corporation container log old configuration selector") AttributeSelector oldConfiguration,
      @QueryParam("passwordType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "passwordType",
          defaultValue = "{ any: true }",
          value = "Corporation container log password type selector") AttributeSelector passwordType,
      @QueryParam("quantity") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "quantity",
          defaultValue = "{ any: true }",
          value = "Corporation container log quantity selector") AttributeSelector quantity,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Corporation container log type ID selector") AttributeSelector typeID) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_CONTAINER_LOG,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<ContainerLog>() {

                                                          @Override
                                                          public List<ContainerLog> getList(SynchronizedEveAccount acct,
                                                                                            long contid, int maxresults,
                                                                                            boolean reverse,
                                                                                            AttributeSelector at,
                                                                                            AttributeSelector... others) throws IOException {
                                                            final int LOG_TIME = 0;
                                                            final int ACTION = 1;
                                                            final int CHARACTER_ID = 2;
                                                            final int LOCATION_FLAG = 3;
                                                            final int CONTAINER_ID = 4;
                                                            final int CONTAINER_TYPE_ID = 5;
                                                            final int LOCATION_ID = 6;
                                                            final int NEW_CONFIGURATION = 7;
                                                            final int OLD_CONFIGURATION = 8;
                                                            final int PASSWORD_TYPE = 9;
                                                            final int QUANTITY = 10;
                                                            final int TYPE_ID = 11;

                                                            return ContainerLog.accessQuery(acct, contid, maxresults,
                                                                                            reverse, at,
                                                                                            others[LOG_TIME],
                                                                                            others[ACTION],
                                                                                            others[CHARACTER_ID],
                                                                                            others[LOCATION_FLAG],
                                                                                            others[CONTAINER_ID],
                                                                                            others[CONTAINER_TYPE_ID],
                                                                                            others[LOCATION_ID],
                                                                                            others[NEW_CONFIGURATION],
                                                                                            others[OLD_CONFIGURATION],
                                                                                            others[PASSWORD_TYPE],
                                                                                            others[QUANTITY],
                                                                                            others[TYPE_ID]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                ESISyncEndpoint.CORP_CONTAINER_LOGS, acct);
                                                          }
                                                        }, request, logTime, action, characterID, locationFlag,
                                                        containerID, containerTypeID, locationID, newConfiguration,
                                                        oldConfiguration, passwordType, quantity, typeID);
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
      @QueryParam("medalID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "medalID",
          defaultValue = "{ any: true }",
          value = "Corporation medal ID selector") AttributeSelector medalID,
      @QueryParam("description") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "description",
          defaultValue = "{ any: true }",
          value = "Corporation medal description selector") AttributeSelector description,
      @QueryParam("title") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "title",
          defaultValue = "{ any: true }",
          value = "Corporation medal title selector") AttributeSelector title,
      @QueryParam("created") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "created",
          defaultValue = "{ any: true }",
          value = "Corporation medal created date selector") AttributeSelector created,
      @QueryParam("creatorID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "creatorID",
          defaultValue = "{ any: true }",
          value = "Corporation medal creator ID selector") AttributeSelector creatorID) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_CORPORATION_MEDALS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CorporationMedal>() {

                                                          @Override
                                                          public List<CorporationMedal> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int MEDAL_ID = 0;
                                                            final int DESCRIPTION = 1;
                                                            final int TITLE = 2;
                                                            final int CREATED = 3;
                                                            final int CREATOR_ID = 4;

                                                            return CorporationMedal.accessQuery(acct, contid,
                                                                                                maxresults, reverse, at,
                                                                                                others[MEDAL_ID],
                                                                                                others[DESCRIPTION],
                                                                                                others[TITLE],
                                                                                                others[CREATED],
                                                                                                others[CREATOR_ID]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CORP_MEDALS,
                                                                                        acct);
                                                          }
                                                        }, request, medalID, description, title, created, creatorID);
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
      @QueryParam("medalID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "medalID",
          defaultValue = "{ any: true }",
          value = "Member medal ID selector") AttributeSelector medalID,
      @QueryParam("characterID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "characterID",
          defaultValue = "{ any: true }",
          value = "Member medal character ID selector") AttributeSelector characterID,
      @QueryParam("issued") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "issued",
          defaultValue = "{ any: true }",
          value = "Member medal issued date selector") AttributeSelector issued,
      @QueryParam("issuerID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "issuerID",
          defaultValue = "{ any: true }",
          value = "Member medal issuer ID selector") AttributeSelector issuerID,
      @QueryParam("reason") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "reason",
          defaultValue = "{ any: true }",
          value = "Member medal reason selector") AttributeSelector reason,
      @QueryParam("status") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "status",
          defaultValue = "{ any: true }",
          value = "Member medal status selector") AttributeSelector status) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred, AccountAccessMask.ACCESS_MEMBER_MEDALS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CorporationMemberMedal>() {

                                                          @Override
                                                          public List<CorporationMemberMedal> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int MEDAL_ID = 0;
                                                            final int CHARACTER_ID = 1;
                                                            final int ISSUED = 2;
                                                            final int ISSUER_ID = 3;
                                                            final int REASON = 4;
                                                            final int STATUS = 5;

                                                            return CorporationMemberMedal.accessQuery(acct, contid,
                                                                                                      maxresults,
                                                                                                      reverse, at,
                                                                                                      others[MEDAL_ID],
                                                                                                      others[CHARACTER_ID],
                                                                                                      others[ISSUED],
                                                                                                      others[ISSUER_ID],
                                                                                                      others[REASON],
                                                                                                      others[STATUS]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CORP_MEDALS,
                                                                                        acct);
                                                          }
                                                        }, request, medalID, characterID, issued, issuerID, reason,
                                                        status);
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
      @QueryParam("allianceID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "allianceID",
          defaultValue = "{ any: true }",
          value = "Corporation alliance ID selector") AttributeSelector allianceID,
      @QueryParam("ceoID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "ceoID",
          defaultValue = "{ any: true }",
          value = "Corporation CEO ID selector") AttributeSelector ceoID,
      @QueryParam("corporationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "corporationID",
          defaultValue = "{ any: true }",
          value = "Corporation ID selector") AttributeSelector corporationID,
      @QueryParam("corporationName") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "corporationName",
          defaultValue = "{ any: true }",
          value = "Corporation name selector") AttributeSelector corporationName,
      @QueryParam("description") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "description",
          defaultValue = "{ any: true }",
          value = "Corporation description selector") AttributeSelector description,
      @QueryParam("memberCount") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "memberCount",
          defaultValue = "{ any: true }",
          value = "Corporation member count selector") AttributeSelector memberCount,
      @QueryParam("shares") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "shares",
          defaultValue = "{ any: true }",
          value = "Corporation shares selector") AttributeSelector shares,
      @QueryParam("stationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "stationID",
          defaultValue = "{ any: true }",
          value = "Corporation station ID selector") AttributeSelector stationID,
      @QueryParam("taxRate") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "taxRate",
          defaultValue = "{ any: true }",
          value = "Corporation tax rate selector") AttributeSelector taxRate,
      @QueryParam("ticker") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "ticker",
          defaultValue = "{ any: true }",
          value = "Corporation ticker selector") AttributeSelector ticker,
      @QueryParam("url") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "url",
          defaultValue = "{ any: true }",
          value = "Corporation URL selector") AttributeSelector url,
      @QueryParam("dateFounded") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "dateFounded",
          defaultValue = "{ any: true }",
          value = "Corporation founding date selector") AttributeSelector dateFounded,
      @QueryParam("creatorID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "creatorID",
          defaultValue = "{ any: true }",
          value = "Corporation creator ID selector") AttributeSelector creatorID,
      @QueryParam("factionID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "factionID",
          defaultValue = "{ any: true }",
          value = "Corporation faction ID selector") AttributeSelector factionID,
      @QueryParam("px64x64") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "px64x64",
          defaultValue = "{ any: true }",
          value = "Corporation 64x64 image URL selector") AttributeSelector px64x64,
      @QueryParam("px128x128") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "px128x128",
          defaultValue = "{ any: true }",
         value = "Corporation 128x128 image URL selector") AttributeSelector px128x128,
      @QueryParam("px256x256") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "px256x256",
          defaultValue = "{ any: true }",
          value = "Corporation 256x256 image URL selector") AttributeSelector px256x256,
      @QueryParam("warEligible") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "warEligible",
          defaultValue = "{ any: true }",
          value = "Corporation war eligible selector") AttributeSelector warEligible) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_CORPORATION_SHEET,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CorporationSheet>() {

                                                          @Override
                                                          public List<CorporationSheet> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int ALLIANCE_ID = 0;
                                                            final int CEO_ID = 1;
                                                            final int CORPORATION_ID = 2;
                                                            final int CORPORATION_NAME = 3;
                                                            final int DESCRIPTION = 4;
                                                            final int MEMBER_COUNT = 5;
                                                            final int SHARES = 6;
                                                            final int STATION_ID = 7;
                                                            final int TAX_RATE = 8;
                                                            final int TICKER = 9;
                                                            final int URL = 10;
                                                            final int DATE_FOUNDED = 11;
                                                            final int CREATOR_ID = 12;
                                                            final int FACTION_ID = 13;
                                                            final int PX64X64 = 14;
                                                            final int PX128X128 = 15;
                                                            final int PX256X256 = 16;
                                                            final int WAR_ELIGIBLE = 17;

                                                            return CorporationSheet.accessQuery(acct, contid,
                                                                                                maxresults,
                                                                                                reverse, at,
                                                                                                others[ALLIANCE_ID],
                                                                                                others[CEO_ID],
                                                                                                others[CORPORATION_ID],
                                                                                                others[CORPORATION_NAME],
                                                                                                others[DESCRIPTION],
                                                                                                others[MEMBER_COUNT],
                                                                                                others[SHARES],
                                                                                                others[STATION_ID],
                                                                                                others[TAX_RATE],
                                                                                                others[TICKER],
                                                                                                others[URL],
                                                                                                others[DATE_FOUNDED],
                                                                                                others[CREATOR_ID],
                                                                                                others[FACTION_ID],
                                                                                                others[PX64X64],
                                                                                                others[PX128X128],
                                                                                                others[PX256X256],
                                                                                                others[WAR_ELIGIBLE]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CORP_SHEET,
                                                                                        acct);
                                                          }
                                                        }, request, allianceID, ceoID, corporationID, corporationName,
                                                        description, memberCount, shares, stationID, taxRate, ticker,
                                                        url, dateFounded, creatorID, factionID, px64x64, px128x128,
                                                        px256x256, warEligible);
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
      @QueryParam("titleID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "titleID",
          defaultValue = "{ any: true }",
          value = "Corporation title ID selector") AttributeSelector titleID,
      @QueryParam("titleName") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "titleName",
          defaultValue = "{ any: true }",
          value = "Corporation title name selector") AttributeSelector titleName) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_CORPORATION_TITLES,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CorporationTitle>() {

                                                          @Override
                                                          public List<CorporationTitle> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int TITLE_ID = 0;
                                                            final int TITLE_NAME = 1;

                                                            return CorporationTitle.accessQuery(acct, contid,
                                                                                                maxresults,
                                                                                                reverse, at,
                                                                                                others[TITLE_ID],
                                                                                                others[TITLE_NAME]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CORP_TITLES,
                                                                                        acct);
                                                          }
                                                        }, request, titleID, titleName);
  }

  @Path("/title_role")
  @GET
  @ApiOperation(
      value = "Get corporation title roles")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation title roles",
              response = CorporationTitleRole.class,
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
  public Response getCorporationTitleRoles(
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
      @QueryParam("titleID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "titleID",
          defaultValue = "{ any: true }",
          value = "Corporation title role ID selector") AttributeSelector titleID,
      @QueryParam("roleName") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "roleName",
          defaultValue = "{ any: true }",
          value = "Corporation title role name selector") AttributeSelector roleName,
      @QueryParam("grantable") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "grantable",
          defaultValue = "{ any: true }",
          value = "Corporation title role grantable selector") AttributeSelector grantable,
      @QueryParam("atHQ") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "atHQ",
          defaultValue = "{ any: true }",
          value = "Corporation title role at HQ selector") AttributeSelector atHQ,
      @QueryParam("atBase") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "atBase",
          defaultValue = "{ any: true }",
          value = "Corporation title role at base selector") AttributeSelector atBase,
      @QueryParam("atOther") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "atOther",
          defaultValue = "{ any: true }",
          value = "Corporation title role at other selector") AttributeSelector atOther) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_CORPORATION_TITLES,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CorporationTitleRole>() {

                                                          @Override
                                                          public List<CorporationTitleRole> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int TITLE_ID = 0;
                                                            final int ROLE_NAME = 1;
                                                            final int GRANTABLE = 2;
                                                            final int AT_HQ = 3;
                                                            final int AT_BASE = 4;
                                                            final int AT_OTHER = 5;

                                                            return CorporationTitleRole.accessQuery(acct, contid,
                                                                                                    maxresults,
                                                                                                    reverse, at,
                                                                                                    others[TITLE_ID],
                                                                                                    others[ROLE_NAME],
                                                                                                    others[GRANTABLE],
                                                                                                    others[AT_HQ],
                                                                                                    others[AT_BASE],
                                                                                                    others[AT_OTHER]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CORP_TITLES,
                                                                                        acct);
                                                          }
                                                        }, request, titleID, roleName, grantable, atHQ, atBase,
                                                        atOther);
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
      @QueryParam("orficeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "officeID",
          defaultValue = "{ any: true }",
          value = "Customs office ID selector") AttributeSelector officeID,
      @QueryParam("solarSystemID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "solarSystemID",
          defaultValue = "{ any: true }",
          value = "Customs office solar system ID selector") AttributeSelector solarSystemID,
      @QueryParam("reinforceExitStart") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "reinforceExitStart",
          defaultValue = "{ any: true }",
          value = "Customs office reinforce exit timer start selector") AttributeSelector reinforceExitStart,
      @QueryParam("reinforceExitEnd") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "reinforceExitEnd",
          defaultValue = "{ any: true }",
          value = "Customs office reinforce exit timer end selector") AttributeSelector reinforceExitEnd,
      @QueryParam("allowAlliance") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "allowAlliance",
          defaultValue = "{ any: true }",
          value = "Customs office allow alliance selector") AttributeSelector allowAlliance,
      @QueryParam("allowStandings") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "allowStandings",
          defaultValue = "{ any: true }",
          value = "Customs office allow standings selector") AttributeSelector allowStandings,
      @QueryParam("standingLevel") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "standingLevel",
          defaultValue = "{ any: true }",
          value = "Customs office standing level selector") AttributeSelector standingLevel,
      @QueryParam("taxRateAlliance") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "taxRateAlliance",
          defaultValue = "{ any: true }",
          value = "Customs office tax rate alliance selector") AttributeSelector taxRateAlliance,
      @QueryParam("taxRateCorp") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "taxRateCorp",
          defaultValue = "{ any: true }",
          value = "Customs office tax rate corporation selector") AttributeSelector taxRateCorp,
      @QueryParam("taxRateStandingExcellent") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "taxRateStandingExcellent",
          defaultValue = "{ any: true }",
          value = "Customs office tax rate standing excellent selector") AttributeSelector taxRateStandingExcellent,
      @QueryParam("taxRateStandingGood") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "taxRateStandingGood",
          defaultValue = "{ any: true }",
          value = "Customs office tax rate standing good selector") AttributeSelector taxRateStandingGood,
      @QueryParam("taxRateStandingNeutral") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "taxRateStandingNeutral",
          defaultValue = "{ any: true }",
          value = "Customs office tax rate standing neutral selector") AttributeSelector taxRateStandingNeutral,
      @QueryParam("taxRateStandingBad") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "taxRateStandingBad",
          defaultValue = "{ any: true }",
          value = "Customs office tax rate standing bad selector") AttributeSelector taxRateStandingBad,
      @QueryParam("taxRateStandingTerrible") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "taxRateStandingTerrible",
          defaultValue = "{ any: true }",
          value = "Customs office tax rate standing terrible selector") AttributeSelector taxRateStandingTerrible) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_ASSETS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<CustomsOffice>() {

                                                          @Override
                                                          public List<CustomsOffice> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int OFFICE_ID = 0;
                                                            final int SOLAR_SYSTEM_ID = 1;
                                                            final int REINFORCE_EXIT_START = 2;
                                                            final int REINFORCE_EXIT_END = 3;
                                                            final int ALLOW_ALLIANCE = 4;
                                                            final int ALLOW_STANDINGS = 5;
                                                            final int STANDING_LEVEL = 6;
                                                            final int TAX_RATE_ALLIANCE = 7;
                                                            final int TAX_RATE_CORP = 8;
                                                            final int TAX_RATE_STANDING_EXCELLENT = 9;
                                                            final int TAX_RATE_STANDING_GOOD = 10;
                                                            final int TAX_RATE_STANDING_NEUTRAL = 11;
                                                            final int TAX_RATE_STANDING_BAD = 12;
                                                            final int TAX_RATE_STANDING_TERRIBLE = 13;

                                                            return CustomsOffice.accessQuery(acct, contid,
                                                                                             maxresults,
                                                                                             reverse, at,
                                                                                             others[OFFICE_ID],
                                                                                             others[SOLAR_SYSTEM_ID],
                                                                                             others[REINFORCE_EXIT_START],
                                                                                             others[REINFORCE_EXIT_END],
                                                                                             others[ALLOW_ALLIANCE],
                                                                                             others[ALLOW_STANDINGS],
                                                                                             others[STANDING_LEVEL],
                                                                                             others[TAX_RATE_ALLIANCE],
                                                                                             others[TAX_RATE_CORP],
                                                                                             others[TAX_RATE_STANDING_EXCELLENT],
                                                                                             others[TAX_RATE_STANDING_GOOD],
                                                                                             others[TAX_RATE_STANDING_NEUTRAL],
                                                                                             others[TAX_RATE_STANDING_BAD],
                                                                                             others[TAX_RATE_STANDING_TERRIBLE]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CORP_CUSTOMS,
                                                                                        acct);
                                                          }
                                                        }, request, officeID, solarSystemID, reinforceExitStart,
                                                        reinforceExitEnd,
                                                        allowAlliance, allowStandings, standingLevel, taxRateAlliance,
                                                        taxRateCorp, taxRateStandingExcellent, taxRateStandingGood,
                                                        taxRateStandingNeutral, taxRateStandingBad,
                                                        taxRateStandingTerrible);
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
      @QueryParam("wallet") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "wallet",
          defaultValue = "{ any: true }",
          value = "Division wallet indicator selector") AttributeSelector wallet,
      @QueryParam("division") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "division",
          defaultValue = "{ any: true }",
          value = "Division ID selector") AttributeSelector division,
      @QueryParam("name") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "name",
          defaultValue = "{ any: true }",
          value = "Division name selector") AttributeSelector name) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_CORPORATION_SHEET,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Division>() {

                                                          @Override
                                                          public List<Division> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int WALLET = 0;
                                                            final int DIVISION = 1;
                                                            final int NAME = 2;

                                                            return Division.accessQuery(acct, contid,
                                                                                        maxresults,
                                                                                        reverse, at,
                                                                                        others[WALLET],
                                                                                        others[DIVISION],
                                                                                        others[NAME]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CORP_DIVISIONS,
                                                                                        acct);
                                                          }
                                                        }, request, wallet, division, name);
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
      @QueryParam("facilityID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "facilityID",
          defaultValue = "{ any: true }",
          value = "Facility ID selector") AttributeSelector facilityID,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Facility type ID selector") AttributeSelector typeID,
      @QueryParam("solarSystemID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "solarSystemID",
          defaultValue = "{ any: true }",
          value = "Facility solar system ID selector") AttributeSelector solarSystemID) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_INDUSTRY_JOBS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Facility>() {

                                                          @Override
                                                          public List<Facility> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int FACILITY_ID = 0;
                                                            final int TYPE_ID = 1;
                                                            final int SOLAR_SYSTEM_ID = 2;

                                                            return Facility.accessQuery(acct, contid,
                                                                                        maxresults,
                                                                                        reverse, at,
                                                                                        others[FACILITY_ID],
                                                                                        others[TYPE_ID],
                                                                                        others[SOLAR_SYSTEM_ID]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CORP_FACILITIES,
                                                                                        acct);
                                                          }
                                                        }, request, facilityID, typeID, solarSystemID);
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
      @QueryParam("starbaseID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "starbaseID",
          defaultValue = "{ any: true }",
          value = "Fuel starbase ID selector") AttributeSelector starbaseID,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Fuel type ID selector") AttributeSelector typeID,
      @QueryParam("quantity") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "quantity",
          defaultValue = "{ any: true }",
          value = "Fuel quantity selector") AttributeSelector quantity) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_STARBASE_LIST,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Fuel>() {

                                                          @Override
                                                          public List<Fuel> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int STARBASE_ID = 0;
                                                            final int TYPE_ID = 1;
                                                            final int QUANTITY = 2;

                                                            return Fuel.accessQuery(acct, contid,
                                                                                    maxresults,
                                                                                    reverse, at,
                                                                                    others[STARBASE_ID],
                                                                                    others[TYPE_ID],
                                                                                    others[QUANTITY]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CORP_STARBASES,
                                                                                        acct);
                                                          }
                                                        }, request, starbaseID, typeID, quantity);
  }

  @Path("/member_role")
  @GET
  @ApiOperation(
      value = "Get corporation member roles")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation member security roles",
              response = MemberRole.class,
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
  public Response getMemberRoles(
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
      @QueryParam("characterID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "characterID",
          defaultValue = "{ any: true }",
          value = "Member role character ID selector") AttributeSelector characterID,
      @QueryParam("roleName") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "roleName",
          defaultValue = "{ any: true }",
          value = "Member role name selector") AttributeSelector roleName,
      @QueryParam("grantable") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "grantable",
          defaultValue = "{ any: true }",
          value = "Member role grantable selector") AttributeSelector grantable,
      @QueryParam("atHQ") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "atHQ",
          defaultValue = "{ any: true }",
          value = "Member role at HQ selector") AttributeSelector atHQ,
      @QueryParam("atBase") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "atBase",
          defaultValue = "{ any: true }",
          value = "Member role at base selector") AttributeSelector atBase,
      @QueryParam("atOther") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "atOther",
          defaultValue = "{ any: true }",
          value = "Member role at other selector") AttributeSelector atOther) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_MEMBER_SECURITY,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<MemberRole>() {

                                                          @Override
                                                          public List<MemberRole> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int CHARACTER_ID = 0;
                                                            final int ROLE_NAME = 1;
                                                            final int GRANTABLE = 2;
                                                            final int AT_HQ = 3;
                                                            final int AT_BASE = 4;
                                                            final int AT_OTHER = 5;

                                                            return MemberRole.accessQuery(acct, contid,
                                                                                          maxresults,
                                                                                          reverse, at,
                                                                                          others[CHARACTER_ID],
                                                                                          others[ROLE_NAME],
                                                                                          others[GRANTABLE],
                                                                                          others[AT_HQ],
                                                                                          others[AT_BASE],
                                                                                          others[AT_OTHER]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CORP_MEMBERSHIP,
                                                                                        acct);
                                                          }
                                                        }, request, characterID, roleName, grantable, atHQ, atBase,
                                                        atOther);
  }

  @Path("/member_role_history")
  @GET
  @ApiOperation(
      value = "Get corporation member role history entries")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation member role history entries",
              response = MemberRoleHistory.class,
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
  public Response getMemberRoleHistory(
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
      @QueryParam("characterID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "characterID",
          defaultValue = "{ any: true }",
          value = "Member role history character ID selector") AttributeSelector characterID,
      @QueryParam("changedAt") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "changedAt",
          defaultValue = "{ any: true }",
          value = "Member role history change time selector") AttributeSelector changedAt,
      @QueryParam("issuerID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "issuerID",
          defaultValue = "{ any: true }",
          value = "Member role history issuer ID selector") AttributeSelector issuerID,
      @QueryParam("roleType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "roleType",
          defaultValue = "{ any: true }",
          value = "Member role history role type selector") AttributeSelector roleType,
      @QueryParam("roleName") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "roleName",
          defaultValue = "{ any: true }",
          value = "Member role history roel name selector") AttributeSelector roleName,
      @QueryParam("old") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "old",
          defaultValue = "{ any: true }",
          value = "Member role history is old selector") AttributeSelector old) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_MEMBER_SECURITY_LOG,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<MemberRoleHistory>() {

                                                          @Override
                                                          public List<MemberRoleHistory> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int CHARACTER_ID = 0;
                                                            final int CHANGED_AT = 1;
                                                            final int ISSUER_ID = 2;
                                                            final int ROLE_TYPE = 3;
                                                            final int ROLE_NAME = 4;
                                                            final int OLD = 5;

                                                            return MemberRoleHistory.accessQuery(acct, contid,
                                                                                                 maxresults,
                                                                                                 reverse, at,
                                                                                                 others[CHARACTER_ID],
                                                                                                 others[CHANGED_AT],
                                                                                                 others[ISSUER_ID],
                                                                                                 others[ROLE_TYPE],
                                                                                                 others[ROLE_NAME],
                                                                                                 others[OLD]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CORP_MEMBERSHIP,
                                                                                        acct);
                                                          }
                                                        }, request, characterID, changedAt, issuerID, roleType,
                                                        roleName, old);
  }

  @Path("/members")
  @GET
  @ApiOperation(
      value = "Get corporation members")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation members",
              response = Member.class,
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
  public Response getMembers(
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
      @QueryParam("characterID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "characterID",
          defaultValue = "{ any: true }",
          value = "Corporation character ID selector") AttributeSelector characterID) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_MEMBER_SECURITY,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Member>() {

                                                          @Override
                                                          public List<Member> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int CHARACTER_ID = 0;

                                                            return Member.accessQuery(acct, contid,
                                                                                      maxresults,
                                                                                      reverse, at,
                                                                                      others[CHARACTER_ID]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CORP_MEMBERSHIP,
                                                                                        acct);
                                                          }
                                                        }, request, characterID);
  }

  @Path("/member_limit")
  @GET
  @ApiOperation(
      value = "Get corporation member limit information")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of corporation member limit information",
              response = MemberLimit.class,
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
  public Response getMemberLimit(
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
      @QueryParam("memberLimit") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "memberLimit",
          defaultValue = "{ any: true }",
          value = "Corporation member limit selector") AttributeSelector memberLimit) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_MEMBER_TRACKING,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<MemberLimit>() {

                                                          @Override
                                                          public List<MemberLimit> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int MEMBER_LIMIT = 0;

                                                            return MemberLimit.accessQuery(acct, contid,
                                                                                           maxresults,
                                                                                           reverse, at,
                                                                                           others[MEMBER_LIMIT]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                ESISyncEndpoint.CORP_TRACK_MEMBERS,
                                                                acct);
                                                          }
                                                        }, request, memberLimit);
  }

  @Path("/member_title")
  @GET
  @ApiOperation(
      value = "Get corporation member titles")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation member titles",
              response = MemberTitle.class,
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
  public Response getMemberTitles(
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
      @QueryParam("characterID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "characterID",
          defaultValue = "{ any: true }",
          value = "Corporation member title character ID selector") AttributeSelector characterID,
      @QueryParam("titleID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "titleID",
          defaultValue = "{ any: true }",
          value = "Corporation member title ID selector") AttributeSelector titleID) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_MEMBER_SECURITY,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<MemberTitle>() {

                                                          @Override
                                                          public List<MemberTitle> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int CHARACTER_ID = 0;
                                                            final int TITLE_ID = 1;

                                                            return MemberTitle.accessQuery(acct, contid,
                                                                                           maxresults,
                                                                                           reverse, at,
                                                                                           others[CHARACTER_ID],
                                                                                           others[TITLE_ID]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CORP_TITLES,
                                                                                        acct);
                                                          }
                                                        }, request, characterID, titleID);
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
      @QueryParam("characterID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "characterID",
          defaultValue = "{ any: true }",
          value = "Member character ID selector") AttributeSelector characterID,
      @QueryParam("baseID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "baseID",
          defaultValue = "{ any: true }",
          value = "Member base ID selector") AttributeSelector baseID,
      @QueryParam("locationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "locationID",
          defaultValue = "{ any: true }",
          value = "Member location ID selector") AttributeSelector locationID,
      @QueryParam("logoffDateTime") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "logoffDateTime",
          defaultValue = "{ any: true }",
          value = "Member logoff time selector") AttributeSelector logoffDateTime,
      @QueryParam("logonDateTime") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "logonDateTime",
          defaultValue = "{ any: true }",
          value = "Member logon time selector") AttributeSelector logonDateTime,
      @QueryParam("shipTypeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "shipTypeID",
          defaultValue = "{ any: true }",
          value = "Member ship type ID selector") AttributeSelector shipTypeID,
      @QueryParam("startDateTime") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "startDateTime",
          defaultValue = "{ any: true }",
          value = "Member start time selector") AttributeSelector startDateTime) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_MEMBER_TRACKING,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<MemberTracking>() {

                                                          @Override
                                                          public List<MemberTracking> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int CHARACTER_ID = 0;
                                                            final int BASE_ID = 1;
                                                            final int LOCATION_ID = 2;
                                                            final int LOGOFF_DATE_TIME = 3;
                                                            final int LOGON_DATE_TIME = 4;
                                                            final int SHIP_TYPE_ID = 5;
                                                            final int START_DATE_TIME = 6;

                                                            return MemberTracking.accessQuery(acct, contid,
                                                                                              maxresults,
                                                                                              reverse, at,
                                                                                              others[CHARACTER_ID],
                                                                                              others[BASE_ID],
                                                                                              others[LOCATION_ID],
                                                                                              others[LOGOFF_DATE_TIME],
                                                                                              others[LOGON_DATE_TIME],
                                                                                              others[SHIP_TYPE_ID],
                                                                                              others[START_DATE_TIME]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                ESISyncEndpoint.CORP_TRACK_MEMBERS,
                                                                acct);
                                                          }
                                                        }, request, characterID, baseID, locationID, logoffDateTime,
                                                        logonDateTime, shipTypeID, startDateTime);
  }

  @Path("/mining_extractions")
  @GET
  @ApiOperation(
      value = "Get mining extractions information")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of mining extractions",
              response = MiningExtraction.class,
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
  public Response getMiningExtractions(
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
      @QueryParam("moonID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "moonID",
          defaultValue = "{ any: true }",
          value = "Moon ID selector") AttributeSelector moonID,
      @QueryParam("structureID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "structureID",
          defaultValue = "{ any: true }",
          value = "Structure ID selector") AttributeSelector structureID,
      @QueryParam("extractionStartTime") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "extractionStartTime",
          defaultValue = "{ any: true }",
          value = "Extraction start time selector") AttributeSelector extractionStartTime,
      @QueryParam("chunkArrivalTime") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "chunkArrivalTime",
          defaultValue = "{ any: true }",
          value = "Chunk arrival time selector") AttributeSelector chunkArrivalTime,
      @QueryParam("naturalDecayTime") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "naturalDecayTime",
          defaultValue = "{ any: true }",
          value = "Natural decay time selector") AttributeSelector naturalDecayTime) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_MINING_LEDGER,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<MiningExtraction>() {

                                                          @Override
                                                          public List<MiningExtraction> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int MOON_ID = 0;
                                                            final int STRUCTURE_ID = 1;
                                                            final int EXTRACTION_START_TIME = 2;
                                                            final int CHUNK_ARRIVAL_TIME = 3;
                                                            final int NATURAL_DECAY_TIME = 4;

                                                            return MiningExtraction.accessQuery(acct, contid,
                                                                                                maxresults,
                                                                                                reverse, at,
                                                                                                others[MOON_ID],
                                                                                                others[STRUCTURE_ID],
                                                                                                others[EXTRACTION_START_TIME],
                                                                                                others[CHUNK_ARRIVAL_TIME],
                                                                                                others[NATURAL_DECAY_TIME]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                ESISyncEndpoint.CORP_MINING,
                                                                acct);
                                                          }
                                                        }, request, moonID, structureID, extractionStartTime,
                                                        chunkArrivalTime, naturalDecayTime);
  }

  @Path("/mining_observers")
  @GET
  @ApiOperation(
      value = "Get mining observers information")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of mining observers",
              response = MiningObserver.class,
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
  public Response getMiningObservers(
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
      @QueryParam("observerID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "observerID",
          defaultValue = "{ any: true }",
          value = "Observer ID selector") AttributeSelector observerID,
      @QueryParam("observerType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "observerType",
          defaultValue = "{ any: true }",
          value = "Observer type selector") AttributeSelector observerType,
      @QueryParam("lastUpdated") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "lastUpdated",
          defaultValue = "{ any: true }",
          value = "Last updated time selector") AttributeSelector lastUpdated) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_MINING_LEDGER,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<MiningObserver>() {

                                                          @Override
                                                          public List<MiningObserver> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int OBSERVER_ID = 0;
                                                            final int OBSERVER_TYPE = 1;
                                                            final int LAST_UPDATED = 2;

                                                            return MiningObserver.accessQuery(acct, contid,
                                                                                              maxresults,
                                                                                              reverse, at,
                                                                                              others[OBSERVER_ID],
                                                                                              others[OBSERVER_TYPE],
                                                                                              others[LAST_UPDATED]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                ESISyncEndpoint.CORP_MINING,
                                                                acct);
                                                          }
                                                        }, request, observerID, observerType, lastUpdated);
  }

  @Path("/mining_observations")
  @GET
  @ApiOperation(
      value = "Get mining observations information")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of mining observations",
              response = MiningObservation.class,
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
  public Response getMiningObservations(
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
      @QueryParam("observerID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "observerID",
          defaultValue = "{ any: true }",
          value = "Observer ID selector") AttributeSelector observerID,
      @QueryParam("characterID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "characterID",
          defaultValue = "{ any: true }",
          value = "Character ID selector") AttributeSelector characterID,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Type ID selector") AttributeSelector typeID,
      @QueryParam("recordedCorporationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "recordedCorporationID",
          defaultValue = "{ any: true }",
          value = "Recorded corporation ID selector") AttributeSelector recordedCorporationID,
      @QueryParam("quantity") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "quantity",
          defaultValue = "{ any: true }",
          value = "Quantity selector") AttributeSelector quantity,
      @QueryParam("lastUpdated") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "lastUpdated",
          defaultValue = "{ any: true }",
          value = "Last updated time selector") AttributeSelector lastUpdated) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_MINING_LEDGER,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<MiningObservation>() {

                                                          @Override
                                                          public List<MiningObservation> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int OBSERVER_ID = 0;
                                                            final int CHARACTER_ID = 1;
                                                            final int TYPE_ID = 2;
                                                            final int RECORDED_CORPORATION_ID = 3;
                                                            final int QUANTITY = 4;
                                                            final int LAST_UPDATED = 5;

                                                            return MiningObservation.accessQuery(acct, contid,
                                                                                                 maxresults,
                                                                                                 reverse, at,
                                                                                                 others[OBSERVER_ID],
                                                                                                 others[CHARACTER_ID],
                                                                                                 others[TYPE_ID],
                                                                                                 others[RECORDED_CORPORATION_ID],
                                                                                                 others[QUANTITY],
                                                                                                 others[LAST_UPDATED]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                ESISyncEndpoint.CORP_MINING,
                                                                acct);
                                                          }
                                                        }, request, observerID, characterID, typeID,
                                                        recordedCorporationID, quantity, lastUpdated);
  }

  @Path("/structures")
  @GET
  @ApiOperation(
      value = "Get structures")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of structures",
              response = Structure.class,
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
  public Response getStructures(
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
      @QueryParam("structureID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "structureID",
          defaultValue = "{ any: true }",
          value = "Structure ID selector") AttributeSelector structureID,
      @QueryParam("corporationID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "corporationID",
          defaultValue = "{ any: true }",
          value = "Structure owning corporation ID selector") AttributeSelector corporationID,
      @QueryParam("fuelExpires") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "fuelExpires",
          defaultValue = "{ any: true }",
          value = "Structure fuel expires time selector") AttributeSelector fuelExpires,
      @QueryParam("nextReinforceApply") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "nextReinforceApply",
          defaultValue = "{ any: true }",
          value = "Structure next reinforce apply time selector") AttributeSelector nextReinforceApply,
      @QueryParam("nextReinforceHour") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "nextReinforceHour",
          defaultValue = "{ any: true }",
          value = "Structure next reinforce hour selector") AttributeSelector nextReinforceHour,
      @QueryParam("nextReinforceWeekday") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "nextReinforceWeekday",
          defaultValue = "{ any: true }",
          value = "Structure next reinforce weekday selector") AttributeSelector nextReinforceWeekday,
      @QueryParam("profileID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "profileID",
          defaultValue = "{ any: true }",
          value = "Structure profile ID selector") AttributeSelector profileID,
      @QueryParam("reinforceHour") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "reinforceHour",
          defaultValue = "{ any: true }",
          value = "Structure reinforce hour selector") AttributeSelector reinforceHour,
      @QueryParam("reinforceWeekday") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "reinforceWeekday",
          defaultValue = "{ any: true }",
          value = "Structure reinforce weekday selector") AttributeSelector reinforceWeekday,
      @QueryParam("state") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "state",
          defaultValue = "{ any: true }",
          value = "Structure state selector") AttributeSelector state,
      @QueryParam("stateTimerEnd") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "stateTimerEnd",
          defaultValue = "{ any: true }",
          value = "Structure state timer end selector") AttributeSelector stateTimerEnd,
      @QueryParam("stateTimerStart") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "stateTimerStart",
          defaultValue = "{ any: true }",
          value = "Structure state timer start selector") AttributeSelector stateTimerStart,
      @QueryParam("systemID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "systemID",
          defaultValue = "{ any: true }",
          value = "Structure system ID selector") AttributeSelector systemID,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Structure type ID selector") AttributeSelector typeID,
      @QueryParam("unanchorsAt") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "unanchorsAt",
          defaultValue = "{ any: true }",
          value = "Strcucture 'unanchors at' time selector") AttributeSelector unanchorsAt) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_STRUCTURES,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Structure>() {

                                                          @Override
                                                          public List<Structure> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int STRUCTURE_ID = 0;
                                                            final int CORPORATION_ID = 1;
                                                            final int FUEL_EXPIRES = 2;
                                                            final int NEXT_REINFORCE_APPLY = 3;
                                                            final int NEXT_REINFORCE_HOUR = 4;
                                                            final int NEXT_REINFORCE_WEEKDAY = 5;
                                                            final int PROFILE_ID = 6;
                                                            final int REINFORCE_HOUR = 7;
                                                            final int REINFORCE_WEEKDAY = 8;
                                                            final int STATE = 9;
                                                            final int STATE_TIMER_END = 10;
                                                            final int STATE_TIMER_START = 11;
                                                            final int SYSTEM_ID = 12;
                                                            final int TYPE_ID = 13;
                                                            final int UNANCHORS_AT = 14;

                                                            return Structure.accessQuery(acct, contid,
                                                                                         maxresults,
                                                                                         reverse, at,
                                                                                         others[STRUCTURE_ID],
                                                                                         others[CORPORATION_ID],
                                                                                         others[FUEL_EXPIRES],
                                                                                         others[NEXT_REINFORCE_APPLY],
                                                                                         others[NEXT_REINFORCE_HOUR],
                                                                                         others[NEXT_REINFORCE_WEEKDAY],
                                                                                         others[PROFILE_ID],
                                                                                         others[REINFORCE_HOUR],
                                                                                         others[REINFORCE_WEEKDAY],
                                                                                         others[STATE],
                                                                                         others[STATE_TIMER_END],
                                                                                         others[STATE_TIMER_START],
                                                                                         others[SYSTEM_ID],
                                                                                         others[TYPE_ID],
                                                                                         others[UNANCHORS_AT]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                ESISyncEndpoint.CORP_STRUCTURES,
                                                                acct);
                                                          }
                                                        }, request, structureID, corporationID, fuelExpires,
                                                        nextReinforceApply, nextReinforceHour, nextReinforceWeekday,
                                                        profileID, reinforceHour, reinforceWeekday, state,
                                                        stateTimerEnd, stateTimerStart, systemID, typeID,
                                                        unanchorsAt);
  }

  @Path("/structure_services")
  @GET
  @ApiOperation(
      value = "Get structure services")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of structure services",
              response = StructureService.class,
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
  public Response getStructureServices(
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
      @QueryParam("structureID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "structureID",
          defaultValue = "{ any: true }",
          value = "Structure ID selector") AttributeSelector structureID,
      @QueryParam("name") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "name",
          defaultValue = "{ any: true }",
          value = "Structure service name selector") AttributeSelector name,
      @QueryParam("state") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "state",
          defaultValue = "{ any: true }",
          value = "Structure service state selector") AttributeSelector state) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_STRUCTURES,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<StructureService>() {

                                                          @Override
                                                          public List<StructureService> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int STRUCTURE_ID = 0;
                                                            final int NAME = 1;
                                                            final int STATE = 2;

                                                            return StructureService.accessQuery(acct, contid,
                                                                                                maxresults,
                                                                                                reverse, at,
                                                                                                others[STRUCTURE_ID],
                                                                                                others[NAME],
                                                                                                others[STATE]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                ESISyncEndpoint.CORP_STRUCTURES,
                                                                acct);
                                                          }
                                                        }, request, structureID, name, state);
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
      @QueryParam("shareholderID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "shareholderID",
          defaultValue = "{ any: true }",
          value = "Shareholder ID selector") AttributeSelector shareholderID,
      @QueryParam("shareholderType") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "shareholderType",
          defaultValue = "{ any: true }",
          value = "Shareholder type selector") AttributeSelector shareholderType,
      @QueryParam("shares") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "shares",
          defaultValue = "{ any: true }",
          value = "Shareholder shares selector") AttributeSelector shares) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_SHAREHOLDERS,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Shareholder>() {

                                                          @Override
                                                          public List<Shareholder> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int SHAREHOLDER_ID = 0;
                                                            final int SHAREHOLDER_TYPE = 1;
                                                            final int SHARES = 2;

                                                            return Shareholder.accessQuery(acct, contid,
                                                                                           maxresults,
                                                                                           reverse, at,
                                                                                           others[SHAREHOLDER_ID],
                                                                                           others[SHAREHOLDER_TYPE],
                                                                                           others[SHARES]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(
                                                                ESISyncEndpoint.CORP_SHAREHOLDERS,
                                                                acct);
                                                          }
                                                        }, request, shareholderID, shareholderType, shares);
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
      @QueryParam("starbaseID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "starbaseID",
          defaultValue = "{ any: true }",
          value = "Starbase ID selector") AttributeSelector starbaseID,
      @QueryParam("typeID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "typeID",
          defaultValue = "{ any: true }",
          value = "Starbase type ID selector") AttributeSelector typeID,
      @QueryParam("systemID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "systemID",
          defaultValue = "{ any: true }",
          value = "Starbase system ID selector") AttributeSelector systemID,
      @QueryParam("moonID") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "moonID",
          defaultValue = "{ any: true }",
          value = "Starbase moon ID selector") AttributeSelector moonID,
      @QueryParam("state") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "state",
          defaultValue = "{ any: true }",
          value = "Starbase state selector") AttributeSelector state,
      @QueryParam("unanchorAt") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "unanchorAt",
          defaultValue = "{ any: true }",
          value = "Starbase unanchor at timestamp selector") AttributeSelector unanchorAt,
      @QueryParam("reinforcedUntil") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "reinforcedUntil",
          defaultValue = "{ any: true }",
          value = "Starbase reinforced until timestamp selector") AttributeSelector reinforcedUntil,
      @QueryParam("onlinedSince") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "onlinedSince",
          defaultValue = "{ any: true }",
          value = "Starbase onlined since timestamp selector") AttributeSelector onlinedSince,
      @QueryParam("fuelBayView") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "fuelBayView",
          defaultValue = "{ any: true }",
          value = "Starbase fuel bay view selector") AttributeSelector fuelBayView,
      @QueryParam("fuelBayTake") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "fuelBayTake",
          defaultValue = "{ any: true }",
          value = "Starbase fuel bay take selector") AttributeSelector fuelBayTake,
      @QueryParam("anchor") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "anchor",
          defaultValue = "{ any: true }",
          value = "Starbase anchor selector") AttributeSelector anchor,
      @QueryParam("unanchor") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "unanchor",
          defaultValue = "{ any: true }",
          value = "Starbase unanchor selector") AttributeSelector unanchor,
      @QueryParam("online") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "online",
          defaultValue = "{ any: true }",
          value = "Starbase online selector") AttributeSelector online,
      @QueryParam("offline") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "offline",
          defaultValue = "{ any: true }",
          value = "Starbase offline selector") AttributeSelector offline,
      @QueryParam("allowCorporationMembers") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "allowCorporationMembers",
          defaultValue = "{ any: true }",
          value = "Starbase allow corporation members selector") AttributeSelector allowCorporationMembers,
      @QueryParam("allowAllianceMembers") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "allowAllianceMembers",
          defaultValue = "{ any: true }",
          value = "Starbase allow alliance members selector") AttributeSelector allowAllianceMembers,
      @QueryParam("useAllianceStandings") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "useAllianceStandings",
          defaultValue = "{ any: true }",
          value = "Starbase use alliance standings selector") AttributeSelector useAllianceStandings,
      @QueryParam("attackStandingThreshold") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "attackStandingThreshold",
          defaultValue = "{ any: true }",
          value = "Starbase attack standing threshold selector") AttributeSelector attackStandingThreshold,
      @QueryParam("attackSecurityStatusThreshold") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "attackSecurityStatusThreshold",
          defaultValue = "{ any: true }",
          value = "Starbase attack security status threshold selector") AttributeSelector attackSecurityStatusThreshold,
      @QueryParam("attackIfOtherSecurityStatusDropping") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "attackIfOtherSecurityStatusDropping",
          defaultValue = "{ any: true }",
          value = "Starbase attack if other security status dropping selector") AttributeSelector attackIfOtherSecurityStatusDropping,
      @QueryParam("attackIfAtWar") @DefaultValue(
          value = "{ any: true }") @ApiParam(
          name = "attackIfAtWar",
          defaultValue = "{ any: true }",
          value = "Starbase attack if at war selector") AttributeSelector attackIfAtWar) {
    return AccountHandlerUtil.handleStandardListRequest(accessKey, accessCred,
                                                        AccountAccessMask.ACCESS_STARBASE_LIST,
                                                        at, contid, maxresults, reverse,
                                                        new AccountHandlerUtil.QueryCaller<Starbase>() {

                                                          @Override
                                                          public List<Starbase> getList(
                                                              SynchronizedEveAccount acct, long contid, int maxresults,
                                                              boolean reverse,
                                                              AttributeSelector at,
                                                              AttributeSelector... others) throws IOException {
                                                            final int STARBASE_ID = 0;
                                                            final int TYPE_ID = 1;
                                                            final int SYSTEM_ID = 2;
                                                            final int MOON_ID = 3;
                                                            final int STATE = 4;
                                                            final int UNANCHOR_AT = 5;
                                                            final int REINFORCED_UNTIL = 6;
                                                            final int ONLINED_SINCE = 7;
                                                            final int FUEL_BAY_VIEW = 8;
                                                            final int FUEL_BAY_TAKE = 9;
                                                            final int ANCHOR = 10;
                                                            final int UNANCHOR = 11;
                                                            final int ONLINE = 12;
                                                            final int OFFLINE = 13;
                                                            final int ALLOW_CORPORATION_MEMBERS = 14;
                                                            final int ALLOW_ALLIANCE_MEMBERS = 15;
                                                            final int USE_ALLIANCE_STANDINGS = 16;
                                                            final int ATTACK_STANDING_THRESHOLD = 17;
                                                            final int ATTACK_SECURITY_STATUS_THRESHOLD = 18;
                                                            final int ATTACK_IF_OTHER_SECURITY_STATUS_DROPPING = 19;
                                                            final int ATTACK_IF_AT_WAR = 20;

                                                            return Starbase.accessQuery(acct, contid,
                                                                                        maxresults,
                                                                                        reverse, at,
                                                                                        others[STARBASE_ID],
                                                                                        others[TYPE_ID],
                                                                                        others[SYSTEM_ID],
                                                                                        others[MOON_ID],
                                                                                        others[STATE],
                                                                                        others[UNANCHOR_AT],
                                                                                        others[REINFORCED_UNTIL],
                                                                                        others[ONLINED_SINCE],
                                                                                        others[FUEL_BAY_VIEW],
                                                                                        others[FUEL_BAY_TAKE],
                                                                                        others[ANCHOR],
                                                                                        others[UNANCHOR],
                                                                                        others[ONLINE],
                                                                                        others[OFFLINE],
                                                                                        others[ALLOW_CORPORATION_MEMBERS],
                                                                                        others[ALLOW_ALLIANCE_MEMBERS],
                                                                                        others[USE_ALLIANCE_STANDINGS],
                                                                                        others[ATTACK_STANDING_THRESHOLD],
                                                                                        others[ATTACK_SECURITY_STATUS_THRESHOLD],
                                                                                        others[ATTACK_IF_OTHER_SECURITY_STATUS_DROPPING],
                                                                                        others[ATTACK_IF_AT_WAR]);
                                                          }

                                                          @Override
                                                          public long getExpiry(SynchronizedEveAccount acct) {
                                                            return handleStandardExpiry(ESISyncEndpoint.CORP_STARBASES,
                                                                                        acct);
                                                          }
                                                        }, request, starbaseID, typeID, systemID, moonID, state,
                                                        unanchorAt, reinforcedUntil, onlinedSince, fuelBayView,
                                                        fuelBayTake, anchor, unanchor, online, offline,
                                                        allowCorporationMembers, allowAllianceMembers,
                                                        useAllianceStandings, attackStandingThreshold,
                                                        attackSecurityStatusThreshold,
                                                        attackIfOtherSecurityStatusDropping, attackIfAtWar);
  }


}
