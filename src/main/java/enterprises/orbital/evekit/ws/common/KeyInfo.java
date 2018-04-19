package enterprises.orbital.evekit.ws.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(
    description = "EveKit Access Key info")
public class KeyInfo {
  @ApiModelProperty(
      value = "key type, either 'corporation' or 'character'")
  public String keyType;
  @ApiModelProperty(
      value = "key entity name, either corporation or character name")
  public String entityName;
  @ApiModelProperty(
      value = "key entity ID, either corporation or character ID")
  public long   entityID;
  @ApiModelProperty(
      value = "key access mask")
  public long   mask;
  @ApiModelProperty(
      value = "if not -1, then the time (millis UTC) when this key will expire")
  public long   expiry;
  @ApiModelProperty(
      value = "if no -1, then the time (millis UTC) of the oldest model entities this key will allow access to")
  public long   limit;
  @ApiModelProperty(
      value = "if true, then the ESI token associated with the synchronized account which owns this key is still" +
          "valid.  Otherwise, the key is not valid and may need to be re-authorized")
  public boolean tokenValid;

  public KeyInfo(String keyType, String entityName, long entityID, long mask, long expiry, long limit, boolean valid) {
    super();
    this.keyType = keyType;
    this.entityName = entityName;
    this.entityID = entityID;
    this.mask = mask;
    this.expiry = expiry;
    this.limit = limit;
    this.tokenValid = valid;
  }

}
