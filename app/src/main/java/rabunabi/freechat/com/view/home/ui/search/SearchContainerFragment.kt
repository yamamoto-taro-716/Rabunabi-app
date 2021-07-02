package rabunabi.freechat.com.view.home.ui.search

import androidx.fragment.app.Fragment
import rabunabi.freechat.com.view.base.BaseContainerFragment

class SearchContainerFragment : BaseContainerFragment() {
    override fun onCreateFirstFragment(): Fragment {
        return SearchFragment()
    }
}