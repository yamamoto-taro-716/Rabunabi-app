package rabunabi.freechat.com.adapter

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import rabunabi.freechat.com.BalloonchatApplication
import rabunabi.freechat.com.common.Const
import rabunabi.freechat.com.model.ChatModel
import rabunabi.freechat.com.model.friends.FriendListModel
import rabunabi.freechat.com.utils.TimeUtil
import rabunabi.freechat.com.view.base.BaseActivity
import rabunabi.freechat.com.view.home.ui.partner.PartnerActivity
import kotlinx.android.synthetic.main.item_bubble_me.view.*
import kotlinx.android.synthetic.main.item_bubble_not_me.view.*
import com.bumptech.glide.request.RequestOptions
import rabunabi.freechat.com.R


class ChatAdapter(
    var userId: Int?,
    var listChat: MutableList<ChatModel>,
    var friendListModel: FriendListModel,
    var callback: ((Int?) -> Unit)? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TYPE_ME = 1
    val TYPE_NOT_ME = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ME) {
            return ViewHolderMe(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_bubble_me,
                    parent,
                    false
                )
            )
        } else {
            return ViewHolderNotMe(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_bubble_not_me,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        System.out.println("diep id "+userId+"== " +listChat.get(position)?.sendId)
        if (userId == listChat.get(position)?.sendId) {
            listChat?.get(position)?.isMe = true
            return TYPE_ME
        }
        listChat?.get(position)?.isMe = false
        return TYPE_NOT_ME
    }

    override fun getItemCount(): Int {
        return listChat?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (listChat?.get(position)?.isMe == true) {
            (holder as ViewHolderMe).bindData(listChat?.get(position))
            (holder as ViewHolderMe).itemView.setOnClickListener { callback?.invoke(position) }
        } else {
            (holder as ViewHolderNotMe).bindData(listChat?.get(position), friendListModel)
            (holder as ViewHolderNotMe).itemView.setOnClickListener { callback?.invoke(position) }
        }
    }

    class ViewHolderMe(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(chatModel: ChatModel? = null) {
            itemView.tvTimeMe.text = TimeUtil.convertTimeToLocalTime(chatModel?.time, false)
            if (chatModel?.type!!.toInt() == 1) {
                itemView.tvContentMe.visibility = View.VISIBLE
                itemView.img_send_from.visibility = View.GONE
                itemView.tvContentMe.text = chatModel?.content
            } else {
                itemView.tvContentMe.visibility = View.GONE
                itemView.img_send_from.visibility = View.VISIBLE
                Glide.with(BalloonchatApplication.context!!).load(chatModel.content)
                    .into(itemView.img_send_from)

            }
        }
    }

    class ViewHolderNotMe(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(
            chatModel: ChatModel? = null,
            friendListModel: FriendListModel
        ) {
            itemView.tvTimeNotme.text = TimeUtil.convertTimeToLocalTime(chatModel?.time, true)
            if (chatModel?.type!!.toInt() == 1) {
                itemView.tvContent.visibility = View.VISIBLE
                itemView.img_send_to.visibility = View.GONE
                itemView.tvContent.text = chatModel?.content
            } else {
                itemView.tvContent.visibility = View.INVISIBLE
                itemView.img_send_to.visibility = View.VISIBLE
                Glide.with(BalloonchatApplication.context!!).load(chatModel.content)
                    .into(itemView.img_send_to)
            }

            Glide.with(BalloonchatApplication.context!!)
                .applyDefaultRequestOptions(
                    RequestOptions()
                        .placeholder(R.drawable.ic_avatar_default2)
                        .error(R.drawable.ic_avatar_default2)
                )
                .load(friendListModel.avatar)
                .into(itemView.img_avatar)

            itemView.img_avatar.setOnClickListener {
                var bundle = Bundle()
                bundle.putParcelable(Const.KEY_FRIEND, friendListModel)
                (itemView.context as? BaseActivity)?.goToActivity(
                    PartnerActivity::class.java,
                    bundle
                )
            }
        }
    }
}