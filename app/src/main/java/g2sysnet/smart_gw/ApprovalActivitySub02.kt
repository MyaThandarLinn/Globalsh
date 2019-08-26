package g2sysnet.smart_gw

import android.app.AlertDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.adapters.ApprovalSub02Adapter
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.ApprovalSub02Model
import g2sysnet.smart_gw.models.ItemInfo
import kotlinx.android.synthetic.main.activity_approval_sub02.*
import org.jetbrains.anko.toast
import java.lang.Exception
import java.util.ArrayList

class ApprovalActivitySub02 : AppCompatActivity(), EventChanger {

    var NO_OPN = ""
    var CD_EMP_W = ""
    var FG_BOARD = "null"
    internal var m_orders: ArrayList<ItemInfo>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approval_sub02)

        //supportActionBar?.hide()

        val NO_APP = intent.getStringExtra("NO_APP")
        FG_BOARD = intent.getStringExtra("FG_BOARD")
        val CD_EMP = H.currentUser!!.CD_EMP

        if (FG_BOARD != "SN_GRW_APP_REG_005") {
            FG_BOARD = "null"
        }

        Search(NO_APP,FG_BOARD)

        H.L("This is NO_AP" + NO_APP)

        btnCommentAdd.setOnClickListener {
            Dialog_Comment()
        }

        btnCommentDelete.setOnClickListener {
            if (H.NO_OPN == "") {
                toast(getString(R.string.msg_no_row_select))
            } else if (CD_EMP != H.CD_EMP_W) {
                toast(R.string.msg_comment_delete_myself)
            } else {

                //region 취소전묻기
                val alertDialog = AlertDialog.Builder(this@ApprovalActivitySub02)
                alertDialog.setTitle("Delete")
                alertDialog.setMessage(getString(R.string.msg_delete))
                //alertDialog.setIcon(R.drawable.icon_alert);
                alertDialog.setPositiveButton(
                    "YES"
                ) { dialog, which -> Delete(H.NO_OPN) }
                alertDialog.setNegativeButton(
                    "NO"
                ) { dialog, which -> dialog.cancel() }
                val alert = alertDialog.create()
                alert.show()
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#A3E4D7"))
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#A3E4D7"))

            }
        }
        approval_sub02_recycler.layoutManager = LinearLayoutManager(this)
        Search(NO_APP, FG_BOARD)

    }

    private fun Search(NO_APP: String = "", FG_BOARD: String = "") {
        H.L("This is FG_Board " + FG_BOARD)
        val CD_FIRM = H.currentUser!!.CD_FIRM
        val params = RequestParams()
        params.put("nm_sp", "SP_GRW_APP_OPN_S")
        params.put("param", "$CD_FIRM|$NO_APP|$FG_BOARD")
        val route = H.getRoute(ip = H.ip_address)

        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                H.L(result)
                val jAry = H.strToJArr(result)
                H.APPROVAL_SUB02_LIST = Gson().fromJson(jAry.toString(), Array<ApprovalSub02Model>::class.java).toList()
                val adapter = ApprovalSub02Adapter(
                    this@ApprovalActivitySub02,
                    H.APPROVAL_SUB02_LIST!!,
                    this@ApprovalActivitySub02
                )
                approval_sub02_recycler.adapter = adapter
                adapter.notifyDataSetChanged()
                H.L("Recycler adapting successful!!!")
                H.L(H.APPROVAL_SUB02_LIST!![0].DC_OPN)
            }

            override fun error(err: String) {
                H.L("this is error " + err)
            }
        })
    }

    private fun Delete(no_opn : String) {
        H.L("hey I am delete action")
        val CD_FIRM = H.currentUser!!.CD_FIRM
        val NO_APP = H.APPROVAL_LIST!![0].NO_APP

        val params = RequestParams()
        params.put("nm_sp", "SP_GRW_APP_OPN_D")

        params.put("param", "$CD_FIRM|$NO_APP|$no_opn")
        val route = H.getRoute(ip = H.ip_address)

        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                toast(result)
                try {
                    val jAry = H.strToJArr(result)
                    val save_result = jAry.getJSONObject(0).getString("FG")
                    if (save_result.contentEquals("S")) {
                        toast(R.string.msg_delete_ok)
                        Search(NO_APP,FG_BOARD)
                    }
                } catch (e: Exception) {
                    toast(R.string.msg_save_error)
                    e.printStackTrace()
                    H.L(e.toString())
                }
            }

            override fun error(err: String) {
                toast(err)
            }

        })
        H.NO_OPN = ""
    }

    private fun Dialog_Comment() {
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.setTitle("코멘트 등록")        // Comment registration
        alertDialog.setMessage("코멘트를 적어주세요") // Write a comment

        // Set an EditText view to get user input
        val input = EditText(this)
        alertDialog.setView(input)

        alertDialog.setPositiveButton("Ok") { dialog, whichButton ->
            val value = input.text.toString()
            if(!value.contentEquals("")){
                Save(value)
            }
            else{
                toast("Please enter a comment")
            }

        }


        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog, whichButton ->
            // Canceled.
        }
        val alert = alertDialog.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#A3E4D7"))
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#A3E4D7"))
    }

    private fun Save(DC_OPN: String) {
        val CD_FIRM = H.currentUser!!.CD_FIRM
        val NO_APP = H.APPROVAL_LIST!![0].NO_APP
        val CD_EMP = H.currentUser!!.CD_EMP
        val CD_DEPT = H.currentUser!!.CD_DEPT

        val params = RequestParams()
        params.put("nm_sp", "SP_GRW_APP_OPN_I")
        params.put("param", "$CD_FIRM|$NO_APP|$CD_EMP|$DC_OPN|$CD_DEPT")
        val route = H.getRoute(ip = H.ip_address)
        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                if (result.contentEquals("")) {
                    H.L(
                        "Empty result                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               "
                    )
                }
                H.L(result)
                try {
                    val jAry = H.strToJArr(result)
                    val save_result = (jAry.getJSONObject(0).getString("FG"))

                    if (save_result.equals("S")) {
                        toast(R.string.msg_save_ok)
                        Search(NO_APP,FG_BOARD)
                    }
                } catch (e: Exception) {
                    toast(R.string.msg_save_error)
                    e.printStackTrace()
                    H.L(e.toString())
                }
            }

            override fun error(err: String) {
                toast(R.string.msg_save_error)
            }

        })
    }

    override fun change(id: Int) {
        H.curRow = id
    }
}
