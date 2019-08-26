package g2sysnet.smart_gw

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.iid.FirebaseInstanceId
import g2sysnet.smart_gw.libby.H
import kotlinx.android.synthetic.main.sunli_flash.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat




class SmartNoteFlash : AppCompatActivity() {
    val MY_PERMISSIONS_REQUEST_CODE = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sunli_flash)

        H.hideDecorView(supportActionBar, window)

        if (!H.checkPlayServices(this)) finish()
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            val deviceToken = instanceIdResult.token
            H.token = deviceToken
            instanceID.setText(getString(R.string.registering_message_complete))
            progressBar.visibility= View.GONE
            checkPermission()
        }
    }


    fun checkPermission(){
        if ((ContextCompat.checkSelfPermission(this@SmartNoteFlash, Manifest.permission.CAMERA)
                    + ContextCompat.checkSelfPermission(
                this@SmartNoteFlash,Manifest.permission.READ_EXTERNAL_STORAGE)
                    + ContextCompat.checkSelfPermission(
                this@SmartNoteFlash, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )) != PackageManager.PERMISSION_GRANTED
        ) {

            // Do something, when permissions not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@SmartNoteFlash, Manifest.permission.CAMERA
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    this@SmartNoteFlash, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                val builder = AlertDialog.Builder(this@SmartNoteFlash)
                builder.setMessage("Camera, Read Contacts and Write External" + " Storage permissions are required to do the task.")
                builder.setTitle("Please grant those permissions")
                builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                    ActivityCompat.requestPermissions(
                        this@SmartNoteFlash,
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_CODE
                    )
                })
                builder.setNeutralButton("Cancel", null)
                val dialog = builder.create()
                dialog.show()
            } else {
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                    this@SmartNoteFlash,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_CODE
                )
            }
        } else {
            // Do something, when permissions are already granted
            Toast.makeText(this@SmartNoteFlash, "Permissions already granted", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@SmartNoteFlash, LoginActivity::class.java))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CODE -> {
                // When request is cancelled, the results array are empty
                if (grantResults.size > 0 && (grantResults[0]
                            + grantResults[1]
                            + grantResults[2]) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permissions are granted
                    Toast.makeText(this@SmartNoteFlash, "Permissions granted.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SmartNoteFlash, LoginActivity::class.java))
                } else {
                    // Permissions are denied
                    Toast.makeText(this@SmartNoteFlash, "Permissions denied.", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }


}
