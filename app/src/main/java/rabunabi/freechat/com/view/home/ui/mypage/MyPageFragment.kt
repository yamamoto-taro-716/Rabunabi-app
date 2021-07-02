package rabunabi.freechat.com.view.home.ui.mypage

import android.view.View
import rabunabi.freechat.com.R
import rabunabi.freechat.com.view.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar.*

class MyPageFragment : BaseFragment() {

    override fun getIdContainer(): Int {
        return R.layout.fragment_message
    }

    override fun initView() {
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initToolbar() {
        rl_action_left.visibility = View.GONE
        imv_action_left.setImageResource(R.drawable.ic_icon_back_p)
//        tv_title_toolbar.text = getString(R.string.text_my_page)
    }
}
