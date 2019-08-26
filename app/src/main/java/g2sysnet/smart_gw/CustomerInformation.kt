package g2sysnet.smart_gw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.AllocationListModel
import g2sysnet.smart_gw.models.CustomerModel
import g2sysnet.smart_gw.models.SpinnerChildModel
import kotlinx.android.synthetic.main.activity_customer_infromation.*
import org.jetbrains.anko.toast
import com.jaredrummler.materialspinner.MaterialSpinner

class CustomerInformation : AppCompatActivity() {

    var asProvince:MutableList<String> = mutableListOf()
    var asMuni:MutableList<String> = mutableListOf()

    var CD_PROVINCE=""
    var CP_CITY=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_infromation)

        // Set the toolbar as support action bar
        setSupportActionBar(as_customerinfo_toolbar)
        // Now get the support action bar
        val actionBar = supportActionBar
        // Set toolbar title/app title
        actionBar!!.title = getString(R.string.title_customer_info)
        // Set action bar elevation
        actionBar.elevation = 4.0F
        // Display the app icon in action bar/toolbar
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setLogo(R.mipmap.ic_left_logo)
        actionBar.setDisplayUseLogoEnabled(true)

        val list: AllocationListModel = H.asAllocationList!![H.curRow]
        val CD_FIRM = list.CD_FIRM
        val cdperson = list.CD_PERSON

        val param = RequestParams()
        param.put("nm_sp", "SP_MBL_PERSON_S")
        param.put("param", "$CD_FIRM|$cdperson")
        val url = H.getRoute(ip = H.ip_address)
        H.doPost(url,param,object :WaitInter{
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.customer = Gson().fromJson(jAry.toString(), Array<CustomerModel>::class.java).toList()
                val customer = H.customer!![0]
                edt_cd_customer.text = customer.CD_PERSON
                customer_name.setText(customer.NM_PERSON)
                mobile_no.setText(customer.NO_HP)
                phone_no.setText(customer.NO_TEL)
                reg.text = customer.NM_BIZ
                address_no_post.setText(String.format("%05d",customer.NO_POST.toString().toInt()))
                as_dc_address.setText(customer.DC_ADDRESS)
                as_dc_address_detail.setText(customer.DC_ADDRESS_DETAIL)
                as_dc_rmk.setText(customer.DC_RMK)

                CD_PROVINCE=customer.CD_PROVINCE
                CP_CITY=customer.CD_CITY

                setUp_ProvienceSpinner()
                H.L(String.format("%02d",5))
            }

            override fun error(err: String) {
                toast(err)
            }

        })

        btn_to_allocationlist.setOnClickListener {
            finish()
//            startActivity(Intent(this@CustomerInformation, AsAllocationList::class.java))
        }

        btn_save_modify.setOnClickListener {
            val CD_PERSON  = edt_cd_customer.text.toString()
            val NM_PERSON  = customer_name.text.toString()
            val no  = mobile_no.text.toString()
            val NO_HP = no.replace("-","")
            val notel  = phone_no.text.toString()
            val NO_TEL = notel.replace("-","")
            val DC_ADDRESS  =as_dc_address.text.toString()
            val DC_ADDRESS_DETAIL  =as_dc_address_detail.text.toString()
            val DC_RMK  =as_dc_rmk.text.toString()
            val url = H.getRoute(ip = H.ip_address)
            val param = RequestParams()
            param.put("nm_sp","SP_MBL_PERSON_U")
            param.put(
                "param",
                "$CD_FIRM|$CD_PERSON|$NM_PERSON|$NO_HP|$NO_TEL|$CD_PROVINCE|$CP_CITY|$DC_ADDRESS|$DC_ADDRESS_DETAIL|$DC_RMK")
            H.L(param.toString())
            H.L(url)
            H.doPost(url,param,object : WaitInter{
                override fun responseSuccess(result: String) {
                    H.L(result)
                    toast("Saved Successfully!")
                }

                override fun error(err: String) {
                    toast(err)
                }

            })
        }
        btn_reception_info.setOnClickListener {
            startActivity(Intent(this@CustomerInformation,ReceptionInformation::class.java))
        }
        btn_to_as_complete.setOnClickListener {
            startActivity(Intent(this@CustomerInformation,ASComplete::class.java))
        }
    }

    //Province Spinner
    private fun setUp_ProvienceSpinner(){
        val asProvienceparams=RequestParams()
        asProvienceparams.put("nm_sp",getString(R.string.shipping_mbl_mas_codel_s_01))
        asProvienceparams.put("param",H.joinString(arrayOf(H.currentUser!!.CD_FIRM, "MAS033", " | |")))
        val asProvnceroute=H.getRoute(ip=H.ip_address)
        H.doPost(asProvnceroute,asProvienceparams, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry=H.strToJArr(result)
                H.spAsProvince = Gson().fromJson(jAry.toString(), Array<SpinnerChildModel>::class.java).toList()

                var metrolist: MutableList<String> = mutableListOf()
                H.spAsProvince?.forEach {
                    metrolist.add(it.NM_FLAG)
                    asProvince.add(it.CD_FLAG)
                }

                spn_metro.setItems(metrolist)
                spn_metro.selectedIndex=asProvince.indexOf(CD_PROVINCE)

                setUp_Spinner_muni(metrolist[spn_metro.selectedIndex],CP_CITY)

                spn_metro.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
                    reqMuniSpData(item)
                    H.L(item)
                    CD_PROVINCE = asProvince[position]
                    H.L("CD_PROVIENCE"+CD_PROVINCE)
                })
            }

            override fun error(err: String) {
                toast(err)
            }

        })

    }

    //Muni Spinner
    private fun setUp_Spinner_muni(NM_PROVIENCE : String,cdProvience:String){
        H.L("Reached SetUP"+NM_PROVIENCE)
        val CD_FIRM = H.currentUser!!.CD_FIRM
        val asMunispparams=RequestParams()
        asMunispparams.put("nm_sp",getString(R.string.shipping_mbl_mas_codel_s_01_child))
        asMunispparams.put("param", "$CD_FIRM|MAS112|||$NM_PROVIENCE")
        val asMunisproute=H.getRoute(ip=H.ip_address)
        H.doPost(asMunisproute,asMunispparams, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry=H.strToJArr(result)
                H.spAsMuni = Gson().fromJson(jAry.toString(), Array<SpinnerChildModel>::class.java).toList()

                var munilist: MutableList<String> = mutableListOf()
                asMuni.clear()
                H.spAsMuni?.forEach {
                    munilist.add(it.NM_FLAG)
                    asMuni.add(it.CD_FLAG)
                }
                //H.L(munilist.toString())
                H.L(asMuni.toString())
                spn_muni.setItems(munilist)
                spn_muni.selectedIndex=asMuni.indexOf(cdProvience)
                CP_CITY=asMuni[spn_muni.selectedIndex]

                spn_muni.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
                    CP_CITY=asMuni[position]

                    H.L("CD_CITY"+CP_CITY)
                })

            }

            override fun error(err: String) {
                toast(err)
            }
        })
    }

    private fun reqMuniSpData(nm_provience:String){
        val CD_FIRM = H.currentUser!!.CD_FIRM
        val asMunispparams=RequestParams()
        asMunispparams.put("nm_sp",getString(R.string.shipping_mbl_mas_codel_s_01_child))
        asMunispparams.put("param", "$CD_FIRM|MAS112|||$nm_provience")
        val asMunisproute=H.getRoute(ip=H.ip_address)
        H.doPost(asMunisproute,asMunispparams, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry=H.strToJArr(result)
                H.spAsMuni = Gson().fromJson(jAry.toString(), Array<SpinnerChildModel>::class.java).toList()

                var munilist: MutableList<String> = mutableListOf()
                asMuni.clear()
                H.spAsMuni?.forEach {
                    munilist.add(it.NM_FLAG)
                    asMuni.add(it.CD_FLAG)
                }
                setUp_Spinner_muni(nm_provience,asMuni[0])
            }

            override fun error(err: String) {
                toast(err)
            }
        })
    }
}
