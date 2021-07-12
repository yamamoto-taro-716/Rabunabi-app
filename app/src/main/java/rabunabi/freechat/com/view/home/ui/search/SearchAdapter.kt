package rabunabi.freechat.com.view.home.ui.search

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import rabunabi.freechat.com.R
import rabunabi.freechat.com.model.friends.FriendListModel
import rabunabi.freechat.com.utils.TimeUtil
import kotlinx.android.synthetic.main.item_search_user.view.*
import kotlin.collections.ArrayList


class SearchAdapter(
    var context: Context?,
    var listUser: ArrayList<FriendListModel>?,
    var callback: ((Int?) -> Unit)? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var userModel = listUser?.get(position)
        val viewholder = holder as ViewHolder
        viewholder?.bindData(userModel)
        viewholder?.itemView?.setOnClickListener { callback?.invoke(position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_search_user,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listUser?.size ?: 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(userObj: FriendListModel?) {
//            if (TextUtils.isEmpty(userObj?.avatar)) {
//                itemView.imageViewAvatar.setImageResource(
//                    when (userObj?.gender) {
//                        1 -> R.drawable.ic_gender_male
//                        else -> {
//                            R.drawable.ic_gender_female
//                        }
//                    }
//                )
//            } else {

            if (userObj!!.isSelected == 1) {
                itemView.ivOverlay.visibility = View.VISIBLE
            } else {
                itemView.ivOverlay.visibility = View.INVISIBLE
            }
            // avatar_status = 1 là đã được confirm bởi admin
            if (userObj?.avatar_status == 1 && !TextUtils.isEmpty(userObj?.avatar)) {
                Glide.with(itemView.context).load(userObj?.avatar)
                    .into(itemView.imageViewAvatar)
            } else {
                Glide.with(itemView.context).load(R.drawable.ic_avatar_default2)
                    .into(itemView.imageViewAvatar)
            }
            itemView.tvIntro.text = userObj?.intro
            itemView.tvNickName.text = userObj?.nickname
            itemView.tvAge.text =
                userObj?.age.toString() + itemView.context.resources.getString(R.string.age2)
            itemView.tvProvince.text = userObj?.prefecture
            itemView.tvOnlineTime.text =
                TimeUtil.convertISOToTime(itemView.context, userObj?.login_time)
            //System.out.println("DIEP userObj?.login_time "+itemView.tvOnlineTime.text)

            when (userObj?.gender) {
                1 -> { // nam
                    itemView.tvNickName.setTextColor(itemView.tvNickName.context.resources.getColor(R.color.nam));
                    Glide.with(itemView.context).load(R.drawable.ic_nam)
                        .into(itemView.ivSex)
                    // itemView.tvNickName.setTextColor(itemView.context.resources.getColor(R.color.black))
                    // itemView.tvAge.setTextColor(itemView.context.resources.getColor(R.color.black))
                    //itemView.tvProvince.setTextColor(itemView.context.resources.getColor(R.color.black))
                }
                else -> { // nu
                    itemView.tvNickName.setTextColor(itemView.tvNickName.context.resources.getColor(R.color.nu));
                    Glide.with(itemView.context).load(R.drawable.ic_nu)
                        .into(itemView.ivSex)
                    //itemView.tvNickName.setTextColor(itemView.context.resources.getColor(R.color.red))
                    //itemView.tvAge.setTextColor(itemView.context.resources.getColor(R.color.red))
                    //itemView.tvProvince.setTextColor(itemView.context.resources.getColor(R.color.red))
                }
            }
        }
    }
}

