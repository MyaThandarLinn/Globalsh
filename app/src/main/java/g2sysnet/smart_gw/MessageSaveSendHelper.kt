package g2sysnet.smart_gw

import android.content.Context
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import org.jetbrains.anko.toast


class MessageSaveSendHelper {

    var msgmodel=H.messageModel!![H.curRow]

    fun sendNotes(context: Context) {           //Send_Note
        //CD_FIRM, "S", NO_MESSAGE
        //GRW_SOCKET.aspx
        val params = RequestParams()
        params.put("nm_sp", "")
        params.put("param", "")
        var route = H.getRoute(ip = H.ip_address, endpart = "GRW_SOCKET.aspx")

        // there is no param in this route only in nm_sp
        //this is  a  SendNotes() method in this

    }



    fun saveAndSend(slip_number:String,msg2_txt_content:String,msg2_txt_recipients:String,context:Context) {


        //SAVE AND SEND
        if (slip_number == "" || slip_number == null) {
            context.toast("전표번호 채번에 실패하였습니다")
            return
        }

        //nm_sp =>  "SP_GRW_MESSAGE_SEND_I", "8001|msg20190700022|"
        // pararm=>CD_FIRM, NO_SLIP, CD_EMP, _txt03.getText().toString(), _txt02.getText().toString(), FG_SEND, DC_RKEY, CD_MENU

        val params = RequestParams()
        params.put("nm_sp", "SP_GRW_MESSAGE_SEND_I")
        params.put(
            "param",
            "${H.currentUser!!.CD_FIRM}|$slip_number${H.currentUser!!.CD_EMP}$msg2_txt_content|$msg2_txt_recipients|${msgmodel.FG_SEND}|${msgmodel.DC_RKEY}|${msgmodel.CD_MENU}"
        )
        val route = H.getRoute(ip = H.ip_address)
        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                H.L("Save and Send Data is \n $result *****Success*****")
            }

            override fun error(err: String) {
                H.L("Save and Send Error is $err Fail")
            }
        })
    }



    fun saveAndReceive(slip_number: String,context: Context) {  //Save_Receive

        var TMP_CD_ORG_RECEIVE: String
        var TMP_CD_EMP_RECEIVE: String

        if (slip_number == "" || slip_number == null) {
           context.toast("전표번호 채번에 실패하였습니다.")//전표번호 채번에 실패하였습니다.
            return
        }

      var  receive_user = H!!.receive_user_message
        if (receive_user != null) {
            var endPoint = receive_user!!.size
            for (i in 0.until(endPoint - 1)) {
                if (H.FG_HEADER == "H") {
                    TMP_CD_ORG_RECEIVE = receive_user!![i].CD_RECEIVE
                    TMP_CD_EMP_RECEIVE = ""
                } else {
                    TMP_CD_ORG_RECEIVE = msgmodel.CD_ORG_RECEIVE
                    TMP_CD_EMP_RECEIVE = receive_user!![i].CD_RECEIVE


                }

                //param=>CD_FIRM, NO_SLIP, CD_EMP, FG_ORG_RECEIVE, TMP_CD_EMP_RECEIVE, TMP_CD_ORG_RECEIVE
                //nm_sp=>"SP_GRW_MESSAGE_RECEIVE_I

                val cd_firm = H.currentUser!!.CD_FIRM
                val cd_emp = H.currentUser!!.CD_EMP
                val fg_org_receive = msgmodel.FG_ORG_RECEIVE
                var endLoop = H.getMessageUserModel!!.size
                var dataUser = H.getMessageUserModel

                val params = RequestParams()
                params.put("nm_sp", "SP_GRW_MESSAGE_RECEIVE_I")
                params.put(
                    "param",
                    "$cd_firm|$slip_number|$cd_emp|$fg_org_receive|$TMP_CD_EMP_RECEIVE|$TMP_CD_ORG_RECEIVE"
                )

                val route = H.getRoute(ip = H.ip_address)

                H.doPost(route, params, object : WaitInter {
                    override fun responseSuccess(result: String) {
                        H.L("*******************@@@@@@@@@@@@@@Save and Receive from Data is $result@@@@@@@@@@@@@@************")
                    }

                    override fun error(err: String) {
                        H.L("*******************@@@@@@@@@@@@@@Save and Receive from Error is $err @@@@@@@@@@@@@@************")

                    }

                })


            }


        } else {
            context.toast("RECEIVE USER IS NULL")
        }


    }




}