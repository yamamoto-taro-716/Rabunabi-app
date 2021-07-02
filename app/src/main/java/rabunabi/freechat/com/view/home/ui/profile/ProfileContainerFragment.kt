package rabunabi.freechat.com.view.home.ui.profile

import androidx.fragment.app.Fragment
import rabunabi.freechat.com.view.base.BaseContainerFragment

class ProfileContainerFragment : BaseContainerFragment() {
    override fun onCreateFirstFragment(): Fragment {
        return FirstProfileFragment()
    }
}