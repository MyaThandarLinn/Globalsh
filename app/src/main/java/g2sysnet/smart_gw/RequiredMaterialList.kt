package g2sysnet.smart_gw


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.loopj.android.http.AsyncHttpClient.log
import g2sysnet.smart_gw.adapters.UsedPartAdapter
import g2sysnet.smart_gw.inters.EventChanger
import g2sysnet.smart_gw.inters.EventTextChange
import g2sysnet.smart_gw.libby.H
import kotlinx.android.synthetic.main.activity_required_material_list.*

class RequiredMaterialList : AppCompatActivity(), EventChanger,EventTextChange {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_required_material_list)

        setSupportActionBar(title_toolbar)

        val actionBar = supportActionBar
        // Set toolbar title/app title
        actionBar!!.title = getString(R.string.usedparts)
        // Set action bar elevation
        actionBar.elevation = 4.0F
        // Display the app icon in action bar/toolbar
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setLogo(R.mipmap.ic_left_logo)
        actionBar.setDisplayUseLogoEnabled(true)

        var tf=0
        for(i in 0..H.requiredMaterialList!!.size-1){
            tf+=H.requiredMaterialList!![i].QT.toDouble().toInt()
        }
        if(tf==0){
            btn_save_modified_cost.visibility= View.INVISIBLE
            btn_back_to_p15.visibility=View.INVISIBLE
        }else{
            btn_save_modified_cost.visibility=View.VISIBLE
            btn_back_to_p15.visibility=View.VISIBLE
        }

        usable_popup_nm_item.text=H.as_com_current_nm_item
        H.QTData.clear()
        val adapter = UsedPartAdapter(this@RequiredMaterialList, H.requiredMaterialList!!,this@RequiredMaterialList)
        usable_popup_nm_item.text=H.as_com_current_nm_item
        used_part_recycler.layoutManager = LinearLayoutManager(this@RequiredMaterialList)
        used_part_recycler.adapter = adapter


        btn_save_modified_cost.setOnClickListener {
            H.totalCost=0
            for(i in 0..H.requiredMaterialList!!.size-1){
                H.totalCost+=H.requiredMaterialList!![i].UM_SUPPLY.toDouble().toInt()*H.QTData!![i]
            }
            val intent=Intent(this@RequiredMaterialList, ASComplete::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
            startActivity(intent)
        }

        btn_back_to_p15.setOnClickListener {
            finish()
        }
    }

    override fun change(id: Int) {
        H.curRow = id
    }

    override fun changeList(p: Int, value: String) {
        var dtexist=0
        if(value.equals("0")||value.equals("")){
            for(i in 0..H.QTData.size-1){
                dtexist+=H.QTData[i]
            }
            if (dtexist==0){
                btn_save_modified_cost.visibility= View.INVISIBLE
                btn_back_to_p15.visibility=View.INVISIBLE
            }
        }else {
            btn_save_modified_cost.visibility=View.VISIBLE
            btn_back_to_p15.visibility=View.VISIBLE
        }
    }
}
