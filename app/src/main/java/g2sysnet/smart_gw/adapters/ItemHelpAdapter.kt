package g2sysnet.smart_gw.adapters

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import g2sysnet.smart_gw.R
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.models.ItemHelpModel
import kotlinx.android.synthetic.main.item_help_row.view.*

class ItemHelpAdapter(val context: Context, val lists: List<ItemHelpModel>, val eventChange: EventChanger) :
    RecyclerView.Adapter<ItemHelpAdapter.ViewHolder>() {

    var selected_position = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_help_row, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val sm = lists[p1]
        val no = p1+1

        p0.itemView.setBackgroundColor(if (selected_position === p1) Color.LTGRAY else Color.TRANSPARENT)

        p0.itemView.no.text = no.toString()
        p0.itemView.product_name.text = sm.NM_ITEM
        p0.itemView.part_number.text = sm.CD_ITEM
        p0.itemView.circulation.text = sm.NM_DUMMY01
        p0.itemView.setOnClickListener {
            eventChange.change(p1)

            if (p0.adapterPosition == RecyclerView.NO_POSITION) {
            }

            notifyItemChanged(selected_position)
            selected_position = p0.adapterPosition
            notifyItemChanged(selected_position)
        }
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}