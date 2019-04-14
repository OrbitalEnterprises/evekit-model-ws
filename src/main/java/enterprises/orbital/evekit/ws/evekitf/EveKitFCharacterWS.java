package enterprises.orbital.evekit.ws.evekitf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import enterprises.orbital.eve.esi.client.model.GetCharactersCharacterIdOnlineOk;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.character.CharacterOnline;
import enterprises.orbital.evekit.ws.ServiceError;
import enterprises.orbital.evekit.ws.ServiceUtil;
import io.swagger.annotations.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static enterprises.orbital.evekit.ws.AccountHandlerUtil.handleIOError;

@Path("/evekitf/v1/char")
@Consumes({
    "application/json"
})
@Produces({
    "application/json"
})
@Api(
    tags = {
        "EveKitF Character"
    },
    produces = "application/json",
    consumes = "application/json")
public class EveKitFCharacterWS {

  // const path_SG_ESI_LOCATION_READ_ONLINE_V1 = "/characters/{character_id}/online/"
  @Path("/characters/character_id/online/")
  @GET
  @ApiOperation(
      value = "Serve /characters/character_id/online/ history")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = 200,
              message = "Next online snapshot after given time",
              response = GetCharactersCharacterIdOnlineOk.class),
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
  public Response getCharactersCharacterIdOnline(
      @Context HttpServletRequest request,
      @QueryParam("accessKey") @ApiParam(
          name = "accessKey",
          required = true,
          value = "History access key") int accessKey,
      @QueryParam("accessCred") @ApiParam(
          name = "accessCred",
          required = true,
          value = "History access credential") String accessCred,
      @QueryParam("snapshotTime") @ApiParam(
          required = true,
          name = "snapshotTime",
          value = "Model lifeline selector (defaults to current live data)") long snapshotTime) {
    // Retrieve the first result object which is live after "snapshotTime".
    ServiceUtil.AccessConfig cfg = ServiceUtil.start(accessKey, accessCred, AttributeSelector.any(),
                                                     AccountAccessMask.ACCESS_ACCOUNT_STATUS);
    if (cfg.fail) return cfg.response;
    try {
      // Retrieve results
      List<CharacterOnline> query = CharacterOnline.ekfQuery(cfg.owner,
                                                             0,
                                                             1,
                                                             snapshotTime);
      // Assemble into correct return type
      if (query.isEmpty()) {
        return Response.ok()
                       .build();
      } else {
        CharacterOnline next = query.get(0);
        ModGetCharactersCharacterIdOnlineOk val = new ModGetCharactersCharacterIdOnlineOk();
        if (next.getLastLogin() > 0)
          val.setLastLogin(new DateTime(next.getLastLogin(), DateTimeZone.UTC));
        if (next.getLastLogout() > 0)
          val.setLastLogout(new DateTime(next.getLastLogout(), DateTimeZone.UTC));
        val.setLogins(next.getLogins());
        val.setOnline(next.isOnline());

        return Response.ok()
                       .entity(val)
                       .header("date", new Date(next.getLifeStart()))
                       .build();
      }
    } catch (IOException e) {
      return handleIOError(e);
    }
  }

  class ModGetCharactersCharacterIdOnlineOk extends GetCharactersCharacterIdOnlineOk {

    @Override
    @JsonProperty("last_login")
    @JsonSerialize(using = CustomDateSerializer.class)
    public DateTime getLastLogin() {
      return super.getLastLogin();
    }

    @Override
    @JsonProperty("last_logout")
    @JsonSerialize(using = CustomDateSerializer.class)
    public DateTime getLastLogout() {
      return super.getLastLogout();
    }

  }

}
