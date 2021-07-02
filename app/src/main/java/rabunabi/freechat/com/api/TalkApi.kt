package rabunabi.freechat.com.api

import rabunabi.freechat.com.api.base.OnApiResponseListener
import rabunabi.freechat.com.api.base.ResponseApi
import rabunabi.freechat.com.api.base.RetrofitSingleton
import rabunabi.freechat.com.api.base.enqueue
import rabunabi.freechat.com.common.Const
import rabunabi.freechat.com.utils.SharePreferenceUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class TalkApi {
    fun sendRandom(message: String, nationality: String, onResponse: ((ResponseApi) -> Unit)?) {
        val data: HashMap<String, String> = HashMap()
        data.put(
            "Authorization",
            SharePreferenceUtils.getInstances().getString(Const.AUTH).toString()
        )
        data.put("message", message)
        data.put("nationality", nationality)
        data.put("gender", SharePreferenceUtils.getInstances().getUserInfo()!!.gender.toString())

        var sendRandom =
            RetrofitSingleton.getInstance().create(TalkApiInterface::class.java)?.sendRandom(data)
        sendRandom?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }

    fun sendRandomBalloonChat(
        sex: String,
        message: String,
        nationality: String,
        onResponse: ((ResponseApi) -> Unit)?
    ) {
        val data: HashMap<String, String> = HashMap()
        data.put(
            "Authorization",
            SharePreferenceUtils.getInstances().getString(Const.AUTH).toString()
        )
        data.put("message", message)
        data.put("nationality", nationality)
        data.put("gender", sex)

        val auP = RequestBody.create(MediaType.parse("multipart/form-data"), SharePreferenceUtils.getInstances().getString(
            Const.AUTH).toString())
        val sexP = RequestBody.create(MediaType.parse("multipart/form-data"), sex)
        val messageP = RequestBody.create(MediaType.parse("multipart/form-data"), message)
        val nationalityP = RequestBody.create(MediaType.parse("multipart/form-data"), nationality)

        var sendRandom =
            RetrofitSingleton.getInstance().create(TalkApiInterface::class.java)?.sendRandom2(
                auP,
                messageP,
                nationalityP,
                sexP
            )
        sendRandom?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }


    fun sendMessageImage(
        friendId: Int,
        image: MultipartBody.Part,
        onResponse: ((ResponseApi) -> Unit)?
    ) {
        val authorization = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            SharePreferenceUtils.getInstances().getString(Const.AUTH)
        )
        val friendId =
            RequestBody.create(MediaType.parse("multipart/form-data"), friendId.toString())
        var loginApi = RetrofitSingleton.getInstance().create(TalkApiInterface::class.java)
            ?.sendMessageImage(authorization, friendId, image)
        loginApi?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }


    fun joinChat(id: Int, onResponse: ((ResponseApi) -> Unit)?) {
        val data: HashMap<String, String> = HashMap()
        data.put(
            "Authorization",
            SharePreferenceUtils.getInstances().getString(Const.AUTH).toString()
        )
        data.put("id", id.toString())

        var joinChat =
            RetrofitSingleton.getInstance().create(TalkApiInterface::class.java)?.joinChat(data)
        joinChat?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }

    fun getHistoryChat(friendId: Int, onResponse: ((ResponseApi) -> Unit)?) {
        val data: HashMap<String, String> = HashMap()
        data.put(
            "Authorization",
            SharePreferenceUtils.getInstances().getString(Const.AUTH).toString()
        )
        data.put("friend_id", friendId.toString())
        var historyChat = RetrofitSingleton.getInstance().create(TalkApiInterface::class.java)
            ?.getHistoryMessage(data)
        historyChat?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }

        })
    }

    fun getContactMessage(onResponse: ((ResponseApi) -> Unit)?) {
        val data: HashMap<String, String> = HashMap()
        data.put(
            "Authorization",
            SharePreferenceUtils.getInstances().getString(Const.AUTH).toString()
        )
//        data.put("account_id", SharePreferenceUtils.getInstances().getUserInfo().id.toString())
        data.put("account_id", "0")
        data.put("friend_id", "0")
        var historyChat =
            RetrofitSingleton.getInstance().create(TalkApiInterface::class.java)
                ?.getContactMessage(data)
        historyChat?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }

        })
    }

}