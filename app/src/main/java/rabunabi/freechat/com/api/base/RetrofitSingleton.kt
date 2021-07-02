package rabunabi.freechat.com.api.base

import android.util.Log
import rabunabi.freechat.com.BalloonchatApplication
import rabunabi.freechat.com.BuildConfig
import rabunabi.freechat.com.common.Const
import rabunabi.freechat.com.common.extensions.encodeSH256
import rabunabi.freechat.com.utils.NetworkUtils
import rabunabi.freechat.com.utils.SharePreferenceUtils
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


class RetrofitSingleton {
    var retrofit: Retrofit? = null

    companion object {
        private var retrofitSingleton: RetrofitSingleton? = null
        fun getInstance(isVerifyAccount: Boolean = false): RetrofitSingleton {
            if (isVerifyAccount) {
                return RetrofitSingleton(isVerifyAccount)
            } else {
                return retrofitSingleton
                    ?: synchronized(this) {
                        RetrofitSingleton(isVerifyAccount)
                    }
            }
        }
    }

    private constructor(isVerifyAccount: Boolean = false) {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        //////////////

        var builder = OkHttpClient.Builder()
        builder.addInterceptor(logging);
        builder.readTimeout(30, TimeUnit.SECONDS)
        builder.writeTimeout(30, TimeUnit.SECONDS)
        builder.connectTimeout(30, TimeUnit.SECONDS)
        builder.addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()

            var uuid = SharePreferenceUtils.getInstances().getString(Const.DEVICE_ID)
            var userAgent = "android"
            var timeStamp = System.currentTimeMillis().toString()
            var version = BuildConfig.VERSION_NAME
            requestBuilder.addHeader("Uuid", uuid)
            requestBuilder.addHeader("User-Agent", userAgent)
            requestBuilder.addHeader("Timestamp", timeStamp)
            requestBuilder.addHeader("Version", version)

            var checkSum: String = ("BLC-PCM-2018" + uuid + userAgent + timeStamp).encodeSH256()

            Log.d("diep@@@ BLC-PCM-2018:",  "BLC-PCM-2018")
            Log.d("diep@@@@@ uuid: ",  uuid!! )
            Log.d("diep@@@@@ userAgent: ",  userAgent )
            Log.d("diep@@@@@ timeStamp",  timeStamp)

            Log.d("diep@@@@@ checksum", "checkSum: "+checkSum)
            requestBuilder.addHeader("Checksum", checkSum)
            if (!SharePreferenceUtils.getInstances().getString(Const.AUTH).equals("")) {
                requestBuilder.addHeader("Authorization", SharePreferenceUtils.getInstances().getString(Const.AUTH))
                Log.d("diep@@@ Authorization", SharePreferenceUtils.getInstances().getString(Const.AUTH)!!)
            }
            chain.proceed(requestBuilder.build())
        }
        retrofit = Retrofit.Builder()
            .baseUrl(Const.SERVER_VERIFY)
            .client(builder.build())
            .build()
    }

    fun <Entity> create(interfaceApiClass: Class<Entity>): Entity? {
        return this.retrofit?.create(interfaceApiClass)
    }
}


interface OnApiResponseListener {
    fun onCompleted(responseApi: ResponseApi)
}

fun Call<ResponseBody>.enqueue(onApiResponseListener: OnApiResponseListener?) {
    if (NetworkUtils.isNetworkConnected(BalloonchatApplication.context)) {
        this.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("diep 1call.request().url(): "+call.request().url())
                onApiResponseListener?.onCompleted(ResponseApi(t))
            }

            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                println("diep 2 call.request().url(): "+call.request().url())
                onApiResponseListener?.onCompleted(
                    ResponseApi(
                        response
                    )
                )
            }
        })
    } else {
        onApiResponseListener?.onCompleted(ResponseApi())
    }

}