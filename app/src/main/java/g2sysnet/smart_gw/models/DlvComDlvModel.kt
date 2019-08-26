package g2sysnet.smart_gw.models

data class DlvComDlvModel(
    val NM_PERSON: String,
    val NO_HP: String,
    val DC_ADDRESS: String,
    val DC_ADDRESS_DETAIL: String,
    val NM_ITEM: String,
    var NO_SN: String,
    val NM_CONFIRM: String,
    val FG_RELATION: String,
    val DT_DLV: String,
    val DC_CONFIRM_SIGN: String,
    val CD_NOTDLV: String,
    val DC_NOTDLV: String,
    val URL_DC_CONFIRM_SIGN : String
)
