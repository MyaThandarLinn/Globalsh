package g2sysnet.smart_gw

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity
import cz.msebera.android.httpclient.client.methods.HttpPost
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient
import cz.msebera.android.httpclient.message.BasicNameValuePair
import cz.msebera.android.httpclient.util.EntityUtils
import g2sysnet.smart_gw.inters.NameValuePair
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import kotlinx.android.synthetic.main.activity_approval_sub01.*
import org.jetbrains.anko.toast
import org.jetbrains.anko.webView
import org.json.HTTP
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder
import java.util.ArrayList

class ApprovalActivitySub01 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approval_sub01)

        supportActionBar?.hide()

        supportActionBar?.title = "ApprovalActivitySub01"
        //H.hideDecorView(supportActionBar,window)

        val NO_APP = intent.getStringExtra("NO_APP")
        val YN_FILE = intent.getStringExtra("YN_FILE")
        val FG_BOARD = intent.getStringExtra("FG_BOARD")

        H.L("This is NO APP "+NO_APP)


        btnApprove.isEnabled = false
        btnReturn.isEnabled = false
        btnPending.isEnabled = false

        if (FG_BOARD == "SN_GRW_APP_REG_001" || FG_BOARD == "SN_GRW_APP_REG_008" || FG_BOARD == "SN_GRW_APP_REG_009") {
            btnApprove.isEnabled = true
            btnReturn.isEnabled = true
        } else if (FG_BOARD == "SN_GRW_APP_REG_002" || FG_BOARD == "SN_GRW_APP_REG_003") {
            btnPending.isEnabled = true
        }

        btnComment.setOnClickListener {
            val intent = Intent(this@ApprovalActivitySub01, ApprovalActivitySub02::class.java)
            intent.putExtra("NO_APP", NO_APP)
            intent.putExtra("FG_BOARD", FG_BOARD)

            startActivityForResult(intent, 1)
        }

        btnFile.setOnClickListener {

            if (YN_FILE == "N") {
                toast(getString(R.string.msg_no_file))
                return@setOnClickListener
            } else {
                val intent = Intent(this@ApprovalActivitySub01, ApprovalActivitySub03::class.java)
                intent.putExtra("NO_APP", NO_APP)
                startActivityForResult(intent, 1)
            }
        }

        btnApprove.setOnClickListener {
            SaveMethod("A", "", NO_APP)
            //FG_APP;  /* A 승인, R 반려, H 보류, C 취소*/
            // A = Approval , R = Reply , H = Hold , C = Cancel
        }

        btnReturn.setOnClickListener {
            Alert_Save(NO_APP)
            //FG_APP;  /* A 승인, R 반려, H 보류, C 취소*/
        }

        btnPending.setOnClickListener {
            SaveMethod("C", "",NO_APP)
            //FG_APP;  /* A 승인, R 반려, H 보류, C 취소*/
        }
        Search(NO_APP)
    }

    private fun Search(NO_APP: String) {
        val CD_FIRM = H.currentUser!!.CD_FIRM
        val CD_EMP = H.currentUser!!.CD_EMP
        H.L(NO_APP)

        val params = RequestParams()
        params.put("nm_sp", "UP_MOB_GET_APP_BODY")
        params.put("param", "$CD_FIRM|$NO_APP")
        val route = H.getRoute(ip = H.ip_address, page = "html_json")

        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                //H.L("This is Result1 $result")
                if (result.contentEquals("[]")) {
                    toast(R.string.msg_search_none)
                    H.L("no message here")
                    return
                }
                //toast("RESULT OK")
                try {
                    val sb = StringBuilder()
                    val json = JSONObject(result)
                    val jAry = json.getJSONArray("Table")
                    H.L(jAry.toString())
                    val Arr_lenth = jAry.length()

                    for (i in 0..Arr_lenth - 1) {
                        sb.append(jAry.getJSONObject(i).getString("DC_FORM"))
                    }
                    wevbiew.setWebViewClient(WebViewClient()) // 이걸 안해주면 새창이 뜸
                    wevbiew.loadData(sb.toString(), "text/html;  charset=UTF-8", null)

                    wevbiew.getSettings().setSupportZoom(true)
                    wevbiew.getSettings().setBuiltInZoomControls(true)
                    wevbiew.getSettings().setDisplayZoomControls(false)
                    wevbiew.getSettings().setUseWideViewPort(true)
                    wevbiew.getSettings().setLoadWithOverviewMode(true)

                    wevbiew.requestFocus(View.FOCUS_DOWN)

                } catch (e: JSONException) {
                    toast(R.string.msg_search_none)
                    e.printStackTrace()
                }

            }

            override fun error(err: String) {

            }

        })
    }

    private fun Alert_Save(no_app : String) {
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.setTitle("반려사유")
        alertDialog.setMessage("반려사유를 적어주세요")

        // Set an EditText view to get user input
        val input = EditText(this)
        alertDialog.setView(input)

        alertDialog.setPositiveButton("Ok") { dialog, whichButton ->
            val DC_APP = input.text.toString()
            SaveMethod("R", DC_APP,no_app)
        }

        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog, whichButton ->
            // Canceled.
        }

        val alert = alertDialog.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#A3E4D7"))
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#A3E4D7"))

    }

    private fun SaveMethod(FG_APP: String, DC_APP: String, NO_APP: String) {
        //H.L("Reached Save Method in Approval Sub01")
        //H.L("$FG_APP,$DC_APP,$NO_APP")
        val DT_APP = H.getToday().replace("-", "")
        val CD_FIRM = H.currentUser!!.CD_FIRM
        val CD_EMP = H.currentUser!!.CD_EMP

        val params = RequestParams()
        params.put("param", "$CD_FIRM|$NO_APP|$CD_EMP|$FG_APP|$DT_APP|$DC_APP")
        //H.L("Param is "+params.toString())
        //params.put("param","8001|201908120010|1000|C|20190820|")
        //                   (8001|201908120004|1000|A|20190822|)
        val route = H.getRoute(ip = H.ip_address, page = "GRW_APPROVAL")
        H.L(route)

        doPostRequest(route,params,object : WaitInter{
                override fun responseSuccess(result: String) {
                    H.L("Reached doPost Request "+result)
                    val splitStr = result.split("|").toTypedArray()
                    //H.L("This is split 0 "+splitStr[0])
                    //H.L("This is split 1 "+splitStr[1])
                    if(result!=null){
                        if(splitStr[0].contentEquals("T")){
                            finish()
                        }
                        toast(splitStr[1])

                    }
                    else{
                        toast("Data Saving Failed!!")
                    }
                }

                override fun error(err: String) {
                    toast(err)
                    H.L(err)
                }

            })
    }
    fun doPostRequest(route: String, params: RequestParams, inter: WaitInter){
        val client = AsyncHttpClient()
        client.post(route, params, object : TextHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                when (responseString) {
                    "" -> inter.error("Connection Error")
                    "[]" -> inter.error("Blank Data Return")
                    else -> inter.responseSuccess(responseString!!)
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, throwable: Throwable?) {
                inter.error(responseString.toString())
            }
        })
    }
}
