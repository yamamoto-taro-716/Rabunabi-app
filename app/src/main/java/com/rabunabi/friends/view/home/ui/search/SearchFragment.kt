package com.rabunabi.friends.view.home.ui.search

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.rabunabi.friends.BalloonchatApplication
import com.rabunabi.friends.R
import com.rabunabi.friends.common.Const
import com.rabunabi.friends.common.extensions.showError
import com.rabunabi.friends.model.BlockEvent
import com.rabunabi.friends.model.friends.FriendListModel
import com.rabunabi.friends.utils.DBManager
import com.rabunabi.friends.utils.SharePreferenceUtils
import com.rabunabi.friends.utils.TimeUtil
import com.rabunabi.friends.view.base.BaseFragment
import com.rabunabi.friends.view.home.ui.partner.PartnerActivity
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.toolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SearchFragment : BaseFragment() {

    private val KEY_LIMIT = "40"
    private var mHasAvatar: String = ""
    private var mPrefecture: String = ""
    private var mAgeFrom: String = ""
    private var mAgeTo: String = ""
    private var mPurpose: String = ""
    private var mMarriage: String = ""
    private var mNickname: String = ""
    private val limit = KEY_LIMIT
    private var mGender: String = ""
    private var mGenderUser: String = ""

    private var isRefresh: Boolean = false
    private var isLoadMore: Boolean = false
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var scrollListener: RecyclerView.OnScrollListener

    private var userAdapter: SearchAdapter? = null
    var searchViewModel: SearchViewModel? = null

    override fun getIdContainer(): Int {
        return R.layout.fragment_search;
    }

    private fun registerSubscribers() {
        try {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this)
            }
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
    fun onBlockEvent(event: BlockEvent) {
        if (searchViewModel != null && searchViewModel!!.listUser != null) {
            for (item: FriendListModel in searchViewModel!!.listUser!!) {
                if (item.id == event.id) {
                    searchViewModel!!.listUser!!.remove(item);
                    rvUser.adapter?.notifyDataSetChanged()
                    // xoa lich su chat
                    dbManager!!.deleteMessage("" + event.id!!)
                    break
                }
            }
        }
    }

    override fun initView() {
        if (SharePreferenceUtils.getInstances().getUserInfo() == null) {
            Toast.makeText(activity, getString(R.string.err_disconnect), Toast.LENGTH_LONG).show()
            return
        }
        registerSubscribers();

        loadMessageDB();

        initToolbar()
        searchViewModel = SearchViewModel()
        mGenderUser = BalloonchatApplication.userGender.toString()

        layoutManager =
            GridLayoutManager(activity, 3)
        rvUser.layoutManager = layoutManager
        rvUser.setHasFixedSize(true)
        setRecyclerViewScrollListener()
        initAdapter()
        initListener()

        if (1 == SharePreferenceUtils.getInstances().getUserInfo()!!.gender) {
            mGender = "女性"
        } else {
            mGender = "男性"
        }
        System.out.println(
            "diep mGender " + mGender + "  __ " + SharePreferenceUtils.getInstances()
                .getUserInfo()!!.gender
        )

        loadApiSearchUser()
        tvRightToolbar?.visibility = View.GONE;
    }

    fun initToolbar() {
//        tv_title_toolbar.text = getString(R.string.title_search)
        img_title.setImageResource(R.drawable.title_tab_search_p)
        rl_action_left.visibility = View.GONE
        rl_action_right.visibility = View.VISIBLE
        imv_action_right.visibility = View.VISIBLE

        imv_action_right.setImageResource(R.drawable.ic_search_p)
//        imv_bmessage.setImageResource(R.drawable.ic_h_reload)
//        imv_bmessage.visibility = View.GONE

        //img_title.setImageResource(R.drawable.mypoint_title_p)
        img_title.visibility = View.GONE
        tvTitle.setText("友達検索")
        tvTitle.visibility = View.VISIBLE
    }

    private fun initListener() {
        if (SharePreferenceUtils.getInstances().getUserInfo() == null) {
            Toast.makeText(activity, getString(R.string.err_disconnect), Toast.LENGTH_LONG).show()
            return
        }
        imv_action_right.setOnClickListener {
            val dialog = context?.let { it1 ->
                SearchDialog1(
                    it1,
                    mNickname,
                    mGender,
                    mPrefecture,
                    mAgeFrom,
                    mAgeTo,
                    mHasAvatar,
                    mPurpose,
                    mMarriage,
//                    "指定なし",
//                    "指定なし",
                    SharePreferenceUtils.getInstances().getUserInfo()!!.gender.toString(),
                    OnSearchUserListener { nickname, gender, area, ageFrom, ageTo, hasAvatar, purpose, marriage ->
                        mNickname = nickname
                        mGender = gender
                        mPrefecture = area
                        mAgeFrom = ageFrom
                        mAgeTo = ageTo
                        mHasAvatar = hasAvatar
                        mPurpose = purpose
                        mMarriage = marriage
                        isRefresh = true
                        searchViewModel!!.listUser?.clear()
                        userAdapter?.notifyDataSetChanged()
                        searchViewModel!!.offset = 0;
                        loadApiSearchUser()
                    })
            }
            dialog?.show()
        }
//        imv_bmessage.setOnClickListener {
//            refreshListUser()
//        }
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setOnRefreshListener { refreshListUser() }
    }

    private fun refreshListUser() {
        if (SharePreferenceUtils.getInstances().getUserInfo() == null) {
            Toast.makeText(activity, getString(R.string.err_disconnect), Toast.LENGTH_LONG).show()
            return
        }
        mNickname = ""

        if (1 == SharePreferenceUtils.getInstances().getUserInfo()!!.gender) {
            mGender = "女性"
        } else {
            mGender = "男性"
        }
        System.out.println(
            "diep mGender " + mGender + "  __ " + SharePreferenceUtils.getInstances()
                .getUserInfo()!!.gender
        )
        mPrefecture = ""
        mAgeFrom = ""
        mAgeTo = ""
        mHasAvatar = ""
        isRefresh = true
        searchViewModel?.offset = 0;

        loadApiSearchUser()
    }

    private fun initAdapter() {
        userAdapter = SearchAdapter(context, searchViewModel?.listUser) {
            for (item in searchViewModel!!.listUser!!) {
                item.isSelected = 0;
                if(item.id ==searchViewModel!!.listUser?.get(it!!)!!.id ){
                    item.isSelected = 1;
                }
            }
            userAdapter!!.notifyDataSetChanged();

            var bundle = Bundle()
            bundle.putParcelable(Const.KEY_FRIEND, searchViewModel!!.listUser?.get(it!!))
            goToActivity(PartnerActivity::class.java, bundle)
        }
        rvUser.adapter = userAdapter
    }

    var dialog: AlertDialog? = null

    private fun showLoadingDialog(context: Context?) {
        if (dialog != null && dialog!!.isShowing) {
            dialog?.dismiss()
        }
        var dialogBuilder = AlertDialog.Builder(context, R.style.CustomProgress)
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.progress_dialog, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(true)
        dialog = dialogBuilder.create()
        dialog?.show()
    }

    fun dismiss() {
        try {
            dialog?.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        dialog = null;
    }
    var isLoading:Boolean = false;
    private fun loadApiSearchUser() {
        if(isLoading){
            return;
        }
        isLoading = true;
        showLoadingDialog(activity);

        searchViewModel?.searchUser(
            mNickname,
            mGender,
            mHasAvatar,
            mPrefecture,
            mAgeFrom,
            mAgeTo,
            mPurpose,
            mMarriage,
            isRefresh,
            limit
        ) {
            dismiss()
            isLoading = false;
            if (it == null) {
                // sort người online gần nhất lên đầu
                TimeUtil.sortByDate(activity, searchViewModel!!.listUser);
                //end
                if (rvUser != null) {
                    rvUser.adapter?.notifyDataSetChanged()
                    //check update avata trong db
                    updateDBAvatar()
                }
            } else {
                context?.showError(it)
            }
            try {
                swipeRefreshLayout.isRefreshing = false
            } catch (e: Exception) {
            }
            isRefresh = false

        }

    }

    private fun updateDBAvatar() {
        if (searchViewModel != null && searchViewModel!!.listUser != null) {
            for (item: FriendListModel in searchViewModel!!.listUser!!) {
                // nếu avatar chưa được confirm thì set "" để ko hiển thị lên
                if (item.avatar_status != 1) {
                    item.avatar = "";
                }
                // cập nhật lại avatar trong db, bảng message
                if (listMessageModel != null) {
                    for (mess: FriendListModel in listMessageModel!!) {
                        if (item.id == mess.id) {
                            mess.avatar = item.avatar;
                            var result = dbManager?.updateMessage(mess);
                            System.out.println("@@@@ DIEP update db avater " + item.nickname + " result " + result);
                        }
                    }
                }
            }
        }
    }

    var listMessageModel: MutableList<FriendListModel>? = null
    var dbManager: DBManager? = null

    public fun loadMessageDB() {
        dbManager = DBManager(activity!!)
        listMessageModel = ArrayList()
        listMessageModel = dbManager?.getAllMessage()
    }

    private fun setRecyclerViewScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (layoutManager.findLastVisibleItemPosition() >= userAdapter?.listUser!!.size - 1) {
                    System.out.println("Ok searchViewModel?.isLoadMore!! " + searchViewModel?.isLoadMore!!)
                    if (searchViewModel?.isLoadMore!! && dialog == null && !isLoading) {
                        if(searchViewModel!!.listUser !=null) {
                            searchViewModel!!.listUser!!.clear();
                        }
                        loadApiSearchUser()
                    }
                } else {
                    //  System.out.println("Ok searchViewModel?.isLoadMore!!1111 " + searchViewModel?.isLoadMore!!);
                    // System.out.println("Ok searchViewModel?.isLoadMore!!1111 " + layoutManager.findLastVisibleItemPosition() +">"+( userAdapter?.listUser!!.size - 1));
                }
            }
        }
        rvUser.addOnScrollListener(scrollListener)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReloadListEvent(event: ReloadListEvent) {
        try {
            refreshListUser();
        } catch (e: Exception) {
        }
    }
}
