package rabunabi.freechat.com.view.home.ui.mypage

import androidx.fragment.app.Fragment
import rabunabi.freechat.com.view.base.BaseContainerFragment

class MyPageContainerFragment : BaseContainerFragment() {
    override fun onCreateFirstFragment(): Fragment {
        return ListFriendFragment()
    }
}