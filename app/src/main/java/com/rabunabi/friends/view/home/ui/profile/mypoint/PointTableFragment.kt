package com.rabunabi.friends.view.home.ui.profile.mypoint

import android.graphics.Color
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
import kotlinx.android.synthetic.main.fragment_point_table.*
import kotlinx.android.synthetic.main.fragment_profile_container.*
import kotlinx.android.synthetic.main.toolbar.*
import com.rabunabi.friends.R
import com.rabunabi.friends.utils.SharePreferenceUtils
import com.rabunabi.friends.view.base.BaseFragment
import com.rabunabi.friends.view.home.ui.profile.ProfileContainerFragment
import java.lang.StringBuilder

class PointTableFragment : BaseFragment() {

    var billingClient: BillingClient? = null
    var listener: ConsumeResponseListener? = null

    override fun getIdContainer(): Int {
        return R.layout.fragment_point_table
    }

    override fun initView() {
        initToolbar()


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
        tvTitle.setText("ポイント表")
        tvTitle.visibility = View.VISIBLE
        rl_action_right.visibility = View.INVISIBLE
        rl_action_left.setOnClickListener {
            (parentFragment as ProfileContainerFragment?)?.popFragment()
        }
        //imv_action_left.visibility = View.INVISIBLE
    }


    override fun onResume() {
        super.onResume()
        initData()

    }
    private fun initData() {
        var point = SharePreferenceUtils.getInstances().getPointInfo();
        if (point!!.readMessage!=0) {
            readMessage.text = point!!.readMessage.toString() + " pt"
            readMessage.setTextColor(Color.BLACK)
        }
        if (point!!.sendMessage!=0) {
            sendMessage.text = point!!.sendMessage.toString() + " pt"
            sendMessage.setTextColor(Color.BLACK)
        }
        if (point!!.sendImage!=0) {
            sendImage.text = point!!.sendImage.toString() + " pt"
            sendImage.setTextColor(Color.BLACK)
        }

    }


}