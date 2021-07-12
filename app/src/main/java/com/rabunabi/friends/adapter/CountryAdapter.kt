package com.rabunabi.friends.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rabunabi.friends.BalloonchatApplication.Companion.context
import com.rabunabi.friends.R
import com.rabunabi.friends.model.CountryModel
import kotlinx.android.synthetic.main.item_countries.view.*


class CountryAdapter(
    var countriesList: ArrayList<CountryModel>?,
    var callback: ((CountryModel?) -> Unit)? = null
) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var countriesModel = countriesList?.get(position)
        val viewholder = holder as ViewHolder
        viewholder?.bindData(countriesModel)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_countries,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return countriesList?.size ?: 0
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(countriesList: CountryModel?) {
            val resources = context?.getResources()
//            val resourceId = resources?.getIdentifier(
//                "flag_${countriesList?.code?.toLowerCase()}",
//                "drawable",
//                context?.getPackageName()
//            )
//            resourceId?.let {
//                itemView.image_code.setImageDrawable(resources?.getDrawable(resourceId))
//            }
            itemView.tv_name.text = countriesList?.name
            itemView?.setOnClickListener { callback?.invoke(countriesList) }
        }
    }
}