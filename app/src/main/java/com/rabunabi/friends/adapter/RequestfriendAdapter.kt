package com.rabunabi.friends.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rabunabi.friends.R
import com.rabunabi.friends.common.extensions.from
import com.rabunabi.friends.model.friends.FriendListRequestModel
import com.rabunabi.friends.utils.TimeUtil
import kotlinx.android.synthetic.main.item_requestfriend.view.*


class RequestfriendAdapter(
    var requestfriend: ArrayList<FriendListRequestModel>?,
    var clickCancel: ((Int?) -> Unit)? = null, var clickOk: ((Int?) -> Unit)? = null
) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var friend = requestfriend?.get(position)
        val viewholder = holder as ViewHolder
        viewholder?.bindData(friend)
        viewholder?.itemView?.btn_OK?.setOnClickListener { clickOk?.invoke(friend?.id) }
        viewholder?.itemView?.btn_Cancel?.setOnClickListener { clickCancel?.invoke(friend?.id) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.item_requestfriend, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return requestfriend?.size ?: 0
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(requestfriend: FriendListRequestModel?) {
            itemView.profile_image.from(requestfriend?.avatar)
            itemView.img_ensign.setImageResource(
                when (requestfriend?.nationality) {
                    "VN" -> R.drawable.flag_vn
                    "JP" -> R.drawable.flag_jp
                    "US" -> R.drawable.flag_an
                    else -> {
                        R.drawable.flag_vn
                    }
                }
            )
            itemView.tv_country.text = requestfriend?.nationality
            itemView.img_gender.setImageResource(
                when (requestfriend?.gender) {
                    1 -> R.drawable.ic_gender_male
                    else -> {
                        R.drawable.ic_gender_female
                    }
                }
            )
            itemView.tv_username.text = requestfriend?.nickname
            itemView.tv_introduce.text = requestfriend?.message
            itemView.tv_numberMs.text = TimeUtil.convertTimeToLocalTime(requestfriend?.created,false)
        }
    }

}