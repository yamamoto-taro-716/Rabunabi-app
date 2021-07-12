package com.rabunabi.friends.view.home.ui.mypage

import android.view.View
import com.rabunabi.friends.R
import com.rabunabi.friends.view.base.BaseFragment
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
        rl_action_left.visibility = View.INVISIBLE
        imv_action_left.setImageResource(R.drawable.ic_icon_back_p)
//        tv_title_toolbar.text = getString(R.string.text_my_page)
    }
}
