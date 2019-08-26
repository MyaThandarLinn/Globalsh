package g2sysnet.smart_gw

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.CompoundButton
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.DDInfo
import g2sysnet.smart_gw.models.DeliveryListModel
import g2sysnet.smart_gw.models.InstallConfirmationModel
import kotlinx.android.synthetic.main.activity_installation_confirmation.*
import kotlinx.android.synthetic.main.activity_installation_confirmation.btn_finish
import kotlinx.android.synthetic.main.activity_installation_confirmation.btn_save_and_finish
import kotlinx.android.synthetic.main.activity_installation_confirmation.cbAdapter
import kotlinx.android.synthetic.main.activity_installation_confirmation.cbBattery
import kotlinx.android.synthetic.main.activity_installation_confirmation.cbKey
import kotlinx.android.synthetic.main.activity_installation_confirmation.cbKeyin_1
import kotlinx.android.synthetic.main.activity_installation_confirmation.cbManual
import kotlinx.android.synthetic.main.activity_installation_confirmation.cbShelf
import kotlinx.android.synthetic.main.activity_installation_confirmation.cb_agree
import kotlinx.android.synthetic.main.activity_installation_confirmation.cb_disagree
import kotlinx.android.synthetic.main.activity_installation_confirmation.cb_jbox
import kotlinx.android.synthetic.main.activity_installation_confirmation.cb_mod1_1
import kotlinx.android.synthetic.main.activity_installation_confirmation.cb_mod1_2
import kotlinx.android.synthetic.main.activity_installation_confirmation.cb_mod1_3
import kotlinx.android.synthetic.main.activity_installation_confirmation.cb_mod1_4
import kotlinx.android.synthetic.main.activity_installation_confirmation.cb_mod1_5
import kotlinx.android.synthetic.main.activity_installation_confirmation.cb_mod1_6
import kotlinx.android.synthetic.main.activity_installation_confirmation.et_mod1_Phone_number
import kotlinx.android.synthetic.main.activity_installation_confirmation.et_mod1_customer_name
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class InstallationConfirmation : AppCompatActivity(), CompoundButton.OnCheckedChangeListener,
    SignaturePad.OnSignedListener {
    override fun onStartSigning() {
        uploadSign.isEnabled = true
        btnCls.isEnabled = true
    }

    override fun onClear() {
        uploadSign.isEnabled = false
        btnCls.isEnabled = false
    }

    override fun onSigned() {
        uploadSign.isEnabled = true
        btnCls.isEnabled = true
    }

    var q_a_str = "1010^N,1020^N,1030^N"
    var q_b_str = "1040^N,1050^N,1060^N"
    var q_a_str_parent = ""

    var q_e_str_1 = "2010^N,2020^N,2030^N"
    var q_e_str_2 = "2040^N,2050^N,2060^N"
    var q_e_str_parent = ""

    var dc_quest_d = ""

    var ynPvtInfo = ""

    var signature_pad = ""
    var image_path = ""

    private val REQUEST_IMAGE_CAPTURE = 12

    val view: View? = null
    val route = H.getRoute(ip = H.ip_address)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_installation_confirmation)

        setSupportActionBar(tvtitle)

        val actionBar = supportActionBar
        // Set toolbar title/app title
        actionBar!!.title = getString(R.string.install_confirmation)
        // Set action bar elevation
        actionBar.elevation = 4.0F
        // Display the app icon in action bar/toolbar
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setLogo(R.mipmap.ic_left_logo)
        actionBar.setDisplayUseLogoEnabled(true)

        val list: DeliveryListModel = H.deliveryList!![H.curRow]

        val param = arrayOf(list.CD_FIRM, list.NO_SO, list.NO_LINE, list.NO_LINE_DLV)

        val d_params = RequestParams()
        d_params.put("nm_sp", "SP_MBL_DLV_DETAIL_S")
        d_params.put("param", H.joinString(param))

        H.doPost(route, d_params, object : WaitInter {
            override fun responseSuccess(result: String) {

                val jAry = H.strToJArr(result)
                H.ddinfos = Gson().fromJson(jAry.toString(), Array<DDInfo>::class.java).toList()

            }

            override fun error(err: String) {
                toast(err)
            }

        })

        val params = RequestParams()
        params.put("nm_sp", "SP_MBL_PRM_INSTALL_QUEST_S")
        params.put("param", "${list.CD_FIRM}|${list.NO_SO}|${list.NO_LINE}|${list.NO_LINE_DLV}")
        val route = H.getRoute(ip = H.ip_address)

        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                H.L(result)
                val jAry = H.strToJArr(result)
                H.installConfirm =
                    Gson().fromJson(jAry.toString(), Array<InstallConfirmationModel>::class.java).toList()
                var currentInstall = H.installConfirm!![0]

                et_mod1_customer_name.setText(currentInstall.NM_PERSON)
                et_mod1_Phone_number.setText(H.changeTelFormat(currentInstall.NO_HP))
                et_mod1_Address.setText(currentInstall.DC_ADDRESS)
                et_dcd_address_detail.setText(currentInstall.DC_ADDRESS_DETAIL)

                cb_mod1_1.text = currentInstall.NM_Q1
                cb_mod1_2.text = currentInstall.NM_Q2
                cb_mod1_3.text = currentInstall.NM_Q3
                cb_mod1_4.text = currentInstall.NM_Q4
                cb_mod1_5.text = currentInstall.NM_Q5
                cb_mod1_6.text = currentInstall.NM_Q6

                cbShelf.text = currentInstall.NM_CHK1
                cbAdapter.text = currentInstall.NM_CHK2
                cbKey.text = currentInstall.NM_CHK3

                cbBattery.text = currentInstall.NM_CHK4
                cbManual.text = currentInstall.NM_CHK5
                cb_jbox.text = currentInstall.NM_CHK6
            }

            override fun error(err: String) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }


        })

        cb_mod1_1.setOnCheckedChangeListener(this)
        cb_mod1_2.setOnCheckedChangeListener(this)
        cb_mod1_3.setOnCheckedChangeListener(this)

        cb_mod1_4.setOnCheckedChangeListener(this)
        cb_mod1_5.setOnCheckedChangeListener(this)
        cb_mod1_6.setOnCheckedChangeListener(this)

        cbShelf.setOnCheckedChangeListener(this)
        cbBattery.setOnCheckedChangeListener(this)
        cbManual.setOnCheckedChangeListener(this)
        cbKey.setOnCheckedChangeListener(this)
        cbAdapter.setOnCheckedChangeListener(this)
        cb_jbox.setOnCheckedChangeListener(this)

        cbKeyin_1.setOnCheckedChangeListener(this)
        cbKeyin_2.setOnCheckedChangeListener(this)
        cbKeyin_3.setOnCheckedChangeListener(this)

        cb_agree.setOnCheckedChangeListener(this)
        cb_disagree.setOnCheckedChangeListener(this)

        btn_finish.setOnClickListener {
            finish()
        }

        ic_imgV.setOnSignedListener(this)

        btn_save_and_finish.setOnClickListener {

            //toast("Save and Finish"+q_a_str+q_b_str)
            q_a_str_parent = "$q_a_str,$q_b_str"
            q_e_str_parent = "$q_e_str_1,$q_e_str_2"

            val q_a = q_a_str_parent
            val q_b = q_e_str_parent
            val q_d = dc_quest_d

            H.L(q_a)
            H.L(q_b)
            H.L(q_d)
            H.L(ynPvtInfo)

            val CD_FIRM = H.currentUser!!.CD_FIRM
            val NO_SO = H.ddinfos!![0].NO_SO
            val NO_LINE = H.ddinfos!![0].NO_LINE
            val NO_LINE_DLV = H.ddinfos!![0].NO_LINE_DLV

            val upload_file_name = H.joinString(arrayOf(NO_SO, NO_LINE, NO_LINE_DLV)).replace("|", "_")

            val curtimeMills = System.currentTimeMillis()
            val CONFIRM_SIGN = createUpFileName(upload_file_name, "S", curtimeMills)
            val CD_USER = H.currentUser!!.CD_USER

            H.L(CONFIRM_SIGN)

            val params = RequestParams()
            params.put("nm_sp", "SP_MBL_PRM_INSTALL_QUEST_U")
            params.put("param",
                "${"$CD_FIRM|$NO_SO|$NO_LINE|$NO_LINE_DLV|$q_a|$q_b|$q_d|$ynPvtInfo|$CONFIRM_SIGN|$CD_USER"}"
            )
//            params.put(
//                "param",
//                "${H.currentUser!!.CD_FIRM}|$NO_SO|" +
//                        "$NO_LINE|" + "$NO_LINE_DLV|" +
//                        "$q_a|$q_b|$q_d|$ynPvtInfo|" +
//                        "$CONFIRM_SIGN|${H.currentUser!!.CD_USER}"
//            )
            H.L(params.toString())
            val route = H.getRoute(ip = H.ip_address)
            H.doPost(route, params, object : WaitInter {
                override fun responseSuccess(result: String) {
                    uploadImage(signature_pad, upload_file_name, CONFIRM_SIGN)
                    finish()
                    toast("Saved Successfully!")
                    H.L(result)
                }

                override fun error(err: String) {
                    H.L(err)
                    toast(err)
                }


            })
        }

        uploadSign.setOnClickListener {

            val bitmap = ic_imgV.signatureBitmap
            val toadd = addBitmapToGallery(bitmap)
            if (toadd == "") {
                toast("UPLOAD PICTURE UNSUCCESSFUL")
            } else {
                toast("Success")
                signature_pad = toadd
                it.isEnabled = false
                ic_imgV.isEnabled = false
                btn_save_and_finish.isEnabled = true
            }

        }
        btnCls.setOnClickListener {
            ic_imgV.clear()
            signature_pad = ""
            ic_imgV.isEnabled = true
        }
    }

    fun createUpFileName(filename: String, suffix: String, miliSecond: Long): String =
        String.format("${filename}_${suffix}_$miliSecond.png")

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        val key1 = et_DT_DLV_SCH.text.toString()
        val key2 = et_D_FG_TIME.text.toString()
        val key3 = et_D_FG_DLV.text.toString()
        when (buttonView) {

            //DC_QUEST_A
            cb_mod1_1 -> {
                val con = isChecked
                if (con) {
                    if (cb_mod1_2.isChecked && cb_mod1_3.isChecked) {
                        q_a_str = "1010^Y,1020^Y,1030^Y"
                    } else if (cb_mod1_2.isChecked && !cb_mod1_3.isChecked) {
                        q_a_str = "1010^Y,1020^Y,1030^N"
                    } else if (!cb_mod1_2.isChecked && cb_mod1_3.isChecked) {
                        q_a_str = "1010^Y,1020^N,1030^Y"
                    } else {
                        q_a_str = "1010^Y,1020^N,1030^N"
                    }
                } else if (!con) {
                    if (cb_mod1_2.isChecked && cb_mod1_3.isChecked) {
                        q_a_str = "1010^N,1020^Y,1030^Y"
                    } else if (cb_mod1_2.isChecked && !cb_mod1_3.isChecked) {
                        q_a_str = "1010^N,1020^Y,1030^N"
                    } else if (!cb_mod1_2.isChecked && cb_mod1_3.isChecked) {
                        q_a_str = "1010^N,1020^N,1030^Y"
                    } else {
                        q_a_str = "1010^N,1020^N,1030^N"
                    }


                }


            }
            cb_mod1_2 -> {
                val con = isChecked
                if (con) {
                    if (cb_mod1_1.isChecked && cb_mod1_3.isChecked) {
                        q_a_str = "1010^Y,1020^Y,1030^Y"
                    } else if (cb_mod1_1.isChecked && !cb_mod1_3.isChecked) {
                        q_a_str = "1010^Y,1020^Y,1030^N"
                    } else if (!cb_mod1_1.isChecked && cb_mod1_3.isChecked) {
                        q_a_str = "1010^N,1020^Y,1030^Y"
                    } else {
                        q_a_str = "1010^N,1020^Y,1030^N"
                    }
                } else if (!con) {
                    if (cb_mod1_1.isChecked && cb_mod1_3.isChecked) {
                        q_a_str = "1010^Y,1020^N,1030^Y"
                    } else if (cb_mod1_1.isChecked && !cb_mod1_3.isChecked) {
                        q_a_str = "1010^Y,1020^N,1030^N"
                    } else if (!cb_mod1_1.isChecked && cb_mod1_3.isChecked) {
                        q_a_str = "1010^N,1020^N,1030^Y"
                    } else {
                        q_a_str = "1010^N,1020^N,1030^N"
                    }


                }
            }
            cb_mod1_3 -> {
                val con = isChecked
                if (con) {
                    if (cb_mod1_1.isChecked && cb_mod1_2.isChecked) {
                        q_a_str = "1010^Y,1020^Y,1030^Y"
                    } else if (cb_mod1_1.isChecked && !cb_mod1_2.isChecked) {
                        q_a_str = "1010^Y,1020^N,1030^Y"
                    } else if (!cb_mod1_1.isChecked && cb_mod1_2.isChecked) {
                        q_a_str = "1010^N,1020^N,1030^Y"
                    } else {
                        q_a_str = "1010^N,1020^N,1030^Y"
                    }
                } else if (!con) {
                    if (cb_mod1_1.isChecked && cb_mod1_2.isChecked) {
                        q_a_str = "1010^Y,1020^Y,1030^N"
                    } else if (cb_mod1_1.isChecked && !cb_mod1_2.isChecked) {
                        q_a_str = "1010^Y,1020^N,1030^N"
                    } else if (!cb_mod1_1.isChecked && cb_mod1_2.isChecked) {
                        q_a_str = "1010^N,1020^Y,1030^N"
                    } else {
                        q_a_str = "1010^N,1020^N,1030^N"
                    }


                }

            }
            cb_mod1_4 -> {
                val con = isChecked
                if (con) {
                    if (cb_mod1_5.isChecked && cb_mod1_6.isChecked) {
                        q_b_str = "1040^Y,1050^Y,1060^Y"
                    } else if (cb_mod1_5.isChecked && !cb_mod1_6.isChecked) {
                        q_b_str = "1040^Y,1050^Y,1060^N"
                    } else if (!cb_mod1_5.isChecked && cb_mod1_6.isChecked) {
                        q_b_str = "1040^Y,1050^N,1060^Y"
                    } else {
                        q_b_str = "1040^Y,1050^N,1060^N"
                    }
                } else if (!con) {
                    if (cb_mod1_5.isChecked && cb_mod1_6.isChecked) {
                        q_b_str = "1040^N,1050^Y,1060^Y"
                    } else if (cb_mod1_5.isChecked && !cb_mod1_6.isChecked) {
                        q_b_str = "1040^N,1050^Y,1060^N"
                    } else if (!cb_mod1_5.isChecked && cb_mod1_6.isChecked) {
                        q_b_str = "1040^N,1050^N,1060^Y"
                    } else {
                        q_b_str = "1040^N,1050^N,1060^N"
                    }


                }
            }
            cb_mod1_5 -> {
                val con = isChecked
                if (con) {
                    if (cb_mod1_4.isChecked && cb_mod1_6.isChecked) {
                        q_b_str = "1040^Y,1050^Y,1060^Y"
                    } else if (cb_mod1_4.isChecked && !cb_mod1_6.isChecked) {
                        q_b_str = "1040^Y,1050^Y,1060^N"
                    } else if (!cb_mod1_4.isChecked && cb_mod1_6.isChecked) {
                        q_b_str = "1040^N,1050^Y,1060^Y"
                    } else {
                        q_b_str = "1040^N,1050^Y,1060^N"
                    }
                } else if (!con) {
                    if (cb_mod1_4.isChecked && cb_mod1_6.isChecked) {
                        q_b_str = "1040^Y,1050^N,1060^Y"
                    } else if (cb_mod1_4.isChecked && !cb_mod1_6.isChecked) {
                        q_b_str = "1040^Y,1050^N,1060^N"
                    } else if (!cb_mod1_4.isChecked && cb_mod1_6.isChecked) {
                        q_b_str = "1040^N,1050^N,1060^Y"
                    } else {
                        q_b_str = "1040^N,1050^N,1060^N"
                    }


                }
            }
            cb_mod1_6 -> {
                val con = isChecked
                if (con) {
                    if (cb_mod1_4.isChecked && cb_mod1_5.isChecked) {
                        q_b_str = "1040^Y,1050^Y,1060^Y"
                    } else if (cb_mod1_4.isChecked && !cb_mod1_5.isChecked) {
                        q_b_str = "1040^Y,1050^N,1060^Y"
                    } else if (!cb_mod1_4.isChecked && cb_mod1_5.isChecked) {
                        q_b_str = "1040^N,1050^Y,1060^Y"
                    } else {
                        q_b_str = "1040^N,1050^N,1060^Y"
                    }
                } else if (!con) {
                    if (cb_mod1_4.isChecked && cb_mod1_5.isChecked) {
                        q_b_str = "1040^Y,1050^Y,1060^N"
                    } else if (cb_mod1_4.isChecked && !cb_mod1_5.isChecked) {
                        q_a_str = "1040^Y,1050^N,1060^N"
                    } else if (!cb_mod1_4.isChecked && cb_mod1_5.isChecked) {
                        q_b_str = "1040^N,1050^Y,1060^N"
                    } else {
                        q_b_str = "1040^N,1050^N,1060^N"
                    }


                }
            }

            //DC_QUEST_B
            cbShelf -> {
                val con = isChecked
                if (con) {
                    if (cbAdapter.isChecked && cbKey.isChecked) {
                        q_e_str_1 = "2010^Y,2020^Y,2030^Y"
                    } else if (cbAdapter.isChecked && !cbKey.isChecked) {
                        q_e_str_1 = "2010^Y,2020^Y,2030^N"
                    } else if (!cbAdapter.isChecked && cbKey.isChecked) {
                        q_e_str_1 = "2010^Y,2020^N,2030^Y"
                    } else {
                        q_e_str_1 = "2010^Y,2020^N,2030^N"
                    }
                } else if (!con) {
                    if (cbAdapter.isChecked && cbKey.isChecked) {
                        q_e_str_1 = "2010^N,2020^Y,2030^Y"
                    } else if (cbAdapter.isChecked && !cbKey.isChecked) {
                        q_e_str_1 = "2010^N,2020^Y,2030^N"
                    } else if (!cbAdapter.isChecked && cbKey.isChecked) {
                        q_e_str_1 = "2010^N,2020^N,2030^Y"
                    } else {
                        q_e_str_1 = "2010^N,2020^N,2030^N"
                    }
                }
            }
            cbAdapter -> {
                val con = isChecked
                if (con) {
                    if (cbShelf.isChecked && cbKey.isChecked) {
                        q_e_str_1 = "2010^Y,2020^Y,2030^Y"
                        H.L(q_e_str_1)
                    } else if (cbShelf.isChecked && !cbKey.isChecked) {
                        q_e_str_1 = "2010^Y,2020^Y,2030^N"
                        H.L(q_e_str_1)
                    } else if (!cbShelf.isChecked && cbKey.isChecked) {
                        q_e_str_1 = "2010^N,2020^N,2030^Y"
                        H.L(q_e_str_1)
                    } else {
                        q_e_str_1 = "2010^N,2020^Y,2030^N"
                        H.L(q_e_str_1)
                    }
                } else if (!con) {
                    if (cbShelf.isChecked && cbKey.isChecked) {
                        q_e_str_1 = "2010^Y,2020^N,2030^Y"
                        H.L(q_e_str_1)
                    } else if (cbShelf.isChecked && !cbKey.isChecked) {
                        q_e_str_1 = "2010^Y,2020^N,2030^N"
                        H.L(q_e_str_1)
                    } else if (!cbShelf.isChecked && cbKey.isChecked) {
                        q_e_str_1 = "2010^N,2020^Y,2030^N"
                        H.L(q_e_str_1)
                    } else {
                        q_e_str_1 = "2010^N,2020^N,2030^N"
                        H.L(q_e_str_1)
                    }
                }
            }
            cbKey -> {
                val con = isChecked
                if (con) {
                    if (cbShelf.isChecked && cbAdapter.isChecked) {
                        q_e_str_1 = "2010^Y,2020^Y,2030^Y"
                    } else if (cbShelf.isChecked && !cbAdapter.isChecked) {
                        q_e_str_1 = "2010^Y,2020^N,2030^Y"
                    } else if (!cbShelf.isChecked && cbAdapter.isChecked) {
                        q_e_str_1 = "2010^N,2020^Y,2030^Y"
                    } else {
                        q_e_str_1 = "2010^N,2020^N,2030^Y"
                    }
                } else if (!con) {
                    if (cbShelf.isChecked && cbAdapter.isChecked) {
                        q_e_str_1 = "2010^Y,2020^Y,2030^N"
                    } else if (cbShelf.isChecked && !cbAdapter.isChecked) {
                        q_e_str_1 = "2010^Y,2020^N,2030^N"
                    } else if (!cbShelf.isChecked && cbAdapter.isChecked) {
                        q_e_str_1 = "2010^N,2020^Y,2030^N"
                    } else {
                        q_e_str_1 = "2010^N,2020^N,2030^N"
                    }
                }
            }
            cbBattery -> {
                val con = isChecked
                if (con) {
                    if (cbManual.isChecked && cb_jbox.isChecked) {
                        q_e_str_2 = "2040^Y,2050^Y,2060^Y"
                        H.L(q_e_str_2)
                    } else if (cbManual.isChecked && !cb_jbox.isChecked) {
                        q_e_str_2 = "2040^Y,2050^Y,2060^N"
                        H.L(q_e_str_2)
                    } else if (!cbManual.isChecked && cb_jbox.isChecked) {
                        q_e_str_2 = "2040^Y,2050^N,2060^Y"
                        H.L(q_e_str_2)
                    } else {
                        q_e_str_2 = "2040^Y,2050^N,2060^N"
                        H.L(q_e_str_2)
                    }
                } else if (!con) {
                    if (cbManual.isChecked && cb_jbox.isChecked) {
                        q_e_str_2 = "2040^N,2050^Y,2060^Y"
                        H.L(q_e_str_2)
                    } else if (cbManual.isChecked && !cb_jbox.isChecked) {
                        q_e_str_2 = "2040^N,2050^Y,2060^N"
                        H.L(q_e_str_2)
                    } else if (!cbManual.isChecked && cb_jbox.isChecked) {
                        q_e_str_2 = "2040^N,2050^N,2060^Y"
                        H.L(q_e_str_2)
                    } else {
                        q_e_str_2 = "2040^N,2050^N,2060^N"
                        H.L(q_e_str_2)
                    }
                }
            }
            cbManual -> {
                val con = isChecked
                if (con) {
                    if (cbBattery.isChecked && cb_jbox.isChecked) {
                        q_e_str_2 = "2040^Y,2050^Y,2060^Y"
                    } else if (cbBattery.isChecked && !cb_jbox.isChecked) {
                        q_e_str_2 = "2040^Y,2050^Y,2060^N"
                    } else if (!cbBattery.isChecked && cb_jbox.isChecked) {
                        q_e_str_2 = "2040^N,2050^Y,2060^Y"
                    } else {
                        q_e_str_2 = "2040^N,2050^Y,2060^N"
                    }
                } else if (!con) {
                    if (cbBattery.isChecked && cb_jbox.isChecked) {
                        q_e_str_2 = "2040^Y,2050^N,2060^Y"
                    } else if (cbBattery.isChecked && !cb_jbox.isChecked) {
                        q_e_str_2 = "2040^Y,2050^N,2060^N"
                    } else if (!cbBattery.isChecked && cb_jbox.isChecked) {
                        q_e_str_2 = "2040^N,2050^N,2060^Y"
                    } else {
                        q_e_str_2 = "2040^N,2050^N,2060^N"
                    }
                }
            }
            cb_jbox -> {
                val con = isChecked
                if (con) {
                    if (cbBattery.isChecked && cbManual.isChecked) {
                        q_e_str_2 = "2040^Y,2050^Y,2060^Y"
                    } else if (cbBattery.isChecked && !cbManual.isChecked) {
                        q_e_str_2 = "2040^Y,2050^N,2060^Y"
                    } else if (!cbBattery.isChecked && cbManual.isChecked) {
                        q_e_str_2 = "2040^N,2050^Y,2060^Y"
                    } else {
                        q_e_str_2 = "2040^N,2050^N,2060^Y"
                    }
                } else if (!con) {
                    if (cbBattery.isChecked && cbManual.isChecked) {
                        q_e_str_2 = "2040^Y,2050^Y,2060^N"
                    } else if (cbBattery.isChecked && !cbManual.isChecked) {
                        q_e_str_2 = "2040^Y,2050^N,2060^N"
                    } else if (!cbBattery.isChecked && cbManual.isChecked) {
                        q_e_str_2 = "2040^N,2050^Y,2060^N"
                    } else {
                        q_e_str_2 = "2040^N,2050^N,2060^N"
                    }
                }
            }

            //DC_QUEST_D
            cbKeyin_1 -> {
                val con = isChecked
                if (con && !key1.contentEquals("")) {
                    et_DT_DLV_SCH.setBackgroundResource(R.color.white)
                    if (cbKeyin_2.isChecked && cbKeyin_3.isChecked) {
                        dc_quest_d = "$key1,$key2,$key3"
                    } else if (!cbKeyin_2.isChecked && cbKeyin_3.isChecked) {
                        dc_quest_d = "$key1,,$key3"
                    } else if (cbKeyin_2.isChecked && !cbKeyin_3.isChecked) {
                        dc_quest_d = "$key1,$key2,"
                    } else if (!cbKeyin_2.isChecked && !cbKeyin_3.isChecked) {
                        dc_quest_d = "$key1,,"
                    }
                } else {
                    cbKeyin_1.isChecked = false
                    et_DT_DLV_SCH.setText("")
                    et_DT_DLV_SCH.setBackgroundResource(R.color.color_transparent)
                    //toast("Please insert data to checked EditText")
                }

            }
            cbKeyin_2 -> {
                val con = isChecked
                if (con && !key2.contentEquals("")) {
                    et_D_FG_TIME.setBackgroundResource(R.color.white)
                    if (cbKeyin_1.isChecked && cbKeyin_3.isChecked) {
                        dc_quest_d = "$key1,$key2,$key3"
                    } else if (!cbKeyin_1.isChecked && cbKeyin_3.isChecked) {
                        dc_quest_d = ",$key2,$key3"
                    } else if (cbKeyin_1.isChecked && !cbKeyin_3.isChecked) {
                        dc_quest_d = "$key1,$key2,"
                    } else if (!cbKeyin_1.isChecked && !cbKeyin_3.isChecked) {
                        dc_quest_d = ",$key2,"
                    }
                } else {
                    cbKeyin_2.isChecked = false
                    et_D_FG_TIME.setText("")
                    et_D_FG_TIME.setBackgroundResource(R.color.color_transparent)
                    //toast("Please insert data to checked EditText")
                }
            }
            cbKeyin_3 -> {
                val con = isChecked
                if (con && !key3.contentEquals("")) {
                    et_D_FG_DLV.setBackgroundResource(R.color.white)
                    if (cbKeyin_1.isChecked && cbKeyin_2.isChecked) {
                        dc_quest_d = "$key1,$key2,$key3"
                    } else if (!cbKeyin_1.isChecked && cbKeyin_2.isChecked) {
                        dc_quest_d = ",$key2,$key3"
                    } else if (cbKeyin_1.isChecked && !cbKeyin_2.isChecked) {
                        dc_quest_d = "$key1,,$key3"
                    } else if (!cbKeyin_1.isChecked && !cbKeyin_2.isChecked) {
                        dc_quest_d = ",,$key3"
                    }
                } else {
                    cbKeyin_3.isChecked = false
                    et_D_FG_DLV.setBackgroundResource(R.color.color_transparent)
                    et_D_FG_DLV.setText("")
                    //toast("Please insert data to checked EditText")
                }
            }

            //YN_PVTINFO
            cb_agree -> {
                val con = isChecked
                if (con) {
                    ynPvtInfo = "Y"
                    cb_disagree.isChecked = false
                }
            }

            cb_disagree -> {
                val con = isChecked
                if (con) {
                    ynPvtInfo = "N"
                    cb_agree.isChecked = false
                }
            }

        }
    }

    private fun getImageStorageDir(): File {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Smart-Note")
        if (!file.mkdirs()) {
            //toast("SignaturePad Directory not created")
        }
        return file
    }


    private fun addBitmapToGallery(bmap: Bitmap): String {
        var result = ""

        try {
            val photo = File(getImageStorageDir(), String.format("Signature_%d.png", System.currentTimeMillis()))
            saveBitmapToJPG(bmap, photo)
            result = photo.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return result
    }

//    private fun scanMediaFile(photo: File) {
//        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//        val contentUri = Uri.fromFile(photo)
//        uploadImage(photo.absolutePath)
//
//    }

    fun saveBitmapToJPG(bitmap: Bitmap, photo: File) {
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.WHITE)
        val matrix = Matrix()
        matrix.reset()
        //matrix.postTranslate((-bitmap.width / 2).toFloat(), (-bitmap.height / 2).toFloat())
        matrix.postTranslate(0.0F, 0.0F)
        canvas.drawBitmap(bitmap, matrix, null)
        val stream = FileOutputStream(photo)
        newBitmap.compress(Bitmap.CompressFormat.PNG, 85, stream)
        stream.close()
    }


    fun getStoragePermission() {
        H.L("Permission is revoked")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            H.L("Permission: $permissions[0]was$grantResults[0]")
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val imgPath = getRealPathFromURI(data!!.getData())
            val imageUri: Uri = data.data
            val imageStream: InputStream = contentResolver.openInputStream(imageUri)
            //val bmap: Bitmap = BitmapFactory.decodeStream(imageStream)
            //download_image.setImageBitmap(bmap)
            image_path = imgPath
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                val bmap = data.extras.get("data") as Bitmap
                image_path = addBitmapToGallery(bmap)
            } else {
                toast("Fail")
            }
        }
    }

    // 이미지 URI절대경로 가져오기
    private fun getRealPathFromURI(contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = this.contentResolver.query(contentUri, proj, null, null, null);
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun uploadImage(path: String, foldername: String, filename: String) {

        val client = AsyncHttpClient()
        val params = RequestParams()
        val file = File(path)

        params.put("COLUMN", filename)
        params.put("slipno", foldername)
        params.put("MODULE", "PRM")
        params.put("CD_FIRM", H.deliveryList!![0].CD_FIRM)
        params.put("uploadedfile", file)
        client.post(
            "http://${H.ip_address}/mob_json/UploadFromAndroid.ashx",
            params,
            object : TextHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                    H.L(responseString!!)
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseString: String?,
                    throwable: Throwable?
                ) {
                    H.L(throwable!!.message!!)
                }

            })

    }

    private fun checkPermission(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(this@InstallationConfirmation, Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

}
