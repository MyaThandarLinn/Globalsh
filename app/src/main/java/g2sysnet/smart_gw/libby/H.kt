package g2sysnet.smart_gw.libby

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import androidx.appcompat.app.ActionBar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import g2sysnet.smart_gw.BoardItemInfo
import g2sysnet.smart_gw.BoardItemInfoCount
import g2sysnet.smart_gw.BoardSub01Model
import g2sysnet.smart_gw.R
import g2sysnet.smart_gw.inters.WaitInter1
import g2sysnet.smart_gw.inters.WaitInter
import g2sysnet.smart_gw.m_model.EmpDepartmentModel
import g2sysnet.smart_gw.m_model.GetMessageUserModel
import g2sysnet.smart_gw.m_model.ItemInfo_m_orders
import g2sysnet.smart_gw.m_model.ReceiveItem_receive_user
import g2sysnet.smart_gw.model.MessageModel
import g2sysnet.smart_gw.models.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class H {
    companion object {

        var ip_address = ""
        var token = ""
        const val HOST_PREFIX = "http://"
        const val HOST_SUFFIX = "/mob_json/"
        const val FINAL_SUFFIX = ".aspx"

        var curRow = 0
        var curSelectedPosition = 0

        var currentUser: User? = null
        var currentDevice: Device? = null
        var deliveryVan: List<DeliveryVan>? = null
        var categoryList: List<SpinnerChildModel>? = null
        var paymethodList: List<SpinnerChildModel>? = null
        var ynList: List<SpinnerChildModel>? = null
        var unpaidReasonList: List<SpinnerChildModel>? = null
        var fgStairList: List<SpinnerChildModel>? = null
        var spRelationList: List<SpinnerChildModel>? = null
        var spAsProvince: List<SpinnerChildModel>? = null
        var spAsMuni: List<SpinnerChildModel>? = null
        var deliveryList: List<DeliveryListModel>? = null
        var asAllocationList: List<AllocationListModel>? = null
        var ddinfos: List<DDInfo>? = null
        var dcInstalls: List<DeliveryCompleteModel>? = null
        var dcDlv: List<DlvComDlvModel>? = null
        var installConfirm: List<InstallConfirmationModel>? = null
        var customer: List<CustomerModel>? = null
        var receptinInfo: List<ReceptionInfoModel>? = null
        var asComSymptomCode: List<SymptomCodeModel>? = null
        var asComDetailCode: List<SymptomCodeModel>? = null
        var asComRepairCode: List<RepairCodeModel>? = null
        var asComplete: List<ASCompleteModel>? = null
        var itemhelp_spinner_list: List<SpinnerChildModel>? = null
        var itemhelp: List<ItemHelpModel>? = null
        var requiredMaterialList: List<RequiredMaterialModel>? = null

        var QTData = mutableListOf<Int>()
        var restoreOldCost: List<RequiredMaterialModel>? = null
        var totalCost = 0

        //From Menu Page
        var QT_COUNt: List<QT_COUNT_MODEL>? = null

        //From Approval Page
        var APPROVAL_LIST: List<ApprovalModel>? = null
        var APPROVAL_SUB03_LIST: List<ApprovalSub03Model>? = null
        var APPROVAL_SUB02_LIST: List<ApprovalSub02Model>? = null
        var NO_OPN : String = ""
        var CD_EMP_W : String= ""

        //From Dept
        var organizationModel: List<OrganizationModel>? = null

        //From Message Activity
        var TP_RETURN = ""
        var FG_TAB = "TAB01"
        var FG_HEADER = ""
        var FG_SP = ""
        var messageModel: List<MessageModel>? = null
        var getMessageUserModel: List<GetMessageUserModel>? = null
        var deptNameList = ""
        var empCon = false
        var m_order_emp_message: List<EmpDepartmentModel>? = null
        var receive_user_message: ArrayList<ReceiveItem_receive_user>? = null
        var deviceTokenMd: List<ItemInfo_m_orders>? = null
        var UnreadMessageCount : Int = 0

        //From Board Acitvity
        var iteminfoCount: List<BoardItemInfoCount>? = null
        var boardItemInfo: List<BoardItemInfo>? = null
        var boardsub01Model: List<BoardSub01Model>? = null
        //var m_orders_count: List<BoardItemInfoCountModel>? = null

        var cal = Calendar.getInstance()
        var path = ""
        var as_com_current_nm_item = ""
        var as_com_current_cd_item = ""
        var m_order_emp = ""
        var receive_user = "    "
        var chooseUserModel:ItemInfo?=null

        fun progressDialog(context: Context): Dialog {
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
//            dialog.window!!.setBackgroundDrawable(
//                ColorDrawable(Color.TRANSPARENT)
//            )
            return dialog
        }

        fun L(logs: String) {
            Log.d("my_message", logs)
        }

        fun getPrefValue(context: Context, key: String): String {
            val mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return mySharedPreferences.getString(key, "error")
        }


        fun joinString(params: Array<String>): String {
            val sb = StringBuilder()
            var intty = 0
            for (param in params) {
                sb.append(param)
                if (intty < params.size - 1)
                    sb.append("|")
                intty++
            }
            return sb.toString()
        }


        fun SMPOST(activity: Activity, nmsp: String, param: String, page: String): String {
            val result = StringBuffer()
            var reqParam = URLEncoder.encode("nm_sp", "UTF-8") + "=" + URLEncoder.encode(nmsp, "UTF-8")
            reqParam += "&" + URLEncoder.encode("param", "UTF-8") + "=" + URLEncoder.encode(param, "UTF-8")
            ip_address = getPrefValue(activity, "IP")
            val mURL = URL(HOST_PREFIX + ip_address + HOST_SUFFIX + page + FINAL_SUFFIX)
            L(mURL.toString())


            with(mURL.openConnection() as HttpURLConnection) {
                requestMethod = "POST"

                val wr = OutputStreamWriter(outputStream)
                wr.write(reqParam)
                wr.flush()

                println("URL : $url")
                println("Response Code : $responseCode")

                BufferedReader(InputStreamReader(inputStream)).use {
                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        result.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                }
            }
            return result.toString()
        }

        // Construct Route
        fun getRoute(
            prefix: String = "http://",
            ip: String,
            suffix: String = "/mob_json/",
            page: String = "mob_json",
            endpart: String = ".aspx"
        ): String {
            return "$prefix$ip$suffix$page$endpart"
        }


        // Do Server Request
        fun doPost(route: String, params: RequestParams, inter: WaitInter) {
            val client = AsyncHttpClient()
            client.post(route, params, object : TextHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                    when (responseString) {
                        "" -> inter.error("Connection Error")
                        "[]" -> inter.error("Blank Data Return")
                        else -> inter.responseSuccess(responseString!!)
                    }
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseString: String?,
                    throwable: Throwable?
                ) {
                    inter.error(responseString.toString())
                }
            })
        }

        fun getToday(): String {
            val today = Date()
            val DT_NOW = SimpleDateFormat("yyyy-MM-dd")

            return DT_NOW.format(today)
        }

        fun hideDecorView(a: ActionBar?, w: Window) {
            a?.hide()
            w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            val currentApiVersion = Build.VERSION.SDK_INT

            val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

            if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

                w.decorView.systemUiVisibility = flags

                val decorView = w.decorView
                decorView
                    .setOnSystemUiVisibilityChangeListener { visibility ->
                        if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                            decorView.systemUiVisibility = flags
                        }
                    }
            }
        }


        //Google Play Service 환경 체크
        fun checkPlayServices(context: Activity): Boolean {
            val status = GoogleApiAvailability.getInstance()
            val resultCode = status.isGooglePlayServicesAvailable(context)
            if (resultCode != ConnectionResult.SUCCESS) {
                if (status.isUserResolvableError(resultCode)) {
                    status.getErrorDialog(context, resultCode, 100).show()
                }
                context.toast(context.getString(R.string.your_device_is_not_support))
                return false
            }
            return true
        }

        fun backJob(
            context: Activity,
            param: String,
            link: String,
            cb: WaitInter1,
            name: String = "first",
            page: String = "mob_json",
            jAryName: String = "Table"
        ) {
            doAsync {
                val result = SMPOST(context, link, param, page)
                uiThread {
                    L(result)
                    when (result) {
                        "" -> cb.error(context.getString(R.string.msg_connection_error))
                        "[]" -> cb.error("Blank Data Return")
                        else -> {
                            val json = JSONObject(result)
                            val jArr = json.getJSONArray(jAryName)
                            cb.callback(jArr, name)
                        }
                    }
                }
            }
        }

        fun strToJArr(strToConvert: String, jAryName: String = "Table"): JSONArray {
            val jObj = JSONObject(strToConvert)
            val jsonArray = jObj.getJSONArray(jAryName)
            return jsonArray
        }

        fun getDate(context: Activity): String {
            val myFormat = "yyyy/MM/dd" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val dt = sdf.format(cal.getTime())
            return dt
        }

        fun updateView(context: Activity, y: Int, m: Int, d: Int) {
            cal.set(Calendar.YEAR, y)
            cal.set(Calendar.MONTH, m)
            cal.set(Calendar.DAY_OF_MONTH, d)
        }

        //Save Signature to Local Storage
        fun addBitmapToGallery(bmap: Bitmap): String {
            var result = "n"

            try {
                val photo = File(getImageStorageDir(), String.format("Signature_%d.png", System.currentTimeMillis()))
                saveBitmapToJPG(bmap, photo)
                path = photo.absolutePath
                result = "y"
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return result
        }

        //Create Directory
        fun getImageStorageDir(): File {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Smart-Note")
            if (!file.mkdirs()) {
            }
            return file
        }

        //Compress Image
        fun saveBitmapToJPG(bitmap: Bitmap, photo: File) {
            val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(newBitmap)
            canvas.drawColor(Color.WHITE)
            val matrix = Matrix()
            matrix.reset()
            matrix.postTranslate(0.0F, 0.0F)
            canvas.drawBitmap(bitmap, matrix, null)
            val stream = FileOutputStream(photo)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
            stream.close()
        }

        //Create FileName
        fun createUpFileName(context: Activity, filename: String, suffix: String, miliSecond: Long): String =
            String.format("${filename}_${suffix}_$miliSecond.png")

        //Upload Image

        //val DC_DUMMY02 =H.createUpFileName(this@ASComplete,upload_file_name, "BF_P", current_milisecond)
        fun uploadImage(
            context: Activity,
            CD_FIRM: String,
            path: String,
            foldername: String,
            filename: String,
            module: String
        ) {

            val client = AsyncHttpClient()
            val params = RequestParams()
            val file = File(path)
            params.put("COLUMN", filename)
            params.put("slipno", foldername)
            params.put("MODULE", module)
            params.put("CD_FIRM", CD_FIRM)
            params.put("uploadedfile", file)
            params.setForceMultipartEntityContentType(true)

            client.post(
                "http://$ip_address/mob_json/UploadFromAndroid.ashx",
                params,
                object : TextHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                        L(responseString!!)
                    }

                    override fun onFailure(
                        statusCode: Int,
                        headers: Array<out Header>?,
                        responseString: String?,
                        throwable: Throwable?
                    ) {
                        L(throwable!!.message!!)
                    }
                })
        }

        //Get RealPath from Uri
        fun getRealPathFromURI(context: Activity, contentUri: Uri): String {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(contentUri, proj, null, null, null);
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        }

        fun changeTelFormat(inputstr: String): String {
            val fmtedstr = inputstr.replace("-", "")
            return fmtedstr.substring(0, fmtedstr.length - 8) + "-" + fmtedstr.substring(
                fmtedstr.length - 8,
                fmtedstr.length - 4
            ) + "-" + fmtedstr.substring(fmtedstr.length - 4, fmtedstr.length)
        }

        fun updateCost(requestList: List<RequiredMaterialModel>) {
            for (i in 0..H.QTData.size - 1) {
                save(
                    requestList!![i].CD_FIRM,
                    requestList!![i].NO_SO,
                    requestList!![i].NO_LINE,
                    requestList!![i].NO_LINE_SO,
                    requestList!![i].CD_MATL,
                    QTData[i], requestList!![i].UM_SUPPLY.toDouble().toInt()
                )
            }
        }

        fun save(cdFirm: String, noSo: String, noLine: String, noLineSo: String, cdMatl: String, qt: Int, price: Int) {
            val routes = H.getRoute(ip = H.ip_address)
            val para = RequestParams()
            para.put("nm_sp", "SP_MBL_CRM_SOL_MATL_U")
            para.put(
                "param",
                "$cdFirm|$noSo|$noLine|$noLineSo|$cdMatl|$qt|$price"
            )
            L(para.toString())
            doPost(routes, para, object : WaitInter {
                override fun responseSuccess(result: String) {
                }

                override fun error(err: String) {
                }
            })
        }

        fun requestAsAllocationData(dt: String) {
            val CD_DATE = dt.replace("/", "")

            val param = arrayOf(currentUser!!.CD_FIRM, currentUser!!.CD_USER, CD_DATE, "|")

            val params = RequestParams()
            params.put("nm_sp", "SP_MBL_CRM_S")
            params.put("param", joinString(param))

            doPost(getRoute(ip = ip_address), params, object : WaitInter {
                override fun responseSuccess(result: String) {
                    val jAry = strToJArr(result)
                    asAllocationList = Gson().fromJson(jAry.toString(), Array<AllocationListModel>::class.java).toList()
                }

                override fun error(err: String) {
                    asAllocationList = arrayListOf()

                }
            })
        }
    }
}