package rabunabi.freechat.com.view.home.ui.withdraw

import android.content.Intent
import android.view.View
import rabunabi.freechat.com.R
import rabunabi.freechat.com.common.Const
import rabunabi.freechat.com.common.extensions.showError
import rabunabi.freechat.com.utils.SharePreferenceUtils
import rabunabi.freechat.com.view.base.BaseFragment
import rabunabi.freechat.com.view.home.ui.profile.ProfileActivity
import rabunabi.freechat.com.view.home.ui.setting.SettingContainerFragment
import kotlinx.android.synthetic.main.fragment_withdraw.*
import kotlinx.android.synthetic.main.toolbar.*
import rabunabi.freechat.com.view.home.ui.profile.ProfileContainerFragment

class DisableUserFragment : BaseFragment() {
    var mViewModel: DisableUserViewModel? = null

    override fun getIdContainer(): Int {
        return R.layout.fragment_withdraw
    }

    override fun initView() {

        mViewModel = DisableUserViewModel()
        initToolbar()
        initListener()
    }

    private fun initToolbar() {
//        tv_title_toolbar.text = getString(R.string.withdraw)
        img_title.setImageResource(R.drawable.logout_title_p)
        rl_action_left.visibility = View.VISIBLE
        imv_action_left.setImageResource(R.drawable.ic_icon_back_p)
        rl_action_left.setOnClickListener {
            (parentFragment as ProfileContainerFragment?)!!.popFragment()
        }

        img_title.visibility = View.GONE
        tvTitle.setText("退会")
        tvTitle.visibility = View.VISIBLE
    }

    private fun initListener() {
        btnCancel.setOnClickListener {
            (parentFragment as ProfileContainerFragment?)!!.popFragment()
        }
        btnConfirm.setOnClickListener {
            sendApi()
        }
    }

    private fun sendApi() {
        mViewModel?.disableUser(){
            if (it == null) {
                SharePreferenceUtils.getInstances().saveString(Const.AUTH, "")
                val intent = Intent(activity, ProfileActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                activity!!.finish()
            } else {
                context!!.showError(it)
            }
        }

    }
}