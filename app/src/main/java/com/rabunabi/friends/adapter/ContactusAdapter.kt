package com.rabunabi.friends.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rabunabi.friends.R
import com.rabunabi.friends.model.ContactusModel
import kotlinx.android.synthetic.main.item_bubble_me.view.*
import kotlinx.android.synthetic.main.item_bubble_not_me.view.*

class ContactusAdapter(
    var listContent: MutableList<ContactusModel>? = null,
    var clickBookSchedule: (() -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TYPE_ME = 1
    val TYPE_NOT_ME = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ME) {
            return ViewHolderMe(
                LayoutInflater.from(
                    parent.context
                ).inflate(R.layout.item_bubble_me, parent, false)
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

    override fun getItemCount(): Int {
        return listContent?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (listContent?.get(position)?.send_id != 0) { // = 0 admin
            (holder as ViewHolderMe).bindData(listContent?.get(position))
        } else {
            (holder as ViewHolderNotMe).bindData(listContent?.get(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (listContent?.get(position)?.send_id != 0) {
            return TYPE_ME
        }
        return TYPE_NOT_ME
    }

    class ViewHolderMe(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(contactusModel: ContactusModel? = null) {
            itemView.tvContentMe.text = contactusModel?.content
            itemView.tvTimeMe.text = contactusModel?.time
        }
    }

    class ViewHolderNotMe(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(contactusModel: ContactusModel? = null) {
            itemView.tvContent.text = contactusModel?.content
            itemView.tvTimeNotme.text = contactusModel?.time
            itemView.img_avatar.setBackgroundResource(R.drawable.ic_launcher)
        }
    }

}