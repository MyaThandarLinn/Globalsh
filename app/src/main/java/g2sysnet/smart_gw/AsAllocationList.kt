package g2sysnet.smart_gw

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.adapters.AllocationListAdapter
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.AllocationListModel
import kotlinx.android.synthetic.main.activity_as_allocation_list.*
import org.jetbrains.anko.toast
import java.util.*

class AsAllocationList : AppCompatActivity(), EventChanger {

    val route=H.getRoute(ip=H.ip_address)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_as_allocation_list)

        // Set the toolbar as support action bar
        setSupportActionBar(toolbar)

        // Now get the support action bar
        val actionBar = supportActionBar

        // Set toolbar title/app title
        actionBar!!.title = getString(R.string.title_allocation_list)

        // Set action bar elevation
        actionBar.elevation = 4.0F

        // Display the app icon in action bar/toolbar
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setLogo(R.mipmap.ic_left_logo)
        actionBar.setDisplayUseLogoEnabled(true)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        H.updateView(this@AsAllocationList,year,month,day)
        as_dt_tv.text=H.getDate(this@AsAllocationList)
        as_current_usr.text=H.currentUser!!.NM_USER!!

        requestData()
        
        as_dt_tv.setOnClickListener{
            clickDataPicker(it)
        }

        as_btn_left.setOnClickListener{
            H.cal.add(Calendar.DATE, -1)
            as_dt_tv.text=H.getDate(this@AsAllocationList)
            requestData()
        }

        as_btn_right.setOnClickListener{
            H.cal.add(Calendar.DATE, 1)
            as_dt_tv.text=H.getDate(this@AsAllocationList)
            requestData()
        }

        astableRecycler.layoutManager = LinearLayoutManager(this)
//        toast("As Allocation List")

        btn_f_to_cust_info.setOnClickListener {
            startActivity(Intent(this@AsAllocationList,CustomerInformation::class.java))
        }

        btn_to_recetption_info.setOnClickListener {
            startActivity(Intent(this@AsAllocationList,ReceptionInformation::class.java))
        }

        btn_to_ascomplete.setOnClickListener {
            startActivity(Intent(this@AsAllocationList,ASComplete::class.java))
        }


    }

    private fun requestData() {
        val dt=as_dt_tv.text.toString()
        val CD_DATE = dt.replace("/", "")

        val param = arrayOf(H.currentUser!!.CD_FIRM,H.currentUser!!.CD_USER, CD_DATE, "|")

        val params=RequestParams()
        params.put("nm_sp",getString(R.string.sp_mbl_crm_s))
        params.put("param",H.joinString(param))

        H.doPost(route,params, object : WaitInter {
            override fun responseSuccess(result: String) {

                val jAry=H.strToJArr(result)
                H.asAllocationList = Gson().fromJson(jAry.toString(), Array<AllocationListModel>::class.java).toList()
                val adapter = AllocationListAdapter(this@AsAllocationList, H.asAllocationList!!, this@AsAllocationList)
                astableRecycler.adapter = adapter

                btn_f_to_cust_info.isEnabled=true
                btn_to_recetption_info.isEnabled=true
                btn_to_ascomplete.isEnabled=true

            }
            override fun error(err: String) {
                toast(err)
                H.asAllocationList = arrayListOf()
                val adapter = AllocationListAdapter(this@AsAllocationList, H.asAllocationList!!, this@AsAllocationList)
                astableRecycler.adapter = adapter

                btn_f_to_cust_info.isEnabled=false
                btn_to_recetption_info.isEnabled=false
                btn_to_ascomplete.isEnabled=false
            }

        })
    }

    private fun clickDataPicker(it: View?) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            H.updateView(this@AsAllocationList,year,monthOfYear,dayOfMonth)
            as_dt_tv.text=H.getDate(this@AsAllocationList)
            requestData()
        }, year, month, day)
        dpd.show()
    }

    override fun change(id: Int) {
        H.curRow = id
    }
    override fun onResume() {
        super.onResume()
        H.curRow=0
        H.requestAsAllocationData(as_dt_tv.text.toString())
        requestData()
    }
}
