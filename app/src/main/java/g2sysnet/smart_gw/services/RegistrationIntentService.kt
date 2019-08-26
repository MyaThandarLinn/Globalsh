package g2sysnet.smart_gw.services

import android.app.IntentService
import android.content.Intent

class RegistrationIntentService(TAG: String) : IntentService(TAG) {

    private val TAG: String = "RegistrationIntentService"

    constructor() : this("RegistrationIntentService")

    override fun onHandleIntent(intent: Intent?) {


    }
}

/*
Broadcast Receiver Style

class FlashActivity : AppCompatActivity() {

    private var mRegistrationBroadcastReceiver: BroadcastReceiver? = null
    private val PlayServiceResolutionRequest = 9000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_screen)

        registerBroadcastAndReceiver()

        if (checkPlayServices()) {
            getInstanceIdToken()
        }

        btn_to_process.setOnClickListener {
            startActivity(Intent(this@FlashActivity, ProcessActivity::class.java))
        }

    }

    //region LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
    private fun registerBroadcastAndReceiver() {
        mRegistrationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    H.REGISTRATION_READY -> txt_Token.visibility = View.GONE
                    H.REGISTRATION_GENERATING -> {
                        txt_Token.visibility = View.VISIBLE
                        txt_Token.text = getString(R.string.registering_message_generating)
                    }
                    H.REGISTRATION_COMPLETE -> {
                        if (!H.token.isEmpty()) {
                            load_token_progress.visibility = View.GONE
                            btn_to_process.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }


    //region Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
    private fun getInstanceIdToken() {
        if (checkPlayServices()) {
            H.L("Play Service Exit!")
            startService(Intent(this, RegistrationIntentService::class.java))
        } else {
            H.L("Play Service Not Exit!")
        }
    }

    //Google Play Service 환경 체크
    private fun checkPlayServices(): Boolean {
        val status = GoogleApiAvailability.getInstance()
        val resultCode = status.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (status.isUserResolvableError(resultCode)) {
                status.getErrorDialog(this, resultCode, PlayServiceResolutionRequest)
                    .show()
            } else {
                H.L("This device is not supported.")
                finish()
            }
            return false
        }
        return true
    }

    /**
     * 앱이 실행되어 화면에 나타날때 LocalBoardcastManager에 액션을 정의하여 등록한다.
     */
    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mRegistrationBroadcastReceiver!!, IntentFilter(H.REGISTRATION_READY)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mRegistrationBroadcastReceiver!!, IntentFilter(H.REGISTRATION_GENERATING)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mRegistrationBroadcastReceiver!!, IntentFilter(H.REGISTRATION_COMPLETE)
        )
    }

    /**
     * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
     */
    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver!!)
        super.onPause()
    }
}
 */