package g2sysnet.smart_gw

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.adapters.DlvComDlvAdapter
import g2sysnet.smart_gw.inters.EventTextChange
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.DDInfo
import g2sysnet.smart_gw.models.DeliveryListModel
import g2sysnet.smart_gw.models.DlvComDlvModel
import g2sysnet.smart_gw.models.SpinnerChildModel
import kotlinx.android.synthetic.main.activity_delivery_complete_delivery.*
import org.jetbrains.anko.toast
import java.util.*

class DeliveryCompleteDelivery : AppCompatActivity(),EventTextChange, SignaturePad.OnSignedListener {
    override fun onStartSigning() {
        dcd_signature_save.isEnabled = true
        btn_clear.isEnabled = true
    }

    override fun onClear() {
        dcd_signature_save.isEnabled = false
        btn_clear.isEnabled = false
    }

    override fun onSigned() {
        dcd_signature_save.isEnabled = true
        btn_clear.isEnabled = true
    }

    var spRelation_list:MutableList<String> = mutableListOf()
    var unpaid_reason_list:MutableList<String> = mutableListOf()
    val route=H.getRoute(ip=H.ip_address)
    var signature_path=""
    var SP_CD_NOTDLV = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_delivery_complete_delivery)

        // Set the toolbar as support action bar
        setSupportActionBar(dcd_toolbar)
        // Now get the support action bar
        val actionBar = supportActionBar
        // Set toolbar title/app title
        actionBar!!.title = getString(R.string.title_delivery_complete_delivery)
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
        H.updateView(this@DeliveryCompleteDelivery,year,month,day)
        dcd_dt_dlv_tv.text=H.getDate(this@DeliveryCompleteDelivery)

        val list: DeliveryListModel = H.deliveryList!![H.curRow]
        //H.L("This is list "+list.toString())

        //Get Dlv detail first
        val dlvdetailparams=RequestParams()
        val param = arrayOf(list.CD_FIRM, list.NO_SO, list.NO_LINE, list.NO_LINE_DLV)
        dlvdetailparams.put("nm_sp","SP_MBL_DLV_DETAIL_S")
        dlvdetailparams.put("param",H.joinString(param))
        val dlvdetailroute=H.getRoute(ip=H.ip_address)
        H.doPost(dlvdetailroute,dlvdetailparams, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry=H.strToJArr(result)
                H.ddinfos = Gson().fromJson(jAry.toString(), Array<DDInfo>::class.java).toList()
                //H.L("This is DD info "+H.ddinfos.toString())
            }
            override fun error(err: String) {
                toast(err)
            }
        })

        dcd_recycler.layoutManager= LinearLayoutManager(this)

        //Relation Spinner Child
        val spRelation=RequestParams()
        spRelation.put("nm_sp",getString(R.string.shipping_mbl_mas_codel_s_01))
        spRelation.put("param",H.joinString(arrayOf(H.currentUser!!.CD_FIRM, "PRM070", " | |")))
        val spRelationroute=H.getRoute(ip=H.ip_address)
        H.doPost(spRelationroute,spRelation, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry=H.strToJArr(result)
                H.spRelationList = Gson().fromJson(jAry.toString(), Array<SpinnerChildModel>::class.java).toList()

                var sp_relation_list: MutableList<String> = mutableListOf()
                H.spRelationList?.forEach {
                    sp_relation_list.add(it.NM_FLAG)
                    spRelation_list.add(it.CD_FLAG)
                }
                dcd_sp_relation.adapter = ArrayAdapter(this@DeliveryCompleteDelivery, R.layout.spinner_item , sp_relation_list)
            }

            override fun error(err: String) {
                toast(err)
            }

        })

        dcd_imgV.setOnSignedListener(this)


        //Unpaid Reason Spinner Child
        val unpaidReason=RequestParams()
        unpaidReason.put("nm_sp",getString(R.string.shipping_mbl_mas_codel_s_01))
        unpaidReason.put("param",H.joinString(arrayOf(H.currentUser!!.CD_FIRM, "PRM026", " | |")))
        val unpaidReasonroute=H.getRoute(ip=H.ip_address)
        H.doPost(unpaidReasonroute,unpaidReason, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry=H.strToJArr(result)
                H.unpaidReasonList = Gson().fromJson(jAry.toString(), Array<SpinnerChildModel>::class.java).toList()

                var unpaidrsonList: MutableList<String> = mutableListOf("")
                H.unpaidReasonList?.forEach {
                    unpaidrsonList.add(it.NM_FLAG)
                    unpaid_reason_list.add(it.CD_FLAG)
                }
                dcd_cd_nodlv.setItems(unpaidrsonList)
                SP_CD_NOTDLV = unpaid_reason_list[0]
                dcd_cd_nodlv.setOnItemSelectedListener { view, position, id, item ->
                    SP_CD_NOTDLV = unpaid_reason_list[position]
                    if(position!=0){
                        dcd_dc_notdlv.setBackgroundResource(R.color.white)
                        dcd_dc_notdlv.isEnabled = true
                    }else{
                        dcd_dc_notdlv.setBackgroundResource(R.color.color_transparent)
                        dcd_dc_notdlv.setText("")
                        dcd_dc_notdlv.isEnabled = false
                    }
                }
            }

            override fun error(err: String) {
                toast(err)
            }

        })


        //DCD Data Request
        val dcd_param=arrayOf(list.CD_FIRM,list.NO_SO,list.NO_LINE,list.NO_LINE_DLV)
        H.L("This is DCD Param "+dcd_param.toString()+list.CD_FIRM)
        val params=RequestParams()
        params.put("nm_sp","SP_MBL_DLV_DLV_S")
        params.put("param",H.joinString(dcd_param))

        H.doPost(route,params, object : WaitInter {
            override fun responseSuccess(result: String) {
                    val jArr=H.strToJArr(result)
                    H.dcDlv = Gson().fromJson(jArr.toString(), Array<DlvComDlvModel>::class.java).toList()
                    val adapter = DlvComDlvAdapter(this@DeliveryCompleteDelivery,H.dcDlv!!,this@DeliveryCompleteDelivery)
                    dcd_recycler.adapter = adapter

                    val currentDCI = H.dcDlv!![0]

                    H.L("This is DCI "+currentDCI)

                    dcd_nm_person.setText(currentDCI.NM_PERSON)
                    dcd_no_hp.setText(currentDCI.NO_HP)
                    dcd_address.setText(currentDCI.DC_ADDRESS)
                    dcd_address_detail.setText(currentDCI.DC_ADDRESS_DETAIL)
                    dcd_nm_confirm.setText(currentDCI.NM_CONFIRM)
                    //dcd_dc_notdlv.setText(currentDCI.CD_NOTDLV)
            }

            override fun error(err: String) {
                toast(err)
            }
        })

        dcd_dt_dlv_btn_left.setOnClickListener{
            H.cal.add(Calendar.DATE, -1)
            dcd_dt_dlv_tv.text=H.getDate(this@DeliveryCompleteDelivery)
//            requestData()
        }

        dcd_dt_dlv_btn_right.setOnClickListener{
            H.cal.add(Calendar.DATE, 1)
            dcd_dt_dlv_tv.text=H.getDate(this@DeliveryCompleteDelivery)
//            requestData()
        }

        dcd_dt_dlv_tv.setOnClickListener{
            clickDataPicker(it)
        }

        btn_go_to_pg_4.setOnClickListener {
            startActivity(Intent(this@DeliveryCompleteDelivery,DeliveryList::class.java))
        }

        btn_go_to_pg5.setOnClickListener {
            startActivity(Intent(this@DeliveryCompleteDelivery,DeliveryDetail::class.java))
        }

        btnCls.setOnClickListener {
            finish()
        }

        btn_save_and_stay.setOnClickListener {
            val CD_FIRM=list.CD_FIRM
            val NO_SO=list.NO_SO
            val NO_LINE=list.NO_LINE
            val NO_LINE_DLV =list.NO_LINE_DLV
            val NO_SN=getNoSn()
            val NM_CONFIRM=dcd_nm_confirm.text
            val FG_RELATION=spRelation_list[dcd_sp_relation.selectedItemPosition]
            val DT_DLV=dcd_dt_dlv_tv.text.toString().replace("/","")
            val upload_file_name=H.joinString(arrayOf(NO_SO,NO_LINE,NO_LINE_DLV)).replace("|","_")
            val current_milisecond=System.currentTimeMillis()
            val DC_CONFIRM_SIGN=H.createUpFileName(this@DeliveryCompleteDelivery,upload_file_name,"S",current_milisecond)
            val CD_NOTDLV=SP_CD_NOTDLV
            val DC_NOTDLV=dcd_dc_notdlv.text
            val CD_USER=H.currentUser!!.CD_USER

            val params = RequestParams()
            params.put("nm_sp","SP_MBL_DLV_DLV_U")
            params.put(
                "param","$CD_FIRM|$NO_SO|$NO_LINE|$NO_LINE_DLV|$NO_SN|$NM_CONFIRM|$FG_RELATION|$DT_DLV|$DC_CONFIRM_SIGN|$CD_NOTDLV|$DC_NOTDLV|$CD_USER|"
            )

            val route = H.getRoute(ip = H.ip_address)
            H.doPost(route, params, object : WaitInter {
                override fun responseSuccess(result: String) {
                    toast("Saved Successfully!")
                    H.uploadImage(this@DeliveryCompleteDelivery,CD_FIRM,signature_path,upload_file_name,DC_CONFIRM_SIGN,"PRM")
                    finish()
                }
                override fun error(err: String) {
                    toast(err)
                    H.L(err)
                }
            })
        }

        dcd_signature_save.setOnClickListener {
            val bitmap = dcd_imgV.signatureBitmap
            val added=H.addBitmapToGallery(bitmap)
            if (added.equals("n")) {
                toast("Fail")
            } else {
                toast("Success")
                signature_path=H.path
                it.isEnabled = false
                dcd_imgV.isEnabled = false
                btn_save_and_stay.isEnabled = true
            }
        }

        btn_clear.setOnClickListener {
            dcd_imgV.clear()
            signature_path=""
            dcd_imgV.isEnabled = true
        }
    }

    private fun getNoSn():String{
        val sb = StringBuilder()
        var count = 0
        for (dcdModel:DlvComDlvModel in H.dcDlv!!) {
            sb.append(dcdModel.NO_SN )
            if (count < H.dcDlv!!.size - 1)
                sb.append(",")
            count++
        }
        Log.d("str",sb.toString())
        return sb.toString()
    }

    private fun clickDataPicker(it: View?) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            H.updateView(this@DeliveryCompleteDelivery,year,monthOfYear,dayOfMonth)
            dcd_dt_dlv_tv.text=H.getDate(this@DeliveryCompleteDelivery)
//            requestData()
        }, year, month, day)
        dpd.show()
    }
    fun backToPage4(v: View) {
        startActivity(Intent(this@DeliveryCompleteDelivery,DeliveryList::class.java))
    }

    override fun changeList(p: Int, value: String) {
        H.dcDlv!![p].NO_SN=value
    }
}
