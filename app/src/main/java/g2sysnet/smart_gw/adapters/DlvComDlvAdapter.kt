package g2sysnet.smart_gw.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import g2sysnet.smart_gw.R
import g2sysnet.smart_gw.inters.EventTextChange
import g2sysnet.smart_gw.models.DlvComDlvModel
import kotlinx.android.synthetic.main.dlv_complete_dlv_item_row.view.*


class DlvComDlvAdapter(val context: Context, val lists:List<DlvComDlvModel>,val eventTextChange: EventTextChange):
    RecyclerView.Adapter<DlvComDlvAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.dlv_complete_dlv_item_row, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int=lists.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.no.text=(p1+1).toString()
        p0.name.text=lists[p1].NM_ITEM
        p0.sn.setText(lists[p1].NO_SN)

        p0.sn.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                eventTextChange.changeList(p1,p0.sn.text.toString())
            }

        })
    }
    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val no=itemView.dcd_no
        val name=itemView.dcd_nm_item
        var sn=itemView.dcd_sn
    }
}