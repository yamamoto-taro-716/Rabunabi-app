package com.rabunabi.friends.view.home.ui.mypage

import androidx.fragment.app.Fragment
import com.rabunabi.friends.view.base.BaseContainerFragment

class MyPageContainerFragment : BaseContainerFragment() {
    override fun onCreateFirstFragment(): Fragment {
        return ListFriendFragment()
    }
}