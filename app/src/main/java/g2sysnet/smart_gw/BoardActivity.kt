package g2sysnet.smart_gw

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.libby.divider
import kotlinx.android.synthetic.main.activity_board.*
import kotlinx.android.synthetic.main.activity_board_row.view.*
import org.jetbrains.anko.toast
import java.util.ArrayList

class BoardActivity : AppCompatActivity() {

    val route = H.getRoute(ip = H.ip_address)
    var m_orders: ArrayList<BoardItemInfo>? = null
    var m_orders_count: ArrayList<BoardItemInfoCount>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        supportActionBar?.hide()
        Search_Count()
    }

    fun Search_Count() {
        val param = RequestParams()
        param.put("nm_sp", "SP_GRW_APP_COUNT_MBL")
        param.put("param", "${H.currentUser!!.CD_FIRM}|${H.currentUser!!.CD_EMP}||")

        H.doPost(route, param, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.iteminfoCount = null
                H.iteminfoCount = Gson().fromJson(jAry.toString(), Array<BoardItemInfoCount>::class.java).toList()
                m_orders_count = ArrayList()
                for (i in 0..H.iteminfoCount!!.size - 1) {
                    m_orders_count!!.add(
                        BoardItemInfoCount(
                            H.iteminfoCount!![i].CD_MENU,
                            H.iteminfoCount!![i].NM_MENU,
                            H.iteminfoCount!![i].QT_CNT
                        )
                    )
                }
                Search()
            }

            override fun error(err: String) {
                toast(err)
            }
        })
    }

    fun Search() {
        val params = RequestParams()
        params.put("nm_sp", "SP_GRW_BRD_SEARCH_01")
        params.put("param", "${H.currentUser!!.CD_FIRM}|${H.currentUser!!.CD_USER}")
        val route = H.getRoute(ip = H.ip_address)
        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.boardItemInfo = null
                H.boardItemInfo = Gson().fromJson(jAry.toString(), Array<BoardItemInfo>::class.java).toList()

                m_orders = ArrayList()

                for (j in 0..H.boardItemInfo!!.size - 1) {
                    m_orders!!.add(
                        BoardItemInfo(
                            H.boardItemInfo!![j].CD_MENU,
                            H.boardItemInfo!![j].NM_MENU,
                            H.boardItemInfo!![j].NO_POS,
                            H.boardItemInfo!![j].FG_MENU
                        )
                    )
                }

                H.L(m_orders.toString())
                val adapter = BoardAdapter(this@BoardActivity, m_orders!!, m_orders_count!!)
                boardRecycler.layoutManager = LinearLayoutManager(this@BoardActivity)
                boardRecycler.addItemDecoration(divider(Color.parseColor("#5C6BC0"), 2))
                boardRecycler.adapter = adapter
            }

            override fun error(err: String) {
            }

        })
    }
}

class BoardAdapter(
    var context: Context,
    var lists: ArrayList<BoardItemInfo>,
    var listsC: ArrayList<BoardItemInfoCount>
) :
    RecyclerView.Adapter<BoardAdapter.ViewHolder>() {

    var index = -1

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.activity_board_row, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val itemlist = lists[p1]
        val icFolder = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_menu_24dp, null)
        val icBoard = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_document, null)

        //폴더 : MGM,  화면 : REG
        if (itemlist.FG_MENU.equals("MGM")) {
            p0.itemView.img01.setImageDrawable(icFolder)

        } else {
            p0.itemView.img01.setImageDrawable(icBoard)
        }

        if (p0.itemView.txt01 != null) {
            p0.itemView.txt01.setText(itemlist.NM_MENU)
        }

        p0.itemView.txtBadge01.setVisibility(View.GONE)

        for (j in 0..listsC!!.size - 1) {
            if (itemlist.CD_MENU.equals(listsC.get(j).CD_MENU)) {
                if (listsC.get(j).QT_CNT.equals("0")) {
                    p0.itemView.txtBadge01.setVisibility(View.GONE)
                } else {
                    p0.itemView.txtBadge01.setVisibility(View.VISIBLE)
                    p0.itemView.txtBadge01.setText(listsC.get(j).QT_CNT)
                }
            }
        }
        if (index == p1) {
            p0.itemView.setBackgroundColor(Color.parseColor("#5C6BC0"))
        } else {
            p0.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        p0.itemView.setOnClickListener {
            index = p1
            notifyDataSetChanged()
            if (itemlist.FG_MENU.equals("REG")) {
                val intent = Intent(context, BoardActivitySub01::class.java)
                intent.putExtra("CD_MENU", H.boardItemInfo!![p1].CD_MENU)
                context.startActivity(intent)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

data class BoardItemInfo(
    val CD_MENU: String,
    val NM_MENU: String,
    val NO_POS: String,
    val FG_MENU: String
)

data class BoardItemInfoCount(
    val CD_MENU: String,
    val NM_MENU: String,
    val QT_CNT: String
)
