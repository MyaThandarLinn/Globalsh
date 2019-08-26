package g2sysnet.smart_gw.models

data class DeliveryCompleteModel(
    val NM_ITEM: String,
    val NO_SN: String,
    val DC_RMK: String,
    val FG_MONEY: String,
    val TP_PAY: String,
    val AM_TOTAL: String,
    val AM_DLV: String,
    val AM_EQUIP: String,
    val AM_DLV_ADD: String,
    val FG_STAIR: String,
    val NM_CONFIRM: String,
    val FG_RELATION: String,
    val DT_DLV: String,
    val DC_PHOTO: String,
    val DC_CONFIRM_SIGN: String,
    val CD_NOTDLV: String,
    val DC_NOTDLV: String,
    val URL_DC_PHOTO : String,
    val URL_DC_CONFIRM_SIGN : String
)
