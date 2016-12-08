package enterprises.orbital.evekit.ws.map;

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
import enterprises.orbital.evekit.model.map.FactionWarSystem;
import enterprises.orbital.evekit.model.map.MapJump;
import enterprises.orbital.evekit.model.map.MapKill;
import enterprises.orbital.evekit.model.map.Sovereignty;
import enterprises.orbital.evekit.ws.ServiceError;
import enterprises.orbital.evekit.ws.ServiceUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/ws/v1/map")
@Consumes({
    "application/json"
})
@Produces({
    "application/json"
})
@Api(
    tags = {
        "Map"
    },
    produces = "application/json",
    consumes = "application/json")
public class ModelMapWS {

  @Path("/faction_war_system")
  @GET
  @ApiOperation(
      value = "Get faction war system list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested faction war systems",
              response = FactionWarSystem.class,
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
  public Response getFactionWarSystems(
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
                                       @QueryParam("occupyingFactionID") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "occupyingFactionID",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Occupying faction ID selector") AttributeSelector occupyingFactionID,
                                       @QueryParam("occupyingFactionName") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "occupyingFactionName",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Occupying faction name selector") AttributeSelector occupyingFactionName,
                                       @QueryParam("owningFactionID") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "owningFactionID",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Owning faction ID selector") AttributeSelector owningFactionID,
                                       @QueryParam("owningFactionName") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "owningFactionName",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Owning faction name selector") AttributeSelector owningFactionName,
                                       @QueryParam("solarSystemID") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "solarSystemID",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Solar system ID selector") AttributeSelector solarSystemID,
                                       @QueryParam("solarSystemName") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "solarSystemName",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Solar system name selector") AttributeSelector solarSystemName,
                                       @QueryParam("contested") @DefaultValue(
                                           value = "{ any: true }") @ApiParam(
                                               name = "contested",
                                               required = false,
                                               defaultValue = "{ any: true }",
                                               value = "Contested selector") AttributeSelector contested) {
    ServiceUtil.sanitizeAttributeSelector(at, occupyingFactionID, occupyingFactionName, owningFactionID, owningFactionName, solarSystemID, solarSystemName,
                                          contested);
    maxresults = Math.min(1000, maxresults);
    try {
      List<FactionWarSystem> result = FactionWarSystem.accessQuery(contid, maxresults, reverse, at, occupyingFactionID, occupyingFactionName, owningFactionID,
                                                                   owningFactionName, solarSystemID, solarSystemName, contested);
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

  @Path("/map_jump")
  @GET
  @ApiOperation(
      value = "Get map jump list")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested map jumps",
              response = MapJump.class,
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
  public Response getMapJumps(
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
                              @QueryParam("solarSystemID") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "solarSystemID",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Solar system ID selector") AttributeSelector solarSystemID,
                              @QueryParam("shipJumps") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "shipJumps",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Ship jumps selector") AttributeSelector shipJumps) {
    ServiceUtil.sanitizeAttributeSelector(at, solarSystemID, shipJumps);
    maxresults = Math.min(1000, maxresults);
    try {
      List<MapJump> result = MapJump.accessQuery(contid, maxresults, reverse, at, solarSystemID, shipJumps);
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

  @Path("/map_kill")
  @GET
  @ApiOperation(
      value = "Get map kills")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested map kills",
              response = MapKill.class,
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
  public Response getMapKills(
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
                              @QueryParam("factionKills") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "factionKills",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Faction kills selector") AttributeSelector factionKills,
                              @QueryParam("podKills") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "podKills",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Pod kills selector") AttributeSelector podKills,
                              @QueryParam("shipKills") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "shipKills",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Ship kills selector") AttributeSelector shipKills,
                              @QueryParam("solarSystemID") @DefaultValue(
                                  value = "{ any: true }") @ApiParam(
                                      name = "solarSystemID",
                                      required = false,
                                      defaultValue = "{ any: true }",
                                      value = "Solar system ID selector") AttributeSelector solarSystemID) {
    ServiceUtil.sanitizeAttributeSelector(at, factionKills, podKills, shipKills, solarSystemID);
    maxresults = Math.min(1000, maxresults);
    try {
      List<MapKill> result = MapKill.accessQuery(contid, maxresults, reverse, at, factionKills, podKills, shipKills, solarSystemID);
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

  /// evekit-model/src/main/java/enterprises/orbital/evekit/model/map/Sovereignty.java
  @Path("/sovereignty")
  @GET
  @ApiOperation(
      value = "Get sovereignty")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "list of requested sovereignty",
              response = Sovereignty.class,
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
  public Response getSovereignty(
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
                                         value = "Corporation ID selector") AttributeSelector corporationID,
                                 @QueryParam("factionID") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "factionID",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Faction ID selector") AttributeSelector factionID,
                                 @QueryParam("solarSystemID") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "solarSystemID",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Solar system ID selector") AttributeSelector solarSystemID,
                                 @QueryParam("solarSystemName") @DefaultValue(
                                     value = "{ any: true }") @ApiParam(
                                         name = "solarSystemName",
                                         required = false,
                                         defaultValue = "{ any: true }",
                                         value = "Solar system name selector") AttributeSelector solarSystemName) {
    ServiceUtil.sanitizeAttributeSelector(at, allianceID, corporationID, factionID, solarSystemID, solarSystemName);
    maxresults = Math.min(1000, maxresults);
    try {
      List<Sovereignty> result = Sovereignty.accessQuery(contid, maxresults, reverse, at, allianceID, corporationID, factionID, solarSystemID, solarSystemName);
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

}
