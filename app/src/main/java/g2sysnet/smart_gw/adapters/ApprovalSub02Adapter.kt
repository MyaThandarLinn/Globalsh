package g2sysnet.smart_gw.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import g2sysnet.smart_gw.R
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.ApprovalSub02Model
import kotlinx.android.synthetic.main.activity_approval_sub02_row.view.*

class ApprovalSub02Adapter(val context: Context, val lists: List<ApprovalSub02Model>, val eventChange: EventChanger) :
    RecyclerView.Adapter<ApprovalSub02Adapter.ViewHolder>() {
    var selected_position = -1
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.activity_approval_sub02_row, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val sm = lists[p1]

        p0.itemView.setBackgroundColor(if (selected_position === p1) context.getResources().getColor(R.color.divider_colour) else Color.TRANSPARENT)

            p0.itemView.txt01.text = sm.DC_OPN
            p0.itemView.txt02.text = sm.NM_EMP

        p0.itemView.setOnClickListener {
            eventChange.change(p1)
            if (p0.adapterPosition == RecyclerView.NO_POSITION) {
            }
            var sub02_list = H.APPROVAL_SUB02_LIST!![H.curRow]
            H.NO_OPN = sub02_list.NO_OPN
            H.CD_EMP_W = sub02_list.CD_EMP

            notifyItemChanged(selected_position)
            selected_position = p0.adapterPosition
            notifyItemChanged(selected_position)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}