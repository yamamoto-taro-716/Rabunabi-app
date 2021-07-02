
package rabunabi.freechat.com.view.home.ui.setting

import androidx.fragment.app.Fragment
import rabunabi.freechat.com.view.base.BaseContainerFragment

class SettingContainerFragment : BaseContainerFragment() {
    override fun onCreateFirstFragment(): Fragment {
        return SettingFragment()
    }
}