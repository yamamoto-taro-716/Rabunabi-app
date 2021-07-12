package rabunabi.freechat.com.adapter

import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import rabunabi.freechat.com.R
import rabunabi.freechat.com.common.extensions.from
import rabunabi.freechat.com.model.friends.FriendListModel
import rabunabi.freechat.com.utils.TimeUtil
import kotlinx.android.synthetic.main.item_message.view.*

class MessageAdapter(
    var listMessage: MutableList<FriendListModel>? = null,
    var callback: ((Int?) -> Unit)? = null,
    var callbackAvatar: ((Int?) -> Unit)? = null, var callbackDelete: ((Int?) -> Unit)? = null
) : RecyclerSwipeAdapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_message,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listMessage?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var messageModel = listMessage?.get(position)
        mItemManger.bindView(holder.itemView, position)
        var hd = holder as ViewHolder
        hd.bindData(messageModel)
        hd.itemView.relay_left.setOnClickListener { callback?.invoke(position) }
        hd.itemView.swipe.showMode = SwipeLayout.ShowMode.PullOut
        hd.itemView.swipe.addDrag(SwipeLayout.DragEdge.Right, hd.itemView.bottom_wrapper)
        //  hd.itemView.img_avatar_local.setOnClickListener { callbackAvatar?.invoke(position) }
        hd.itemView.bottom_wrapper.setOnClickListener { callbackDelete?.invoke(position) }
        hd.itemView.swipe.isClickToClose = true

        hd.itemView.swipe.addSwipeListener(object : SwipeLayout.SwipeListener {
            override fun onOpen(layout: SwipeLayout?) {

            }

            override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {

            }

            override fun onStartOpen(layout: SwipeLayout?) {
            }

            override fun onStartClose(layout: SwipeLayout?) {

            }

            override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
            }

            override fun onClose(layout: SwipeLayout?) {
            }

        }
        )
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }

    fun remove(position: Int) {
        listMessage?.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listMessage!!.size)
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(friendListModel: FriendListModel?) {
            itemView.tv_country_local.text = friendListModel?.nationality
            itemView.img_flag.setImageResource(
                when (friendListModel?.nationality) {
                    "VN" -> R.drawable.flag_vn
                    "JP" -> R.drawable.flag_jp
                    "US" -> R.drawable.flag_an
                    else -> {
                        R.drawable.flag_vn
                    }
                }
            )
            if (!TextUtils.isEmpty(friendListModel?.created)) {
                itemView.tv_time.text =
                    TimeUtil.convertISOToTime(itemView.context, friendListModel?.created)
            } else {
                itemView.tv_time.text = ""
            }
            itemView.img_gender.visibility = View.INVISIBLE
            System.out.println("diep mess adapter messageModel?.totalUnread : " + friendListModel?.totalUnread!! + " time: " + friendListModel?.created)
            if (friendListModel?.totalUnread!! > 0) {
                itemView.tv_count_message.visibility = View.VISIBLE
                if (friendListModel?.totalUnread!! > 0) {
                    itemView.tv_count_message.text = friendListModel?.totalUnread.toString()
                } else {
                    itemView.tv_count_message.text = friendListModel?.totalUnread.toString()
                }
            } else {
                itemView.tv_count_message.visibility = View.INVISIBLE
            }


            //itemView.img_avatar_local.from(friendListModel?.avatar, R.drawable.ic_avatar_default2)
            itemView.tv_name.text = friendListModel?.nickname
            itemView.tv_message.text = friendListModel?.message

            itemView.img_avatar.from(friendListModel?.avatar, R.drawable.ic_avatar_default2)
        }
    }
}