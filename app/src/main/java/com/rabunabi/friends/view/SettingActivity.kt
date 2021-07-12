package com.rabunabi.friends.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Display
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.CompoundButton
import com.rabunabi.friends.R
import com.rabunabi.friends.common.Const
import com.rabunabi.friends.utils.SharePreferenceUtils
import com.rabunabi.friends.view.base.BaseActivity
import com.rabunabi.friends.view.home.HomeActivity
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.dialog_language.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*


class SettingActivity : BaseActivity() {
    companion object {
        var lang: String? = null
        var langText: String? = null
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }


    override fun initView() {
//        tv_title_toolbar.text = getString(R.string.setting)
        rl_action_left.visibility = View.VISIBLE
        imv_action_left.setImageResource(R.drawable.ic_icon_back_p)
        rl_action_left.setOnClickListener {
            var intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            )
            startActivity(intent)
            finish()
        }

        layble_language.text = SharePreferenceUtils.getInstances().getString(
            Const.LANG_TEXT) ?: "English"
        langText = SharePreferenceUtils.getInstances().getString(
            Const.LANG_TEXT)

        if (SharePreferenceUtils.getInstances().getString(Const.LANG_TEXT).equals("")) {
            langText = "English"
            selectLanguage("EN", "English")
        }
        layble_language.text = langText
        lnLanguage.setOnClickListener { showDialogLanague() }
        lnTearm.setOnClickListener { goToActivity(TermsActivity::class.java) }
        lnPrivacy.setOnClickListener { goToActivity(PrivacyActivity::class.java) }
        lnContactus.setOnClickListener { goToActivity(ContacusActivity::class.java) }
        sw_notification.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            SharePreferenceUtils.getInstances().saveBoolean(Const.NOTIFICATION_STATUS, isChecked)
        })
        sw_notification.isChecked = SharePreferenceUtils.getInstances().getBoolean(
            Const.NOTIFICATION_STATUS)

    }


    fun showDialogLanague() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_language)
        val view = (dialog).window
        view!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window = dialog.window
        val display: Display = windowManager.defaultDisplay
        window?.setGravity(Gravity.CENTER)
        dialog.vn_language.setOnClickListener {
            selectLanguage("VN", "Tiếng Việt")
            layble_language.text = "Tiếng Việt"
            dialog.dismiss()
        }
        dialog.el_language.setOnClickListener {
            selectLanguage("EN", "English")
            dialog.dismiss()
        }
        dialog.jp_language.setOnClickListener {
            selectLanguage("JP", "日本語")
            dialog.dismiss()
        }
        dialog.imvClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun selectLanguage(langCode: String, langText: String) {
        System.out.println("diep sss selectLanguage code " + langText)
        SharePreferenceUtils.getInstances().saveString(Const.LANG_CODE, langCode)
        SharePreferenceUtils.getInstances().saveString(Const.LANG_TEXT, langText)
        lang = langCode
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
        layble_language.text = langText
        val res = resources
        val conf = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(myLocale)
        } else {
            conf.locale = myLocale
        }
        res.updateConfiguration(conf, res.displayMetrics)
        startActivity(Intent(this, SettingActivity::class.java))
        finish()
    }


    override fun onBackPressed() {
        var intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)
        finish()
    }


}
