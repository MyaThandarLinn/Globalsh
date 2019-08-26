package g2sysnet.smart_gw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import kotlinx.android.synthetic.main.activity_board_sub02.*
import org.jetbrains.anko.toast
import org.json.JSONException
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class BoardActivitySub02 : AppCompatActivity() {

    var CD_MENU=""
    var NO_NTC=""
    var YN_FILE=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_sub02)
        supportActionBar?.hide()

        CD_MENU=intent.getStringExtra("CD_MENU")
        NO_NTC=intent.getStringExtra("NO_NTC")
        YN_FILE=intent.getStringExtra("YN_FILE")

        //searchWeb
        val rout=H.getRoute(ip=H.ip_address,page = "html_json")
        val sb = StringBuilder()
        val param = RequestParams()
        param.put("nm_sp", "UP_MOB_GET_APP_BODY")
        param.put("param", "${H.currentUser!!.CD_FIRM}|$NO_NTC")
        H.doPost(rout, param, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                val jAll = jAry.length()
                for (i in 0 until jAll) {
                    sb.append(jAry.getJSONObject(i).getString("DC_FORM"))
                }

                try {
                    val params = RequestParams()
                    params.put("nm_sp","SP_GRW_BRD_RD_I")
                    params.put(
                        "param",
                        "${H.currentUser!!.CD_FIRM}|$CD_MENU|$NO_NTC|${H.currentUser!!.CD_EMP}|${H.currentUser!!.CD_DEPT}|${getToday().replace(
                            "-",
                            ""
                        )}|${H.currentUser!!.CD_USER}"
                    )
                    H.doPost(H.getRoute(ip = H.ip_address), params, object : WaitInter {
                        override fun responseSuccess(result: String) {
                            webView.webViewClient = WebViewClient()
                            webView.loadData(sb.toString(), "text/html;  charset=UTF-8", null)
                            webView.settings.builtInZoomControls = true
                            webView.settings.displayZoomControls = false
                            webView.requestFocus(View.FOCUS_DOWN)
                        }
                        override fun error(err: String) {
                            toast(err)
                        }
                    })
                } catch (e: JSONException) {
                    toast(getString(R.string.msg_search_none))
//                    Toast.makeText(baseContext, getString(R.string.msg_search_none), Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                    return
                }
            }

            override fun error(err: String) {
                toast(err)
            }
        })

        btnComment.setOnClickListener {
            val intent = Intent(this@BoardActivitySub02, BoardActivitySub03::class.java)
            intent.putExtra("CD_MENU", CD_MENU)
            intent.putExtra("NO_NTC", NO_NTC)
            startActivity(intent)
        }

        btnFile.setOnClickListener {
            if (YN_FILE.equals("N")) {
                Toast.makeText(this@BoardActivitySub02, getString(R.string.msg_no_file), Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this@BoardActivitySub02, ApprovalActivitySub03::class.java)
                intent.putExtra("NO_APP", NO_NTC)
                startActivity(intent)
            }
        }
    }

    fun getToday(): String {
        val today = Date()
        val DT_NOW = SimpleDateFormat("yyyy-MM-dd")
        return DT_NOW.format(today)
    }
}
