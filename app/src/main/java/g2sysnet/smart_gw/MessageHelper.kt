package g2sysnet.smart_gw

import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.m_model.ItemInfo_m_orders
import g2sysnet.smart_gw.model.MessageModel

class MessageHelper {

    fun getMessageUser(msgmodel:MessageModel) {
        val params = RequestParams()
        //CD_FIRM|NO_MESSAGE

        params.put("nm_sp", "SP_GET_MESSAGE_USER")
        params.put("param", "${H.currentUser!!.CD_FIRM}|${msgmodel.NO_MESSAGE}")
        val route = H.getRoute(ip = H.ip_address)
        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {

                H.L("Get Message User is \n*********** $result")
            }

            override fun error(err: String) {
                H.L("Get Message User Error is $err  Error")
            }

        })


    }


    fun getDeviceToken() {
        var params = RequestParams()

        var cd_firm = H.currentUser!!.CD_FIRM
        params.put("nm_sp", "SP_MOB_DEVICE_ID_S")
        //CD_FIRM
        params.put("param", "$cd_firm")
        var route = H.getRoute(ip = H.ip_address)

        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {

                val jAry = H.strToJArr(result)
                H.deviceTokenMd =
                    Gson().fromJson(jAry.toString(), Array<ItemInfo_m_orders>::class.java).toList()

                H.L("Device token is ${H.deviceTokenMd}")

                var lists = H.deviceTokenMd
                for (x in 0.until(lists!!.size - 1)) {
                    H.L(lists[x].ID_DEVICE + "$x\n \t \t  Device Token **************************************")
                }
            }

            override fun error(err: String) {
                H.L("Error from Device Token $err")
            }

        })


    }





}