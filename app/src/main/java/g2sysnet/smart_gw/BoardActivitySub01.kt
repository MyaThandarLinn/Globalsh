package g2sysnet.smart_gw

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.libby.divider
import kotlinx.android.synthetic.main.activity_board_sub01.*
import kotlinx.android.synthetic.main.activity_board_sub01_row.view.*
import org.jetbrains.anko.toast

class BoardActivitySub01 : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_sub01)
        supportActionBar?.hide()

        val CD_MENU=intent.getStringExtra("CD_MENU")
        boardsub01Info(H.currentUser!!.CD_FIRM,CD_MENU,"","",H.currentUser!!.CD_EMP,H.currentUser!!.CD_DEPT,"SP_MOB_BRD_SEARCH_02")
    }

    fun boardsub01Info(cd_firm: String, cd_menu: String, null1: String, null2: String,cd_emp: String,cd_dept: String,fg_sp: String) {

        val params = RequestParams()
        params.put("nm_sp", fg_sp)
        params.put("param", "$cd_firm|$cd_menu|$null1|$null2|$cd_emp|$cd_dept")

        val route = H.getRoute(ip = H.ip_address)

        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.boardsub01Model=null
                H.boardsub01Model = Gson().fromJson(jAry.toString(), Array<BoardSub01Model>::class.java).toList()
                val adapter = BoardSub01Adapter(this@BoardActivitySub01, H.boardsub01Model!!,cd_menu)
                boardRecyclersub01.layoutManager = LinearLayoutManager(this@BoardActivitySub01)
                boardRecyclersub01.addItemDecoration(divider(Color.parseColor("#5C6BC0"),2))
                boardRecyclersub01.adapter = adapter
            }

            override fun error(err: String) {
                toast(err)
            }

        })
    }
}
class BoardSub01Adapter(var context: Context, var lists: List<BoardSub01Model>,val CD_MENU:String) :
    RecyclerView.Adapter<BoardSub01Adapter.ViewHolder>() {

    var index = -1

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.activity_board_sub01_row, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        val sm = lists[p1]
        if (p0.itemView.txt01 != null) {
            p0.itemView.txt01.setText(sm.NM_NTC)
        }
        if (p0.itemView.txt02 != null) {
            p0.itemView.txt02.setText(sm.DT_WRITE)
        }
        if (p0.itemView.txt03 != null) {
            if (sm.YN_FILE == "Y") {
                p0.itemView.txt03.setText("")
                p0.itemView.txt03.setVisibility(View.VISIBLE)
            } else {
                p0.itemView.txt03.setVisibility(View.INVISIBLE)
            }
        }
        if (p0.itemView.txt04 != null) {
            p0.itemView.txt04.setText(sm.NM_EMP_W)
        }

        if(index==p1){
            p0.itemView.setBackgroundColor(Color.parseColor("#5C6BC0"))
        }else{
            p0.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        p0.itemView.setOnClickListener {
            index=p1
            notifyDataSetChanged()
            val intent = Intent(context,BoardActivitySub02::class.java)
            intent.putExtra("CD_MENU", CD_MENU)
            intent.putExtra("NO_NTC", sm.NO_NTC)
            intent.putExtra("YN_FILE", sm.YN_FILE)
            context.startActivity(intent)
        }
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
data class BoardSub01Model (
    var NO_NTC : String,
    var NM_NTC : String,
    var CD_EMP_W: String,
    var NM_EMP_W: String,
    var DT_WRITE: String,
    var NUM_READ: String,
    var DC_FILE : String,
    var YN_FILE :String
)