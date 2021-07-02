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


class AuthApi {

    fun register(
        nickname: String,
        gender: Int,
        intro: String,
        age: String,
        prefecture: String,
        nationality: String,
        image: MultipartBody.Part,
        onResponse: ((ResponseApi) -> Unit)?
    ) {
        val nickname = RequestBody.create(MediaType.parse("multipart/form-data"), nickname)
        val gender = RequestBody.create(MediaType.parse("multipart/form-data"), gender.toString())
        val intro = RequestBody.create(MediaType.parse("multipart/form-data"), intro)
        val age = RequestBody.create(MediaType.parse("multipart/form-data"), age)
        val prefecture = RequestBody.create(MediaType.parse("multipart/form-data"), prefecture)
        val nationality = RequestBody.create(MediaType.parse("multipart/form-data"), nationality)

        if (image.body().contentLength() > 0) {
            var loginApi = RetrofitSingleton.getInstance().create(AuthApiInterface::class.java)
                ?.register(nickname, gender, intro, age, nationality, image)
            loginApi?.enqueue(object : OnApiResponseListener {
                override fun onCompleted(response: ResponseApi) {
                    onResponse?.invoke(response)
                }
            })
        } else {
            var loginApi = RetrofitSingleton.getInstance().create(AuthApiInterface::class.java)
                ?.register(nickname, gender, intro, age, nationality)
            loginApi?.enqueue(object : OnApiResponseListener {
                override fun onCompleted(response: ResponseApi) {
                    onResponse?.invoke(response)
                }
            })
        }
    }

    fun updateProfile(
        nickname: String,
        gender: Int,
        intro: String,
        age: String,
        prefecture: String,
        nationality: String,
        marriage: String,
        purpose: String,
        image: MultipartBody.Part,
        onResponse: ((ResponseApi) -> Unit)?
    ) {
        val authorization = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            SharePreferenceUtils.getInstances().getString(Const.AUTH)
        )
        val nickname = RequestBody.create(MediaType.parse("multipart/form-data"), nickname)
        val intro = RequestBody.create(MediaType.parse("multipart/form-data"), intro)
        val gender = RequestBody.create(MediaType.parse("multipart/form-data"), gender.toString())
        val age = RequestBody.create(MediaType.parse("multipart/form-data"), age)
        val prefecture = RequestBody.create(MediaType.parse("multipart/form-data"), prefecture)
        val nationality = RequestBody.create(MediaType.parse("multipart/form-data"), nationality)
        val marriage = RequestBody.create(MediaType.parse("multipart/form-data"), marriage)
        val purpose = RequestBody.create(MediaType.parse("multipart/form-data"), purpose)

        if (image.body().contentLength() > 0) {
            var loginApi = RetrofitSingleton.getInstance().create(AuthApiInterface::class.java)
                ?.updateProfile(
                    authorization,
                    nickname,
                    gender,
                    intro,
                    age,
                    prefecture,
                    nationality,
                    marriage,
                    purpose,
                    image
                )
            loginApi?.enqueue(object : OnApiResponseListener {
                override fun onCompleted(response: ResponseApi) {
                    onResponse?.invoke(response)
                }
            })
        } else {
            System.out.println("DIEP XXX prefecture " + prefecture)
            var loginApi = RetrofitSingleton.getInstance().create(AuthApiInterface::class.java)
                ?.updateProfile(
                    authorization,
                    nickname,
                    gender,
                    intro,
                    age,
                    prefecture,
                    nationality,
                    marriage,
                    purpose
                )
            loginApi?.enqueue(object : OnApiResponseListener {
                override fun onCompleted(response: ResponseApi) {
                    onResponse?.invoke(response)
                }
            })
        }

    }

    fun getMyProfile(authorization: String, onResponse: ((ResponseApi) -> Unit)?) {
        var loginApi = RetrofitSingleton.getInstance().create(AuthApiInterface::class.java)
            ?.getMyProfile(authorization)
        loginApi?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }

    fun getUserProfile(id: Int, onResponse: ((ResponseApi) -> Unit)?) {
        var loginApi =
            RetrofitSingleton.getInstance().create(AuthApiInterface::class.java)?.getUserProfile(
                SharePreferenceUtils.getInstances().getString(Const.AUTH), id
            )
        loginApi?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }

    fun getListFriends(onResponse: ((ResponseApi) -> Unit)?) {
        var loginApi = RetrofitSingleton.getInstance().create(AuthApiInterface::class.java)
            ?.friendList(
                SharePreferenceUtils.getInstances().getString(
                    Const.AUTH
                )
            )
        loginApi?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }

    fun addFriend(id: Int, onResponse: ((ResponseApi) -> Unit)?) {
        var loginApi = RetrofitSingleton.getInstance().create(AuthApiInterface::class.java)
            ?.addFriend(SharePreferenceUtils.getInstances().getString(Const.AUTH), id)
        loginApi?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }

    fun unFriend(id: Int, onResponse: ((ResponseApi) -> Unit)?) {
        var loginApi = RetrofitSingleton.getInstance().create(AuthApiInterface::class.java)
            ?.unFriend(SharePreferenceUtils.getInstances().getString(Const.AUTH), id)
        loginApi?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }

    fun report(id: Int, onResponse: ((ResponseApi) -> Unit)?) {
        var loginApi = RetrofitSingleton.getInstance().create(AuthApiInterface::class.java)
            ?.report(SharePreferenceUtils.getInstances().getString(Const.AUTH), id)
        loginApi?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }

    fun blockFriend(id: Int, onResponse: ((ResponseApi) -> Unit)?) {
        var loginApi = RetrofitSingleton.getInstance().create(AuthApiInterface::class.java)
            ?.blockFriend(
                SharePreferenceUtils.getInstances().getString(
                    Const.AUTH
                ), id
            )
        loginApi?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }

    fun unBlockFriend(id: Int, onResponse: ((ResponseApi) -> Unit)?) {
        var loginApi = RetrofitSingleton.getInstance().create(AuthApiInterface::class.java)
            ?.unBlockFriend(
                SharePreferenceUtils.getInstances().getString(
                    Const.AUTH
                ), id
            )
        loginApi?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }

    fun disableUser(onResponse: ((ResponseApi) -> Unit)?) {
        var disableUserApi = RetrofitSingleton.getInstance().create(AuthApiInterface::class.java)
            ?.disableUser(
                SharePreferenceUtils.getInstances().getString(
                    Const.AUTH
                )
            )
        disableUserApi?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }

    fun getNofitication(flg_push: Int, onResponse: ((ResponseApi) -> Unit)?) {
        var loginApi = RetrofitSingleton.getInstance().create(AuthApiInterface::class.java)
            ?.setting(SharePreferenceUtils.getInstances().getString(Const.AUTH), flg_push)
        loginApi?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }


}