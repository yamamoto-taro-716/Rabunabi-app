package com.rabunabi.friends.view.home.ui.profile.mypoint

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener

class BillingClientSetup {
    companion object {
        private val instance: BillingClient? = null

        fun getInstance(context: Context, listener: PurchasesUpdatedListener): BillingClient? {
            return instance ?: setupBillingClient(context, listener)
        }

        private fun setupBillingClient(
            context: Context,
            listener: PurchasesUpdatedListener
        ): BillingClient? {
            return BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener(listener)
                .build()
        }
    }
}