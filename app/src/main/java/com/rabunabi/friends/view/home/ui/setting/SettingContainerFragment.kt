
package com.rabunabi.friends.view.home.ui.setting

import androidx.fragment.app.Fragment
import com.rabunabi.friends.view.base.BaseContainerFragment

class SettingContainerFragment : BaseContainerFragment() {
    override fun onCreateFirstFragment(): Fragment {
        return SettingFragment()
    }
}