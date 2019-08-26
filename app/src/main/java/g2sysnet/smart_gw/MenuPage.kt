package g2sysnet.smart_gw

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.connect.MessageAdapter
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.libby.divider
import g2sysnet.smart_gw.model.MessageModel
import g2sysnet.smart_gw.models.QT_COUNT_MODEL
import kotlinx.android.synthetic.main.activity_menu_page.*
import org.jetbrains.anko.toast
import java.util.ArrayList

class MenuPage : AppCompatActivity(),EventChanger {

    internal var strBtn01 = "SN_GRW_APP_REG_001"    //미결
    internal var strBtn02 = "SN_GRW_APP_REG_002"    //기안
    internal var strBtn03 = "SN_GRW_APP_REG_003"    //진행
    internal var strBtn04 = "SN_GRW_APP_REG_013"    //기결
    internal var strBtn05 = "SN_GRW_APP_REG_004"    //완료
    internal var strBtn06 = "SN_GRW_APP_REG_005"    //반려
    internal var strBtn07 = "SN_GRW_APP_REG_008"    //예결
    internal var strBtn08 = "SN_GRW_APP_REG_009"    //후결
    var m_orders_count: ArrayList<BoardItemInfoCount>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_page)

        btnERP.setOnClickListener {
            startActivity(Intent(this@MenuPage, HomePage::class.java))
        }
        btnApproval.setOnClickListener {
            startActivity(Intent(this@MenuPage, ApprovalActivity::class.java))
        }
        btnUserInfo.setOnClickListener {
            H.chooseUserModel=null
            startActivity(Intent(this@MenuPage, UserInfo::class.java))
        }
        btnOrganization.setOnClickListener {
            startActivity(Intent(this@MenuPage, DeptActivity::class.java))
        }
        btnComunication.setOnClickListener {
            toast(R.string.msg_not_prepared)
        }
        btnBoard.setOnClickListener {
            startActivity(Intent(this@MenuPage, BoardActivity::class.java))
        }
        btnEmail.setOnClickListener {
            val manager = packageManager
            val intent = manager.getLaunchIntentForPackage("com.google.android.gm")
            intent!!.addCategory(Intent.CATEGORY_LAUNCHER)
            //startActivity(intent)
        }

        val cd_firm = H.currentUser!!.CD_FIRM
        val nm_firm = H.currentUser!!.NM_FIRM
        val nm_dept = H.currentUser!!.NM_DEPT
        et_F_and_D.text = "[$nm_firm $nm_dept]"

        val cd_emp = H.currentUser!!.CD_EMP
        val nm_emp = H.currentUser!!.NM_EMP
        et_emp_info.text = "($cd_emp)$nm_emp"

        setUpApprovalCount()
        setUpMessageCount()
        btnMessage.setOnClickListener {
            startActivity(Intent(this@MenuPage,Message::class.java))
        }

    }

    private fun setUpMessageCount() {
        H.FG_TAB = "TAB02"
        val cd_firm = H.currentUser!!.CD_FIRM
        val cd_emp = H.currentUser!!.CD_EMP
        var params = RequestParams()
        params.put("nm_sp", "SP_GRW_MSG_UNREAD_01")
        params.put("param", "$cd_firm|$cd_emp")

        var route = H.getRoute(ip = H.ip_address)

        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.L(result)
                H.messageModel = Gson().fromJson(jAry.toString(), Array<MessageModel>::class.java).toList()
                H.L("Reached Adapter")
                MessageAdapter(this@MenuPage, H.messageModel!!,this@MenuPage)
                if(H.UnreadMessageCount != 0){
                    txtBadge04.setText(H.UnreadMessageCount.toString())
                    txtBadge04.visibility = View.VISIBLE
                }
                H.L(H.UnreadMessageCount.toString())

            }

            override fun error(err: String) {
                H.L(err)
            }

        })
    }

//    private fun setUpBoardCount() {
//        val route = H.getRoute(ip = H.ip_address)
//        val param = RequestParams()
//        param.put("nm_sp", "SP_GRW_APP_COUNT_MBL")
//        param.put("param", "${H.currentUser!!.CD_FIRM}|${H.currentUser!!.CD_EMP}||")
//
//        H.doPost(route, param, object : WaitInter {
//            override fun responseSuccess(result: String) {
//                val jAry = H.strToJArr(result)
//                H.iteminfoCount = null
//                H.iteminfoCount = Gson().fromJson(jAry.toString(), Array<BoardItemInfoCount>::class.java).toList()
//                m_orders_count = ArrayList()
//                for(i in 0..H.iteminfoCount!!.size-1) {
//                    m_orders_count!!.add(BoardItemInfoCount(
//                        H.iteminfoCount!![i].CD_MENU,
//                        H.iteminfoCount!![i].NM_MENU,
//                        H.iteminfoCount!![i].QT_CNT))
//                }
//                val count = H.iteminfoCount!![0]
//                if(!count.QT_CNT.contentEquals("0")){
//                    txtBadge02.setText(count.QT_CNT.toString())
//                    txtBadge02.visibility = View.VISIBLE
//                    H.L("This is Board Count "+count.QT_CNT)
//                }
//
//            }
//            override fun error(err: String) {
//                toast(err)
//            }
//        })
//    }

    private fun setUpApprovalCount() {
        val cd_firm = H.currentUser!!.CD_FIRM
        val cd_emp = H.currentUser!!.CD_EMP
        val params = RequestParams()
        params.put("nm_sp", "SP_GRW_APP_COUNT_MBL")
        params.put("param", "$cd_firm|$cd_emp||")

        val route = H.getRoute(ip = H.ip_address)
        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.QT_COUNt = Gson().fromJson(jAry.toString(), Array<QT_COUNT_MODEL>::class.java).toList()
                val count = H.QT_COUNt!![0]
                if (count.CD_MENU.contentEquals(strBtn01)) {
                    if (!count.QT_CNT.contentEquals("0")) {
                        txtBadge03.setText(count.QT_CNT)
                        txtBadge03.visibility = View.VISIBLE
                        H.L("This is Approval Count "+count.QT_CNT)
                    }
                }
            }

            override fun error(err: String) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    override fun onBackPressed() {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this)
        // set message of alert dialog
        dialogBuilder.setMessage(getString(R.string.msg_finish))
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(getString(R.string.dialog_positive), DialogInterface.OnClickListener { dialog, id ->
                val intent = Intent(this@MenuPage, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            })
            // negative button text and action
            .setNegativeButton(getString(R.string.dialog_negative), DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // show alert dialog
        alert.show()
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#A3E4D7"))
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#A3E4D7"))
    }
    override fun change(id: Int) {
        H.curRow = id
    }
}
