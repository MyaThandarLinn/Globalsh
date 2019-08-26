package g2sysnet.smart_gw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.Device
import g2sysnet.smart_gw.models.User
import g2sysnet.smart_gw.settings.SettingActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        H.hideDecorView(supportActionBar, window)

        btn_to_login.setOnClickListener {

            val id = et_input_id.text.toString()
            val pwd = et_input_password.text.toString()
            val firm_id = H.getPrefValue(this@LoginActivity, "CD_FIRM")
            val ip_add = H.getPrefValue(this@LoginActivity, "IP")

            val route=H.getRoute(ip=ip_add)

            val params =RequestParams()
            params.put("nm_sp","UP_MOB_CHECK_LOGIN")
            params.put("param",H.joinString(arrayOf(firm_id, id, pwd)))

            if (!id.isEmpty() && !pwd.isEmpty() && !firm_id.contentEquals("error") && !ip_add.contentEquals("error")) {
                H.ip_address = ip_add
                H.doPost(route,params, object : WaitInter {
                    override fun responseSuccess(result: String) {

                            val jAry=H.strToJArr(result)
                            val users: List<User> = Gson().fromJson(jAry.toString(), Array<User>::class.java).toList()
                            H.currentUser = users[0]
                            saveCurrentDevice(route)
                    }
                    override fun error(err: String) {
                        toast(err)
                    }

                })
            } else {
                toast(getString(R.string.add_all_require_fields))
            }
        }

        btn_to_setting.setOnClickListener {
            val intent = Intent(this@LoginActivity, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    fun saveCurrentDevice(rt:String) {
        val secondparam=RequestParams()
        secondparam.put("nm_sp","UP_MOB_DEVICE_ID_I")
        secondparam.put("param",H.joinString(arrayOf(H.currentUser!!.CD_FIRM, H.currentUser!!.CD_EMP, H.token)))
        H.doPost(rt,secondparam, object : WaitInter {
            override fun responseSuccess(result: String) {
                    val jAry=H.strToJArr(result)
                    val devices: List<Device> = Gson().fromJson(jAry.toString(), Array<Device>::class.java).toList()
                    H.currentDevice = devices[0]
                    startActivity(Intent(this@LoginActivity, MenuPage::class.java))

            }
            override fun error(err: String) {
                toast(err)
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }
}
