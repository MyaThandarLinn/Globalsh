package g2sysnet.smart_gw.adapters


import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import g2sysnet.smart_gw.R
import g2sysnet.smart_gw.inters.EventTextChange
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.RequiredMaterialModel
import kotlinx.android.synthetic.main.usable_part_row.view.*
import java.text.NumberFormat
import java.util.*

class UsedPartAdapter   (val context: Context, val lists: List<RequiredMaterialModel>,val eventTextChange: EventTextChange) :
    RecyclerView.Adapter<UsedPartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.usable_part_row, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val sm = lists[p1]

        p0.itemView.up_no.text = (p1 + 1).toString()
        p0.itemView.up_product_name.text = sm.NM_ITEM
        p0.itemView.up_unit_price.text = String.format("%,d",sm.UM_SUPPLY.toDouble().toInt())
        p0.itemView.up_quantity.setText(String.format("%,d",sm.QT.toDouble().toInt()))

        H.QTData.add(sm.QT.toDouble().toInt())
        H.L(sm.QT.toDouble().toInt().toString())

        p0.itemView.btn_minus.setOnClickListener {
            if (!p0.itemView.up_quantity.text.toString().equals("0")){
                H.QTData[p1] = H.QTData[p1]-1
                p0.itemView.up_quantity.setText(H.QTData[p1].toString())
            }
        }

        p0.itemView.btn_plus.setOnClickListener {
            H.QTData[p1]= H.QTData[p1]+1
            p0.itemView.up_quantity.setText(H.QTData[p1].toString())
        }

        p0.itemView.up_quantity.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input=p0.itemView.up_quantity.text.toString()
                if(input.equals("0")||input.equals("")) H.QTData[p1]=0
                else H.QTData[p1]=input.toDouble().toInt()
                eventTextChange.changeList(p1,input)
            }
        })
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}