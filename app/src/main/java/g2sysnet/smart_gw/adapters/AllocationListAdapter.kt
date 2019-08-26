package g2sysnet.smart_gw.adapters

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import g2sysnet.smart_gw.R
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.models.AllocationListModel
import kotlinx.android.synthetic.main.allocation_list_row.view.*

class AllocationListAdapter (val context: Context, val lists: List<AllocationListModel>, val eventChange: EventChanger) :
RecyclerView.Adapter<AllocationListAdapter.ViewHolder>(){
    var selected_position = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            val v = LayoutInflater.from(context).inflate(R.layout.allocation_list_row, p0, false)
            return ViewHolder(v)
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val al = lists[p1]

        p0.itemView.setBackgroundColor(if (selected_position === p1)Color.LTGRAY else Color.TRANSPARENT)

        p0.itemView.as_table_row_number.text = (p1+1).toString()
        p0.itemView.as_table_visit_time.text=al.NM_VISIT_TIME
        p0.itemView.as_table_custname.text=al.NM_PERSON
        p0.itemView.as_table_contact.text=changeTelFormat(al.NO_HP)
        p0.itemView.as_table_nmcity.text=al.NM_CITY
        p0.itemView.as_model_name.text=al.NM_MODEL
        p0.itemView.as_symptom_code.text=al.NM_SYMPTOM

        when(al.ST_PROC){
            "C" -> {
                p0.itemView.as_table_row_number.setTextColor(context.resources.getColor(R.color.colorPrimary))
                p0.itemView.as_table_visit_time.setTextColor(context.resources.getColor(R.color.colorPrimary))
                p0.itemView.as_table_custname.setTextColor(context.resources.getColor(R.color.colorPrimary))
                p0.itemView.as_table_contact.setTextColor(context.resources.getColor(R.color.colorPrimary))
                p0.itemView.as_table_nmcity.setTextColor(context.resources.getColor(R.color.colorPrimary))
                p0.itemView.as_model_name.setTextColor(context.resources.getColor(R.color.colorPrimary))
                p0.itemView.as_symptom_code.setTextColor(context.resources.getColor(R.color.colorPrimary))
            }

            "S" -> {
                p0.itemView.as_table_row_number.setTextColor(Color.BLACK)
                p0.itemView.as_table_visit_time.setTextColor(Color.BLACK)
                p0.itemView.as_table_custname.setTextColor(Color.BLACK)
                p0.itemView.as_table_contact.setTextColor(Color.BLACK)
                p0.itemView.as_table_nmcity.setTextColor(Color.BLACK)
                p0.itemView.as_model_name.setTextColor(Color.BLACK)
                p0.itemView.as_symptom_code.setTextColor(Color.BLACK)
            }
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

    fun changeTelFormat(inputstr:String):String{
        val fmtedstr=inputstr.replace("-","")
        return   fmtedstr.substring(0,fmtedstr.length-8)+"-"+fmtedstr.substring(fmtedstr.length-8,fmtedstr.length-4)+"-"+fmtedstr.substring(fmtedstr.length-4,fmtedstr.length)
    }
}