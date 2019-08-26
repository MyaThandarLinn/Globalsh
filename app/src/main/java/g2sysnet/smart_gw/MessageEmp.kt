package g2sysnet.smart_gw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.connect.SearchEmpAdapter
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.m_model.EmpDepartmentModel
import kotlinx.android.synthetic.main.message_emp.*
import org.jetbrains.anko.toast

class MessageEmp : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message_emp)
        supportActionBar!!.title = "This is MessageEmp"
        searchDept()

        btn_dept_confirm.setOnClickListener {
            //finish()
            H.empCon = true
            toast(H.deptNameList + "****************")

            var i = Intent(this@MessageEmp, MessageP2::class.java)
            startActivity(i)
        }
        btn_dept_cancel.setOnClickListener {
            finish()
            H.empCon = false
        }
    }


    fun searchDept() {
        //param=> CD_FIRM
        //nm_sp=>"SP_MOB_GET_DEPT"

        var Dept = ""
        var cd_firm = H.currentUser!!.CD_FIRM
        val params = RequestParams()
        params.put("nm_sp", "SP_MOB_GET_DEPT")
        params.put("param", cd_firm)
        val route = H.getRoute(ip = H.ip_address)
        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {

                val jAry = H.strToJArr(result)
                H.m_order_emp_message = Gson().fromJson(
                    jAry.toString(),
                    Array<EmpDepartmentModel>::class.java
                ).toList() // as ArrayList<EmpDepartmentModel>


                val adapter = SearchEmpAdapter(this@MessageEmp, H.m_order_emp_message!!)

                dialog_search_view1.layoutManager = LinearLayoutManager(this@MessageEmp)

                dialog_search_view1.adapter = adapter



                H.L("SP_MOB_GET_DEPT MessageEmp is ${H.m_order_emp}")

            }

            override fun error(err: String) {
                H.L("SP_MOB_GET_DEPT Message Error is $err")

            }

        })

    }
}
