package rabunabi.freechat.com

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.multidex.MultiDexApplication
import rabunabi.freechat.com.common.Const
import rabunabi.freechat.com.utils.LocaleUtils
import rabunabi.freechat.com.utils.SharePreferenceUtils
import java.util.*

class BalloonchatApplication : MultiDexApplication() {

    companion object {
        var ADS_CONFIG = 123456; // ko hien thi admob fullscreen
        var context: Context? = null
        var AD_DISPLAY_COUNT: String = "AD_DISPLAY_COUNT"
        var userGender: Int = -1;
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        registerActivityLifecycleCallbacks(AppLifecycleTracker())

        intDefaultLocate();
    }

    fun intDefaultLocate() {
        var code = SharePreferenceUtils.getInstances().getString(Const.LANG_TEXT);
        var LANG_CODE = SharePreferenceUtils.getInstances().getString(Const.LANG_CODE);
        System.out.println("diep BalloonchatApplication code " + code)
        System.out.println("diep BalloonchatApplication LANG_CODE " + LANG_CODE)
        if (code == null || "".equals(code)) {
            LocaleUtils.setDefaultLangue()
            var code = SharePreferenceUtils.getInstances().getString(Const.LANG_CODE);
            var text = SharePreferenceUtils.getInstances().getString(Const.LANG_TEXT);
            selectLanguage(code!!, text!!);
        }
    }

    fun selectLanguage(langCode: String, langText: String) {
        SharePreferenceUtils.getInstances().saveString(Const.LANG_CODE, langCode)
        SharePreferenceUtils.getInstances().saveString(Const.LANG_TEXT, langText)
        var lang = langCode
        if (langCode == "JP") {
            lang = "JA"
        }

        val myLocale = Locale(
            lang, when (lang) {
                "JA" -> "JP"
                "EN" -> "US"
                else -> {
                    "VI"
                }
            }
        )
        //layble_language.text = langText
        val res = resources
        val conf = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(myLocale)
        } else {
            conf.locale = myLocale
        }
        res.updateConfiguration(conf, res.displayMetrics)
        // startActivity(Intent(this, ProfileActivity::class.java))
        //   finish()
    }

    class AppLifecycleTracker : Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        companion object {
            private var numStarted = 0
            fun isRunning(): Boolean {
                if (numStarted > 0)
                    return true
                return false
            }

        }

        override fun onActivityStarted(activity: Activity) {
            if (numStarted == 0) {
                println("Activity has started");
            }
            numStarted++
        }

        override fun onActivityStopped(activity: Activity) {
            numStarted--
            if (numStarted == 0) {
                println("Activity  has stopped");
            }
        }
    }
}