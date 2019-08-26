package g2sysnet.smart_gw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.DDInfo
import g2sysnet.smart_gw.models.DeliveryListModel
import kotlinx.android.synthetic.main.activity_delivery_detail.*
import org.jetbrains.anko.toast
import java.text.DecimalFormat

class DeliveryDetail : AppCompatActivity() {

    val route = H.getRoute(ip = H.ip_address)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_detail)

        // Set the toolbar as support action bar
        setSupportActionBar(toolbar)
        // Now get the support action bar
        val actionBar = supportActionBar
        // Set toolbar title/app title
        actionBar!!.title = getString(R.string.title_delivery_detail)
        // Set action bar elevation
        actionBar.elevation = 4.0F
        // Display the app icon in action bar/toolbar
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setLogo(R.mipmap.ic_left_logo)
        actionBar.setDisplayUseLogoEnabled(true)

        val list: DeliveryListModel = H.deliveryList!![H.curRow]

        val param = arrayOf(list.CD_FIRM, list.NO_SO, list.NO_LINE, list.NO_LINE_DLV)

        val params = RequestParams()
        params.put("nm_sp", "SP_MBL_DLV_DETAIL_S")
        params.put("param", H.joinString(param))

        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {

                val jAry = H.strToJArr(result)
                H.ddinfos = Gson().fromJson(jAry.toString(), Array<DDInfo>::class.java).toList()

                val dInfo = H.ddinfos!![0]

                et_DT_DLV_SCH.setText(formatDate(dInfo.DT_DLV_SCH))
                et_CD_DLVTIME.setText(dInfo.CD_DLVTIME)
                et_D_FG_DLV.setText(dInfo.FG_DLV1)
                et_D_FG_TIME.setText(dInfo.FG_TIME)
                et_CP_NM_PERSON.setText(dInfo.NM_PERSON)
                et_NO_HP.setText(dInfo.NO_HP)
                et_CP_NO_TEL.setText(dInfo.NO_TEL)
                et_D_DC_ADDRESS.setText(dInfo.DC_ADDRESS)
                et_D_DC_ADDRESS_DETAIL.setText(dInfo.DC_ADDRESS_DETAIL)
                et_H_NO_SO.setText(dInfo.NO_SO)
                et_H_DT_SO.setText(formatDate(dInfo.DT_SO))
                et_MB_TP_BIZ1.setText(dInfo.TP_BIZ1)
                et_MB_NM_BIZ.setText(dInfo.NM_BIZ)
                et_MI_NM_ITEM.setText(dInfo.NM_ITEM)
                et_SN_NO_SN.setText(dInfo.NO_SN)

                et_D_QT.setText(String.format("%,d", dInfo.QT.toDouble().toInt()))
                et_D_FG_STAIR.setText(dInfo.FG_STAIR)
                et_D_TP_STAIR.setText(dInfo.TP_STAIR)
                et_D_FG_EQUIP.setText(dInfo.FG_EQUIP)
                et_D_TP_BUILDING.setText(dInfo.TP_BUILDING)
                et_FG_LOC.setText(dInfo.FG_LOC)

                val df2 = DecimalFormat("#,###.00")
                et_D_QT_WEIGHT.setText(df2.format(dInfo.QT_WEIGHT.toBigDecimal()))

                et_AM_DLV.setText(String.format("%,d", dInfo.AM_DLV.toDouble().toInt()))
                et_AM_EQUIP.setText(String.format("%,d", dInfo.AM_EQUIP.toDouble().toInt()))
                et_D_AM_TOTAL.setText(String.format("%,d", dInfo.AM_TOTAL.toDouble().toInt()))
                et_ME_NM_EMP.setText(dInfo.NM_EMP)
                edt_remark.setText(dInfo.DC_RMK)

            }

            override fun error(err: String) {
                toast(err)
            }

        })


        btn_to_finish.setOnClickListener {
            finish()
        }

        btn_to_delivery_completion.setOnClickListener {
            val dInfo = H.ddinfos!![0]
            when (dInfo.FG_DLV) {
                "DLV" -> startActivity(Intent(this@DeliveryDetail, DeliveryCompleteDelivery::class.java))
                "SET" -> startActivity(Intent(this@DeliveryDetail, DeliveryCompleteInstall::class.java))
            }
            /*
            SET : 설치  => Delivery Completion (Install)
            DLV : 납품  => Delivery Completion (Delivery)
             */
        }
        btn_to_installation_confirmation.setOnClickListener {
            val dInfo = H.ddinfos!![0]
            when (dInfo.FG_DLV) {
                "SET" -> startActivity(Intent(this@DeliveryDetail, InstallationConfirmation::class.java))

                "DLV" -> it.isEnabled = false
            }
            /*
            SET : 설치  => Installation Confirmation
            DLV : 납품  => Disable Button
             */
        }
    }

    fun formatDate(str: String): String {
        return str.substring(0, 4) + "/" + str.substring(4, 6) + "/" + str.substring(6, str.length)
    }


}
