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
  @Deprecated
  public String entityName;
  @ApiModelProperty(
      value = "key entity ID, either corporation or character ID")
  @Deprecated
  public long entityID;
  @ApiModelProperty(
      value = "Character name")
  public String charName;
  @ApiModelProperty(
      value = "Character ID")
  public int charID;
  @ApiModelProperty(
      value = "Corporation name")
  public String corpName;
  @ApiModelProperty(
      value = "Corporation ID")
  public int corpID;
  @ApiModelProperty(
      value = "key access mask")
  public long mask;
  @ApiModelProperty(
      value = "if not -1, then the time (millis UTC) when this key will expire")
  public long expiry;
  @ApiModelProperty(
      value = "if no -1, then the time (millis UTC) of the oldest model entities this key will allow access to")
  public long limit;
  @ApiModelProperty(
      value = "if true, then the ESI token associated with the synchronized account which owns this key is still" +
          "valid.  Otherwise, the key is not valid and may need to be re-authorized")
  public boolean tokenValid;

  public KeyInfo(String keyType, String charName, int charID, String corpName, int corpID, long mask, long expiry,
                 long limit, boolean valid) {
    super();
    this.keyType = keyType;
    this.charID = charID;
    this.charName = charName;
    this.corpID = corpID;
    this.corpName = corpName;
    this.entityName = keyType == "character" ? charName : corpName;
    this.entityID = keyType == "character" ? charID : corpID;
    this.mask = mask;
    this.expiry = expiry;
    this.limit = limit;
    this.tokenValid = valid;
  }

}
