package g2sysnet.smart_gw.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import g2sysnet.smart_gw.R
import g2sysnet.smart_gw.UserInfo
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.ItemInfo
import kotlinx.android.synthetic.main.dept_row.view.*

class DepartmentAdapter(var context: Context, var lists: ArrayList<ItemInfo>) :
    RecyclerView.Adapter<DepartmentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.dept_row, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        var sm=lists[p1]

        val padding_in_dp = 10  // 6 dps
        val scale = context.resources.getDisplayMetrics().density
        val padding_in_px = (padding_in_dp * scale + 0.5f).toInt()
        val lv = Integer.parseInt(sm.NO_LEVEL) - 1

            if(sm.FG_HEADER.equals("H")){
                p0.itemView.txt01.text = (sm.DEPT)
                p0.itemView.img01.setImageDrawable(ResourcesCompat.getDrawable(context.resources,R.drawable.ic_dept,null))

                if (Build.VERSION.SDK_INT < 23) {
                    p0.itemView.txt01.setTextAppearance(context, R.style.TextAppearanceMedium)
                } else {
                    p0.itemView.txt01.setTextAppearance(R.style.TextAppearanceMedium)
                }
                p0.itemView.txt00.setPadding(padding_in_px * lv, 0, 0, 0)
            }else{
                p0.itemView.txt01.text = "("+ sm.CD_EMP + ")" + sm.NM_EMP
                p0.itemView.img01.setImageDrawable(ResourcesCompat.getDrawable(context.resources,R.drawable.ic_people,null))

                if (Build.VERSION.SDK_INT < 23) {
                    p0.itemView.txt01.setTextAppearance(context, R.style.TextAppearanceSmall)
                } else {
                    p0.itemView.txt01.setTextAppearance(R.style.TextAppearanceSmall)
                }
                p0.itemView.txt00.setPadding(padding_in_px * (lv + 1), 0, 0, 0)
                p0.itemView.setOnClickListener{
                    H.chooseUserModel=lists[p1]
                    context.startActivity(Intent(context, UserInfo::class.java))
                }
            }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}