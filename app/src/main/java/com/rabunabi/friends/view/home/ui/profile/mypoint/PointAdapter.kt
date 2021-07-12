package com.rabunabi.friends.view.home.ui.profile.mypoint

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import com.rabunabi.friends.R

class PointAdapter(
    var skuDetaiList: MutableList<SkuDetails>?,
    var billingClient: BillingClient?,
    var activity1: Activity
) :
    RecyclerView.Adapter<PointAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_price, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvPoint.text = skuDetaiList?.get(position)?.title

        holder.setListener(object : CallbackOnClick {
            override fun onClick(view: View, position: Int) {
                var billingFlowParams =
                    skuDetaiList?.get(position)?.let {
                        BillingFlowParams.newBuilder().setSkuDetails(
                            it
                        ).build()
                    }

                var response =
                    billingFlowParams?.let {
                        billingClient?.launchBillingFlow(
                            activity1,
                            it
                        )?.responseCode
                    }

                when (response) {
//                    BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> Toast.makeText(
//                        activity1,
//                        "BILLING_UNAVAILABLE",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    BillingClient.BillingResponseCode.DEVELOPER_ERROR -> Toast.makeText(
//                        activity1,
//                        "DEVELOPER_ERROR",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    BillingClient.BillingResponseCode.ERROR -> Toast.makeText(
//                        activity1,
//                        "ERROR",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> Toast.makeText(
//                        activity1,
//                        "FEATURE_NOT_SUPPORTED",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> Toast.makeText(
//                        activity1,
//                        "ITEM_ALREADY_OWNED",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> Toast.makeText(
//                        activity1,
//                        "ITEM_NOT_OWNED",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> Toast.makeText(
//                        activity1,
//                        "ITEM_UNAVAILABLE",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    BillingClient.BillingResponseCode.OK -> Toast.makeText(
//                        activity1,
//                        "OK",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> Toast.makeText(
//                        activity1,
//                        "SERVICE_DISCONNECTED",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> Toast.makeText(
//                        activity1,
//                        "SERVICE_TIMEOUT",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> Toast.makeText(
//                        activity1,
//                        "SERVICE_UNAVAILABLE",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    BillingClient.BillingResponseCode.USER_CANCELED -> Toast.makeText(
//                        activity1,
//                        "USER_CANCELED",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return skuDetaiList!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvPoint: TextView = itemView.findViewById(R.id.tvPoint)
        lateinit var callbackOnClick: CallbackOnClick

        init {
            itemView.setOnClickListener {
                callbackOnClick.onClick(itemView, adapterPosition)
            }
        }

        fun setListener(listener: CallbackOnClick) {
            this.callbackOnClick = listener
        }
    }

    interface CallbackOnClick {
        fun onClick(view: View, position: Int)
    }
}