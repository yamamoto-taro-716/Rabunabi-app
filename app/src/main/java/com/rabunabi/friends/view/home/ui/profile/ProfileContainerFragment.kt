package com.rabunabi.friends.view.home.ui.profile

import androidx.fragment.app.Fragment
import com.rabunabi.friends.view.base.BaseContainerFragment

class ProfileContainerFragment : BaseContainerFragment() {
    override fun onCreateFirstFragment(): Fragment {
        return FirstProfileFragment()
    }
}