package enterprises.orbital.evekit.ws.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@SuppressWarnings("unused")
@ApiModel(
    description = "EveKit Access Key Mask List")
public class MaskList {
  @ApiModelProperty(
      value = "access account status")
  public final long ACCESS_ACCOUNT_STATUS           = 1L;
  @ApiModelProperty(
      value = "access account balance")
  public final long ACCESS_ACCOUNT_BALANCE          = 1L << 1;
  @ApiModelProperty(
      value = "access assets")
  public final long ACCESS_ASSETS                   = 1L << 2;
  @ApiModelProperty(
      value = "access contact list")
  public final long ACCESS_CONTACT_LIST             = 1L << 3;
  @ApiModelProperty(
      value = "access blueprints")
  public final long ACCESS_BLUEPRINTS               = 1L << 38;
  @ApiModelProperty(
      value = "access bookmarks")
  public final long ACCESS_BOOKMARKS                = 1L << 39;
  @ApiModelProperty(
      value = "access contracts")
  public final long ACCESS_CONTRACTS                = 1L << 4;
  @ApiModelProperty(
      value = "access faction war statistics")
  public final long ACCESS_FAC_WAR_STATS            = 1L << 7;
  @ApiModelProperty(
      value = "access industry jobs")
  public final long ACCESS_INDUSTRY_JOBS            = 1L << 8;
  @ApiModelProperty(
      value = "access kill log")
  public final long ACCESS_KILL_LOG                 = 1L << 9;
  @ApiModelProperty(
      value = "access asset locations")
  public final long ACCESS_LOCATIONS                = 1L << 41;
  @ApiModelProperty(
      value = "access market orders")
  public final long ACCESS_MARKET_ORDERS            = 1L << 10;
  @ApiModelProperty(
      value = "access mining ledger")
  public final long ACCESS_MINING_LEDGER            = 1L << 43;
  @ApiModelProperty(
      value = "access standings")
  public final long ACCESS_STANDINGS                = 1L << 11;
  @ApiModelProperty(
      value = "access wallet journal")
  public final long ACCESS_WALLET_JOURNAL           = 1L << 12;
  @ApiModelProperty(
      value = "access wallet transactions")
  public final long ACCESS_WALLET_TRANSACTIONS      = 1L << 13;
  @ApiModelProperty(
      value = "allow changes to meta-data")
  public final long ALLOW_METADATA_CHANGES          = 1L << 14;

  // Character Specific Resources
  @ApiModelProperty(
      value = "access character calendar event attendees")
  public final long ACCESS_CALENDAR_EVENT_ATTENDEES = 1L << 15;
  @ApiModelProperty(
      value = "access character fleet information")
  public final long ACCESS_CHARACTER_FLEETS = 1L << 44;
  @ApiModelProperty(
      value = "access character sheet")
  public final long ACCESS_CHARACTER_SHEET          = 1L << 16;
  @ApiModelProperty(
      value = "access character chat channels")
  public final long ACCESS_CHAT_CHANNELS            = 1L << 40;
  @ApiModelProperty(
      value = "access character contact notifications")
  public final long ACCESS_CONTACT_NOTIFICATIONS    = 1L << 17;
  @ApiModelProperty(
      value = "access character ship fittings")
  public final long ACCESS_FITTINGS                     = 1L << 42;
  @ApiModelProperty(
      value = "access character mail")
  public final long ACCESS_MAIL                     = 1L << 18;
  @ApiModelProperty(
      value = "access character mailing lists")
  public final long ACCESS_MAILING_LISTS            = 1L << 19;
  @ApiModelProperty(
      value = "access character medals")
  public final long ACCESS_MEDALS                   = 1L << 20;
  @ApiModelProperty(
      value = "access character notifications")
  public final long ACCESS_NOTIFICATIONS            = 1L << 21;
  @ApiModelProperty(
      value = "access character research")
  public final long ACCESS_RESEARCH                 = 1L << 22;
  @ApiModelProperty(
      value = "access character skill in training")
  public final long ACCESS_SKILL_IN_TRAINING        = 1L << 23;
  @ApiModelProperty(
      value = "access character skill queue")
  public final long ACCESS_SKILL_QUEUE              = 1L << 24;
  @ApiModelProperty(
      value = "access character upcoming calendar events")
  public final long ACCESS_UPCOMING_CALENDAR_EVENTS = 1L << 5;

  // Corporation Specific Resources
  @ApiModelProperty(
      value = "access corporation container log")
  public final long ACCESS_CONTAINER_LOG            = 1L << 25;
  @ApiModelProperty(
      value = "access corporation sheet")
  public final long ACCESS_CORPORATION_SHEET        = 1L << 26;
  @ApiModelProperty(
      value = "access corporation medals")
  public final long ACCESS_CORPORATION_MEDALS       = 1L << 27;
  @ApiModelProperty(
      value = "access corporation memeber medals")
  public final long ACCESS_MEMBER_MEDALS            = 1L << 28;
  @ApiModelProperty(
      value = "access corporation member security")
  public final long ACCESS_MEMBER_SECURITY          = 1L << 29;
  @ApiModelProperty(
      value = "access corporation member security log")
  public final long ACCESS_MEMBER_SECURITY_LOG      = 1L << 30;
  @ApiModelProperty(
      value = "access corporation member tracking")
  public final long ACCESS_MEMBER_TRACKING          = 1L << 31;
  // Removed during ESI migration
  //  @ApiModelProperty(
  //      value = "access corporation output list")
  //  public final long ACCESS_OUTPOST_LIST             = 1L << 32;
  @ApiModelProperty(
      value = "access corporation shareholders")
  public final long ACCESS_SHAREHOLDERS             = 1L << 34;
  @ApiModelProperty(
      value = "access corporation starbase list")
  public final long ACCESS_STARBASE_LIST            = 1L << 35;
  @ApiModelProperty(
      value = "access corporation structures")
  public final long ACCESS_STRUCTURES            = 1L << 45;
  @ApiModelProperty(
      value = "access corporation titles")
  public final long ACCESS_CORPORATION_TITLES       = 1L << 37;

  MaskList() {}

}
