package com.rabunabi.friends.view.home.ui.profile.mypoint

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.*
import kotlinx.android.synthetic.main.fragment_my_point.*
import kotlinx.android.synthetic.main.fragment_my_point.pointValue
import kotlinx.android.synthetic.main.fragment_my_point.rvPay
import kotlinx.android.synthetic.main.fragment_point.*
import kotlinx.android.synthetic.main.fragment_profile_container.*
import kotlinx.android.synthetic.main.toolbar.*
import com.rabunabi.friends.R
import com.rabunabi.friends.utils.SharePreferenceUtils
import com.rabunabi.friends.view.base.BaseContainerFragment
import com.rabunabi.friends.view.base.BaseFragment
import com.rabunabi.friends.view.home.HomeActivity
import com.rabunabi.friends.view.home.ui.chat.ChatActivity
import com.rabunabi.friends.view.home.ui.profile.ProfileContainerFragment
import java.lang.StringBuilder

class PointFragment : BaseFragment(), PurchasesUpdatedListener {

    var billingClient: BillingClient? = null
    var listener: ConsumeResponseListener? = null

    override fun getIdContainer(): Int {
        return R.layout.fragment_point
    }

    override fun initView() {
        initToolbar()
        setupBillingClient()
        setupList()
        setOnclick()
    }
    private fun setOnclick() {
        btnPointTable.setOnClickListener{
            if(parentFragment == null) {
                goToActivity(PointTableActivity::class.java)

            }else {
                (parentFragment as ProfileContainerFragment?)?.addChild(PointTableFragment())
            }
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PointFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun initToolbar() {
        rl_action_left.visibility = View.VISIBLE
        imv_action_left.setImageResource(R.drawable.ic_icon_back_p)

        img_title.visibility = View.INVISIBLE
        tvTitle.setText("ポイント購入")
        tvTitle.visibility = View.VISIBLE
        rl_action_right.visibility = View.INVISIBLE
        rl_action_left.setOnClickListener {
            (parentFragment as ProfileContainerFragment?)?.popFragment()
        }
        //imv_action_left.visibility = View.INVISIBLE
    }

    private fun setupBillingClient() {
        listener = ConsumeResponseListener { billingResult, s ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Toast.makeText(context, "Consume OK", Toast.LENGTH_SHORT).show()
            }
        }

        billingClient = context?.let { BillingClientSetup.getInstance(it, this) }

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(p0: BillingResult) {
                if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(context, "Success to connect billing", Toast.LENGTH_SHORT).show()
                    var purchase =
                        billingClient!!.queryPurchases(BillingClient.SkuType.INAPP).purchasesList
                    handleItemAlreadyPurchase(purchase)

                } else {
                    Toast.makeText(context, "Error code" + p0.responseCode, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onBillingServiceDisconnected() {
                Toast.makeText(
                    context,
                    "You are disconect from Billing Service",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
    override fun onResume() {
        super.onResume()
        initData()

    }
    private fun initData() {
        var point = SharePreferenceUtils.getInstances().getPointInfo();
        pointValue.text = point!!.points.toString()
    }
    private fun handleItemAlreadyPurchase(purchase: List<Purchase>?) {
        var purchaseItem = StringBuilder()
        purchase?.forEach {
            if (it.sku == "POINT_50") {
                var consumeParams =
                    ConsumeParams.newBuilder().setPurchaseToken(it.purchaseToken).build()

                listener?.let { it1 -> billingClient?.consumeAsync(consumeParams, it1) }
            }
            purchaseItem.append("\n" + it.sku + "\n")
        }
        Log.e("P_Point", purchaseItem.toString())
    }

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
        TODO("Not yet implemented")
    }

    private fun setupList() {
        var pointInfo = SharePreferenceUtils.getInstances().getPointInfo()
        pointValue.text = pointInfo!!.points.toString()
        var layoutManager = LinearLayoutManager(context)
        rvPay.layoutManager = layoutManager

        //load Data Point
        if (billingClient!!.isReady) {
            var params =
                SkuDetailsParams.newBuilder().setSkusList(mutableListOf("POINT_50", "POINT_100"))
                    .setType(BillingClient.SkuType.INAPP)
                    .build()

            billingClient?.querySkuDetailsAsync(params, object : SkuDetailsResponseListener {
                override fun onSkuDetailsResponse(p0: BillingResult, p1: MutableList<SkuDetails>?) {
                    if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                        loadProductToRecyclerView(p1)
                    } else {
                        Toast.makeText(
                            context,
                            "Error code: " + p0.responseCode,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }

    }

    private fun loadProductToRecyclerView(p1: MutableList<SkuDetails>?) {
        var adapter = activity?.let { PointAdapter(p1, billingClient, it) }
        rvPay.adapter = adapter
    }
}