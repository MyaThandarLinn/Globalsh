package g2sysnet.smart_gw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.ReceptionInfoModel
import kotlinx.android.synthetic.main.activity_reception_information.*
import org.jetbrains.anko.toast

class ReceptionInformation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reception_information)

        // Set the toolbar as support action bar
        setSupportActionBar(as_receptioninfo_toolbar)
        // Now get the support action bar
        val actionBar = supportActionBar
        // Set toolbar title/app title
        actionBar!!.title = getString(R.string.title_reception_information)
        // Set action bar elevation
        actionBar.elevation = 4.0F
        // Display the app icon in action bar/toolbar
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setLogo(R.mipmap.ic_left_logo)
        actionBar.setDisplayUseLogoEnabled(true)

        val CD_FIRM = H.currentUser!!.CD_FIRM
        val asinfo = H.asAllocationList!![H.curRow]

        val params = RequestParams()
        params.put("nm_sp", "SP_MBL_CRM_DETAIL_S")
        params.put("param", "$CD_FIRM|${asinfo.NO_SO}|${asinfo.NO_LINE_ITEM}|${asinfo.CD_ITEM}|${asinfo.NO_LINE}")
        val route = H.getRoute(ip = H.ip_address)

        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.receptinInfo = Gson().fromJson(jAry.toString(), Array<ReceptionInfoModel>::class.java).toList()
                val recInfo = H.receptinInfo!![0]

                reg_no.text=recInfo.NO_SO
                h_tp_so.text=recInfo.TP_SO
                h_dt_so.setText(formatDate(recInfo.DT_SO))
                cd_emp.setText(recInfo.NM_EMP)
                ri_nm_person.text=recInfo.NM_PERSON
                cd_dummy02.text=recInfo.CD_DUMMY02
                cd_dummy03.text=recInfo.CD_DUMMY03
                cd_dummy04.text=recInfo.CD_DUMMY04
                ri_cd_item.text=recInfo.CD_ITEM
                nm_biz.text=recInfo.NM_BIZ
                ri_nm_item.text=recInfo.NM_ITEM
                ri_no_sn.text=recInfo.NO_SN
                ri_nmsymptom_p.text=recInfo.NM_SYMPTOM_P
                ri_nmsymptom.text=recInfo.NM_SYMPTOM
                ri_dc_symptom.text=recInfo.DC_SYMPTOM
                ri_dc_remark.text=recInfo.DC_RMK
                ri_fg_so1.text=recInfo.FG_SO1
                ri_nm_empengineer.text=recInfo.NM_EMP_ENGINNER
                h_dt_visit_plan.setText(formatDate(recInfo.DT_VISIT_PLAN))
                h_cd_visit_time.text=recInfo.CD_VISIT_TIME
                l_yn_am.text=recInfo.YN_AM

                l_am.setText(String.format("%,d",recInfo.AM.toDouble().toInt()))
                l_am_matl.setText(String.format("%,d",recInfo.AM_MATL.toDouble().toInt()))
                l_am_labor.setText(String.format("%,d",recInfo.AM_LABOR.toDouble().toInt()))
                l_am_visit.setText(String.format("%,d",recInfo.AM_VISIT.toDouble().toInt()))
                l_am_agency.setText(String.format("%,d",recInfo.AM_AGENCY.toDouble().toInt()))
            }

            override fun error(err: String) {
                toast(err)
            }

        })

                btn_to_allolist.setOnClickListener {
            startActivity(Intent(this@ReceptionInformation,AsAllocationList::class.java))
        }

        btn_to_cust_info.setOnClickListener {
            startActivity(Intent(this@ReceptionInformation,CustomerInformation::class.java))
        }

        btn_to_as_complete.setOnClickListener {
            startActivity(Intent(this@ReceptionInformation,ASComplete::class.java))
        }



    }
    fun  formatDate(str:String):String{
        return str.substring(0, 4) + "/" + str.substring(4,6)+"/"+str.substring(6,str.length)
    }
}
