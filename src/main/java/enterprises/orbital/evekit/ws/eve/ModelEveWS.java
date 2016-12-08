package enterprises.orbital.evekit.ws.eve;

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
import enterprises.orbital.evekit.model.eve.Alliance;
import enterprises.orbital.evekit.model.eve.AllianceMemberCorporation;
import enterprises.orbital.evekit.model.eve.CharacterKillStat;
import enterprises.orbital.evekit.model.eve.CharacterVictoryPointStat;
import enterprises.orbital.evekit.model.eve.ConquerableStation;
import enterprises.orbital.evekit.model.eve.CorporationKillStat;
import enterprises.orbital.evekit.model.eve.CorporationVictoryPointStat;
import enterprises.orbital.evekit.model.eve.ErrorType;
import enterprises.orbital.evekit.model.eve.FactionKillStat;
import enterprises.orbital.evekit.model.eve.FactionStats;
import enterprises.orbital.evekit.model.eve.FactionVictoryPointStat;
import enterprises.orbital.evekit.model.eve.FactionWar;
import enterprises.orbital.evekit.model.eve.FactionWarSummary;
import enterprises.orbital.evekit.model.eve.RefType;
import enterprises.orbital.evekit.model.eve.RequiredSkill;
import enterprises.orbital.evekit.model.eve.SkillBonus;
import enterprises.orbital.evekit.model.eve.SkillGroup;
import enterprises.orbital.evekit.model.eve.SkillMember;
import enterprises.orbital.evekit.ws.ServiceError;
import enterprises.orbital.evekit.ws.ServiceUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/ws/v1/eve")
@Consumes({
    "application/json"
})
@Produces({
    "application/json"
})
@Api(
    tags = {
        "EVE"
    },
    produces = "application/json",
    consumes = "application/json")
public class ModelEveWS {

  @Path("/alliance")
  @GET
  @ApiOperation(
      value = "Get alliance list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested alliances",
              response = Alliance.class,
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
  public Response getAlliances(
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
                               @QueryParam("allianceID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "allianceID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Alliance ID selector") AttributeSelector allianceID,
                               @QueryParam("executorCorpID") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "executorCorpID",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Executing corporation ID selector") AttributeSelector executorCorpID,
                               @QueryParam("memberCount") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "memberCount",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Member count selector") AttributeSelector memberCount,
                               @QueryParam("name") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "name",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Name selector") AttributeSelector name,
                               @QueryParam("shortName") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "shortName",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Short name selector") AttributeSelector shortName,
                               @QueryParam("startDate") @DefaultValue(
                                   value = "{ any: true }") @ApiParam(
                                       name = "startDate",
                                       required = false,
                                       defaultValue = "{ any: true }",
                                       value = "Start date selector") AttributeSelector startDate) {
    ServiceUtil.sanitizeAttributeSelector(at, allianceID, executorCorpID, memberCount, name, shortName, startDate);
    maxresults = Math.min(1000, maxresults);
    try {
      List<Alliance> result = Alliance.accessQuery(contid, maxresults, reverse, at, allianceID, executorCorpID, memberCount, name, shortName, startDate);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getAllianceListExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/alliance_member")
  @GET
  @ApiOperation(
      value = "Get alliance member list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested alliance members",
              response = AllianceMemberCorporation.class,
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
  public Response getAllianceMembers(
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
                                     @QueryParam("allianceID") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "allianceID",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Alliance ID selector") AttributeSelector allianceID,
                                     @QueryParam("corporationID") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "corporationID",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Member corporation ID selector") AttributeSelector corporationID,
                                     @QueryParam("startDate") @DefaultValue(
                                         value = "{ any: true }") @ApiParam(
                                             name = "startDate",
                                             required = false,
                                             defaultValue = "{ any: true }",
                                             value = "Start date selector") AttributeSelector startDate) {
    ServiceUtil.sanitizeAttributeSelector(at, allianceID, corporationID, startDate);
    maxresults = Math.min(1000, maxresults);
    try {
      List<AllianceMemberCorporation> result = AllianceMemberCorporation.accessQuery(contid, maxresults, reverse, at, allianceID, corporationID, startDate);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getAllianceListExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/char_kill_stat")
  @GET
  @ApiOperation(
      value = "Get character kill stats")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character kill stats",
              response = CharacterKillStat.class,
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
  public Response getCharacterKillStats(
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
                                        @QueryParam("attribute") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "attribute",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Stat attribute (LAST_WEEK, TOTAL, YESTERDAY) selector") AttributeSelector attribute,
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
                                                value = "Character name selector") AttributeSelector characterName,
                                        @QueryParam("kills") @DefaultValue(
                                            value = "{ any: true }") @ApiParam(
                                                name = "kills",
                                                required = false,
                                                defaultValue = "{ any: true }",
                                                value = "Kill count selector") AttributeSelector kills) {
    ServiceUtil.sanitizeAttributeSelector(at, attribute, characterID, characterName, kills);
    maxresults = Math.min(1000, maxresults);
    try {
      List<CharacterKillStat> result = CharacterKillStat.accessQuery(contid, maxresults, reverse, at, attribute, characterID, characterName, kills);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getFacWarTopStatsExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/char_vp_stat")
  @GET
  @ApiOperation(
      value = "Get character victory point stats")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested character victory point stats",
              response = CharacterVictoryPointStat.class,
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
  public Response getCharacterVictoryPointStats(
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
                                                @QueryParam("attribute") @DefaultValue(
                                                    value = "{ any: true }") @ApiParam(
                                                        name = "attribute",
                                                        required = false,
                                                        defaultValue = "{ any: true }",
                                                        value = "Stat attribute (LAST_WEEK, TOTAL, YESTERDAY) selector") AttributeSelector attribute,
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
                                                        value = "Character name selector") AttributeSelector characterName,
                                                @QueryParam("victoryPoints") @DefaultValue(
                                                    value = "{ any: true }") @ApiParam(
                                                        name = "victoryPoints",
                                                        required = false,
                                                        defaultValue = "{ any: true }",
                                                        value = "Victory points selector") AttributeSelector victoryPoints) {
    ServiceUtil.sanitizeAttributeSelector(at, attribute, characterID, characterName, victoryPoints);
    maxresults = Math.min(1000, maxresults);
    try {
      List<CharacterVictoryPointStat> result = CharacterVictoryPointStat.accessQuery(contid, maxresults, reverse, at, attribute, characterID, characterName,
                                                                                     victoryPoints);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getFacWarTopStatsExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/conq_station")
  @GET
  @ApiOperation(
      value = "Get conquerable stations list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of conquerable stations",
              response = ConquerableStation.class,
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
  public Response getConquerableStations(
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
                                         @QueryParam("corporationID") @DefaultValue(
                                             value = "{ any: true }") @ApiParam(
                                                 name = "corporationID",
                                                 required = false,
                                                 defaultValue = "{ any: true }",
                                                 value = "Corporation ID selector") AttributeSelector corporationID,
                                         @QueryParam("corporationName") @DefaultValue(
                                             value = "{ any: true }") @ApiParam(
                                                 name = "corporationName",
                                                 required = false,
                                                 defaultValue = "{ any: true }",
                                                 value = "Corporation name selector") AttributeSelector corporationName,
                                         @QueryParam("solarSystemID") @DefaultValue(
                                             value = "{ any: true }") @ApiParam(
                                                 name = "solarSystemID",
                                                 required = false,
                                                 defaultValue = "{ any: true }",
                                                 value = "Solar system ID selector") AttributeSelector solarSystemID,
                                         @QueryParam("stationID") @DefaultValue(
                                             value = "{ any: true }") @ApiParam(
                                                 name = "stationID",
                                                 required = false,
                                                 defaultValue = "{ any: true }",
                                                 value = "Station ID selector") AttributeSelector stationID,
                                         @QueryParam("stationName") @DefaultValue(
                                             value = "{ any: true }") @ApiParam(
                                                 name = "stationName",
                                                 required = false,
                                                 defaultValue = "{ any: true }",
                                                 value = "Station name selector") AttributeSelector stationName,
                                         @QueryParam("stationTypeID") @DefaultValue(
                                             value = "{ any: true }") @ApiParam(
                                                 name = "stationTypeID",
                                                 required = false,
                                                 defaultValue = "{ any: true }",
                                                 value = "Station type ID selector") AttributeSelector stationTypeID,
                                         @QueryParam("x") @DefaultValue(
                                             value = "{ any: true }") @ApiParam(
                                                 name = "x",
                                                 required = false,
                                                 defaultValue = "{ any: true }",
                                                 value = "X position selector") AttributeSelector x,
                                         @QueryParam("y") @DefaultValue(
                                             value = "{ any: true }") @ApiParam(
                                                 name = "y",
                                                 required = false,
                                                 defaultValue = "{ any: true }",
                                                 value = "Y position selector") AttributeSelector y,
                                         @QueryParam("z") @DefaultValue(
                                             value = "{ any: true }") @ApiParam(
                                                 name = "z",
                                                 required = false,
                                                 defaultValue = "{ any: true }",
                                                 value = "Z position selector") AttributeSelector z) {
    ServiceUtil.sanitizeAttributeSelector(at, corporationID, corporationName, solarSystemID, stationID, stationName, stationTypeID, x, y, z);
    maxresults = Math.min(1000, maxresults);
    try {
      List<ConquerableStation> result = ConquerableStation.accessQuery(contid, maxresults, reverse, at, corporationID, corporationName, solarSystemID,
                                                                       stationID, stationName, stationTypeID, x, y, z);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getConquerableStationsExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/corp_kill_stat")
  @GET
  @ApiOperation(
      value = "Get corporation kill stats list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation kill stats",
              response = CorporationKillStat.class,
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
  public Response getCorporationKillStats(
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
                                          @QueryParam("attribute") @DefaultValue(
                                              value = "{ any: true }") @ApiParam(
                                                  name = "attribute",
                                                  required = false,
                                                  defaultValue = "{ any: true }",
                                                  value = "Stat attribute (LAST_WEEK, TOTAL, YESTERDAY) selector") AttributeSelector attribute,
                                          @QueryParam("corporationID") @DefaultValue(
                                              value = "{ any: true }") @ApiParam(
                                                  name = "corporationID",
                                                  required = false,
                                                  defaultValue = "{ any: true }",
                                                  value = "Corporation ID selector") AttributeSelector corporationID,
                                          @QueryParam("corporationName") @DefaultValue(
                                              value = "{ any: true }") @ApiParam(
                                                  name = "corporationName",
                                                  required = false,
                                                  defaultValue = "{ any: true }",
                                                  value = "Corporation name selector") AttributeSelector corporationName,
                                          @QueryParam("kills") @DefaultValue(
                                              value = "{ any: true }") @ApiParam(
                                                  name = "kills",
                                                  required = false,
                                                  defaultValue = "{ any: true }",
                                                  value = "Kill count selector") AttributeSelector kills) {
    ServiceUtil.sanitizeAttributeSelector(at, attribute, corporationID, corporationName, kills);
    maxresults = Math.min(1000, maxresults);
    try {
      List<CorporationKillStat> result = CorporationKillStat.accessQuery(contid, maxresults, reverse, at, attribute, corporationID, corporationName, kills);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getFacWarTopStatsExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/corp_vp_stat")
  @GET
  @ApiOperation(
      value = "Get corporation victory point stats list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested corporation victory point stats",
              response = CorporationVictoryPointStat.class,
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
  public Response getCorporationVictoryPointStats(
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
                                                  @QueryParam("attribute") @DefaultValue(
                                                      value = "{ any: true }") @ApiParam(
                                                          name = "attribute",
                                                          required = false,
                                                          defaultValue = "{ any: true }",
                                                          value = "Stat attribute (LAST_WEEK, TOTAL, YESTERDAY) selector") AttributeSelector attribute,
                                                  @QueryParam("corporationID") @DefaultValue(
                                                      value = "{ any: true }") @ApiParam(
                                                          name = "corporationID",
                                                          required = false,
                                                          defaultValue = "{ any: true }",
                                                          value = "Corporation ID selector") AttributeSelector corporationID,
                                                  @QueryParam("corporationName") @DefaultValue(
                                                      value = "{ any: true }") @ApiParam(
                                                          name = "corporationName",
                                                          required = false,
                                                          defaultValue = "{ any: true }",
                                                          value = "Corporation name selector") AttributeSelector corporationName,
                                                  @QueryParam("victoryPoints") @DefaultValue(
                                                      value = "{ any: true }") @ApiParam(
                                                          name = "victoryPoints",
                                                          required = false,
                                                          defaultValue = "{ any: true }",
                                                          value = "Victory points selector") AttributeSelector victoryPoints) {
    ServiceUtil.sanitizeAttributeSelector(at, attribute, corporationID, corporationName, victoryPoints);
    maxresults = Math.min(1000, maxresults);
    try {
      List<CorporationVictoryPointStat> result = CorporationVictoryPointStat.accessQuery(contid, maxresults, reverse, at, attribute, corporationID,
                                                                                         corporationName, victoryPoints);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getFacWarTopStatsExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/error_type")
  @GET
  @ApiOperation(
      value = "Get error type list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested error types",
              response = ErrorType.class,
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
  public Response getErrorTypes(
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
                                @QueryParam("errorCode") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "errorCode",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Error code selector") AttributeSelector errorCode,
                                @QueryParam("errorText") @DefaultValue(
                                    value = "{ any: true }") @ApiParam(
                                        name = "errorText",
                                        required = false,
                                        defaultValue = "{ any: true }",
                                        value = "Error text selector") AttributeSelector errorText) {
    ServiceUtil.sanitizeAttributeSelector(at, errorCode, errorText);
    maxresults = Math.min(1000, maxresults);
    try {
      List<ErrorType> result = ErrorType.accessQuery(contid, maxresults, reverse, at, errorCode, errorText);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getErrorListExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/faction_kill_stat")
  @GET
  @ApiOperation(
      value = "Get faction kill stats list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested faction kill stats",
              response = FactionKillStat.class,
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
  public Response getFactionKillStats(
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
                                      @QueryParam("attribute") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "attribute",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Stat attribute (LAST_WEEK, TOTAL, YESTERDAY) selector") AttributeSelector attribute,
                                      @QueryParam("factionID") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "factionID",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Faction ID selector") AttributeSelector factionID,
                                      @QueryParam("factionName") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "factionName",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Faction name selector") AttributeSelector factionName,
                                      @QueryParam("kills") @DefaultValue(
                                          value = "{ any: true }") @ApiParam(
                                              name = "kills",
                                              required = false,
                                              defaultValue = "{ any: true }",
                                              value = "Kill count selector") AttributeSelector kills) {
    ServiceUtil.sanitizeAttributeSelector(at, attribute, factionID, factionName, kills);
    maxresults = Math.min(1000, maxresults);
    try {
      List<FactionKillStat> result = FactionKillStat.accessQuery(contid, maxresults, reverse, at, attribute, factionID, factionName, kills);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getFacWarTopStatsExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/faction_stats")
  @GET
  @ApiOperation(
      value = "Get faction stats list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested faction stats",
              response = FactionStats.class,
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
  public Response getFactionStats(
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
                                  @QueryParam("factionID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "factionID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Faction ID selector") AttributeSelector factionID,
                                  @QueryParam("factionName") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "factionName",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Faction name selector") AttributeSelector factionName,
                                  @QueryParam("killsLastWeek") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "killsLastWeek",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Last week's kill count selector") AttributeSelector killsLastWeek,
                                  @QueryParam("killsTotal") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "killsTotal",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Total kill count selector") AttributeSelector killsTotal,
                                  @QueryParam("killsYesterday") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "killsYesterday",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Yesterday's kill count selector") AttributeSelector killsYesterday,
                                  @QueryParam("pilots") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "pilots",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Pilot count selector") AttributeSelector pilots,
                                  @QueryParam("systemsControlled") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "systemsControlled",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Controlled systems count selector") AttributeSelector systemsControlled,
                                  @QueryParam("victoryPointsLastWeek") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "victoryPointsLastWeek",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Last week's victory points count selector") AttributeSelector victoryPointsLastWeek,
                                  @QueryParam("victoryPointsTotal") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "victoryPointsTotal",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Total victory points count selector") AttributeSelector victoryPointsTotal,
                                  @QueryParam("victoryPointsYesterday") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "victoryPointsYesterday",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Yesterday's victory point count selector") AttributeSelector victoryPointsYesterday) {
    ServiceUtil.sanitizeAttributeSelector(at, factionID, factionName, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled,
                                          victoryPointsLastWeek, victoryPointsTotal, victoryPointsYesterday);
    maxresults = Math.min(1000, maxresults);
    try {
      List<FactionStats> result = FactionStats.accessQuery(contid, maxresults, reverse, at, factionID, factionName, killsLastWeek, killsTotal, killsYesterday,
                                                           pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal, victoryPointsYesterday);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getFacWarStatsExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/faction_vp_stat")
  @GET
  @ApiOperation(
      value = "Get faction victory points stats list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested faction victory points stats",
              response = FactionVictoryPointStat.class,
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
  public Response getFactionVictoryPointsStatus(
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
                                                @QueryParam("attribute") @DefaultValue(
                                                    value = "{ any: true }") @ApiParam(
                                                        name = "attribute",
                                                        required = false,
                                                        defaultValue = "{ any: true }",
                                                        value = "Stat attribute (LAST_WEEK, TOTAL, YESTERDAY) selector") AttributeSelector attribute,
                                                @QueryParam("factionID") @DefaultValue(
                                                    value = "{ any: true }") @ApiParam(
                                                        name = "factionID",
                                                        required = false,
                                                        defaultValue = "{ any: true }",
                                                        value = "Faction ID selector") AttributeSelector factionID,
                                                @QueryParam("factionName") @DefaultValue(
                                                    value = "{ any: true }") @ApiParam(
                                                        name = "factionName",
                                                        required = false,
                                                        defaultValue = "{ any: true }",
                                                        value = "Faction name selector") AttributeSelector factionName,
                                                @QueryParam("victoryPoints") @DefaultValue(
                                                    value = "{ any: true }") @ApiParam(
                                                        name = "victoryPoints",
                                                        required = false,
                                                        defaultValue = "{ any: true }",
                                                        value = "Victory points selector") AttributeSelector victoryPoints) {
    ServiceUtil.sanitizeAttributeSelector(at, attribute, factionID, factionName, victoryPoints);
    maxresults = Math.min(1000, maxresults);
    try {
      List<FactionVictoryPointStat> result = FactionVictoryPointStat.accessQuery(contid, maxresults, reverse, at, attribute, factionID, factionName,
                                                                                 victoryPoints);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getFacWarTopStatsExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/faction_war")
  @GET
  @ApiOperation(
      value = "Get faction war list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested faction wars",
              response = FactionWar.class,
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
  public Response getFactionWars(
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
                                 @QueryParam("againstID") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "againstID",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Against ID selector") AttributeSelector againstID,
                                 @QueryParam("againstName") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "againstName",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Against name selector") AttributeSelector againstName,
                                 @QueryParam("factionID") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "factionID",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Faction ID selector") AttributeSelector factionID,
                                 @QueryParam("factionName") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "factionName",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Faction name selector") AttributeSelector factionName) {
    ServiceUtil.sanitizeAttributeSelector(at, againstID, againstName, factionID, factionName);
    maxresults = Math.min(1000, maxresults);
    try {
      List<FactionWar> result = FactionWar.accessQuery(contid, maxresults, reverse, at, againstID, againstName, factionID, factionName);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getFacWarStatsExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/faction_war_summary")
  @GET
  @ApiOperation(
      value = "Get faction war summary list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested faction war summaries",
              response = Alliance.class,
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
  public Response getFactionWarSummaries(
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
                                         @QueryParam("killsLastWeek") @DefaultValue(
                                             value = "{ any: true }") @ApiParam(
                                                 name = "killsLastWeek",
                                                 required = false,
                                                 defaultValue = "{ any: true }",
                                                 value = "Kills last week selector") AttributeSelector killsLastWeek,
                                         @QueryParam("killsTotal") @DefaultValue(
                                             value = "{ any: true }") @ApiParam(
                                                 name = "killsTotal",
                                                 required = false,
                                                 defaultValue = "{ any: true }",
                                                 value = "Kills total selector") AttributeSelector killsTotal,
                                         @QueryParam("killsYesterday") @DefaultValue(
                                             value = "{ any: true }") @ApiParam(
                                                 name = "killsYesterday",
                                                 required = false,
                                                 defaultValue = "{ any: true }",
                                                 value = "Kills yesterday selector") AttributeSelector killsYesterday,
                                         @QueryParam("victoryPointsLastWeek") @DefaultValue(
                                             value = "{ any: true }") @ApiParam(
                                                 name = "victoryPointsLastWeek",
                                                 required = false,
                                                 defaultValue = "{ any: true }",
                                                 value = "Victory points last week selector") AttributeSelector victoryPointsLastWeek,
                                         @QueryParam("victoryPointsTotal") @DefaultValue(
                                             value = "{ any: true }") @ApiParam(
                                                 name = "victoryPointsTotal",
                                                 required = false,
                                                 defaultValue = "{ any: true }",
                                                 value = "Victory points total selector") AttributeSelector victoryPointsTotal,
                                         @QueryParam("victoryPointsYesterday") @DefaultValue(
                                             value = "{ any: true }") @ApiParam(
                                                 name = "victoryPointsYesterday",
                                                 required = false,
                                                 defaultValue = "{ any: true }",
                                                 value = "Vicory points yesterday selector") AttributeSelector victoryPointsYesterday) {
    ServiceUtil.sanitizeAttributeSelector(at, killsLastWeek, killsTotal, killsYesterday, victoryPointsLastWeek, victoryPointsTotal, victoryPointsYesterday);
    maxresults = Math.min(1000, maxresults);
    try {
      List<FactionWarSummary> result = FactionWarSummary.accessQuery(contid, maxresults, reverse, at, killsLastWeek, killsTotal, killsYesterday,
                                                                     victoryPointsLastWeek, victoryPointsTotal, victoryPointsYesterday);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getFacWarStatsExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/ref_type")
  @GET
  @ApiOperation(
      value = "Get reference types list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested reference types",
              response = RefType.class,
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
  public Response getRefTypes(
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
                              @QueryParam("refTypeID") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "refTypeID",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Reference type ID selector") AttributeSelector refTypeID,
                              @QueryParam("refTypeName") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "refTypeName",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Reference type name selector") AttributeSelector refTypeName) {
    ServiceUtil.sanitizeAttributeSelector(at, refTypeID, refTypeName);
    maxresults = Math.min(1000, maxresults);
    try {
      List<RefType> result = RefType.accessQuery(contid, maxresults, reverse, at, refTypeID, refTypeName);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getRefTypeExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/req_skill")
  @GET
  @ApiOperation(
      value = "Get required skills list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested required skills",
              response = RequiredSkill.class,
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
  public Response getRequiredSkills(
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
                                    @QueryParam("parentTypeID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "parentTypeID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Parent type ID selector") AttributeSelector parentTypeID,
                                    @QueryParam("typeID") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "typeID",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Type ID selector") AttributeSelector typeID,
                                    @QueryParam("level") @DefaultValue(
                                        value = "{ any: true }") @ApiParam(
                                            name = "level",
                                            required = false,
                                            defaultValue = "{ any: true }",
                                            value = "Level selector") AttributeSelector level) {
    ServiceUtil.sanitizeAttributeSelector(at, parentTypeID, typeID, level);
    maxresults = Math.min(1000, maxresults);
    try {
      List<RequiredSkill> result = RequiredSkill.accessQuery(contid, maxresults, reverse, at, parentTypeID, typeID, level);
      // Finish
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getSkillTreeExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/skill_bonus")
  @GET
  @ApiOperation(
      value = "Get skill bonus list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested skill bonuses",
              response = SkillBonus.class,
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
  public Response getSkillBonuses(
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
                                  @QueryParam("typeID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "typeID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Type ID selector") AttributeSelector typeID,
                                  @QueryParam("bonusType") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "bonusType",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Buy type selector") AttributeSelector bonusType,
                                  @QueryParam("bonusValue") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "bonusValue",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Bonus value selector") AttributeSelector bonusValue) {
    ServiceUtil.sanitizeAttributeSelector(at, typeID, bonusType, bonusValue);
    maxresults = Math.min(1000, maxresults);
    try {
      List<SkillBonus> result = SkillBonus.accessQuery(contid, maxresults, reverse, at, typeID, bonusType, bonusValue);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getSkillTreeExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/skill_group")
  @GET
  @ApiOperation(
      value = "Get skill group list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested skill groups",
              response = SkillGroup.class,
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
  public Response getSkillGroups(
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
                                         value = "Group ID selector") AttributeSelector groupID,
                                 @QueryParam("groupName") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "groupName",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Group name selector") AttributeSelector groupName) {
    ServiceUtil.sanitizeAttributeSelector(at, groupID, groupName);
    maxresults = Math.min(1000, maxresults);
    try {
      List<SkillGroup> result = SkillGroup.accessQuery(contid, maxresults, reverse, at, groupID, groupName);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getSkillTreeExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

  @Path("/skill_member")
  @GET
  @ApiOperation(
      value = "Get skill member list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested skill members",
              response = SkillMember.class,
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
  public Response getSkillMembers(
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
                                          value = "Group ID selector") AttributeSelector groupID,
                                  @QueryParam("typeID") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "typeID",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Type ID selector") AttributeSelector typeID,
                                  @QueryParam("description") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "description",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Description selector") AttributeSelector description,
                                  @QueryParam("rank") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "rank",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Rank selector") AttributeSelector rank,
                                  @QueryParam("requiredPrimaryAttribute") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "requiredPrimaryAttribute",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Required primary attribute selector") AttributeSelector requiredPrimaryAttribute,
                                  @QueryParam("requiredSecondaryAttribute") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "requiredSecondaryAttribute",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Required secondary attribute selector") AttributeSelector requiredSecondaryAttribute,
                                  @QueryParam("typeName") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "typeName",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Type name selector") AttributeSelector typeName,
                                  @QueryParam("published") @DefaultValue(
                                      value = "{ any: true }") @ApiParam(
                                          name = "published",
                                          required = false,
                                          defaultValue = "{ any: true }",
                                          value = "Published selector") AttributeSelector published) {
    ServiceUtil.sanitizeAttributeSelector(at, groupID, typeID, description, rank, requiredPrimaryAttribute, requiredSecondaryAttribute, typeName, published);
    maxresults = Math.min(1000, maxresults);
    try {
      List<SkillMember> result = SkillMember.accessQuery(contid, maxresults, reverse, at, groupID, typeID, description, rank, requiredPrimaryAttribute,
                                                         requiredSecondaryAttribute, typeName, published);
      for (RefCachedData next : result) {
        next.prepareDates();
      }
      // Finish
      return ServiceUtil.finishRef(OrbitalProperties.getCurrentTime(), RefData.getRefData().getSkillTreeExpiry(), result, request);
    } catch (NumberFormatException e) {
      ServiceError errMsg = new ServiceError(Status.BAD_REQUEST.getStatusCode(), "An attribute selector contained an illegal value");
      return Response.status(Status.BAD_REQUEST).entity(errMsg).build();
    }
  }

}
