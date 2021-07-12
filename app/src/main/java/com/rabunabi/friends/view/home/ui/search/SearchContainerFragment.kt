package com.rabunabi.friends.view.home.ui.search

import androidx.fragment.app.Fragment
import com.rabunabi.friends.view.base.BaseContainerFragment

class SearchContainerFragment : BaseContainerFragment() {
    override fun onCreateFirstFragment(): Fragment {
        return SearchFragment()
    }
}