package rabunabi.freechat.com.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface StartApiInterface {

    //done
    @GET("super/start-up")
    fun startUp(): Call<ResponseBody>

    //done
    @POST("super/sync-push-token")
    @FormUrlEncoded
    fun sync(@Field("push_token") token: String): Call<ResponseBody>

    //done
    @GET("super/get-friend-pending-count")
    fun getFriendPendding(@Query("Authorization") authorization: String?): Call<ResponseBody>
}