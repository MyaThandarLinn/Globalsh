package g2sysnet.smart_gw.model

data class MessageModel(
    var CD_FIRM : String,
    var NO_MESSAGE: String,
    var CD_EMP_SEND:String,
    var NM_EMP_SEND:String,
    var TM_SEND:String,
    var TXT_MESSAGE:String,
    var FG_ORG_RECEIVE:String,
    var CD_EMP_RECEIVE:String,
    var CD_ORG_RECEIVE:String,
    var DC_EMP_RECEIVE:String,
    var NM_FG_SEND:String,
    var TM_READ:String,
    var FG_SEND:String,
    var DC_RKEY:String,
    var CD_MENU:String
    )
