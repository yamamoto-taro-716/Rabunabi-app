package rabunabi.freechat.com.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import rabunabi.freechat.com.R
import rabunabi.freechat.com.common.extensions.from
import rabunabi.freechat.com.model.friends.FriendListModel
import rabunabi.freechat.com.utils.TimeUtil
import kotlinx.android.synthetic.main.item_listfriend.view.*

class ListfriendAdapter(
    var listfriend: ArrayList<FriendListModel>?, var callbackAvatar: ((Int?) -> Unit)? = null,
    var callback: ((Int?) -> Unit)? = null
) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var listfriendModel = listfriend?.get(position)
        val viewholder = holder as ViewHolder
        viewholder?.bindData(listfriendModel)
        viewholder?.itemView.profile_image_left.setOnClickListener { callbackAvatar?.invoke(listfriendModel?.id) }
        viewholder?.itemView?.setOnClickListener { callback?.invoke(position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_listfriend,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listfriend?.size ?: 0
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(listfriend: FriendListModel?) {
            itemView.profile_image_left.from(listfriend?.avatar, R.drawable.ic_avatar_default)
            itemView.img_ensign.setImageResource(
                when (listfriend?.nationality) {
                    "VN" -> R.drawable.flag_vn
                    "JP" -> R.drawable.flag_jp
                    "US" -> R.drawable.flag_an
                    else -> {
                        R.drawable.flag_vn
                    }
                }
            )
            itemView.tv_country.text = listfriend?.nationality
           // Log.d("AAAAA",listfriend?.gender.toString())
            itemView.img_gender.setImageResource(
                when (listfriend?.gender) {
                    1 -> R.drawable.ic_gender_male
                    else -> {
                        R.drawable.ic_gender_female
                    }
                }
            )
            itemView.tv_username.text = listfriend?.nickname
            itemView.tv_introduce.text = listfriend?.message
            itemView.tv_numberMs1.text = TimeUtil.convertTimeToLocalTime(listfriend?.created,false)
        }
    }

}