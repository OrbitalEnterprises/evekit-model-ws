package enterprises.orbital.evekit.ws.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(
    description = "Model meta-data pair")
public class MetaData {
  @ApiModelProperty(
      value = "meta-data key")
  public String key;
  @ApiModelProperty(
      value = "meta-data value")
  public String value;

  public MetaData(String key, String value) {
    super();
    this.key = key;
    this.value = value;
  }

}
