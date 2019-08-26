package g2sysnet.smart_gw

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.model.MessageModel
import kotlinx.android.synthetic.main.message_p1.*


class MessageP1 : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.message_p1)
        supportActionBar?.hide()

        val msg = H.messageModel!![H.curRow]



        H.L("On Create in Message Model is IN MESSAGE P1$msg")

        msg1_txt_sender.text = msg.NM_EMP_SEND + msg.TM_SEND
        msg1_txt_recipients.text = msg.DC_EMP_RECEIVE
        msg1_txt_content.text = msg.TXT_MESSAGE

        if (msg.NM_FG_SEND == "SYSTEM") {
            msg1_btn_return.visibility=View.GONE
            msg1_btn_returnAll.visibility=View.GONE
            msg1_btn_deliver.visibility=View.GONE
        }

        msg1_btn_return.setOnClickListener {
            H.TP_RETURN="N"
            changeActivity()
        }

        msg1_btn_returnAll.setOnClickListener {
            H.TP_RETURN="A"
            changeActivity()
        }

        msg1_btn_deliver.setOnClickListener {
            H.TP_RETURN="D"
            changeActivity()
        }




        msg1_trash.setOnClickListener {
            if (msg.NO_MESSAGE == "") {
                Toast.makeText(this@MessageP1, getString(R.string.msg_no_row_select), Toast.LENGTH_SHORT)
                    .show()
                finish()
            } else {
                //region 취소전묻기
                val alertDialog = AlertDialog.Builder(this@MessageP1)
                alertDialog.setTitle("Delete")
                alertDialog.setMessage(getString(R.string.msg_delete))
                alertDialog.setIcon(R.drawable.ic_trash_black)
                alertDialog.setPositiveButton("YES") { dialog, which -> Delete(msg) }
                alertDialog.setNegativeButton(
                    "NO"
                ) { dialog, which -> dialog.cancel() }
                alertDialog.show()
                //endregion
            }
        }


        if (H.FG_SP.equals("SP_GRW_MSG_UNREAD_01")) {
            update(msg)
        }
    }

    fun changeActivity(){

        var i= Intent(this@MessageP1,MessageP2::class.java)
        startActivity(i)

    }


    fun Delete(msgDelete: MessageModel) {

        //CD_FIRM, NO_MESSAGE, CD_EMP_SEND, FG_ORG_RECEIVE, CD_EMP_RECEIVE, CD_ORG_RECEIVE

        //SP_GRW_MESSAGE_RECEIVE_D

        H.L("Msg Model from msgDeleteMethod() Message P1 $msgDelete")
        var cd_firm=msgDelete.CD_FIRM
        val deleteParam = RequestParams()
        deleteParam.put("nm_sp", "SP_GRW_MESSAGE_RECEIVE_D")
        deleteParam.put(
            "param", "$cd_firm|${msgDelete.NO_MESSAGE}|${msgDelete.CD_EMP_SEND}|${msgDelete.FG_ORG_RECEIVE}|${msgDelete.CD_EMP_RECEIVE}|${msgDelete.CD_ORG_RECEIVE}"
        )
        H.L("Deletete param is $deleteParam")

        var route = H.getRoute(ip = H.ip_address)

        // H.L("Message Delete param is from Message P1 ${msgDelete.NO_MESSAGE}|${msgDelete.CD_EMP_SEND}|${msgDelete.FG_ORG_RECEIVE}${msgDelete.CD_EMP_RECEIVE}|${msgDelete.CD_ORG_RECEIVE}")
        H.doPost(route, deleteParam, object : WaitInter {
            override fun responseSuccess(result: String) {
                H.L(" Message Delete Successfully Message P1 result $result")
                finish()
            }
            override fun error(err: String) {
                H.L("Message Delete Message P1 Error $err")
            }

        })

    }

    fun update(model: MessageModel) {

        H.L("Msg Model from updateMethod() Message P1 $model")
        var cd_firm=H.messageModel!![H.curRow].CD_FIRM
        val updateparam = RequestParams()
        updateparam.put("nm_sp", "SP_GRW_MESSAGE_RECEIVE_U")
        updateparam.put(
            "param",
            "$cd_firm|${model.NO_MESSAGE}|${model.CD_EMP_SEND}|${model.FG_ORG_RECEIVE}|${model.CD_EMP_RECEIVE}|${model.CD_ORG_RECEIVE}"
        )


        var route=H.getRoute(ip=H.ip_address)
        H.doPost(route,updateparam,object:WaitInter{
            override fun responseSuccess(result: String) {
                H.L("update result $result")
            }
            override fun error(err: String) {
                H.L("update err $err")
            }

        })

    }
}

