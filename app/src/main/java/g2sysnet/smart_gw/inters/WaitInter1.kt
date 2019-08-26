package g2sysnet.smart_gw.inters

import org.json.JSONArray

interface WaitInter1 {
    fun callback(jAry: JSONArray, name: String)
    fun error(err: String)
}