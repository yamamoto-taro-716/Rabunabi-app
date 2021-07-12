package com.rabunabi.friends.view.home.ui.messsage

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.Window
import android.widget.Toast
import com.rabunabi.friends.R
import com.rabunabi.friends.adapter.MessageAdapter
import com.rabunabi.friends.common.Const
import com.rabunabi.friends.common.extensions.from
import com.rabunabi.friends.common.extensions.showAlertDialog
import com.rabunabi.friends.customview.DividerItemDecoration
import com.rabunabi.friends.model.ReloadMessageEvent
import com.rabunabi.friends.model.friends.FriendListModel
import com.rabunabi.friends.utils.DBManager
import com.rabunabi.friends.utils.SharePreferenceUtils
import com.rabunabi.friends.view.base.BaseFragment
import com.rabunabi.friends.view.home.ui.chat.ChatActivity
import com.rabunabi.friends.viewmodel.ListfriendViewModel
import com.rabunabi.friends.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_list_friend.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.dialog_message.*
import kotlinx.android.synthetic.main.toolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import com.rabunabi.friends.api.TalkApi
import com.rabunabi.friends.common.DialogUtils
import com.rabunabi.friends.model.ContactusModel
import com.rabunabi.friends.utils.ParserJsonUtils

// talk screen
class MessageFragment : BaseFragment() {
    var messageAdapter: MessageAdapter? = null
    var listMessageModel: MutableList<FriendListModel>? = null
    var dbManager: DBManager? = null
    var userViewModel: UserViewModel? = null
    var lisfriendViewModel: ListfriendViewModel? = null
    var point: Int = 0;
    var sendMessage: Int = 0;
    var readMessage: Int = 0;
    var sendImage: Int = 0;

    override fun getIdContainer(): Int {
        registerSubscribers()
        return R.layout.activity_message
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
    fun onReloadMessageEvent(event: ReloadMessageEvent) {
        try {
            activity?.runOnUiThread {
                initData()
                if (messageAdapter != null) {
                    messageAdapter?.listMessage = ArrayList<FriendListModel>();
                    messageAdapter?.notifyDataSetChanged();
                }
                initRecycleviewMessage()
                System.out.println("diep reload message talk tab")
            }
        } catch (e: Exception) {
        }
    }

    private fun initListener() {
        swipeRefreshLayout.setOnRefreshListener {
            initData()
            initRecycleviewMessage()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun initView() {
        var pointInfo = SharePreferenceUtils.getInstances().getPointInfo();
        point = pointInfo?.points!!;
        sendMessage = pointInfo?.sendMessage;
        readMessage = pointInfo?.readMessage
        sendImage = pointInfo?.sendImage;
        initToolbar()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        initData()
        initRecycleviewMessage()
    }

    private fun initRecycleviewMessage() {
        var layoutManager =
            LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rcv_message.layoutManager = layoutManager

        messageAdapter = MessageAdapter(listMessageModel, {

            if(readMessage > point && listMessageModel?.get(it!!)?.totalUnread!! > 0) {
                DialogUtils.showDialogMessage(
                    activity,
                    getString(R.string.no_point_read),
                    getString(R.string.ok))
            } else {
                val bundle = Bundle()
                bundle.putInt(
                    Const.FRIEND_ID,
                    listMessageModel?.get(it!!)?.id!!
                )
                bundle.putParcelable(
                    Const.KEY_FRIEND,
                    listMessageModel!!.get(it!!)
                )
                bundle.putInt(Const.KEY_ACTYVITY, 1)
                bundle.putBoolean("point", listMessageModel?.get(it!!)?.totalUnread!! > 0)
//                if (true) {
                if (listMessageModel?.get(it!!)?.totalUnread!! > 0) {
                    TalkApi().decreasePoint(readMessage) {
                        val isSuccess = it.isSuccess()
                        if (isSuccess) {
                            SharePreferenceUtils.getInstances().updatePointInfo(point - readMessage)
                            point -= readMessage
                        }
                    }

                }
                goToActivity(ChatActivity::class.java, bundle)
            }
        }, {
            showDialogAvatar(listMessageModel?.get(it!!)?.id!!)
        }, {
            val listModel = listMessageModel?.get(it!!)
            if (dbManager!!.deleteSave(listModel?.id!!)) {
                Toast.makeText(activity, "削除しました。", Toast.LENGTH_SHORT).show()
            }
            messageAdapter?.remove(it!!)
        })
        rcv_message.addItemDecoration(
            DividerItemDecoration(
                activity,
                R.drawable.divider
            )
        )

        rcv_message.adapter = messageAdapter
    }

    private fun showError(message: String?) {
        activity!!.showAlertDialog(
            message = message,
            positiveText = getString(R.string.close)
        )
    }

    private fun showDialogAvatar(id: Int) {
        var dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_message)
        val view = (dialog).window
        view!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        showLoading()
        userViewModel?.getUserProfile(id ?: 0) {
            if (it == null) {
                dialog.img_avatar.from(userViewModel?.userModel?.avatar)
                dialog.tvNickName.text = userViewModel?.userModel?.nickname
                dialog.tvGender.text = when (userViewModel?.userModel?.gender) {
                    1 -> getString(R.string.male)
                    else -> {
                        getString(R.string.female)
                    }
                }
                dialog.tvLock.text = when (userViewModel?.userModel?.state?.is_blocked) {
                    0 -> getString(R.string.block)
                    else -> {
                        getString(R.string.unblock)
                    }
                }
                dialog.tvNameAbout.text = userViewModel?.userModel?.intro
                dialog.imvFlag.setImageResource(
                    when (userViewModel?.userModel?.nationality) {
                        "VN" -> R.drawable.flag_vn
                        "JP" -> R.drawable.flag_jp
                        else -> {
                            R.drawable.flag_an
                        }
                    }
                )
                dialog.tvCountry.text = when (userViewModel?.userModel?.nationality) {
                    "VN" -> getString(R.string.vn_language)
                    "JP" -> getString(R.string.japan_language)
                    else -> {
                        getString(R.string.english_language)
                    }
                }
            } else {
                showError(it)
            }
            hideLoading()

        }

        dialog.lnTranslate.setOnClickListener {
            showLoading()
            var langCode: String = when (SharePreferenceUtils.getInstances().getString(
                Const.LANG_CODE
            )) {
                "JP" -> "ja"
                "VN" -> "vi"
                else -> {
                    "en"
                }
            }
            lisfriendViewModel?.apiTranslate(dialog.tvNameAbout.text.toString(), langCode) {
                if (it == null) {
                    dialog.tvNameAbout.text = lisfriendViewModel?.textAfterTranslate
                } else {
                    showError(it)
                }
                hideLoading()
            }
        }

        dialog.tv_report.setOnClickListener {
            showLoading()
            lisfriendViewModel?.report(id ?: 0) {
                if (it == null) {
                    hideLoading()
                    lisfriendViewModel?.clearData()
                    rcvRequert.adapter?.notifyDataSetChanged()
                    rcvListfriend.adapter?.notifyDataSetChanged()
                    loadApiData()
                    dialog.dismiss()
                } else {
                    showError(it)
                }
            }
        }


        dialog.tvLock.setOnClickListener {
            showLoading()
            if (userViewModel?.userModel?.state?.is_blocked == 0) {
                showAlertDialog(
                    message = getString(R.string.alert_lock_message),
                    positiveText = getString(R.string.ok),
                    negativeText = getString(R.string.cancel),
                    clickPositive = {
                        lisfriendViewModel?.blockFriend(id ?: 0) {
                            if (it == null) {
                                dialog.dismiss()
                            } else {
                                showError(it)
                            }
                            hideLoading()
                        }
                    })

            } else {
                lisfriendViewModel?.unBlockFriend(id ?: 0) {
                    if (it == null) {
                        dialog.dismiss()
                    } else {
                        showError(it)
                    }
                    hideLoading()
                }

            }
        }


        dialog.imvClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun loadApiData() {

    }

    private fun initData() {
        userViewModel = UserViewModel()
        dbManager = DBManager(activity!!)
        listMessageModel = ArrayList()
        lisfriendViewModel = ListfriendViewModel()
        listMessageModel = dbManager?.getAllMessage()

        if (listMessageModel != null) {
            listMessageModel!!.sortByDescending { friendListModel -> friendListModel.created }
        }
    }

    private fun initToolbar() {
        rl_action_left.visibility = View.INVISIBLE
        imv_action_left.setImageResource(R.drawable.ic_icon_back_p)
        rl_action_left.setOnClickListener { activity!!.finish() }


        img_title.visibility = View.INVISIBLE
        tvTitle.setText("メッセージBOX")
        tvTitle.visibility = View.VISIBLE
    }

    private fun showAlertDialog(
        title: String? = null,
        message: String?,
        positiveText: String,
        negativeText: String? = null,
        clickPositive: (() -> Unit)? = null,
        clickNegative: (() -> Unit)? = null
    ) {
        val alertDialog = AlertDialog.Builder(activity)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(positiveText) { dialog, which ->
            dialog.dismiss()
            clickPositive?.invoke()
        }
        alertDialog.setNegativeButton(negativeText) { dialog, which ->
            dialog.dismiss()
            clickNegative?.invoke()
        }
        val alert = alertDialog.create()
        alert.show()
    }
}
