package g2sysnet.smart_gw.connect

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import g2sysnet.smart_gw.MessageP1
import g2sysnet.smart_gw.R
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.model.MessageModel

import kotlinx.android.synthetic.main.message_row.view.*


class MessageAdapter(var context: Context, var lists: List<MessageModel>, val eventChange: EventChanger) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    var selected_position = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

        val v = LayoutInflater.from(context).inflate(R.layout.message_row, p0, false)
        return ViewHolder(v)


    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        var sm = lists[p1]
        val count = p1+1
        p0.itemView.message_person_name.text = sm.NM_EMP_SEND
        p0.itemView.message_msg.text = sm.TXT_MESSAGE
        p0.itemView.message_time.text = sm.TM_SEND

        p0.itemView.message_img_alarm.visibility = View.INVISIBLE

        if (sm.TM_READ == "") run {
            if (H.FG_TAB == "TAB01" || H.FG_TAB == "TAB02") {
                p0.itemView.message_img_alarm.visibility = View.VISIBLE
            }
        }
        if(H.FG_TAB.contentEquals("TAB02")){
            H.UnreadMessageCount = count
            H.L(H.UnreadMessageCount.toString())
            H.L(p1.toString())
        }

        p0.itemView.setOnClickListener {
            eventChange.change(p1)

            if (p0.adapterPosition == RecyclerView.NO_POSITION) {
            }

            val intent = Intent(context, MessageP1::class.java)

            context.startActivity(intent)


        }

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}