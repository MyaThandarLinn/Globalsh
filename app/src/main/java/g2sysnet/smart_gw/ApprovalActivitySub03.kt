package g2sysnet.smart_gw

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.adapters.ApprovalSub03Adapter
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.libby.divider
import g2sysnet.smart_gw.models.ApprovalSub03Model
import kotlinx.android.synthetic.main.activity_approval_sub03.*

class ApprovalActivitySub03 : AppCompatActivity(),EventChanger {
    override fun change(id: Int) {
        H.curRow= id
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approval_sub03)

        supportActionBar?.hide()
        supportActionBar?.title = "ApprovalActivitySub03"

        val CD_FIRM = H.currentUser!!.CD_FIRM
        val NO_APP = intent.getStringExtra("NO_APP")

        approval_sub03_recycler.layoutManager = LinearLayoutManager(this)

        val params = RequestParams()
        params.put("nm_sp","SP_GRW_APP_FILE_MBL")
        params.put("param","$CD_FIRM|$NO_APP")

        val route = H.getRoute(ip = H.ip_address)

        H.doPost(route,params,object : WaitInter{
            override fun responseSuccess(result: String) {
                    val jAry = H.strToJArr(result)
                    H.APPROVAL_SUB03_LIST = Gson().fromJson(jAry.toString(),Array<ApprovalSub03Model>::class.java).toList()
                    val adapter = ApprovalSub03Adapter(this@ApprovalActivitySub03,H.APPROVAL_SUB03_LIST!!,this@ApprovalActivitySub03)
                    approval_sub03_recycler.addItemDecoration(divider(Color.parseColor("#888888"),2))
                    approval_sub03_recycler.adapter = adapter
            }

            override fun error(err: String) {
                H.L(err)
            }

        })


    }
}
