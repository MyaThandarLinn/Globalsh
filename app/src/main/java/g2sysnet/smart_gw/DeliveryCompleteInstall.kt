package g2sysnet.smart_gw

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.FileProvider
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.gson.Gson
import com.loopj.android.http.RequestParams
import com.squareup.picasso.Picasso
import g2sysnet.smart_gw.libby.H
import kotlinx.android.synthetic.main.activity_delivery_complete_install.*
import org.jetbrains.anko.toast
import java.io.*
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.models.*
import java.text.SimpleDateFormat
import java.util.*


class DeliveryCompleteInstall : AppCompatActivity(), SignaturePad.OnSignedListener{
    var currentPhotoPath = ""
    var imgPath = ""

    override fun onStartSigning() {
        signaturesave.isEnabled = true
        btnCls.isEnabled = true

    }

    override fun onClear() {
        signaturesave.isEnabled = false
        btnCls.isEnabled = false
    }

    override fun onSigned() {
        signaturesave.isEnabled = true
        btnCls.isEnabled = true
    }

    private val REQUEST_IMAGE_CAPTURE = 101
    var category_list: MutableList<String> = mutableListOf()
    var paymethod_list: MutableList<String> = mutableListOf()
    var unpaid_reason_list: MutableList<String> = mutableListOf("")
    var fgstair_list: MutableList<String> = mutableListOf()
    var spRelation_list: MutableList<String> = mutableListOf()

    var CD_NOTDLV = ""

    var image_path = ""
    var signature_path = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_complete_install)

        // Set the toolbar as support action bar
        setSupportActionBar(dci_toolbar)
        // Now get the support action bar
        val actionBar = supportActionBar
        // Set toolbar title/app title
        actionBar!!.title = getString(R.string.title_delivery_complete_install)
        // Set action bar elevation
        actionBar.elevation = 4.0F
        // Display the app icon in action bar/toolbar
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setLogo(R.mipmap.ic_left_logo)
        actionBar.setDisplayUseLogoEnabled(true)


        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        //linearLayout6.requestFocus()
        //et_DC_NOTDLV.showSoftInputOnFocus = false

        H.updateView(this@DeliveryCompleteInstall, year, month, day)
        dt_dlv_tv.text = H.getDate(this@DeliveryCompleteInstall)

        val list: DeliveryListModel = H.deliveryList!![H.curRow]

        //Get Dlv detail first
        val dlvdetailparams = RequestParams()
        val param = arrayOf(list.CD_FIRM, list.NO_SO, list.NO_LINE, list.NO_LINE_DLV)
        dlvdetailparams.put("nm_sp", "SP_MBL_DLV_DETAIL_S")
        dlvdetailparams.put("param", H.joinString(param))
        val dlvdetailroute = H.getRoute(ip = H.ip_address)
        H.doPost(dlvdetailroute, dlvdetailparams, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.ddinfos = Gson().fromJson(jAry.toString(), Array<DDInfo>::class.java).toList()
            }

            override fun error(err: String) {
                toast(err)
            }
        })


        //dlv_complete_install
        val params = RequestParams()
        params.put("nm_sp", "SP_MBL_DLV_SET_S")
        params.put("param", "${list.CD_FIRM}|PRM066 | |")
        params.put("param", "${list.CD_FIRM}|${list.NO_SO}|${list.NO_LINE}|${list.NO_LINE_DLV}")
        val route = H.getRoute(ip = H.ip_address)
        H.doPost(route, params, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.L(result)
                H.dcInstalls = Gson().fromJson(jAry.toString(), Array<DeliveryCompleteModel>::class.java).toList()
                val currentDCI = H.dcInstalls!![0]

                et_MI_NM_ITEM.setText(currentDCI.NM_ITEM)
                et_D_NO_SN.setText(currentDCI.NO_SN)

                et_D_AM_EQUIT.setText(String.format("%,d", currentDCI.AM_EQUIP.toDouble().toInt()))
                et_D_AM_DLV.setText(String.format("%,d", currentDCI.AM_DLV.toDouble().toInt()))
                et_AM_DLV_ADD.setText(String.format("%,d", currentDCI.AM_DLV_ADD.toDouble().toInt()))
                et_D_AM_TOTAL.setText(String.format("%,d", currentDCI.AM_TOTAL.toDouble().toInt()))
                et_D_NM_CONFIRM.setText(currentDCI.NM_CONFIRM)
                ed_dc_rmk.setText(currentDCI.DC_RMK)
            }

            override fun error(err: String) {
                toast(err)
            }
        })

        et_D_AM_DLV.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                et_D_AM_DLV.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (et_D_AM_DLV.text.isEmpty()) {
                            et_D_AM_DLV.setText("0")
                        }
                        var am_dlv = (et_D_AM_DLV.text.toString()).replace(",", "").toInt()
                        var am_equit = (et_D_AM_EQUIT.text.toString()).replace(",", "").toInt()
                        var am_dlv_add = (et_AM_DLV_ADD.text.toString()).replace(",", "").toInt()
                        var t_cost = (am_dlv + am_equit+am_dlv_add)
                        et_D_AM_TOTAL.setText(String.format("%,d", t_cost))
                    }
                })
            } else {
                et_D_AM_DLV.setText(
                    String.format(
                        "%,d",
                        et_D_AM_DLV.text.toString().replace(",", "").toDouble().toInt()
                    )
                )
            }
        }

        et_D_AM_EQUIT.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                et_D_AM_EQUIT.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (et_D_AM_EQUIT.text.isEmpty()) {
                            et_D_AM_EQUIT.setText("0")
                        }
                        var am_equit = (et_D_AM_EQUIT.text.toString()).replace(",", "").toInt()
                        var am_dlv = (et_D_AM_DLV.text.toString()).replace(",", "").toInt()
                        var am_dlv_add = (et_AM_DLV_ADD.text.toString()).replace(",", "").toInt()
                        var t_cost = (am_dlv + am_equit+am_dlv_add)
                        et_D_AM_TOTAL.setText(String.format("%,d", t_cost))
                    }
                })
            } else {
                et_D_AM_EQUIT.setText(
                    String.format(
                        "%,d",
                        et_D_AM_EQUIT.text.toString().replace(",", "").toDouble().toInt()
                    )
                )
            }
        }

        et_AM_DLV_ADD.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                et_AM_DLV_ADD.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (et_AM_DLV_ADD.text.isEmpty()) {
                            et_AM_DLV_ADD.setText("0")
                        }
                        var am_equit = (et_D_AM_EQUIT.text.toString()).replace(",", "").toInt()
                        var am_dlv = (et_D_AM_DLV.text.toString()).replace(",", "").toInt()
                        var am_dlv_add = (et_AM_DLV_ADD.text.toString()).replace(",", "").toInt()
                        var t_cost = (am_dlv + am_equit+am_dlv_add)
                        et_D_AM_TOTAL.setText(String.format("%,d", t_cost))
                    }
                })
            } else {
                et_AM_DLV_ADD.setText(
                    String.format(
                        "%,d",
                        et_AM_DLV_ADD.text.toString().replace(",", "").toDouble().toInt()
                    )
                )
            }
        }
        initSpns()

        dt_dlv_tv.setOnClickListener {
            clickDataPicker(it)
        }

        dt_dlv_btn_left.setOnClickListener {
            H.cal.add(Calendar.DATE, -1)
            dt_dlv_tv.text = H.getDate(this@DeliveryCompleteInstall)
        }

        dt_dlv_btn_right.setOnClickListener {
            H.cal.add(Calendar.DATE, 1)
            dt_dlv_tv.text = H.getDate(this@DeliveryCompleteInstall)
        }

        btn_f_to_delivery_detail.setOnClickListener {
            startActivity(Intent(this@DeliveryCompleteInstall, DeliveryDetail::class.java))
        }
        btn_to_installation_confirmation.setOnClickListener {
            startActivity(Intent(this@DeliveryCompleteInstall, InstallationConfirmation::class.java))
        }

        btn_save_data.setOnClickListener {

            if (image_data_flag.text == "Y") {
                val CD_FIRM = H.ddinfos!![0].CD_FIRM
                val NO_SO = H.ddinfos!![0].NO_SO
                val NO_LINE = H.ddinfos!![0].NO_LINE
                val NO_LINE_DLV = H.ddinfos!![0].NO_LINE_DLV

                val NO_SN = et_D_NO_SN.text
                //val NO_SN = H.ddinfos!![0].NO_SN

                val DC_RMK = ed_dc_rmk.text
                val FG_MONEY = category_list[spinner_cattegory.selectedItemPosition]
                val TP_PAY = paymethod_list[spinner_tp_pay.selectedItemPosition]

                val AM_DLV = (et_D_AM_DLV.text.toString()).replace(",", "")
                val AM_EQUIP = (et_D_AM_EQUIT.text.toString()).replace(",", "")
                val AM_DLV_ADD = (et_AM_DLV_ADD.text.toString()).replace(",", "")

                val FG_STAIR = fgstair_list[sp_fg_stair.selectedItemPosition]
                val NM_CONFIRM = et_D_NM_CONFIRM.text
                val FG_RELATION = spRelation_list[spinner_relations.selectedItemPosition]
                val DT_DLV = dt_dlv_tv.text.toString().replace("/", "")

                val upload_file_name = H.joinString(arrayOf(NO_SO, NO_LINE, NO_LINE_DLV)).replace("|", "_")
                val current_milisecond = System.currentTimeMillis()

                val DC_PHOTO = createUpFileName(upload_file_name, "P", current_milisecond)
                val DC_CONFIRM_SIGN = createUpFileName(upload_file_name, "S", current_milisecond)
                val DC_NOTDLV = et_DC_NOTDLV.text
                val CD_USER = H.currentUser!!.CD_USER

                val params = RequestParams()
                params.put("nm_sp", "SP_MBL_DLV_SET_U")

                params.put(
                    "param", "$CD_FIRM|$NO_SO|$NO_LINE|$NO_LINE_DLV|$NO_SN|" +
                            "$DC_RMK|$FG_MONEY|$TP_PAY|$AM_DLV|$AM_EQUIP|" +
                            "$AM_DLV_ADD|$FG_STAIR|$NM_CONFIRM|$FG_RELATION|$DT_DLV|" +
                            "$DC_PHOTO|$DC_CONFIRM_SIGN|$CD_NOTDLV|$DC_NOTDLV|$CD_USER||"
                )
                val route = H.getRoute(ip = H.ip_address)
                H.doPost(route, params, object : WaitInter {
                    override fun responseSuccess(result: String) {
                        toast("Saved Successfully")
                        H.L(result)
                        H.uploadImage(this@DeliveryCompleteInstall,CD_FIRM,image_path, upload_file_name, DC_PHOTO,"PRM")
                        H.uploadImage(this@DeliveryCompleteInstall,CD_FIRM,signature_path, upload_file_name, DC_CONFIRM_SIGN,"PRM")
                        startActivity(Intent(this@DeliveryCompleteInstall,DeliveryList::class.java))
                    }

                    override fun error(err: String) {
                        toast("Fail to Save")
                        H.L(err)
                    }
                })
            } else if (image_data_flag.text == "N") {
                toast(getString(R.string.photo_register_message))
            }
        }

        imgV.setOnSignedListener(this)

        signaturesave.setOnClickListener {
            val bitmap = imgV.signatureBitmap
            val added = H.addBitmapToGallery(bitmap)
            if (added.equals("n")) {
                toast("Fail")
            } else {
                toast("Success")
                signature_path = H.path
                it.isEnabled = false
                imgV.isEnabled = false
                btn_save_data.isEnabled = true
            }

        }

        btnCls.setOnClickListener {
            imgV.clear()
            imgV.isEnabled = true
            signature_path = ""
        }

        btn_to_take_camera.setOnClickListener {
            if (checkCameraPermission()) {
                    TakePictureIntent(REQUEST_IMAGE_CAPTURE)
            } else {
                getCameraPermission()
            }
        }

        btngallery.setOnClickListener {
            if (checkPermission()) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
            } else {
                getStoragePermission()
            }
        }

    }

    private fun initSpns() {
        //Category Spinner
        val cateparams = RequestParams()
        cateparams.put("nm_sp", getString(R.string.shipping_mbl_mas_codel_s_01))
        cateparams.put("param", H.joinString(arrayOf(H.currentUser!!.CD_FIRM, "CRM007", " | |")))
        val cateroute = H.getRoute(ip = H.ip_address)
        H.doPost(cateroute, cateparams, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.categoryList = Gson().fromJson(jAry.toString(), Array<SpinnerChildModel>::class.java).toList()

                var list: MutableList<String> = mutableListOf()
                H.categoryList?.forEach {
                    list.add(it.NM_FLAG)
                    category_list.add(it.CD_FLAG)
                }
                spinner_cattegory.adapter = ArrayAdapter(this@DeliveryCompleteInstall, R.layout.spinner_item, list)
            }

            override fun error(err: String) {
                toast(err)
            }

        })

        //Payment Method Spinner
        val paymethod = RequestParams()
        paymethod.put("nm_sp", getString(R.string.shipping_mbl_mas_codel_s_01))
        paymethod.put("param", H.joinString(arrayOf(H.currentUser!!.CD_FIRM, "CRM010", " | |")))
        val paymethodroute = H.getRoute(ip = H.ip_address)
        H.doPost(paymethodroute, paymethod, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.paymethodList = Gson().fromJson(jAry.toString(), Array<SpinnerChildModel>::class.java).toList()

                var paymethodlist: MutableList<String> = mutableListOf()
                H.paymethodList?.forEach {
                    paymethodlist.add(it.NM_FLAG)
                    paymethod_list.add(it.CD_FLAG)
                }
                spinner_tp_pay.adapter =
                    ArrayAdapter(this@DeliveryCompleteInstall, R.layout.spinner_item, paymethodlist)
            }

            override fun error(err: String) {
                toast(err)
            }

        })

        //Unpaid Reason Spinner Child
        val unpaidReason = RequestParams()
        unpaidReason.put("nm_sp", getString(R.string.shipping_mbl_mas_codel_s_01))
        unpaidReason.put("param", H.joinString(arrayOf(H.currentUser!!.CD_FIRM, "PRM026", " | |")))
        val unpaidReasonroute = H.getRoute(ip = H.ip_address)
        H.doPost(unpaidReasonroute, unpaidReason, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.unpaidReasonList = Gson().fromJson(jAry.toString(), Array<SpinnerChildModel>::class.java).toList()

                var unpaidrsonList: MutableList<String> = mutableListOf("")
                H.unpaidReasonList?.forEach {
                    unpaidrsonList.add(it.NM_FLAG)
                    unpaid_reason_list.add(it.CD_FLAG)
                }
                spinner_unpaid_reason.setItems(unpaidrsonList)
                CD_NOTDLV = unpaid_reason_list[0]
                spinner_unpaid_reason.setOnItemSelectedListener { view, position, id, item ->
                    CD_NOTDLV = unpaid_reason_list[position]
                    if (position != 0) {
                        et_DC_NOTDLV.setBackgroundResource(R.color.white)
                        et_DC_NOTDLV.isEnabled = true
                    } else {
                        et_DC_NOTDLV.setBackgroundResource(R.color.color_transparent)
                        et_DC_NOTDLV.setText("")
                        et_DC_NOTDLV.isEnabled = false
                    }
                }
            }

            override fun error(err: String) {
                toast(err)
            }

        })

        //FG_Stair Spinner Child
        val fgStair = RequestParams()
        fgStair.put("nm_sp", getString(R.string.shipping_mbl_mas_codel_s_01))
        fgStair.put("param", H.joinString(arrayOf(H.currentUser!!.CD_FIRM, "PRM061", " | |")))
        val fgStairroute = H.getRoute(ip = H.ip_address)
        H.doPost(fgStairroute, fgStair, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.fgStairList = Gson().fromJson(jAry.toString(), Array<SpinnerChildModel>::class.java).toList()

                var fg_stair_list: MutableList<String> = mutableListOf()
                H.fgStairList?.forEach {
                    fg_stair_list.add(it.NM_FLAG)
                    fgstair_list.add(it.CD_FLAG)
                }
                sp_fg_stair.adapter = ArrayAdapter(this@DeliveryCompleteInstall, R.layout.spinner_item, fg_stair_list)
            }

            override fun error(err: String) {
                toast(err)
            }

        })

        //Relation Spinner Child
        val spRelation = RequestParams()
        spRelation.put("nm_sp", getString(R.string.shipping_mbl_mas_codel_s_01))
        spRelation.put("param", H.joinString(arrayOf(H.currentUser!!.CD_FIRM, "PRM070", " | |")))
        val spRelationroute = H.getRoute(ip = H.ip_address)
        H.doPost(spRelationroute, spRelation, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.spRelationList = Gson().fromJson(jAry.toString(), Array<SpinnerChildModel>::class.java).toList()

                var sp_relation_list: MutableList<String> = mutableListOf()
                H.spRelationList?.forEach {
                    sp_relation_list.add(it.NM_FLAG)
                    spRelation_list.add(it.CD_FLAG)
                }
                spinner_relations.adapter =
                    ArrayAdapter(this@DeliveryCompleteInstall, R.layout.spinner_item, sp_relation_list)
            }

            override fun error(err: String) {
                toast(err)
            }

        })
    }

    private fun clickDataPicker(it: View?) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            H.updateView(this@DeliveryCompleteInstall, year, monthOfYear, dayOfMonth)
            dt_dlv_tv.text = H.getDate(this@DeliveryCompleteInstall)
        }, year, month, day)
        dpd.show()
    }

    private fun getCameraPermission() {
        ActivityCompat.requestPermissions(
            this@DeliveryCompleteInstall,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_IMAGE_CAPTURE
        )
    }

    private fun getImageStorageDir(): File {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Smart-Note")
        if (!file.mkdirs()) {
//            toast("SignaturePad Directory not created")
        }
        return file
    }

    fun backToPage4(v: View) {
        finish()
    }

    fun getStoragePermission() {
        H.L("Permission is revoked")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            H.L("Permission: $permissions[0]was$grantResults[0]")
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imgPath = H.getRealPathFromURI(this@DeliveryCompleteInstall, data!!.getData())
            val imageUri: Uri = data.data
            val imageStream: InputStream = contentResolver.openInputStream(imageUri)
//            val bmap: Bitmap = BitmapFactory.decodeStream(imageStream)
//            imgV.setImageBitmap(bmap)
                image_path = imgPath
            image_data_flag.setText("Y")
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            var file = File(currentPhotoPath)
            var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.fromFile(file))
            val ei = ExifInterface(currentPhotoPath)
            val bitmp = rotateIfRequired(ei, bitmap)
            if (file != null) {
                H.addBitmapToGallery(bitmp)
                image_path = H.path
                file.delete()
                image_data_flag.setText("Y")
                toast("Success")
            } else {
                toast("Fail")
            }
        }
    }

    // 이미지 URI절대경로 가져오기

    private fun createUpFileName(filename: String, suffix: String, miliSecond: Long): String =
        String.format("${filename}_${suffix}_$miliSecond.png")


    private fun checkPermission(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(this@DeliveryCompleteInstall, Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this@DeliveryCompleteInstall, Manifest.permission.CAMERA)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun TakePictureIntent(REQUEST_TAKE_PHOTO: Int) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    toast("Error occurred while creating the File")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.mydomain.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".png", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    fun rotateIfRequired(rtinter:ExifInterface,bm:Bitmap):Bitmap{
        val orientation = rtinter.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        var rotatedBitmap: Bitmap?= null
        when(orientation){
            ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(bm, 90.toFloat())
            ExifInterface.ORIENTATION_ROTATE_180-> rotatedBitmap = rotateImage(bm, 180.toFloat())
            ExifInterface.ORIENTATION_ROTATE_270-> rotatedBitmap = rotateImage(bm, 270.toFloat())
            ExifInterface.ORIENTATION_NORMAL->rotatedBitmap=bm
        }
        return rotatedBitmap!!
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }


}
