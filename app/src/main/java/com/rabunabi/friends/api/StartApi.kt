package com.rabunabi.friends.api

import com.rabunabi.friends.api.base.OnApiResponseListener
import com.rabunabi.friends.api.base.ResponseApi
import com.rabunabi.friends.api.base.RetrofitSingleton
import com.rabunabi.friends.api.base.enqueue
import com.rabunabi.friends.common.Const
import com.rabunabi.friends.utils.SharePreferenceUtils


class StartApi {

    fun startUp(onResponse: ((ResponseApi) -> Unit)?) {
        var startApi = RetrofitSingleton.getInstance().create(StartApiInterface::class.java)?.startUp()
        startApi?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }

    fun startSync(token:String,onResponse: ((ResponseApi) -> Unit)?) {
        var startApi = RetrofitSingleton.getInstance().create(StartApiInterface::class.java)?.sync(token)
        startApi?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }

    fun getFriendPendding(onResponse: ((ResponseApi) -> Unit)?) {
        var startApi = RetrofitSingleton.getInstance().create(StartApiInterface::class.java)?.getFriendPendding(
            SharePreferenceUtils.getInstances().getString(Const.AUTH))
        startApi?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }
}