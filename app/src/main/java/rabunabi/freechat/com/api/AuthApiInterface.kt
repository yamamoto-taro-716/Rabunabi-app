package rabunabi.freechat.com.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface AuthApiInterface {

    //done
    @Multipart
    @POST("accounts/register")
    fun register(
        @Part("nickname") nickname: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("intro") intro: RequestBody,
        @Part("age") age: RequestBody,
        @Part("nationality") nationality: RequestBody,
        @Part avatar: MultipartBody.Part
    ): Call<ResponseBody>

    //done
    @Multipart
    @POST("accounts/register")
    fun register(
        @Part("nickname") nickname: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("intro") intro: RequestBody,
        @Part("age") age: RequestBody,
        @Part("nationality") nationality: RequestBody
    ): Call<ResponseBody>

    //done
    @Multipart
    @POST("accounts/update")
    fun updateProfile(
        @Part("Authorization") authorization: RequestBody,
        @Part("nickname") nickname: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("intro") intro: RequestBody,
        @Part("age") age: RequestBody,
        @Part("prefecture") prefecture: RequestBody,
        @Part("nationality") nationality: RequestBody,
        @Part("marriage") marriage: RequestBody,
        @Part("purpose") purpose: RequestBody,
        @Part avatar: MultipartBody.Part
    ): Call<ResponseBody>

    //done
    @Multipart
    @POST("accounts/update")
    fun updateProfile(
        @Part("Authorization") authorization: RequestBody,
        @Part("nickname") nickname: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("intro") intro: RequestBody,
        @Part("age") age: RequestBody,
        @Part("prefecture") prefecture: RequestBody,
        @Part("nationality") nationality: RequestBody,
        @Part("marriage") marriage: RequestBody,
        @Part("purpose") purpose: RequestBody
    ): Call<ResponseBody>

    //done
    @GET("accounts/get")
    fun getMyProfile(@Query("Authorization") authorization: String?): Call<ResponseBody>


    //done
    @GET("accounts/detail")
    fun getUserProfile(
        @Query("Authorization") authorization: String?,
        @Query("id") accountId: Int?
    ): Call<ResponseBody>

    //done
    @GET("accounts/add-friend")
    fun addFriend(
        @Query("Authorization") authorization: String?,
        @Query("id") accountId: Int?
    ): Call<ResponseBody>

    //done
    @GET("accounts/un-friend")
    fun unFriend(
        @Query("Authorization") authorization: String?,
        @Query("id") accountId: Int?
    ): Call<ResponseBody>

    //done
    @GET("accounts/block")
    fun blockFriend(
        @Query("Authorization") authorization: String?,
        @Query("id") accountId: Int?
    ): Call<ResponseBody>

    //done
    @GET("accounts/un-block")
    fun unBlockFriend(
        @Query("Authorization") authorization: String?,
        @Query("id") accountId: Int?
    ): Call<ResponseBody>

    //done
    @GET("accounts/friend-list")
    fun friendList(@Query("Authorization") authorization: String?): Call<ResponseBody>

    //done
    @GET("accounts/report")
    fun report(
        @Query("Authorization") authorization: String?,
        @Query("id") accountId: Int?
    ): Call<ResponseBody>

    @GET("accounts/setting")
    fun setting(
        @Query("Authorization") authorization: String?,
        @Query("flg_push") accountId: Int?
    ): Call<ResponseBody>

    @GET("accounts/ ")
    fun setting(@Query("Authorization") authorization: String?): Call<ResponseBody>

    @POST("account/disable-user")
    fun disableUser(@Query("Authorization") authorization: String?): Call<ResponseBody>
}