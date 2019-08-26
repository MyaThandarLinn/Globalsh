package g2sysnet.smart_gw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.jaredrummler.materialspinner.MaterialSpinner
import com.loopj.android.http.RequestParams
import g2sysnet.smart_gw.adapters.ItemHelpAdapter
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.ItemHelpModel
import g2sysnet.smart_gw.models.SpinnerChildModel
import kotlinx.android.synthetic.main.activity_item_help_popup.*
import org.jetbrains.anko.toast

class ItemHelpPopup : AppCompatActivity(),EventChanger {

    var  CD_FIRM=""
    var  CD_DUMMY01=""
    var  CD_DUMMY02=""
    var  CD_DUMMY03=""
    var  CD_DUMMY04=""

    var CD_DUMMY01_List : MutableList<String> = mutableListOf("")
    var CD_DUMMY02_List : MutableList<String> = mutableListOf("")
    var CD_DUMMY03_List : MutableList<String> = mutableListOf("")
    var CD_DUMMY04_List : MutableList<String> = mutableListOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_help_popup)

        H.curSelectedPosition=0

        setSupportActionBar(item_hlelp_toolbar)

        val actionBar = supportActionBar
        // Set toolbar title/app title
        actionBar!!.title = getString(R.string.item_help)
        // Set action bar elevation
        actionBar.elevation = 4.0F
        // Display the app icon in action bar/toolbar
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setLogo(R.mipmap.ic_left_logo)
        actionBar.setDisplayUseLogoEnabled(true)


        CD_FIRM = H.currentUser!!.CD_FIRM
        item_help_recycler.layoutManager = LinearLayoutManager(this@ItemHelpPopup)

        postSpinner("MAS071",spn_MAS071)
        postSpinner("MAS072",spn_MAS072)
        postSpinner("MAS073",spn_MAS073)
        postSpinner("MAS074",spn_MAS074)

        itemHelpDataRequest()

        btnSearch.setOnClickListener{
            itemHelpDataRequest()
        }

        btn_go_bck_to_as_com.setOnClickListener {
            H.as_com_current_cd_item=H.itemhelp!![H.curSelectedPosition].CD_ITEM
            H.as_com_current_nm_item=H.itemhelp!![H.curSelectedPosition].NM_ITEM

            val intent = Intent(this@ItemHelpPopup, ASComplete::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        bck_to_pg15.setOnClickListener {
            finish()
        }
    }

    private fun itemHelpDataRequest() {
        val params = RequestParams()
        params.put("nm_sp","SP_MBL_MAS_ITEM_S01")
        params.put("param","$CD_FIRM|$CD_DUMMY01|$CD_DUMMY02|$CD_DUMMY03|$CD_DUMMY04|")

        H.L(params.toString())

        val route = H.getRoute(ip = H.ip_address)
        H.doPost(route,params,object : WaitInter{
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.itemhelp = Gson().fromJson(jAry.toString(),Array<ItemHelpModel>::class.java).toList()
                val adapter = ItemHelpAdapter(this@ItemHelpPopup,H.itemhelp!!,this@ItemHelpPopup)
                item_help_recycler.adapter = adapter
            }

            override fun error(err: String) {
                toast(err)
                H.itemhelp = arrayListOf()
                val adapter = ItemHelpAdapter(this@ItemHelpPopup,H.itemhelp!!,this@ItemHelpPopup)
                item_help_recycler.adapter = adapter
            }
        })
    }

    private fun postSpinner(code: String, spinner: MaterialSpinner){
        val params = RequestParams()
        params.put("nm_sp","SP_MBL_MAS_CODEL_S_01")
        params.put("param","$CD_FIRM|$code||")

        val route = H.getRoute(ip = H.ip_address)
        H.doPost(route,params,object : WaitInter{
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.itemhelp_spinner_list = Gson().fromJson(jAry.toString(),Array<SpinnerChildModel>::class.java).toList()
                var list: MutableList<String> = mutableListOf("")
                H.itemhelp_spinner_list?.forEach {
                    list.add(it.NM_FLAG)
                    when(code){
                        "MAS071" ->CD_DUMMY01_List.add(it.CD_FLAG)
                        "MAS072" ->CD_DUMMY02_List.add(it.CD_FLAG)
                        "MAS073" ->CD_DUMMY03_List.add(it.CD_FLAG)
                        else->CD_DUMMY04_List.add(it.CD_FLAG)
                    }
                }

                Log.d("aaaaaa",list.toString())
                spinner.setItems(list)
                spinner.setOnItemSelectedListener { view, position, id, item ->
                    when(code) {
                        "MAS071" -> CD_DUMMY01=CD_DUMMY01_List[position]
                        "MAS072" -> CD_DUMMY02=CD_DUMMY02_List[position]
                        "MAS073" -> CD_DUMMY03=CD_DUMMY03_List[position]
                        else -> CD_DUMMY04=CD_DUMMY04_List[position]
                    }
                }
            }

            override fun error(err: String) {
                toast(err)
            }
        })
    }

    override fun change(id: Int) {
        H.curSelectedPosition=id
    }
}
