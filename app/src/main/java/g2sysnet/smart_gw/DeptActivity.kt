package g2sysnet.smart_gw

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.adapters.DepartmentAdapter
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.OrganizationModel
import kotlinx.android.synthetic.main.activity_dept.*
import java.util.ArrayList
import g2sysnet.smart_gw.libby.divider
import g2sysnet.smart_gw.models.ItemInfo


class DeptActivity : AppCompatActivity() {
    var FG_SP: String = ""
    var FG_HEADER = ""
    var Dept = ""
    internal var m_orders: ArrayList<ItemInfo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dept)

        var cd_firm = H.currentUser!!.CD_FIRM

        FG_SP = "SP_MOB_GET_DEPT"
        deptInfo("$cd_firm", FG_SP)
    }

    fun deptInfo(cd_firm: String, fg_sp: String) {

        var params = RequestParams()
        params.put("nm_sp", fg_sp)
        params.put("param", cd_firm)

        var route = H.getRoute(ip = H.ip_address)

        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.organizationModel = null
                H.organizationModel = Gson().fromJson(jAry.toString(), Array<OrganizationModel>::class.java).toList()
                m_orders = ArrayList()
                for (i in 0..H.organizationModel!!.size - 1) {
                    if (H.organizationModel!![i].NO.equals("1")) {
                        Dept = H.organizationModel!![i].DEPT
                        m_orders!!.add(
                            ItemInfo(
                                "H",
                                H.organizationModel!![i].CD_DEPT,
                                H.organizationModel!![i].NM_DEPT,
                                H.organizationModel!![i].DEPT,
                                H.organizationModel!![i].CD_EMP,
                                H.organizationModel!![i].NM_EMP,
                                H.organizationModel!![i].NO_LEVEL,
                                H.organizationModel!![i].NO_POS,
                                H.organizationModel!![i].YN_END,
                                H.organizationModel!![i].NO_HP,
                                H.organizationModel!![i].DC_EMAIL,
                                H.organizationModel!![i].DC_PHOTO,
                                H.organizationModel!![i].NO
                            )
                        )
                        for (j in 0..H.organizationModel!!.size - 1) {
                            if (Dept.equals(H.organizationModel!![j].DEPT)) {
                                if (H.organizationModel!![j].CD_EMP != "") {
                                    m_orders!!.add(
                                        ItemInfo(
                                            "D",
                                            H.organizationModel!![j].CD_DEPT,
                                            H.organizationModel!![j].NM_DEPT,
                                            H.organizationModel!![j].DEPT,
                                            H.organizationModel!![j].CD_EMP,
                                            H.organizationModel!![j].NM_EMP,
                                            H.organizationModel!![j].NO_LEVEL,
                                            H.organizationModel!![j].NO_POS,
                                            H.organizationModel!![j].YN_END,
                                            H.organizationModel!![j].NO_HP,
                                            H.organizationModel!![j].DC_EMAIL,
                                            H.organizationModel!![j].DC_PHOTO,
                                            H.organizationModel!![j].NO
                                        )
                                    )
                                }
                            }
                        }
                    }


                }

                val adapter = DepartmentAdapter(this@DeptActivity, m_orders!!)
                organ_recycler1.addItemDecoration(divider(Color.GRAY, 1))
                organ_recycler1.layoutManager = LinearLayoutManager(this@DeptActivity)
                organ_recycler1.adapter = adapter
            }

            override fun error(err: String) {
                H.L(err)
            }
        })
    }
}
