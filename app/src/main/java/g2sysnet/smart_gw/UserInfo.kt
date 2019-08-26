package g2sysnet.smart_gw

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.squareup.picasso.Picasso
import g2sysnet.smart_gw.libby.H
import kotlinx.android.synthetic.main.activity_user_info.*
import org.jetbrains.anko.image
import org.jetbrains.anko.toast


class UserInfo : AppCompatActivity() {

    var NM_DEPT = ""
    var CD_DEPT = ""
    var NM_EMP = ""
    var CD_EMP = ""
    var HP = ""
    var EMAIL = ""
    var url = ""


    val dept_model = H.chooseUserModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        HP = H.currentUser!!.NO_HP
        url = H.currentUser!!.DC_PHOTO

        if (dept_model != null) {
            H.L("you choose" + H.chooseUserModel.toString())
            CD_DEPT = dept_model.CD_DEPT
            NM_EMP = dept_model.NM_EMP
            CD_EMP = dept_model.CD_EMP
            NM_DEPT = dept_model.NM_DEPT
            txt_dept.setText("$NM_DEPT($CD_DEPT)")
            //txt_dept.setText("${dept_model.NM_DEPT}(${dept_model.CD_DEPT})")
            txt_name.setText("$NM_EMP($CD_EMP)")
            txt_phone.setText(dept_model.NO_HP)
            txt_email.setText(dept_model.DC_EMAIL)
            if(dept_model.DC_PHOTO!=""){
                Picasso.get().load(url)
                    .into(imageView)
            }

           H.L("DC photo is ${dept_model.DC_PHOTO}")


        } else {
            NM_DEPT = H.currentUser!!.NM_DEPT
            CD_DEPT = H.currentUser!!.CD_DEPT
            NM_EMP = H.currentUser!!.NM_EMP
            CD_EMP = H.currentUser!!.CD_EMP
            HP = H.currentUser!!.NO_HP
            EMAIL = H.currentUser!!.DC_EMAIL
            url = H.currentUser!!.DC_PHOTO
            txt_dept.setText("$NM_DEPT($CD_DEPT)")
            //txt_dept.setText("${dept_model.NM_DEPT}(${dept_model.CD_DEPT})")
            txt_name.setText("$NM_EMP($CD_EMP)")
            txt_phone.setText(HP)
            txt_email.setText(EMAIL)
            Picasso.get().load(url)
                .into(imageView)

        }

        H.L(dept_model.toString())



        // H.L("cd dept is ${dept_model.CD_DEPT}")


        txt_phone.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$HP"))
                startActivity(intent)

            } catch (e: Exception) {
                e.printStackTrace()
                toast("Can't connect ")
            }


        }

    }

    fun callPermission() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.CALL_PHONE),
//            1
//        )
    }


    fun Context.makePhoneCall(): Boolean {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:09682770265"))
            startActivity(intent)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}

