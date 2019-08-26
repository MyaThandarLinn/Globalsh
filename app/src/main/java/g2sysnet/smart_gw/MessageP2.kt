package g2sysnet.smart_gw

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.loopj.android.http.RequestParams

import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.m_model.GetMessageUserModel
import g2sysnet.smart_gw.m_model.EmpDepartmentModel
import g2sysnet.smart_gw.m_model.ReceiveItem_receive_user
import kotlinx.android.synthetic.main.message_p2.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.util.*

class MessageP2 : AppCompatActivity(), EventChanger {


    var msgmodel = H.messageModel!![H.curRow]
    var _listPopupEMP: ListView? = null
    var Popup_CD_EMP = ""
    var Popup_NM_EMP = ""
    var Popup_CD_DEPT = ""
    var Popup_NM_DEPT = ""
    var Popup_FG_HEADER = ""
    var slip_number = ""
    var receive_user: ArrayList<ReceiveItem_receive_user>? = null
    var departmentModel: List<EmpDepartmentModel>? = null
    private val sectionHeader = TreeSet<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message_p2)
        supportActionBar?.hide()

        initSearch()
        if(H.empCon){
            msg2_txt_recipients.text=H.deptNameList

        }
        msg2_txt_sender.text = H.currentUser!!.NM_EMP
        msg2_txt_content.setText(msgmodel.TXT_MESSAGE)
        getSlipNumber()

        if (H.TP_RETURN == "N") {
            toast("U check  N ")
            clearMsg()
            if (receive_user != null) {
                receive_user!!.clear()
            }

        } else if (H.TP_RETURN == "A") {

            toast("U check  A")
            msg2_btn_emp.visibility = View.GONE
            msg2_uncheck_message.visibility = View.GONE
            msg2_txt_recipients.text = msgmodel.DC_EMP_RECEIVE

        } else if (H.TP_RETURN == "D") {

            toast("U check  D ")
            clearMsg()
            if (receive_user != null) {
                receive_user!!.clear()
            }

        }


        msg2_btn_send.setOnClickListener {
            sendAlert()
        }

        msg2_btn_cancel.setOnClickListener {
            finish()
        }


        msg2_uncheck_message.setOnClickListener {
            uncheckAlert()
        }




        msg2_btn_emp.setOnClickListener {
            //empGetResultData()

            var intent = Intent(this@MessageP2, MessageEmp::class.java)
            startActivity(intent)



        }

        val task = SearchTask()
        task.execute()

        var msgHelper = MessageHelper()
        msgHelper.getMessageUser(msgmodel)
        msgHelper.getDeviceToken()

        //searchDept()
    }

    fun sendAlert() {

        val alertDialog = android.app.AlertDialog.Builder(this@MessageP2)
        alertDialog.setTitle("Send")
        alertDialog.setMessage(getString(R.string.msg_send))

        var messageSaveSendHelper = MessageSaveSendHelper()
        alertDialog.setPositiveButton("YES") { dialog, which ->
            messageSaveSendHelper.saveAndSend(
                slip_number,
                msg2_txt_content.text.toString(),
                msg2_txt_recipients.text.toString(),
                this@MessageP2
            )
            messageSaveSendHelper.saveAndReceive(slip_number, this@MessageP2)
            messageSaveSendHelper.sendNotes(this@MessageP2)

        }
        alertDialog.setNegativeButton(
            "NO"
        ) { dialog, which -> dialog.cancel() }

        alertDialog.show()
    }

    fun uncheckAlert() {
        alert {
            this.title = "Uncheck"
            this.message = getString(R.string.msg_delete)
            yesButton {
                msg2_txt_recipients.text = ""
                if (receive_user != null) {
                    receive_user!!.clear()
                }
                noButton {
                    it.cancel()
                }

                toast("You click YES from UNCHECK")
            }

        }.show()

    }

    fun clearMsg() {
        msg2_btn_emp.visibility = View.VISIBLE
        msg2_uncheck_message.visibility = View.VISIBLE
        msg2_txt_recipients.text = ""
    }

    fun getSerchGetReceive() {
        var cd_firm = H.currentUser!!.CD_FIRM
        var nm_sp = "SP_GET_RECEIVE_ALL_MESSAGE"
        //CD_FIRM,NO_MESSAGE
        var param = "$cd_firm|${msgmodel.NO_MESSAGE}"
        //dataMehtod(nm_sp, param, "Get Search Get Receive")
    }


    fun sendPush() {

    }


    //getting a slilp number
    fun getSlipNumber() {
        /*nm_sp=>UP_MOB_AutoCount
       param=>CD_FIRM, "GRW", "7", "MSG"
        */
        //GET SLIP_NO
        val params = RequestParams()
        params.put("nm_sp", "UP_MOB_AutoCount")
        params.put("param", "1000|GRW|7|MSG")
        val route = H.getRoute(ip = H.ip_address)

        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                H.L("Slip number is ******************\n $result")
                slip_number = result
            }

            override fun error(err: String) {
                H.L("Slip number error is *****************\n $err")
            }

        })


    }

    fun initSearch() {


        /*  nm_sp=>SP_GET_MESSAGE_USER
            param=>CD_FIRM|NO_MESSAGE
         */


        var cd_firm = H.currentUser!!.CD_FIRM
        var no_message = H.messageModel!![H.curRow].NO_MESSAGE
        val params = RequestParams()
        params.put("nm_sp", "SP_GET_MESSAGE_USER")
        params.put("param", "$cd_firm|$no_message")
        val route = H.getRoute(ip = H.ip_address)
        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                H.L("Get Message User************ SP_GET_MESSAGE_USER \n************* $result")
                val jAry = H.strToJArr(result)
                H.getMessageUserModel =
                    Gson().fromJson(jAry.toString(), Array<GetMessageUserModel>::class.java).toList()
                H.FG_HEADER = "H"
            }

            override fun error(err: String) {
                H.L("Get Message User Error  ************ SP_GET_MESSAGE_USER \n************* $err")
            }

        })

    }

    fun searchDept() {
        //param=> CD_FIRM
        //nm_sp=>"SP_MOB_GET_DEPT"

        var Dept = ""
        var cd_firm = H.currentUser!!.CD_FIRM
        val params = RequestParams()
        params.put("nm_sp", "SP_MOB_GET_DEPT")
        params.put("param", cd_firm)
        val route = H.getRoute(ip = H.ip_address)
        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {

                val jAry = H.strToJArr(result)
                H.m_order_emp_message = Gson().fromJson(
                    jAry.toString(),
                    Array<EmpDepartmentModel>::class.java
                ).toList() // as ArrayList<EmpDepartmentModel>

                /* **************** */
                departmentModel = H.m_order_emp_message!! // as ArrayList<EmpDepartmentModel>
                var loopFinish = departmentModel!!.size
                for (i in 0.until(loopFinish - 1)) {
                    H.FG_HEADER = "H"
                    if (departmentModel!![i].NO == "1") {
                        Dept = departmentModel!![i].DEPT
                    }
                    for (j in 0.until(loopFinish - 1)) {
                        if (Dept == departmentModel!![j].DEPT) {
                            if (departmentModel!![j].CD_EMP != "") {
                                H.FG_HEADER = "D"
                            }
                        }
                    }
                }


            }

            override fun error(err: String) {
            }

        })

    }

    fun searchEmp() {


//        if (departmentModel != null) {
//            var m_adapter = ItemAdpterEmp(
//                this@MessageP2, R.layout.dialog_emp_row,
//                departmentModel!! as ArrayList<EmpDepartmentModel>
//            )
//            _listPopupEMP!!.setAdapter(m_adapter)
//
//        }

    }


    fun empGetResultData() {


        var item_info_emp_list = H.m_order_emp_message


        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_emp, null)
        val builder = AlertDialog.Builder(this@MessageP2, R.style.AppTheme_Dark_Dialog)

        builder.setTitle(getString(R.string.title_emp_help))
        builder.setIcon(android.R.drawable.ic_menu_search)
        _listPopupEMP = dialogView.findViewById<View>(R.id.dialog_search_view) as ListView


//        val imageView = dialogView.findViewById<ImageView>(R.id.message_dialog_search)
//        val msg2_tv_Search = dialogView.findViewById<TextView>(R.id.msg2_tv_Search)
//        val search_edt = dialogView.findViewById<TextView>(R.id.search_edt)

        //ItemInfoEmp  EmpDepartmentModel

        searchEmp()

        _listPopupEMP!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            //val listItem = _listPopupEMP!!.getItemAtPosition(position) as Int
            Popup_CD_EMP = item_info_emp_list!![position].CD_EMP
            Popup_NM_EMP = item_info_emp_list!![position].NM_EMP
            Popup_CD_DEPT = item_info_emp_list!![position].CD_DEPT
            Popup_NM_DEPT = item_info_emp_list!![position].NM_DEPT
            Popup_FG_HEADER = H.FG_HEADER
            toast("YOU CLICK LIST ITEM $position")
            toast("FG_HEADER is ${H.FG_HEADER}")

        })




        builder.setView(dialogView)

        builder.setPositiveButton("확인") { dialog, which ->
            Toast.makeText(
                applicationContext,
                "확인", Toast.LENGTH_SHORT
            ).show()


            var Tmp_txt = msg2_txt_recipients.text

            if (Popup_CD_EMP == "" || Popup_CD_DEPT == "") {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.msg_no_row_select) + "No row is selected Error",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                //  val Array_size = receive_user!!.size
//                            if(Array_size > 0){
//                                Array_size = Array_size;
//                            }
                if (Popup_FG_HEADER == "H") {
                    Tmp_txt = Tmp_txt.toString() + Popup_NM_DEPT + ";"
                    msg2_txt_recipients.text = Tmp_txt
                    H.FG_HEADER = "H"

                    H.receive_user_message!!.add(
                        ReceiveItem_receive_user(
                            Popup_CD_DEPT
                        )

                    )
                } else {
                    Tmp_txt = (Tmp_txt).toString() + Popup_NM_EMP + ";"
                    msg2_txt_recipients.text = Tmp_txt
                    H.FG_HEADER = "D"
                    H.receive_user_message!!.add(
                        ReceiveItem_receive_user(
                            Popup_CD_DEPT
                        )
                    )

                }

                //return@setPositiveButton
            }


        }



        builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which -> })

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)//없어지지 않도록 설정
        dialog.show()

    }


    fun Search(result: String) {

        if (result == null || result == "") {
            //Toast.makeText(this, getString(R.string.msg_search_none), Toast.LENGTH_SHORT).show()
            toast("getString(R.string.msg_search_none)")
            return
        } else {
            val jAry = H.strToJArr(result)
            H.getMessageUserModel =
                Gson().fromJson(jAry.toString(), Array<GetMessageUserModel>::class.java).toList()


            var lists = H.getMessageUserModel
            var list_size = H!!.getMessageUserModel!!.size
            if (lists != null) {
                for (i in 0.until(list_size)) {


                }
            }

        }

    }


    override fun change(id: Int) {
        H.curRow = id
    }


    //    inner class ItemAdpterEmp(context: Context, textViewResourceId: Int, private val items: ArrayList<EmpDepartmentModel>) :
//        ArrayAdapter<EmpDepartmentModel>(context, textViewResourceId, items) {
//
//        private val TP_DEPT = 0
//        private val TP_EMP = 1
//
//        override fun getItemViewType(position: Int): Int {
//            return if (sectionHeader.contains(position)) TP_DEPT else TP_EMP
//        }
//
//        override fun getItem(position: Int): EmpDepartmentModel? {
//            return items[position]
//        }
//
//        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//            var v = convertView
//            val holder = ViewHolder()
//            holder.imageView = null
//
//            if (v == null) {
//                val vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                v = vi.inflate(R.layout.dialog_emp_row, null)
//            }
//
//            val p = items[position]
//            if (p != null) {
//                val txt01 = v!!.findViewById<View>(R.id.txt01) as TextView
//                val txt00 = v.findViewById<View>(R.id.txt00) as TextView
//
//                val img01 = v.findViewById<View>(R.id.img01) as ImageView
//
//                holder.imageView = v.findViewById<View>(R.id.img01) as ImageView
//                holder.imageView!!.setImageResource(R.drawable.ic_dept)
//                val lay01 = v.findViewById<View>(R.id.msg_helper_lay01) as RelativeLayout
//
//                val padding_in_dp = 10  // 6 dps
//                val scale = resources.displayMetrics.density
//                val padding_in_px = (padding_in_dp * scale + 0.5f).toInt()
//
//                val lv = Integer.parseInt(p!!.NO_LEVEL) - 1
//
//                txt01.setText(p!!.DEPT)
//                if (H.FG_HEADER== "H") {
//                    txt01.setText(p!!.DEPT)
//                    img01.setImageResource(R.drawable.ic_dept)
//                    //                    holder.imageView.setImageResource(R.drawable.ic_dept);
//                    txt00.setPadding(padding_in_px * lv, 0, 0, 0)
//                    if (Build.VERSION.SDK_INT < 23) {
//                        txt01.setTextAppearance(this@MessageP2, android.R.style.TextAppearance_Medium)
//                    } else {
//                        txt01.setTextAppearance(android.R.style.TextAppearance_Medium)
//                    }
//                    //                    String strColor = "#2d3c99";
//                    //                    lay01.setBackgroundColor(Color.parseColor(strColor));
//
//                } else {
//                    txt01.text = "(" + p!!.CD_EMP + ") " + p!!.NM_EMP
//
//                    if (Build.VERSION.SDK_INT < 23) {
//                        txt01.setTextAppearance(this@MessageP2, android.R.style.TextAppearance_Small)
//                    } else {
//                        txt01.setTextAppearance(android.R.style.TextAppearance_Small)
//                    }
//                    img01.setImageResource(R.drawable.ic_person)
//                    //                    holder.imageView.setImageResource(R.drawable.ic_people);
//                    txt00.setPadding(padding_in_px * (lv + 1), 0, 0, 0)
//
//                    //                    String strColor = "#3F51B5";
//                    //                    lay01.setBackgroundColor(Color.parseColor(strColor));
//                }
//            }
//            v!!.tag = position
//
//            return v
//        }
//
//        inner class ViewHolder {
//            internal var textView: TextView? = null
//            internal var imageView: ImageView? = null
//        }
//
//    }
    inner class SearchTask : AsyncTask<String, Integer, String>() {

        internal var asyncDialog = ProgressDialog(this@MessageP2)
        override fun onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            asyncDialog.setMessage(getString(R.string.msg_loading))
            // show dialog
            asyncDialog.show()
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String {
            return ""
        }

        override fun onPostExecute(result: String?) {
            asyncDialog.dismiss()
            super.onPostExecute(result)
        }

    }


}



