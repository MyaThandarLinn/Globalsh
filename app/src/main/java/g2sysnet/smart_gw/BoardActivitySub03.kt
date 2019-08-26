package g2sysnet.smart_gw

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.libby.divider
import kotlinx.android.synthetic.main.activity_board.*
import kotlinx.android.synthetic.main.activity_board_sub03.*
import kotlinx.android.synthetic.main.activity_board_sub03_row.view.*
import org.jetbrains.anko.padding
import org.jetbrains.anko.toast
import java.util.ArrayList

class BoardActivitySub03 : AppCompatActivity() {

    var CD_MENU=""
    var NO_NTC = ""
    var NO_ADD = ""
    var CD_EMP_W= ""
    var LV = ""

    var m_orders: ArrayList<board03Model>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_sub03)
        supportActionBar?.hide()

        CD_MENU = intent.getStringExtra("CD_MENU")
        NO_NTC = intent.getStringExtra("NO_NTC")

        btnCommentAdd.setOnClickListener{
            NO_ADD = "" //항번 초기화
            LV = "1"    //댓글은 무조건 1레벨
            Dialog_Comment(LV)
        }

        search()

    }

    fun search() {
        val params = RequestParams()
        params.put("nm_sp", "SP_GRW_BRD_ADD_S")
        params.put("param", "${H.currentUser!!.CD_FIRM}|$CD_MENU|$NO_NTC")
        val route = H.getRoute(ip = H.ip_address)

        val client = AsyncHttpClient()
        client.post(route,params,object : TextHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                H.L(responseString!!)
                when(responseString){
                    "" -> toast("Connection Error")
                    "[]" -> {
                        toast(getString(R.string.msg_search_none))

                        if (m_orders != null) {
                            m_orders=null
                            m_orders = ArrayList<board03Model>()
                            val adapter = BoardSub03Adapter(this@BoardActivitySub03,m_orders!!)
                            boardRecycler03.layoutManager = LinearLayoutManager(this@BoardActivitySub03)
                            boardRecycler03.adapter = adapter
                        }

                    }
                    else -> {
                        m_orders = ArrayList<board03Model>()
                        val jAry = H.strToJArr(responseString)
                        val board03 = Gson().fromJson(jAry.toString(), Array<board03Model>::class.java).toList()

                        for(i in 0..board03.size-1){
                            m_orders!!.add(board03Model(
                                board03[i].CD_BRD,
                                board03[i].NO_NTC,
                                board03[i].NO_ADD,
                                board03[i].TXT_BODY,
                                board03[i].CD_EMP_W,
                                board03[i].NM_EMP_W,
                                board03[i].DT_WRITE,
                                board03[i].LV,
                                board03[i].YN_CHILD
                            ))
                        }
                        val adapter = BoardSub03Adapter(this@BoardActivitySub03,m_orders!!)
                        boardRecycler03.layoutManager = LinearLayoutManager(this@BoardActivitySub03)
                        boardRecycler03.addItemDecoration(divider(Color.parseColor("#5C6BC0"),2))
                        boardRecycler03.adapter = adapter
                    }
                }
            }
            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseString: String?,
                throwable: Throwable?
            ) {
                toast(getString(R.string.msg_search_error))
            }
        })
    }


    private fun Dialog_Comment(fg_lv: String) {
        val alert = AlertDialog.Builder(this)
        alert.setTitle(getString(R.string.dialog_comment_title))
        alert.setMessage(getString(R.string.dialog_comment_msg))

        val input = EditText(this)
        alert.setView(input)

        alert.setPositiveButton("Ok") { dialog, whichButton ->
            val value = input.text.toString()
            Toast.makeText(this@BoardActivitySub03, getString(R.string.positive_btn), Toast.LENGTH_SHORT).show()
            Save(value,fg_lv)
        }

        alert.setNegativeButton("Cancel") { dialog, whichButton ->
            dialog.dismiss()
        }

        alert.show()
//        Search()
    }


    fun Save(TXT_BODY:String,FG_LV :String ) {
        val params = RequestParams()
        params.put("nm_sp", "SP_GRW_BRD_ADD_I")
        params.put(
            "param",
            "${H.currentUser!!.CD_FIRM}|$CD_MENU|$NO_NTC|$NO_ADD|$FG_LV|$TXT_BODY|${H.currentUser!!.CD_EMP}|${H.currentUser!!.CD_DEPT}|${H.currentUser!!.CD_DATE}|${H.currentUser!!.CD_USER}"
        )
        val route = H.getRoute(ip = H.ip_address)

        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                val save_result = jAry.getJSONObject(0).getString("FG")
                Toast.makeText(this@BoardActivitySub03, getString(R.string.msg_save_ok), Toast.LENGTH_SHORT).show()

                if (save_result.equals("S")) {
                    search()
                    //초기화
                    NO_NTC = ""
                    NO_ADD = ""
                    CD_EMP_W = ""
                    LV = ""
                }
            }

            override fun error(err: String) {
                toast(getString(R.string.msg_save_error))
            }
        })
        search()
    }

    fun Delete() {
        val params = RequestParams()
        params.put("nm_sp", "SP_GRW_BRD_ADD_D")
        params.put("param", "${H.currentUser!!.CD_FIRM}|$CD_MENU|$NO_NTC|$NO_ADD")
        val route = H.getRoute(ip = H.ip_address)
        H.L(params.toString())
        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                val save_result = jAry.getJSONObject(0).getString("FG")

                if (save_result.contentEquals("S")) {
                    toast(getString(R.string.msg_delete_ok))
                    search()
                }
            }

            override fun error(err: String) {
                toast(getString(R.string.msg_save_error))
            }

        })
    }

    inner class BoardSub03Adapter(var context: Context, var lists: ArrayList<board03Model>) :
        RecyclerView.Adapter<BoardSub03Adapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            val v = LayoutInflater.from(context).inflate(R.layout.activity_board_sub03_row, p0, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int = lists.size

        override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
            val sm = lists[p1]

            if (p0.itemView.txt01 != null) {
                p0.itemView.txt01.setText(sm.TXT_BODY)
            }
            if (p0.itemView.txt02 != null) {
                p0.itemView.txt02.setText(sm.NM_EMP_W)
            }

            if (sm.LV.equals("1")){
                p0.itemView.img.setImageResource(R.drawable.ic_document)
            }else{
                p0.itemView.img.setPadding(30,30,30,30)
                p0.itemView.img.setImageResource(R.drawable.ic_reply_24dp)
            }

            p0.itemView.btnReply.setOnClickListener {
                    NO_NTC = sm.NO_NTC
                    NO_ADD = sm.NO_ADD
                    CD_EMP_W = sm.CD_EMP_W
                    LV = sm.LV

                if (sm.NO_ADD == "") {
                    toast(getString(R.string.msg_no_row_select))
                } else {
                    val i = Integer.parseInt(sm.LV) + 1
                    val strLV = i.toString()
                    Dialog_Comment(strLV)
                }
            }

            p0.itemView.btnDelete.setOnClickListener {

                NO_NTC = sm.NO_NTC
                NO_ADD = sm.NO_ADD

                if (sm.YN_CHILD.equals("Y")){
                    context.toast(getString(R.string.msg_reply_no))
                }else if (sm.NO_NTC.equals("")){
                    context.toast(getString(R.string.msg_no_row_select))
                }else if(H.currentUser!!.CD_EMP != sm.CD_EMP_W){
                    context.toast(getString(R.string.msg_comment_delete_myself))
                }else{
                    val alertDialog = AlertDialog.Builder(context)
                    alertDialog.setTitle(getString(R.string.title_delete))
                    alertDialog.setMessage(getString(R.string.msg_delete))
                    alertDialog.setPositiveButton("YES") { dialog, which ->
                        Delete()
                    }
                    alertDialog.setNegativeButton(
                        "NO"
                    ) { dialog, whichButtom ->
                        dialog.cancel()
                    }
                    alertDialog.show()
                }
            }

        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

    override fun onResume() {
        super.onResume()
        search()
    }

}

data class board03Model(
    val CD_BRD: String,
    val NO_NTC: String,
    val NO_ADD: String,
    val TXT_BODY: String,
    val CD_EMP_W: String,
    val NM_EMP_W: String,
    val DT_WRITE: String,
    val LV: String,
    val YN_CHILD: String
)


