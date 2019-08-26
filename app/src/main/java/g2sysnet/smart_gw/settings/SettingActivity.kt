package g2sysnet.smart_gw.settings

import android.os.Bundle
import android.preference.PreferenceActivity
import g2sysnet.smart_gw.R

class SettingActivity : PreferenceActivity() {
    private val prefs = R.xml.pref_setting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(prefs)
    }
}

