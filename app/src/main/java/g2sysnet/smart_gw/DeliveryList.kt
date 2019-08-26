package g2sysnet.smart_gw

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.ArrayAdapter
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.adapters.ShippingListAdapter
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.DeliveryListModel
import g2sysnet.smart_gw.models.DeliveryVan
import kotlinx.android.synthetic.main.activity_delivery_list.*
import org.jetbrains.anko.custom.style
import org.jetbrains.anko.toast
import java.util.*

class DeliveryList : AppCompatActivity(),EventChanger {

    var dc_van_list: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_list)

        // Set the toolbar as support action bar
        setSupportActionBar(toolbar)
        // Now get the support action bar

        val actionBar = supportActionBar
        // Set toolbar title/app title
        actionBar!!.title = getString(R.string.title_delivery_list)
        // Set action bar elevation
        actionBar.elevation = 4.0F
        // Display the app icon in action bar/toolbar
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setLogo(R.mipmap.ic_left_logo)
        actionBar.setDisplayUseLogoEnabled(true)
        saEt.text=H.currentUser!!.NM_USER!!
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        H.updateView(this@DeliveryList,year,month,day)
        dlvlist_dt_tv.text=H.getDate(this@DeliveryList)

        btn_f_to_delivery_detail.setOnClickListener {
            var intent = Intent(this@DeliveryList, DeliveryDetail::class.java)
            startActivity(intent)
        }

        btn_complete_processing.setOnClickListener {
            val curDelList = H.deliveryList!![H.curRow]
            when (curDelList.FG_DLV) {
                "DLV" -> startActivity(Intent(this@DeliveryList, DeliveryCompleteDelivery::class.java))
                "SET" -> startActivity(Intent(this@DeliveryList, DeliveryCompleteInstall::class.java))
            }
            /*
            SET : 설치 => Delivery Completion (Install)
            DLV : 납품 => Delivery Completion (delivery)
            */
        }


        btn_installation_confirm.setOnClickListener {
            val curDelList = H.deliveryList!![H.curRow]
            when (curDelList.FG_DLV) {
                "DLV" ->{
                    it.isEnabled = false
                }
                "SET" -> {
                    startActivity(Intent(this@DeliveryList, InstallationConfirmation::class.java))
                }
            }
            /*
            SET : 설치 => Installation Confirmation
            DLV : 납품 => Disable Button
            */
        }

        dlvlist_dt_tv.setOnClickListener {
            clickDataPicker(it)
        }

        dlvlist_btn_left.setOnClickListener{
            H.cal.add(Calendar.DATE, -1)
            dlvlist_dt_tv.text=H.getDate(this@DeliveryList)
            requestData()
        }

        dlvlist_btn_right.setOnClickListener{
            H.cal.add(Calendar.DATE, 1)
            dlvlist_dt_tv.text=H.getDate(this@DeliveryList)
            requestData()
        }

        tableRecycler.layoutManager = LinearLayoutManager(this)

        val params=RequestParams()
        params.put("nm_sp",getString(R.string.shipping_mbl_mas_codel_s_01))
        params.put("param",H.joinString(arrayOf(H.currentUser!!.CD_FIRM, "PRM066", " | |")))

        val route=H.getRoute(ip=H.ip_address)
        H.doPost(route,params, object : WaitInter {
            override fun responseSuccess(result: String) {
                    val jAry=H.strToJArr(result)
                    H.deliveryVan = Gson().fromJson(jAry.toString(), Array<DeliveryVan>::class.java).toList()

                    var list: MutableList<String> = mutableListOf()
                    H.deliveryVan?.forEach {
                        list.add(it.NM_FLAG)
                        dc_van_list.add(it.CD_FLAG)
                    }
                    vehicle_spinner.adapter = ArrayAdapter(this@DeliveryList, android.R.layout.simple_spinner_item, list)
                    requestData()
            }

            override fun error(err: String) {
                toast(err)
            }
        })
    }

    private fun requestData() {
        val dt=dlvlist_dt_tv.text.toString()

        val CD_FLAG = dc_van_list[vehicle_spinner.selectedItemPosition]
        val CD_DATE = dt.replace("/", "")
        val param = arrayOf(H.currentUser!!.CD_FIRM, CD_FLAG, CD_DATE, "|")

        val secondparams=RequestParams()
        secondparams.put("nm_sp",getString(R.string.sp_mbl_dlv_s))
        secondparams.put("param",H.joinString(param))
        val route = H.getRoute(ip = H.ip_address)
        H.doPost(route,secondparams, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry=H.strToJArr(result)
                H.deliveryList = Gson().fromJson(jAry.toString(), Array<DeliveryListModel>::class.java).toList()
                val adapter = ShippingListAdapter(this@DeliveryList, H.deliveryList!!, this@DeliveryList)
                tableRecycler.adapter = adapter
                btn_installation_confirm.isEnabled=!H.deliveryList!![H.curRow].FG_DLV.equals("DLV")
                btn_f_to_delivery_detail.isEnabled=true
                btn_complete_processing.isEnabled=true

            }

            override fun error(err: String) {
                toast(err)
                H.deliveryList= arrayListOf()
                val adapter = ShippingListAdapter(this@DeliveryList, H.deliveryList!!, this@DeliveryList)
                tableRecycler.adapter = adapter

                btn_f_to_delivery_detail.isEnabled=false
                btn_complete_processing.isEnabled=false
                btn_installation_confirm.isEnabled=false

            }
        })
    }


    private fun clickDataPicker(it: View?) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            H.updateView(this@DeliveryList,year,monthOfYear,dayOfMonth)
            dlvlist_dt_tv.text=H.getDate(this@DeliveryList)
            requestData()
        }, year, month, day)
        dpd.show()
    }

    override fun change(id: Int) {
        H.curRow= id
        btn_installation_confirm.isEnabled=!H.deliveryList!![H.curRow].FG_DLV.equals("DLV")

    }
}
