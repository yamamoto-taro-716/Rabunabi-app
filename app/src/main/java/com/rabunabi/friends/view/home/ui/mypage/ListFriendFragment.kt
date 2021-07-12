package com.rabunabi.friends.view.home.ui.mypage

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import android.view.Window
import com.rabunabi.friends.R
import com.rabunabi.friends.adapter.ListfriendAdapter
import com.rabunabi.friends.adapter.RequestfriendAdapter
import com.rabunabi.friends.common.Const
import com.rabunabi.friends.common.extensions.from
import com.rabunabi.friends.common.extensions.showAlertDialog
import com.rabunabi.friends.common.extensions.showError
import com.rabunabi.friends.utils.SharePreferenceUtils
import com.rabunabi.friends.view.home.ui.chat.ChatActivity
import com.rabunabi.friends.view.base.BaseFragment
import com.rabunabi.friends.viewmodel.ListfriendViewModel
import com.rabunabi.friends.viewmodel.RequestfriendViewModel
import com.rabunabi.friends.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_list_friend.*
import kotlinx.android.synthetic.main.dialog_message.*
import kotlinx.android.synthetic.main.dialog_unfriend.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class ListFriendFragment : BaseFragment() {
    var lisfriendViewModel: ListfriendViewModel? = null
    var requestfriendModel: RequestfriendViewModel? = null
    var userViewModel: UserViewModel? = null
    override fun getIdContainer(): Int {
        return R.layout.activity_list_friend
    }

    override fun initView() {
        lisfriendViewModel = ListfriendViewModel()
        requestfriendModel = RequestfriendViewModel()
        userViewModel = UserViewModel()
        initToolbar()

        var layoutManager =
            LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.reverseLayout = true

        rcvListfriend.layoutManager = layoutManager
        initListFriendAdapter()

        var layoutManager1 =
            LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.reverseLayout = true
        rcvRequert.layoutManager = layoutManager1
        initFriendAdapter()

        loadApiData()

        rl_action_right.setOnClickListener {
            lisfriendViewModel?.clearData()
            rcvRequert.adapter?.notifyDataSetChanged()
            rcvListfriend.adapter?.notifyDataSetChanged()
            loadApiData()

        }
    }

    fun initListFriendAdapter() {
        try {
            Collections.reverse(lisfriendViewModel?.listFriend!!)
        } catch (e: Exception) {
        }
        rcvListfriend.adapter =
            ListfriendAdapter(lisfriendViewModel?.listFriend, {
                showDialogAvatar(it)
            }, {
                var bundle = Bundle()
                bundle.putInt(
                    Const.FRIEND_ID,
                    lisfriendViewModel!!.listFriend?.get(it!!)!!.id!!
                )
                bundle.putParcelable(
                    Const.KEY_FRIEND,
                    lisfriendViewModel!!.listFriend?.get(it!!)
                )
                bundle.putInt(Const.KEY_ACTYVITY, 0)
                goToActivity(ChatActivity::class.java, bundle)
            })

    }

    fun initFriendAdapter() {
        rcvRequert.adapter = RequestfriendAdapter(
            lisfriendViewModel?.listFriendRequest,
            {
                showDialogUnfriend(it)

            },
            {
                showLoading()
                if (it != null) {
                    lisfriendViewModel?.addFriend(it) {
                        if (it == null) {
                            lisfriendViewModel?.clearData()
                            rcvRequert.adapter?.notifyDataSetChanged()
                            rcvListfriend.adapter?.notifyDataSetChanged()
                            loadApiData()
                        } else {
                            context?.showError(it)
                        }
                    }
                    hideLoading()
                } else {
                    hideLoading()
                }
            })
    }

    fun loadApiData() {
        showLoading()
        lisfriendViewModel?.getListFriends() {
            if (it == null) {
                initFriendAdapter()
                initListFriendAdapter()
                rcvListfriend.adapter?.notifyDataSetChanged()
                rcvRequert.adapter?.notifyDataSetChanged()

            } else {
                context?.showError(it)
            }
        }
        hideLoading()
    }

    fun showDialogUnfriend(id: Int?) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_unfriend)
        val view = (dialog).window
        view!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window = dialog.window
        dialog.tv_Okfriend.setOnClickListener {
            lisfriendViewModel?.unFriend(id!!) {
                if (it == null) {
                    lisfriendViewModel?.clearData()
                    rcvRequert.adapter?.notifyDataSetChanged()
                    rcvListfriend.adapter?.notifyDataSetChanged()
                    loadApiData()
                } else {
                    context?.showError(it)
                }
                hideLoading()
            }

        }
        dialog.tv_cancel_friend.setOnClickListener { dialog.dismiss() }

        window?.setGravity(Gravity.CENTER)
        dialog.show()
    }

    private fun showDialogAvatar(id: Int?) {
        var dialog = Dialog(context!!)
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
                context?.showError(it)
            }
            hideLoading()

        }
        dialog.lnTranslate.setOnClickListener {
            //  Toast.makeText(this, "AAA", Toast.LENGTH_SHORT).show()
        }
        dialog.tv_report.setOnClickListener {
            showLoading()
            lisfriendViewModel?.report(id ?: 0) {
                if (it == null) {
                    hideLoading()
                    lisfriendViewModel?.clearData()
                    rcvRequert.adapter?.notifyDataSetChanged()
                    rcvListfriend.adapter?.notifyDataSetChanged()
                    //loadApiData()
                    dialog.dismiss()
                } else {
                    context?.showError(it)
                }
                hideLoading()
            }
        }


        dialog.tvLock.setOnClickListener {
            showLoading()
            if (userViewModel?.userModel?.state?.is_blocked == 0) {
                context?.showAlertDialog(
                    message = getString(R.string.alert_lock_message),
                    positiveText = getString(R.string.ok),
                    negativeText = getString(R.string.cancel),
                    clickPositive = {
                        lisfriendViewModel?.blockFriend(id ?: 0) {
                            if (it == null) {
                                dialog.dismiss()
                            } else {
                                context?.showError(it)
                            }
                            hideLoading()
                        }
                    })

            } else {
                lisfriendViewModel?.unBlockFriend(id ?: 0) {
                    if (it == null) {
                        dialog.dismiss()
                    } else {
                        context?.showError(it)
                    }
                    hideLoading()
                }

            }
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
                    context?.showError(it)
                }
                hideLoading()
            }
        }

        dialog.imvClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun initToolbar() {
//        tv_title_toolbar.text = getString(R.string.list_friend)
        rl_action_left.visibility = View.INVISIBLE
        rl_action_right.visibility = View.VISIBLE
        imv_action_right.visibility = View.VISIBLE
        imv_action_right.setImageResource(R.mipmap.ic_reload)
        imv_action_left.setImageResource(R.drawable.ic_icon_back_p)
    }
}