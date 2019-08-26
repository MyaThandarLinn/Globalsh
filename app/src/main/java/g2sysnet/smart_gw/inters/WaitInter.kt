package g2sysnet.smart_gw.inters

interface WaitInter {
    fun responseSuccess(result: String )
    fun error(err: String)
}