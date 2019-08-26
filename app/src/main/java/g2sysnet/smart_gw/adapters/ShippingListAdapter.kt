package g2sysnet.smart_gw.adapters


import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import g2sysnet.smart_gw.R
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.models.DeliveryListModel
import kotlinx.android.synthetic.main.shipping_list_row.view.*
import org.jetbrains.anko.textColor


class ShippingListAdapter(val context: Context, val lists: List<DeliveryListModel>, val eventChange: EventChanger) :
    RecyclerView.Adapter<ShippingListAdapter.ViewHolder>() {
    var selected_position = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.shipping_list_row, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val sm = lists[p1]

        p0.itemView.setBackgroundColor(if (selected_position === p1) Color.LTGRAY else Color.TRANSPARENT)

        p0.itemView.table_number.text = (p1 + 1).toString()
        p0.itemView.table_time.text = sm.NM_DLVTIME
        p0.itemView.table_municipality.text = sm.NM_CITY
        p0.itemView.table_customer_name.text = sm.NM_PERSON
        p0.itemView.table_product.text = sm.NM_ITEM
        p0.itemView.table_contact_us.text = changeTelFormat(sm.NO_HP)
        p0.itemView.table_quantity.text = String.format("%,d", sm.QT.toDouble().toInt())
        p0.itemView.table_co_delivery.text = sm.YN_CODLV
        p0.itemView.table_delivery_category.text = sm.NO_SO

        when (sm.FG_DLV) {
            "DLV" -> p0.itemView.table_delivery_category.text = "납품"
            "SET" -> p0.itemView.table_delivery_category.text = "설치"
        }

        if (sm.ST_DLV.contentEquals("Y")) {
            p0.itemView.table_number.setTextColor(context.resources.getColor(R.color.colorPrimary))
            p0.itemView.table_time.setTextColor(context.resources.getColor(R.color.colorPrimary))
            p0.itemView.table_municipality.setTextColor(context.resources.getColor(R.color.colorPrimary))
            p0.itemView.table_customer_name.setTextColor(context.resources.getColor(R.color.colorPrimary))
            p0.itemView.table_product.setTextColor(context.resources.getColor(R.color.colorPrimary))
            p0.itemView.table_contact_us.setTextColor(context.resources.getColor(R.color.colorPrimary))
            p0.itemView.table_quantity.setTextColor(context.resources.getColor(R.color.colorPrimary))
            p0.itemView.table_co_delivery.setTextColor(context.resources.getColor(R.color.colorPrimary))
            p0.itemView.table_delivery_category.setTextColor(context.resources.getColor(R.color.colorPrimary))

        } else if (sm.ST_DLV.contentEquals("")) {
            p0.itemView.table_number.textColor = Color.BLACK
            p0.itemView.table_time.textColor = Color.BLACK
            p0.itemView.table_municipality.textColor = Color.BLACK
            p0.itemView.table_customer_name.textColor = Color.BLACK
            p0.itemView.table_product.textColor = Color.BLACK
            p0.itemView.table_contact_us.textColor = Color.BLACK
            p0.itemView.table_quantity.textColor = Color.BLACK
            p0.itemView.table_co_delivery.textColor = Color.BLACK
            p0.itemView.table_delivery_category.textColor = Color.BLACK
        }

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

    fun changeTelFormat(inputstr: String): String {
        val fmtedstr = inputstr.replace("-", "")
        return fmtedstr.substring(0, fmtedstr.length - 8) + "-" + fmtedstr.substring(
            fmtedstr.length - 8,
            fmtedstr.length - 4
        ) + "-" + fmtedstr.substring(fmtedstr.length - 4, fmtedstr.length)
    }
}