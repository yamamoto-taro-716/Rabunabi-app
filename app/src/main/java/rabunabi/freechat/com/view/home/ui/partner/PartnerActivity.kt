package rabunabi.freechat.com.view.home.ui.partner

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import rabunabi.freechat.com.R
import rabunabi.freechat.com.common.Const
import rabunabi.freechat.com.common.extensions.from
import rabunabi.freechat.com.common.extensions.showError
import rabunabi.freechat.com.model.BlockEvent
import rabunabi.freechat.com.model.friends.FriendListModel
import rabunabi.freechat.com.view.home.ui.chat.ChatActivity
import rabunabi.freechat.com.view.base.BaseActivity
import rabunabi.freechat.com.view.dialog.DialogMessage
import rabunabi.freechat.com.view.dialog.DialogMessage.OnDialogMessageListener
import kotlinx.android.synthetic.main.activity_profile_partner.*
import kotlinx.android.synthetic.main.item_search_user.view.*
import kotlinx.android.synthetic.main.toolbar.*
import org.greenrobot.eventbus.EventBus
import java.lang.Exception

class PartnerActivity : BaseActivity() {
    private var partnerId: Int = 0
    var friendListModel: FriendListModel? = null

    var partnerViewModel: PartnerViewModel? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_profile_partner
    }

    override fun initView() {
        partnerViewModel = PartnerViewModel()

        initToolbar()

        extraData {
            friendListModel = it.getParcelable(Const.KEY_FRIEND)
        }

        partnerId = friendListModel?.id!!

        bindData()
        initListener()
    }

    private fun initListener() {
        btnstartChat.setOnClickListener { v ->
            goToChatActivity()
        }
        btnBlock.setOnClickListener { v ->
            showConfirmBlock()
        }
        btnReport.setOnClickListener { v ->
            showConfirmReport()
        }
    }

    private fun showConfirmReport() {
        val dialogMessage = DialogMessage(
            this,
            getString(R.string.message_report_confirm),
            getString(R.string.ok),
            OnDialogMessageListener {
                sendRequestReport()
            })
        dialogMessage.show()
    }

    private fun showConfirmBlock() {
        val dialogMessage = DialogMessage(
            this,
            getString(R.string.message_block_confirm),
            getString(R.string.ok),
            OnDialogMessageListener {
                sendRequestBlock()
            })
        dialogMessage.show()
    }

    private fun sendRequestReport() {
        showLoading()
        partnerViewModel?.reportUser(
            partnerId
        ) {
            if (it == null) {
            } else {
                showError(it)
            }
        }
        hideLoading()
    }

    private fun sendRequestBlock() {
        showLoading()
        partnerViewModel?.blockUser(
            partnerId
        ) {
            if (it == null) {
                try {
                    // bắn eventbus xử lý để ko hiển thị người vừa block hiển thị lên list
                    var b = BlockEvent();
                    b.id = friendListModel!!.id!!;
                    EventBus.getDefault().post(b)
                } catch (e: Exception) {
                }
                // block xong thì finish activity và di chuyển về màn hình trước
                finish()
            } else {
                showError(it)
            }
        }
        hideLoading()
    }

    private fun goToChatActivity() {
        var bundle = Bundle()
        bundle.putInt(Const.FRIEND_ID, partnerId)
        bundle.putParcelable(Const.KEY_FRIEND, friendListModel)
        bundle.putInt(Const.KEY_ACTYVITY, 0)
        goToActivity(ChatActivity::class.java, bundle)
    }

    private fun bindData() {
        tvPartnerName.text = friendListModel?.nickname
        if (!TextUtils.isEmpty(friendListModel!!.avatar)) {
            img_avatar.from(friendListModel!!.avatar, R.drawable.ic_avatar_default)
        }
        tvPartnerPrefecture.text = friendListModel!!.prefecture
        tvPartnerAge.text = friendListModel!!.age.toString() + " " + getString(R.string.age2)

        tvPartnerPurpose.text = friendListModel!!.purpose
        tvPartnerMarried.text = friendListModel!!.marriage

        when (friendListModel!!.gender) {
            1 ->  // nam
                Glide.with(ivSex.context).load(R.drawable.ic_nam).into(ivSex)
            else -> { // nu
                Glide.with(ivSex.context).load(R.drawable.ic_nu).into(ivSex)
            }
        }
        edtIntro.text = friendListModel!!.intro

        // title
        tvTitle.text = friendListModel?.nickname;
        tvTitle.visibility = View.VISIBLE
    }

    private fun initToolbar() {
        rl_action_left.visibility = View.VISIBLE
        imv_action_left.setImageResource(R.drawable.ic_icon_back_p)
        rl_action_left.setOnClickListener {
            finish()
        }
        //  img_title.setImageResource(R.drawable.ic_title_detail_profile_p)
    }
}