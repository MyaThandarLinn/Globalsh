package g2sysnet.smart_gw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.adapters.ApprovalAdapter
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.ApprovalModel
import kotlinx.android.synthetic.main.activity_approval.*
import org.jetbrains.anko.toast

class ApprovalActivity : AppCompatActivity(), EventChanger {

    internal var strBtn01 = "SN_GRW_APP_REG_001"    //미결
    internal var strBtn02 = "SN_GRW_APP_REG_002"    //기안
    internal var strBtn03 = "SN_GRW_APP_REG_003"    //진행
    internal var strBtn04 = "SN_GRW_APP_REG_013"    //기결
    internal var strBtn05 = "SN_GRW_APP_REG_004"    //완료
    internal var strBtn06 = "SN_GRW_APP_REG_005"    //반려
    internal var strBtn07 = "SN_GRW_APP_REG_008"    //예결
    internal var strBtn08 = "SN_GRW_APP_REG_009"    //후결

    internal var FG_BOARD = "SN_GRW_APP_REG_001"

    var msg_loading: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approval)

        supportActionBar?.hide()

        msg_loading = getString(R.string.msg_loading)

        requestData(FG_BOARD)

        btn01.setOnClickListener {
            txtMain.setText(btn01.text)
            FG_BOARD = strBtn01
            requestData(FG_BOARD)
            //toast("Incomplete")
        }
        btn02.setOnClickListener {
            txtMain.setText(btn02.text)
            FG_BOARD = strBtn02
            requestData(FG_BOARD)
            //toast("Draft")
        }
        btn03.setOnClickListener {
            txtMain.setText(btn03.text)
            FG_BOARD = strBtn03
            requestData(FG_BOARD)
            //toast("Proceed")
        }
        btn04.setOnClickListener {
            txtMain.setText(btn04.text)
            FG_BOARD = strBtn04
            requestData(FG_BOARD)
            //toast("Faults")
        }
        btn05.setOnClickListener {
            txtMain.setText(btn05.text)
            FG_BOARD = strBtn05
            requestData(FG_BOARD)
            //toast("Completed")
        }
        btn06.setOnClickListener {
            txtMain.setText(btn06.text)
            FG_BOARD = strBtn06
            requestData(FG_BOARD)
            //toast("Sorry!")
        }
        btn07.setOnClickListener {
            txtMain.setText(btn07.text)
            FG_BOARD = strBtn07
            requestData(FG_BOARD)
            //toast("Yes Defects")
        }
        btn08.setOnClickListener {
            txtMain.setText(btn08.text)
            FG_BOARD = strBtn08
            requestData(FG_BOARD)
            //toast("Post-defect")
        }

        approval_recycler.layoutManager = LinearLayoutManager(this)

    }

    private fun requestData(fG_BOARD: String) {
        val dialog = H.progressDialog(this@ApprovalActivity)
        dialog.show()
        val params = RequestParams()
        val CD_FIRM = H.currentUser!!.CD_FIRM
        val CD_EMP = H.currentUser!!.CD_EMP

        params.put("nm_sp", "SP_GRW_APP_DETAIL_MBL")
        params.put("param", "$CD_FIRM|$CD_EMP|$fG_BOARD||")

        val route = H.getRoute(ip = H.ip_address)
        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.APPROVAL_LIST = Gson().fromJson(jAry.toString(), Array<ApprovalModel>::class.java).toList()
                val adapter = ApprovalAdapter(this@ApprovalActivity, H.APPROVAL_LIST!!, this@ApprovalActivity, fG_BOARD)
                approval_recycler.adapter = adapter
                dialog.dismiss()
            }

            override fun error(err: String) {
                approval_recycler.removeAllViewsInLayout()
                toast(R.string.msg_search_none)

                dialog.dismiss()
                H.L(err)
            }

        })

    }

    override fun change(id: Int) {
        H.curRow = id
    }
}
