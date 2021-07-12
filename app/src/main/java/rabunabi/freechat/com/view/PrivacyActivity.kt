package rabunabi.freechat.com.view

import android.view.View
import rabunabi.freechat.com.R
import rabunabi.freechat.com.common.Const
import rabunabi.freechat.com.utils.SharePreferenceUtils
import rabunabi.freechat.com.view.base.BaseActivity
import kotlinx.android.synthetic.main.activity_privacy.*
import kotlinx.android.synthetic.main.toolbar.*

class PrivacyActivity : BaseActivity() {
    companion object {
        var languageCode:String? = null
        var code:String? = null
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_privacy
    }
    override fun initView() {
      initToolbar()
        languageCode = SharePreferenceUtils.getInstances().getString(
            Const.LANG_CODE)
        when(languageCode){
            "EN" -> code = "en"
            "JP" -> code = ""
            "VN" -> code = "en"
        }
        webview.loadUrl(Const.HOST+"/rabunabi/pages/policy/"+ code)
    }
    private fun initToolbar() {

        rl_action_left.visibility = View.VISIBLE
        imv_action_left.visibility = View.VISIBLE
        imv_action_left.setImageResource(R.drawable.ic_icon_back_p)
        rl_action_left.setOnClickListener {
            finish()
        }


        img_title.visibility = View.INVISIBLE
        tvTitle.visibility = View.VISIBLE
        tvTitle.setText("プライバシーポリシー")
//        tv_title_toolbar.setText(R.string.policy)
    }

}
