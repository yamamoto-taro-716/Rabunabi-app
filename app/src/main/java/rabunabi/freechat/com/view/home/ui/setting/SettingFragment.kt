package rabunabi.freechat.com.view.home.ui.setting

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.fragment.app.Fragment
import android.view.Display
import android.view.Gravity.CENTER
import android.view.View
import android.view.Window
import android.widget.CompoundButton
import rabunabi.freechat.com.R
import rabunabi.freechat.com.common.Const
import rabunabi.freechat.com.utils.SharePreferenceUtils
import rabunabi.freechat.com.view.ContacusActivity
import rabunabi.freechat.com.view.PrivacyActivity
import rabunabi.freechat.com.view.TermsActivity
import rabunabi.freechat.com.view.base.BaseFragment
import rabunabi.freechat.com.view.home.HomeActivity
import rabunabi.freechat.com.view.home.ui.withdraw.DisableUserFragment
import kotlinx.android.synthetic.main.dialog_language.*
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class SettingFragment : BaseFragment() {
    companion object {
        var lang: String? = null
        var langText: String? = null
    }
    override fun getIdContainer(): Int {
        return R.layout.fragment_setting;
    }
    override fun initView() {
//        tv_title_toolbar.text = getString(R.string.setting)

        tvTitle.setText(R.string.setting)
        rl_action_left.visibility = View.INVISIBLE
        imv_action_left.setImageResource(R.drawable.ic_icon_back_p)
        rl_action_left.setOnClickListener {
            var intent = Intent(activity, HomeActivity::class.java)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            )
            startActivity(intent)
        }

        layble_language.text = SharePreferenceUtils.getInstances().getString(
            Const.LANG_TEXT) ?: "English"
        langText = SharePreferenceUtils.getInstances().getString(
            Const.LANG_TEXT)

        if (SharePreferenceUtils.getInstances().getString(Const.LANG_TEXT).equals("")) {
            langText = "English"
            selectLanguage("EN", "English")
        }
        layble_language.text = SettingFragment.langText

        lnLanguage.setOnClickListener { showDialogLanague() }


        lnTearm.setOnClickListener { goToActivity(TermsActivity::class.java) }
        lnPrivacy.setOnClickListener { goToActivity(PrivacyActivity::class.java) }
        lnContactus.setOnClickListener { goToActivity(ContacusActivity::class.java) }
        sw_notification.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            SharePreferenceUtils.getInstances().saveBoolean(Const.NOTIFICATION_STATUS, isChecked)
        })
        sw_notification.isChecked = SharePreferenceUtils.getInstances().getBoolean(
            Const.NOTIFICATION_STATUS)

        lnWithdraw.setOnClickListener{
            var fragment = DisableUserFragment()
            if (fragment != null) {
                (parentFragment as SettingContainerFragment?)!!.addChild(fragment)
            }
        }
    }
    private fun showDialogLanague() {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_language)
        val view = dialog.window
        view!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window = dialog.window
        val display: Display? = activity?.windowManager?.defaultDisplay
        window?.setGravity(CENTER)
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
        layble_language.text = langText
        val res = resources
        val conf = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(myLocale)
        } else {
            conf.locale = myLocale
        }
        res.updateConfiguration(conf, res.displayMetrics)
        goToMain()
    }

    fun goToMain() {
        val intent = Intent(activity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
