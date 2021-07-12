package com.rabunabi.friends.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TalkApiInterface {

    //done
    // send quoc te
    @POST("talk/send-random")
    @FormUrlEncoded
    fun sendRandom(@FieldMap options: Map<String, String>): Call<ResponseBody>

    // send trong nuoc
    @Multipart
    @POST("talk/send-random")
    fun sendRandom2(
        @Part("Authorization") Authorization: RequestBody,
        @Part("message") message: RequestBody,
        @Part("nationality") nationality: RequestBody,
        @Part("gender") gender: RequestBody
    ): Call<ResponseBody>

//    @GET("talk/get-messages-history")
//    fun getHistoryMessage(@Query("Authorization") authorization: String?, @Query("friend_id") accountId: Int?): Call<ResponseBody>

    @GET("talk/get-messages-history")
    fun getHistoryMessage(@QueryMap options: Map<String, String>): Call<ResponseBody>

    //done
    @GET("talk/join-chat")
    fun joinChat(@QueryMap options: Map<String, String>): Call<ResponseBody>

    //done
    @Multipart
    @POST("talk/send-chat-image")
    fun sendMessageImage(
        @Part("Authorization") authorization: RequestBody,
        @Part("friend_id") friendId: RequestBody,
        @Part("sendImage") sendImage: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<ResponseBody>

    @GET("talk/get-contact-history")
    fun getContactMessage(@QueryMap options: Map<String, String>): Call<ResponseBody>

    @Multipart
    @POST("talk/decrease-point")
    fun decreasePoint(
        @Part("Authorization") authorization: RequestBody,
        @Part("point") point: RequestBody
    ):Call<ResponseBody>


}