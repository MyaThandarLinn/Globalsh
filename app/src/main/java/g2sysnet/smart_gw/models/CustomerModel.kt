package g2sysnet.smart_gw.models

data class CustomerModel (
    val  CD_PERSON:String,
    var NM_PERSON:String,
    var NO_HP:String,
    var NO_TEL:String,
    val NM_BIZ:String,
    var CD_PROVINCE:String,
    var CD_CITY:String,
    val NO_POST:Int,
    var DC_ADDRESS:String,
    var DC_ADDRESS_DETAIL:String,
    var DC_RMK:String
)