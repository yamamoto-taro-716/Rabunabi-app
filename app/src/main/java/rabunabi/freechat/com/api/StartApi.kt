package rabunabi.freechat.com.api

import rabunabi.freechat.com.api.base.OnApiResponseListener
import rabunabi.freechat.com.api.base.ResponseApi
import rabunabi.freechat.com.api.base.RetrofitSingleton
import rabunabi.freechat.com.api.base.enqueue
import rabunabi.freechat.com.common.Const
import rabunabi.freechat.com.utils.SharePreferenceUtils


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