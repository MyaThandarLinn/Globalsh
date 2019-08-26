package g2sysnet.smart_gw

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.gson.Gson
import com.jaredrummler.materialspinner.MaterialSpinner
import com.loopj.android.http.RequestParams
import com.squareup.picasso.Picasso
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.libby.H
import g2sysnet.smart_gw.models.*
import kotlinx.android.synthetic.main.activity_action_complete_processing.*
import org.jetbrains.anko.toast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ASComplete : AppCompatActivity() {

    var CD_FIRM = ""

    private val REQUEST_BF_IMAGE_CAPTURE = 102
    private val REQUEST_BF_IMAGE_GALLERY = 103

    private val REQUEST_AF_IMAGE_CAPTURE = 104
    private val REQUEST_AF_IMAGE_GALLERY = 105

    var cd_symptom_list: MutableList<String> = mutableListOf()
    var cd_detail_list: MutableList<String> = mutableListOf()
    var cd_repair_code: MutableList<String> = mutableListOf()
    var yn_list: MutableList<String> = mutableListOf()
    var as_com_paymethod_list: MutableList<String> = mutableListOf()

    var img_bf_path = ""
    var img_af_path = ""
    var signature_path = ""

    var CD_SYMPTOM = ""
    var CD_SYMPTOM_DETAIL=""
    var CD_REPAIR = ""
    var YN_AM = ""
    var FG_COL = ""


    var SAVED = "N"
    var currentPhotoPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_complete_processing)

        setSupportActionBar(title_toolbar)
        val actionBar = supportActionBar
        // Set toolbar title/app title
        actionBar!!.title = getString(R.string.as_action_complete)
        // Set action bar elevation
        actionBar.elevation = 4.0F
        // Display the app icon in action bar/toolbar
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setLogo(R.mipmap.ic_left_logo)
        actionBar.setDisplayUseLogoEnabled(true)

        CD_FIRM = H.currentUser!!.CD_FIRM
        val asinfo = H.asAllocationList!![H.curRow]

        //As Complete Request Data
        val asComparams = RequestParams()
        asComparams.put("nm_sp", getString(R.string.as_complete_sp_mbl_crm_sol_01))
        asComparams.put("param", "$CD_FIRM|${asinfo.NO_SO}|${asinfo.NO_LINE_ITEM}|${asinfo.CD_ITEM}|${asinfo.NO_LINE}")
        val route = H.getRoute(ip = H.ip_address)
        H.doPost(route, asComparams, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.L(result)
                H.asComplete = Gson().fromJson(jAry.toString(), Array<ASCompleteModel>::class.java).toList()
                //request RML Data
                val currentAsComplete = H.asComplete!![0]
                as_com_nm_person.text = currentAsComplete.NM_PERSON

                if(asinfo.ST_PROC.equals("C")){
                    imgV.visibility=View.GONE
                    sign_img.visibility=View.VISIBLE
                    bf_flag.setText("Y")
                    af_flag.setText("Y")
                    Picasso.get().load(currentAsComplete.URL_DC_FILE1).into(sign_img)
                    btn_as_com_save.isEnabled=false
                }else{
                    imgV.visibility=View.VISIBLE
                    sign_img.visibility=View.GONE
                    bf_flag.setText("N")
                    af_flag.setText("N")
                    btn_as_com_save.isEnabled=true
                }

                CD_SYMPTOM = currentAsComplete.CD_SYMPTOM_P
                CD_SYMPTOM_DETAIL=currentAsComplete.CD_SYMPTOM
                CD_REPAIR = currentAsComplete.CD_REPAIR
                YN_AM = currentAsComplete.YN_AM
                FG_COL = currentAsComplete.FG_COL

                inintSpinners()


                if (H.as_com_current_nm_item.equals("") && H.as_com_current_cd_item.equals("")) {

                    H.as_com_current_nm_item = currentAsComplete.NM_ITEM
                    H.as_com_current_cd_item = currentAsComplete.CD_ITEM

                    as_com_nm_item.setText(currentAsComplete.NM_ITEM)
                    as_com_sn.setText(currentAsComplete.NO_SN)
                } else {
                    as_com_nm_item.setText(H.as_com_current_nm_item)
                }

                if (H.totalCost == 0) {
                    as_com_parts_cost.setText(String.format("%,d", currentAsComplete.AM.toDouble().toInt()))
                } else {
                    as_com_parts_cost.setText(String.format("%,d", H.totalCost.toDouble().toInt()))
                }

                requestRMLData()
                as_com_action.setText(currentAsComplete.DC_DUMMY01)
                as_com_labor.setText(String.format("%,d", currentAsComplete.AM_LABOR.toDouble().toInt()))
                as_com_travel_expense.setText(String.format("%,d", currentAsComplete.AM_VISIT.toDouble().toInt()))

                val partcost = as_com_parts_cost.text.toString().replace(",", "").toInt()
                val total =
                    partcost + currentAsComplete.AM_LABOR.toDouble().toInt() + currentAsComplete.AM_VISIT.toDouble().toInt()
                total_cost.setText(String.format("%,d", total))

                as_com_labor.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        as_com_labor.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                            }

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                if (as_com_labor.text.isEmpty()) {
                                    as_com_labor.setText("0")
                                }
                                var labor_cost = (as_com_labor.text.toString()).replace(",", "").toInt()
                                var travl_cost = (as_com_travel_expense.text.toString()).replace(",", "").toInt()
                                var t_cost = (partcost + labor_cost + travl_cost)
                                total_cost.setText(String.format("%,d", t_cost))
                            }
                        })
                    } else {
                        as_com_labor.setText(
                            String.format(
                                "%,d",
                                as_com_labor.text.toString().replace(",", "").toDouble().toInt()
                            )
                        )
                    }
                }

                as_com_travel_expense.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        as_com_travel_expense.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {

                            }

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                if (as_com_travel_expense.text.isEmpty()) {
                                    as_com_travel_expense.setText("0")
                                }

                                var expense_cost = (as_com_travel_expense.text.toString()).replace(",", "").toInt()
                                var labor_cost = (as_com_labor.text.toString()).replace(",", "").toInt()
                                var t_cost = (partcost + labor_cost + expense_cost)
                                total_cost.setText(String.format("%,d", t_cost))
                            }
                        })
                        as_com_travel_expense.setOnKeyListener(onSoftKeyboardDonePress)
                    } else {
                        as_com_travel_expense.setText(
                            String.format(
                                "%,d",
                                as_com_travel_expense.text.toString().replace(",", "").toDouble().toInt()
                            )
                        )
                    }
                }
            }

            override fun error(err: String) {
                toast(err)
            }
        })

        //Before Img Capture
        btn_capture_bf_img.setOnClickListener {
            if (checkCameraPermission()) {
                TakePictureIntent(REQUEST_BF_IMAGE_CAPTURE)
            } else {
                getCameraPermission(REQUEST_BF_IMAGE_CAPTURE)
            }
        }

        //Before Img Gallery
        btn_bf_gallery_img.setOnClickListener {
            if (checkPermission()) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_BF_IMAGE_GALLERY)
            } else {
                getStoragePermission(REQUEST_BF_IMAGE_GALLERY)
            }
        }

        //After  Img Capture
        btn_capture_af_img.setOnClickListener {
            if (checkCameraPermission()) {
                TakePictureIntent(REQUEST_AF_IMAGE_CAPTURE)
            } else {
                getCameraPermission(REQUEST_AF_IMAGE_CAPTURE)
            }
        }

        //After Img Gallery
        btn_af_gallery_img.setOnClickListener {
            if (checkPermission()) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_AF_IMAGE_GALLERY)
            } else {
                getStoragePermission(REQUEST_AF_IMAGE_GALLERY)
            }
        }

        //OnSigned Listener
        imgV.setOnSignedListener(object : SignaturePad.OnSignedListener {

            override fun onStartSigning() {
                //Event triggered when the pad is touched
            }

            override fun onSigned() {
                signaturesave.isEnabled = true
                btnCls.isEnabled = true
            }

            override fun onClear() {
                signaturesave.isEnabled = false
                btnCls.isEnabled = false
            }
        })

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
                btn_as_com_save.isEnabled = true
            }
        }

        btnCls.setOnClickListener {
            imgV.clear()
            signature_path = ""
            imgV.isEnabled = true
        }

        btn_to_required_material.setOnClickListener {
            val intent = Intent(this@ASComplete, RequiredMaterialList::class.java)
            startActivity(intent)
        }

        btn_item_help.setOnClickListener {
            val intent = Intent(this@ASComplete, ItemHelpPopup::class.java)
            startActivity(intent)
        }

        btn_as_com_save.setOnClickListener {
            SAVED = "Y"

            if (bf_flag.text == "Y" && af_flag.text == "Y") {
                val NO_SO = H.asComplete!![0].NO_SO
                val NO_LINE = H.asComplete!![0].NO_LINE
                val NO_SN = as_com_sn.text
                val DC_DUMMY01 = as_com_action.text
                val AM_LABOR = (as_com_labor.text.toString()).replace(",", "")
                val AM_VISIT = (as_com_travel_expense.text.toString()).replace(",", "")
                val upload_file_name = NO_SO
                val current_milisecond = System.currentTimeMillis()

                val DC_DUMMY02 = H.createUpFileName(this@ASComplete, upload_file_name, "BF_P", current_milisecond)
                val DC_DUMMY03 = H.createUpFileName(this@ASComplete, upload_file_name, "AF_P", current_milisecond)
                val DC_FILE1 = H.createUpFileName(this@ASComplete, upload_file_name, "S", current_milisecond)

                val ST_PROC="C"

                H.updateCost(H.requiredMaterialList!!)

                val asComUpparams = RequestParams()
                asComUpparams.put("nm_sp", "SP_MBL_CRM_SOL_U")
                asComUpparams.put(
                    "param",
                    "$CD_FIRM|$NO_SO|$NO_LINE|${H.as_com_current_cd_item}|" +
                            "$NO_SN|$CD_SYMPTOM|$CD_REPAIR|$DC_DUMMY01|" +
                            "$YN_AM|$AM_LABOR|$AM_VISIT|$FG_COL|$DC_DUMMY02|" +
                            "$DC_DUMMY03|$DC_FILE1|$ST_PROC"
                )

                H.L(asComUpparams.toString())

                val asComroute = H.getRoute(ip = H.ip_address)

                H.doPost(asComroute, asComUpparams, object : WaitInter {
                    override fun responseSuccess(result: String) {
                        toast("Saved Successfully!")
                        H.L(result)
                        H.uploadImage(this@ASComplete, CD_FIRM, img_bf_path, upload_file_name, DC_DUMMY02, "CRM")
                        H.uploadImage(this@ASComplete, CD_FIRM, img_af_path, upload_file_name, DC_DUMMY03, "CRM")
                        H.uploadImage(this@ASComplete, CD_FIRM, signature_path, upload_file_name, DC_FILE1, "CRM")

                        H.as_com_current_cd_item = ""
                        H.as_com_current_nm_item = ""
                        H.totalCost = 0

                        finish()
                    }

                    override fun error(err: String) {
                        toast(err)
                        H.L(err)
                    }
                })
            } else if (bf_flag.text == "N" && af_flag.text == "N") {
                toast(getString(R.string.photo_register_message))
            }

        }

        btn_as_com_cancel.setOnClickListener {
            if(asinfo.ST_PROC.equals("C")){
                val NO_SO = H.asComplete!![0].NO_SO
                val NO_LINE = H.asComplete!![0].NO_LINE
                val CD_ITEM = H.asComplete!![0].CD_ITEM
                val NO_SN = H.asComplete!![0].NO_SN
                val CD_SYMPTOM_OLD= H.asComplete!![0].CD_SYMPTOM
                val CD_REPAIR_OLD= H.asComplete!![0].CD_REPAIR
                val DC_DUMMY01 = H.asComplete!![0].DC_DUMMY01
                val YN_AM_OLD = H.asComplete!![0].YN_AM
                val AM_LABOR = H.asComplete!![0].AM_LABOR
                val AM_VISIT = H.asComplete!![0].AM_VISIT
                val FG_COL_OLD = H.asComplete!![0].FG_COL

                val DC_DUMMY02 = H.asComplete!![0].DC_DUMMY02
                val DC_DUMMY03 = H.asComplete!![0].DC_DUMMY03
                val DC_FILE1 = H.asComplete!![0].DC_FILE1

                val ST_PROC="S"

                val asComUpparams = RequestParams()
                asComUpparams.put("nm_sp", "SP_MBL_CRM_SOL_U")
                asComUpparams.put(
                    "param",
                    "$CD_FIRM|$NO_SO|$NO_LINE|$CD_ITEM|" +
                            "$NO_SN|$CD_SYMPTOM_OLD|$CD_REPAIR_OLD|$DC_DUMMY01|" +
                            "$YN_AM_OLD|$AM_LABOR|$AM_VISIT|$FG_COL_OLD|$DC_DUMMY02|" +
                            "$DC_DUMMY03|$DC_FILE1|$ST_PROC"
                )

                val asComroute = H.getRoute(ip = H.ip_address)

                H.doPost(asComroute, asComUpparams, object : WaitInter {
                    override fun responseSuccess(result: String) {
                        toast("Saved Successfully!")
                        H.L(result)
                    }

                    override fun error(err: String) {
                        toast(err)
                        H.L(err)
                    }
                })
                H.as_com_current_cd_item = ""
                H.as_com_current_nm_item = ""
                H.totalCost = 0
                finish()
            }else{
                H.as_com_current_cd_item = ""
                H.as_com_current_nm_item = ""
                H.totalCost = 0
                finish()
            }
        }

    }

    private val onSoftKeyboardDonePress = object : View.OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            if (event.getKeyCode() === KeyEvent.KEYCODE_ENTER) {
                as_com_travel_expense.setText(
                    String.format(
                        "%,d",
                        as_com_travel_expense.text.toString().replace(",", "").toDouble().toInt()
                    )
                )
            }
            return false
        }
    }

    // Required  material List Data Request
    private fun requestRMLData() {
        val ascominfo = H.asComplete!![0]
        val reqMaterialsparam = RequestParams()
        reqMaterialsparam.put("nm_sp", "SP_MBL_CRM_SOL_MATL_S")
        reqMaterialsparam.put(
            "param",
            "$CD_FIRM|${ascominfo.NO_SO}|${ascominfo.NO_LINE_SO}|${H.as_com_current_cd_item}"
        )

        val reqMaterialRoute = H.getRoute(ip = H.ip_address)
        H.doPost(reqMaterialRoute, reqMaterialsparam, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.requiredMaterialList =
                    Gson().fromJson(jAry.toString(), Array<RequiredMaterialModel>::class.java).toList()

                if (H.restoreOldCost == null) {
                    H.restoreOldCost = H.requiredMaterialList
                }
            }

            override fun error(err: String) {
                toast(err)
            }
        })
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

    //AsComplete Detail Code Spinner
    private fun setUpDetailCodeSpinner(CD_SYMPTOM_P: String,S_DETAIL_SYMPTOM:String) {

        val asComCDDetailParam = RequestParams()
        asComCDDetailParam.put("nm_sp", getString(R.string.symptom_code_list))
        //asComCDDetailParam.put("param", H.joinString(arrayOf(H.currentUser!!.CD_FIRM, "$CD_SYMPTOM_P", "2","")))
        asComCDDetailParam.put("param",H.joinString(arrayOf(H.currentUser!!.CD_FIRM,"$CD_SYMPTOM_P","2","")))
        //asComCDDetailParam.put("param","${H.currentUser!!.CD_FIRM}|$CD_SYMPTOM_P|2|")

        val asComCDDetailRoute = H.getRoute(ip = H.ip_address)
        H.doPost(asComCDDetailRoute, asComCDDetailParam, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.asComDetailCode = Gson().fromJson(jAry.toString(), Array<SymptomCodeModel>::class.java).toList()

                var nm_cd_detail: MutableList<String> = mutableListOf()
                cd_detail_list.clear()
                H.asComDetailCode?.forEach {
                    nm_cd_detail.add(it.NM_SYMPTOM)
                    cd_detail_list.add(it.CD_SYMPTOM)
                }

                spn_ascom_detail_code.setItems(nm_cd_detail)
                spn_ascom_detail_code.selectedIndex=cd_detail_list.indexOf(S_DETAIL_SYMPTOM)
                CD_SYMPTOM = cd_detail_list[spn_ascom_detail_code.selectedIndex]

                spn_ascom_detail_code.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
                    CD_SYMPTOM = cd_detail_list[position]
                })
            }

            override fun error(err: String) {
                toast(err)
            }
        })
    }

    //Initialize Spinners
    private fun inintSpinners() {
        //AsComplete Symptom Code Spinner
        val asComSymptomParam = RequestParams()
        asComSymptomParam.put("nm_sp", getString(R.string.symptom_code_list))
        asComSymptomParam.put("param", H.joinString(arrayOf(H.currentUser!!.CD_FIRM, "", "1", "")))
        val asComSymptomRoute = H.getRoute(ip = H.ip_address)
        H.doPost(asComSymptomRoute, asComSymptomParam, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.asComSymptomCode = Gson().fromJson(jAry.toString(), Array<SymptomCodeModel>::class.java).toList()

                var nm_symptom_list: MutableList<String> = mutableListOf()
                cd_symptom_list.clear()
                H.asComSymptomCode?.forEach {
                    nm_symptom_list.add(it.NM_SYMPTOM)
                    cd_symptom_list.add(it.CD_SYMPTOM)
                }

                spn_ascom_cd_symptom.setItems(nm_symptom_list)
                spn_ascom_cd_symptom.selectedIndex=cd_symptom_list.indexOf(CD_SYMPTOM)
                setUpDetailCodeSpinner(cd_symptom_list[spn_ascom_cd_symptom.selectedIndex],CD_SYMPTOM_DETAIL)
                H.L(cd_symptom_list.toString())
                spn_ascom_cd_symptom.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
                    reqCDdetail(cd_symptom_list[position])
                })
            }

            override fun error(err: String) {
                toast(err)
            }

        })

        //As Repair Code Spinner
        val asComCDRepairParams = RequestParams()
        asComCDRepairParams.put("nm_sp", getString(R.string.repair_code_list))
        asComCDRepairParams.put("param", H.joinString(arrayOf(H.currentUser!!.CD_FIRM, "", "", "")))
        val asComCDRepairroute = H.getRoute(ip = H.ip_address)
        H.doPost(asComCDRepairroute, asComCDRepairParams, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.L(result)
                H.asComRepairCode = Gson().fromJson(jAry.toString(), Array<RepairCodeModel>::class.java).toList()
                var nm_repair_code: MutableList<String> = mutableListOf()
                cd_repair_code.clear()
                H.asComRepairCode?.forEach {
                    nm_repair_code.add(it.NM_REPAIR)
                    cd_repair_code.add(it.CD_REPAIR)
                }

                spn_ascom_cd_repair.setItems(nm_repair_code)
                spn_ascom_cd_repair.selectedIndex=cd_repair_code.indexOf(CD_REPAIR)
                CD_REPAIR=cd_repair_code[spn_ascom_cd_repair.selectedIndex]
                H.L("cdrepair"+CD_REPAIR)
                spn_ascom_cd_repair.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
                    CD_REPAIR = cd_repair_code[position]
                })
            }

            override fun error(err: String) {
                toast(err)
            }
        })

        //YN Spinner Child
        val yn = RequestParams()
        yn.put("nm_sp", getString(R.string.shipping_mbl_mas_codel_s_01))
        yn.put("param", H.joinString(arrayOf(H.currentUser!!.CD_FIRM, "CRM007", " | |")))

        val ynroute = H.getRoute(ip = H.ip_address)
        H.doPost(ynroute, yn, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.ynList = Gson().fromJson(jAry.toString(), Array<SpinnerChildModel>::class.java).toList()

                var ynList: MutableList<String> = mutableListOf()
                H.ynList?.forEach {
                    ynList.add(it.NM_FLAG)
                    yn_list.add(it.CD_FLAG)
                }
                yn_spn.setItems(ynList)
                yn_spn.selectedIndex=yn_list.indexOf(YN_AM)
                YN_AM = yn_list[yn_spn.selectedIndex]
                yn_spn.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
                    YN_AM = yn_list[position]
                })
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

                var as_com_paymethodlist: MutableList<String> = mutableListOf()
                H.paymethodList?.forEach {
                    as_com_paymethodlist.add(it.NM_FLAG)
                    as_com_paymethod_list.add(it.CD_FLAG)
                }
                spn_as_com_paymethod.setItems(as_com_paymethodlist)
                spn_as_com_paymethod.selectedIndex=as_com_paymethod_list.indexOf(FG_COL)
                FG_COL = as_com_paymethod_list[spn_as_com_paymethod.selectedIndex]
                spn_as_com_paymethod.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
                    FG_COL = as_com_paymethod_list[position]
                })
            }

            override fun error(err: String) {
                toast(err)
            }
        })

    }

    //Request  Datail Code Data
    private fun reqCDdetail(cdSymptomP:String){
        val asComCDDetailParam = RequestParams()
        asComCDDetailParam.put("nm_sp", getString(R.string.symptom_code_list))
        asComCDDetailParam.put("param",H.joinString(arrayOf(H.currentUser!!.CD_FIRM,"$cdSymptomP","2","")))

        val asComCDDetailRoute = H.getRoute(ip = H.ip_address)
        H.doPost(asComCDDetailRoute, asComCDDetailParam, object : WaitInter {
            override fun responseSuccess(result: String) {
                val jAry = H.strToJArr(result)
                H.asComDetailCode = Gson().fromJson(jAry.toString(), Array<SymptomCodeModel>::class.java).toList()

                var nm_cd_detail: MutableList<String> = mutableListOf()
                cd_detail_list.clear()
                H.asComDetailCode?.forEach {
                    nm_cd_detail.add(it.NM_SYMPTOM)
                    cd_detail_list.add(it.CD_SYMPTOM)
                }
                setUpDetailCodeSpinner(cdSymptomP,cd_detail_list[0])
            }
            override fun error(err: String) {
                toast(err)
            }
        })
    }

    //Camera Permission
    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this@ASComplete, Manifest.permission.CAMERA)
        return result == PackageManager.PERMISSION_GRANTED
    }

    //Get Camera Permission
    private fun getCameraPermission(requestCode: Int) {
        ActivityCompat.requestPermissions(
            this@ASComplete,
            arrayOf(Manifest.permission.CAMERA), requestCode
        )
    }

    //Check Storage Permission
    private fun checkPermission(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(this@ASComplete, Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    //Get Strorage Permission
    fun getStoragePermission(reqCode: Int) {
        H.L("Permission is revoked")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            reqCode
        )
    }

    //OnActivityResult
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BF_IMAGE_GALLERY && resultCode == RESULT_OK) {
            img_bf_path = H.getRealPathFromURI(this@ASComplete, data!!.getData())
            bf_flag.setText("Y")
            toast("success")
        } else if (requestCode == REQUEST_AF_IMAGE_GALLERY && resultCode == RESULT_OK) {
            img_af_path = H.getRealPathFromURI(this@ASComplete, data!!.getData())
            af_flag.setText("Y")
            toast("success")
        } else if (requestCode == REQUEST_BF_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            var file = File(currentPhotoPath)
            var bitmap = getBitmap(this.contentResolver, Uri.fromFile(file))
            val ei = ExifInterface(currentPhotoPath)
            val bitmp = rotateIfRequired(ei, bitmap)

            if (file != null) {
                H.addBitmapToGallery(bitmp)
                img_bf_path = H.path
                bf_flag.setText("Y")
                file.delete()
                toast("success")
            } else {
                toast("Fail")
            }

        } else if (requestCode == REQUEST_AF_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            var file = File(currentPhotoPath)
            val bitmap = getBitmap(this.contentResolver, Uri.fromFile(file))
            val ei = ExifInterface(currentPhotoPath)
            val bitmp =rotateIfRequired(ei,bitmap)
            if (file != null) {
                H.addBitmapToGallery(bitmp)
                img_af_path =H.path
                af_flag.setText("Y")
                file.delete()
                toast("success")
            } else {
                toast("Fail")
            }
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

    override fun onBackPressed() {
        H.as_com_current_cd_item = ""
        H.as_com_current_nm_item = ""
        H.totalCost = 0
        finish()
    }
}
