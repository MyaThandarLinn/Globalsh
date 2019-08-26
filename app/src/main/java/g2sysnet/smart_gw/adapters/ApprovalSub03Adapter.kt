package g2sysnet.smart_gw.adapters

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import g2sysnet.smart_gw.R
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.ApprovalSub03Model
import kotlinx.android.synthetic.main.activity_approval_sub03_row.view.*

class ApprovalSub03Adapter(val context: Context, val lists: List<ApprovalSub03Model>, val eventChange: EventChanger) :
    RecyclerView.Adapter<ApprovalSub03Adapter.ViewHolder>() {
    var selected_position = -1
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.activity_approval_sub03_row, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val sm = lists[p1]

        p0.itemView.setBackgroundColor(if (selected_position === p1) context.getResources().getColor(R.color.divider_colour) else Color.TRANSPARENT)

        p0.itemView.txt01.text = sm.NM_FILE

        p0.itemView.setOnClickListener {
            eventChange.change(p1)

            if (p0.adapterPosition == RecyclerView.NO_POSITION) {
            }
            val sub3_list = H.APPROVAL_SUB03_LIST!![0]
            val DC_URL = sub3_list.DC_URL
            try {
                val uri = Uri.parse("googlechrome://navigate?url=$DC_URL")
                val i = Intent(Intent.ACTION_VIEW, uri)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
            } catch (e: ActivityNotFoundException) {
                H.L(e.toString())
            }

//            notifyItemChanged(selected_position)
//            selected_position = p0.adapterPosition
//            notifyItemChanged(selected_position)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}