package rabunabi.freechat.com.view.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.annotation.DrawableRes
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import rabunabi.freechat.com.R
import rabunabi.freechat.com.common.Const
import rabunabi.freechat.com.model.ReloadMessageEvent
import rabunabi.freechat.com.model.friends.FriendListModel
import rabunabi.freechat.com.utils.DBManager
import rabunabi.freechat.com.utils.SharePreferenceUtils
import rabunabi.freechat.com.view.base.BaseActivity
import rabunabi.freechat.com.view.base.BaseFragmentActivity
import rabunabi.freechat.com.view.home.ui.messsage.MessageFragment
import rabunabi.freechat.com.view.home.ui.profile.ProfileFragment
import rabunabi.freechat.com.view.home.ui.search.SearchContainerFragment
import rabunabi.freechat.com.view.home.ui.setting.SettingContainerFragment
import rabunabi.freechat.com.viewmodel.HomeViewModel
import rabunabi.freechat.com.viewmodel.ListfriendViewModel
import rabunabi.freechat.com.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_home2.*
import kotlinx.android.synthetic.main.dialog_terms.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import rabunabi.freechat.com.view.home.ui.profile.ProfileContainerFragment
import rabunabi.freechat.com.view.home.ui.profile.mypoint.MyPointFragment


class HomeActivity : BaseFragmentActivity() {
    var homeViewModel: HomeViewModel? = null

    companion object {
        var languageCode: String? =
            SharePreferenceUtils.getInstances().getString(Const.LANG_CODE) ?: "EN"
        var code: String? = null
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home2
    }

    override fun initFragments() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        getFragment(transaction, SearchContainerFragment::class.java, null)
        //getFragment(transaction, ChatContainerFragment::class.java, null)
        //getFragment(transaction, MyPageContainerFragment::class.java, null)
        getFragment(transaction, MessageFragment::class.java, null)
        getFragment(transaction, ProfileFragment::class.java, null)
        getFragment(transaction, MyPointFragment::class.java, null)
        commitFragmentTransaction(transaction)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerSubscribers()
        setupTabLayout()
        initListener()
        initFragments()
        setCurrentTabFragment(0)
        initData()
    }

    private fun registerSubscribers() {
        try {
            EventBus.getDefault().register(this)
        } catch (e: Exception) {
        }
    }

    private fun unregisterSubscribers() {
        try {
            EventBus.getDefault().unregister(this)
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        unregisterSubscribers()
        super.onDestroy()
    }

    @Subscribe
    fun onBlockEvent(event: ReloadMessageEvent) {
        try {
            runOnUiThread {
                initData()
                System.out.println("diep reload message HOME bottom tab")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var dbManager: DBManager? = null
    var userViewModel: UserViewModel? = null
    var lisfriendViewModel: ListfriendViewModel? = null
    var listMessageModel: MutableList<FriendListModel>? = null
    fun initData() {
        userViewModel = UserViewModel()
        dbManager = DBManager(this)
        listMessageModel = ArrayList()
        lisfriendViewModel = ListfriendViewModel()
        listMessageModel = dbManager?.getAllMessage()

        if (listMessageModel != null) {
            listMessageModel!!.sortByDescending { friendListModel -> friendListModel.created }
        }
        showUnread()
    }

    private fun showUnread() {
        val view: View = tabLayoutHome.getTabAt(1)?.customView!!
        val tvRead: TextView = view.findViewById(R.id.tv_count_message)
        val llRead: LinearLayout = view.findViewById(R.id.ll_count_message)
        if (listMessageModel != null) {
            var count: Int = 0
            for (item: FriendListModel in listMessageModel!!) {
                count += item.totalUnread!!
            }
            if (count > 0) {
                tvRead.text = "" + count
                llRead.visibility = View.VISIBLE
                System.out.println("diep VISIBLE reload message HOME bottom tab count : " + count)
            } else {
                System.out.println("diep GONE reload message HOME bottom tab count : " + count)
                llRead.visibility = View.GONE
            }
        } else {
            System.out.println("diep GONE reload message HOME bottom tab ")
            llRead.visibility = View.GONE
        }
    }

    override fun initView() {
        initInterstitialAd(FORM_HOME)
        homeViewModel = HomeViewModel()
        if (SharePreferenceUtils.getInstances().getBoolean(Const.SHOW_POPUP_TERMS)) {
            showDialogTerms()
        }
    }

    private fun initListener() {
        tabLayoutHome.addOnTabSelectedListener(this)
    }

    private fun showDialogTerms() {
        var dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_terms)
        val view = (dialog).window
        view!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.imvClose.setOnClickListener {
            dialog.dismiss()
            SharePreferenceUtils.getInstances().saveBoolean(Const.SHOW_POPUP_TERMS, false)
        }
        dialog.show()

        when (HomeActivity.languageCode) {
            "EN" -> HomeActivity.code = "en"
            "JP" -> HomeActivity.code = ""
            "VN" -> HomeActivity.code = "en"
        }
        dialog.webView3.loadUrl(Const.HOST + "/rabunabi/pages/term/${HomeActivity.code}")
        dialog.webView3.settings
        dialog.webView3.setBackgroundColor(Color.WHITE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 1
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 99) {
            // var count_ads = SharePreferenceUtils.getInstances().getStartInfo().count_ads
            // if (count_ads!! == BalloonchatApplication.ADS_CONFIG) {

            //  }else{
            initInterstitialAd(BaseActivity.FORM_HOME_RESULT)
            // }
            //  if (resultCode == Activity.RESULT_OK) {

            //  }
        }
    }

    private fun setupTabLayout() {
        tabLayoutHome.getTabAt(0)?.customView = getTabView(R.drawable.bg_mypage_selector)
        tabLayoutHome.getTabAt(1)?.customView = getTabView(R.drawable.bg_chat_selector)
        tabLayoutHome.getTabAt(2)?.customView = getTabView(R.drawable.bg_search_selector)
        tabLayoutHome.getTabAt(3)?.customView = getTabView(R.drawable.bg_money_selector)
        showUnread()
    }

    private fun getTabView(@DrawableRes icon: Int): View? {
        val view: View =
            LayoutInflater.from(this).inflate(R.layout.item_tab_layout, null)
        val imageTab: AppCompatImageView = view.findViewById(R.id.imageViewTab)
        imageTab.setImageResource(icon)
        return view
    }

    private fun setTabSelected(tab: TabLayout.Tab) {
        val view: View? = tab.customView
        val imageTab: AppCompatImageView = view!!.findViewById(R.id.imageViewTab)
        imageTab.isSelected = true
    }

    private fun setTabUnselected(tab: TabLayout.Tab) {
        val view: View? = tab.customView
        val imageTab: AppCompatImageView = view!!.findViewById(R.id.imageViewTab)
        imageTab.isSelected = false
    }

    private fun setCurrentTabFragment(tabPosition: Int) {
        var b = Bundle()
        when (tabPosition) {
            2 -> showFragment(
                SearchContainerFragment::class.java,
                2,
                null
            )
            1 -> showFragment(
                MessageFragment::class.java,
                1,
                null
            )
            0 -> showFragment(
                ProfileContainerFragment::class.java,
                0, b
            )
            3 -> showFragment(
                MyPointFragment::class.java,
                3,
                null
            )
            else -> {
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        setTabSelected(tab)
        setCurrentTabFragment(tab.position)
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        setTabUnselected(tab!!)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {}
}
