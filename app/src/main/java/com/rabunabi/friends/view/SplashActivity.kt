package com.rabunabi.friends.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import android.view.Window
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.rabunabi.friends.R
import com.rabunabi.friends.common.Const
import com.rabunabi.friends.common.DialogUtils
import com.rabunabi.friends.common.extensions.showError
import com.rabunabi.friends.utils.SharePreferenceUtils
import com.rabunabi.friends.view.base.BaseActivity
import com.rabunabi.friends.view.dialog.DialogMessage
import com.rabunabi.friends.view.home.HomeActivity
import com.rabunabi.friends.view.home.ui.profile.ProfileActivity
import com.rabunabi.friends.view.home.ui.setting.SettingFragment
import com.rabunabi.friends.viewmodel.StartViewModel
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

class SplashActivity : BaseActivity() {
    var startViewModel: StartViewModel? = null
    private val APP_NOTIFICATION_SETTINGS = "android.settings.APP_NOTIFICATION_SETTINGS"
    private val APP_PACKAGE = "app_package"
    private val APP_UID = "app_uid"
    companion object {
        var handler: Handler? = null
    }

    private fun selectLanguage(langCode: String, langText: String) {
        SharePreferenceUtils.getInstances().saveString(Const.LANG_CODE, langCode)
        SharePreferenceUtils.getInstances().saveString(Const.LANG_TEXT, langText)
        SettingFragment.lang = langCode
        if (langCode == "JP") {
            SettingFragment.lang = "JA"
        }
        val myLocale = Locale(
            SettingFragment.lang, when (SettingFragment.lang) {
                "JA" -> "JP"
                "EN" -> "US"
                else -> {
                    "VI"
                }
            }
        )
        val res = resources
        val conf = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(myLocale)
        } else {
            conf.locale = myLocale
        }
        res.updateConfiguration(conf, res.displayMetrics)
    }

    override fun getLayoutId(): Int {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        } catch (e: Exception) {
        }
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        selectLanguage("JP", "日本語");
        return R.layout.activity_splash
    }

    override fun initView() {
        Glide.with(this).load(R.drawable.splash_rabunabi_p).into(ivLogo1)
        checkPermissionNotification()
    }

    private fun checkPermissionNotification() {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            showDialogAllowShowNotification()
        } else {
            goToMain()
        }
    }

    private fun goToMain() {
        startViewModel = StartViewModel()
        showLoading()
        startViewModel?.startUp() {
            if (it == null) {
                handler = Handler()
                handler?.postDelayed({
                    if (!SharePreferenceUtils.getInstances().getString(
                            Const.AUTH
                        ).equals("") && SharePreferenceUtils.getInstances().getInt(
                            Const.USER_START_ID
                        ) > 0
                    ) {
                        goToActivity(HomeActivity::class.java)
                    } else {
                        var b = Bundle();
                        b.putString("from", "splash");
                        goToActivity(ProfileActivity::class.java, b)
                    }
                    finish()
                }, 900)
            } else {
                showError(it)
            }
            hideLoading()
        }
    }
    private fun showDialogAllowShowNotification() {
        DialogUtils.showDialogMessage(
            this,
            getString(R.string.allow_show_notification),
            getString(R.string.setting),
            DialogMessage.OnDialogMessageListener {
                val intent = Intent()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent.action = APP_NOTIFICATION_SETTINGS
                    intent.putExtra(APP_PACKAGE, packageName)
                    intent.putExtra(APP_UID, applicationInfo.uid)
                } else {
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            })
    }
}
