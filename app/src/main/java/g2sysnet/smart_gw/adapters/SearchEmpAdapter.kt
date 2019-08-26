package g2sysnet.smart_gw.connect

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import g2sysnet.smart_gw.R
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.m_model.EmpDepartmentModel
import kotlinx.android.synthetic.main.dialog_emp_row.view.*
import org.jetbrains.anko.image
import org.jetbrains.anko.toast


class SearchEmpAdapter(var context: Context, var lists: List<EmpDepartmentModel>) :
    RecyclerView.Adapter<SearchEmpAdapter.ViewHolder>() {
    val TP_DEPT=0
    val TP_EMP=1
    var selected_position = 0



    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.dialog_emp_row, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        p0.itemView.setBackgroundColor(if (selected_position === p1) Color.parseColor("#010a46") else Color.TRANSPARENT)

        val sm=lists[p1]
        p0.itemView.dept_img01.setImageResource(R.drawable.ic_dept)
        val padding_in_dp = 10
        val lv=sm.NO_LEVEL.toInt()-1

        val padding_in_px=padding_in_dp+1
        p0.itemView.txt001.text=sm.DEPT
        if(H.FG_HEADER=="H")
        {
            p0.itemView.txt001.text=sm.DEPT
            // img01.setImageResource(R.drawable.ic_dept);
            p0.itemView.dept_img01.setImageResource(R.drawable.ic_dept)

            p0.itemView.txt000.setPadding(padding_in_px*padding_in_dp, 0, 0, 0)


            if (Build.VERSION.SDK_INT < 23) {
                p0.itemView.txt001.setTextAppearance(context, android.R.style.TextAppearance_Medium)
            } else {
                p0.itemView.txt001.setTextAppearance(android.R.style.TextAppearance_Medium)
            }


        }else{
            p0.itemView.txt001.text="(" + sm.CD_EMP + ") " + sm.NM_EMP
            if (Build.VERSION.SDK_INT < 23) {
                p0.itemView.txt001.setTextAppearance(context, android.R.style.TextAppearance_Small)
            } else {
                p0.itemView.txt001.setTextAppearance(android.R.style.TextAppearance_Small)
            }

            p0.itemView.dept_img01.setImageResource(R.drawable.ic_person)
//                    holder.imageView.setImageResource(R.drawable.ic_people);
            p0.itemView.txt000.setPadding(padding_in_px * (lv + 1), 0, 0, 0)

        }

        p0.itemView.setOnClickListener {
            //eventChange.change(p1)
            if(H.FG_HEADER=="H"){
                H.deptNameList=sm.NM_DEPT

            }else{


                H.deptNameList=sm.NM_EMP

            }

           context.toast("This is the position $p1 and NM_Dept is ${H.deptNameList} ")
            if (p0.adapterPosition == RecyclerView.NO_POSITION) {
            }

            notifyItemChanged(selected_position)
            selected_position = p0.adapterPosition
            notifyItemChanged(selected_position)






//
//            Popup_CD_EMP = (listItem as ItemInfoEmp).getCD_EMP()
//            Popup_NM_EMP = (listItem as ItemInfoEmp).getNM_EMP()
//
//            Popup_CD_DEPT = (listItem as ItemInfoEmp).getCD_DEPT()
//            Popup_NM_DEPT = (listItem as ItemInfoEmp).getNM_DEPT()
//
//            Popup_FG_HEADER = (listItem as ItemInfoEmp).getFG_HEADER()



        }

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}