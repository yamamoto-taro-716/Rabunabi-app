package com.rabunabi.friends.view.home.ui.profile

import android.os.Bundle
import com.rabunabi.friends.R
import com.rabunabi.friends.common.Const
import com.rabunabi.friends.view.base.BaseActivity
import com.rabunabi.friends.view.home.ui.profile.register.RegisterFragment

class ProfileActivity : BaseActivity() {
    var from: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var b = intent.getBundleExtra(Const.KEY_EXTRA_DATA)
        if (b != null) {
            from = b.getString("from")
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_profile2
    }

    override fun initView() {
        var b = intent.getBundleExtra(Const.KEY_EXTRA_DATA)
        if (b != null) {
            from = b.getString("from")
        }
        val fragment = RegisterFragment()
        fragment.from = from
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, fragment, RegisterFragment::class.java.name)
            .commit()
    }

}
