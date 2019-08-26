package g2sysnet.smart_gw


import android.app.ProgressDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.connect.MessageAdapter
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.libby.divider
import g2sysnet.smart_gw.model.MessageModel
import kotlinx.android.synthetic.main.message.*
import org.jetbrains.anko.toast
import java.util.ArrayList

class Message : AppCompatActivity(), EventChanger {

    var cd_firm = H.currentUser!!.CD_FIRM
    var cd_emp = H.currentUser!!.CD_EMP
    var m_orders: ArrayList<MessageModel>? = null



    override fun onRestart() {
        super.onRestart()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message)
//        var pgsBar = pBar as ProgressBar
//        pgsBar.visibility= View.VISIBLE

        supportActionBar?.hide()

        H.FG_SP = "SP_GRW_MSG_UNREAD_01"
        messageUnreadInfo("$cd_firm", "$cd_emp", H.FG_SP, "Unread Message")
        btn_unread_note.isSelected = true
        btn_received_note.isSelected = false
        btn_send_note.isSelected = false

//        pgsBar.visibility=View.GONE
        btn_unread_note.setOnClickListener {
            //btn02
            H.FG_SP = "SP_GRW_MSG_UNREAD_01"
            H.FG_TAB = "TAB02"
            btn_unread_note.isSelected = true
            btn_received_note.isSelected = false
            btn_send_note.isSelected = false
            toast("this is the Un read message")
            //messageInfo(cd_firm, cd_emp, FG_SP)
            messageUnreadInfo("$cd_firm", "$cd_emp", H.FG_SP, "Unread Message")
}


        btn_received_note.setOnClickListener {
            //btn01
            H.FG_SP = "SP_GRW_MSG_READ_01"
            H.FG_TAB = "TAB01"
            btn_unread_note.isSelected = false
            btn_received_note.isSelected = true
            btn_send_note.isSelected = false
            toast("this is the Received message")
            messageReceiveInfo("$cd_firm", "$cd_emp", H.FG_SP, "Receive Message")


        }


        btn_send_note.setOnClickListener {
            //btn03
            H.FG_SP = "SP_GRW_MSG_SEND_01"
            H.FG_TAB = "TAB03"
            btn_unread_note.isSelected = false
            btn_received_note.isSelected = false
            btn_send_note.isSelected = true
            toast("this is the Send note message")
            messageSendInfo("$cd_firm", "$cd_emp", H.FG_SP, "Send Message ")

        }
    }

    fun messageUnreadInfo(cd_firm: String, cd_emp: String, fg_sp: String, message: String) {


        var pgsBar = pBar as ProgressBar
        pgsBar.visibility= View.VISIBLE
        tv_progress.visibility=View.VISIBLE

        var params = RequestParams()
        params.put("nm_sp", "$fg_sp")//
        params.put("param", "$cd_firm|$cd_emp")
        var route = H.getRoute(ip = H.ip_address)
        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                H.L("$message $result")
                val jAry = H.strToJArr(result)
                H.messageModel =
                    Gson().fromJson(jAry.toString(), Array<MessageModel>::class.java).toList()
                val adapter = MessageAdapter(this@Message, H.messageModel!!, this@Message)
                message_recycler.layoutManager = LinearLayoutManager(this@Message)
                message_recycler.adapter = adapter
                pgsBar.visibility=View.GONE
                tv_progress.visibility=View.GONE


            }

            override fun error(err: String) {
                H.L(err)
                message_recycler.removeAllViewsInLayout()

                pgsBar.visibility=View.GONE
                tv_progress.visibility=View.GONE

            }




        })




    }


    fun messageSendInfo(cd_firm: String, cd_emp: String, fg_sp: String, message: String) {


        var pgsBar = pBar as ProgressBar
        pgsBar.visibility= View.VISIBLE
        tv_progress.visibility=View.VISIBLE
        var params = RequestParams()
        params.put("nm_sp", "$fg_sp")//
        params.put("param", "$cd_firm|$cd_emp")
        var route = H.getRoute(ip = H.ip_address)
        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                H.L("$message $result")
                val jAry = H.strToJArr(result)

                H.messageModel = Gson().fromJson(jAry.toString(), Array<MessageModel>::class.java).toList()

                val adapter = MessageAdapter(this@Message, H.messageModel!!, this@Message)
                message_recycler.layoutManager = LinearLayoutManager(this@Message)
                message_recycler.adapter = adapter

                pgsBar.visibility=View.GONE
                tv_progress.visibility=View.GONE


            }

            override fun error(err: String) {
                H.L(err)
                message_recycler.removeAllViewsInLayout()
                //dialog.dismiss()
                pgsBar.visibility=View.GONE
                tv_progress.visibility=View.GONE
            }

        })


    }

    fun messageReceiveInfo(cd_firm: String, cd_emp: String, fg_sp: String, message: String) {

//        val dialog = H.progressDialog(this@Message)
//        dialog.show()

        var pgsBar = pBar as ProgressBar
        pgsBar.visibility= View.VISIBLE
        tv_progress.visibility=View.VISIBLE
        var params = RequestParams()
        params.put("nm_sp", "$fg_sp")//
        params.put("param", "$cd_firm|$cd_emp")
        var route = H.getRoute(ip = H.ip_address)
        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                H.L("$message $result")
                val jAry = H.strToJArr(result)
                H.messageModel =
                    Gson().fromJson(jAry.toString(), Array<MessageModel>::class.java).toList()
                val adapter = MessageAdapter(this@Message, H.messageModel!!, this@Message)
                message_recycler.layoutManager = LinearLayoutManager(this@Message)
                message_recycler.adapter = adapter

                pgsBar.visibility=View.GONE
                tv_progress.visibility=View.GONE
//                dialog.dismiss()

            }

            override fun error(err: String) {
                H.L(err)
                message_recycler.removeAllViewsInLayout()
                //dialog.dismiss()
                pgsBar.visibility=View.GONE
                tv_progress.visibility=View.GONE
            }

        })



    }

    override fun change(id: Int) {
        H.curRow = id
    }


}
