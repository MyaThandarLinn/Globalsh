package g2sysnet.smart_gw.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import g2sysnet.smart_gw.ApprovalActivitySub01
import g2sysnet.smart_gw.R
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.ApprovalModel
import kotlinx.android.synthetic.main.activity_approval_row.view.*

class ApprovalAdapter(val context: Context, val lists: List<ApprovalModel>, val eventChange: EventChanger,val FG_Board : String) :
    RecyclerView.Adapter<ApprovalAdapter.ViewHolder>() {
    var selected_position = -1
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.activity_approval_row, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val sm = lists[p1]

        //H.L(p1.toString())

        p0.itemView.setBackgroundColor(if (selected_position === p1) context.getResources().getColor(R.color.divider_colour) else Color.TRANSPARENT)
        p0.itemView.txt01.text = sm.NM_DOC
        p0.itemView.txt02.text = sm.DT_APP

        val YN_F = sm.YN_FILE
        if (YN_F.contentEquals("Y")) {
            p0.itemView.txt03.text = ""
            p0.itemView.txt03.visibility = View.VISIBLE
        } else {
            p0.itemView.txt03.visibility = View.INVISIBLE
        }

        p0.itemView.txt04.text = sm.NM_EMP

        p0.itemView.setOnClickListener {
            eventChange.change(p1)

            if (p0.adapterPosition == RecyclerView.NO_POSITION) {
            }
            val curDelList = H.APPROVAL_LIST!![H.curRow]
            val NO_APP = curDelList.NO_APP
            val YN_FILE = curDelList.YN_FILE
            val intent = Intent(context,ApprovalActivitySub01::class.java)
            intent.putExtra("NO_APP", NO_APP)
            intent.putExtra("YN_FILE", YN_FILE)
            intent.putExtra("FG_BOARD", FG_Board)
            context.startActivity(intent)

            notifyItemChanged(selected_position)
            selected_position = p0.adapterPosition
            notifyItemChanged(selected_position)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}