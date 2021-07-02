package rabunabi.freechat.com.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface GoogleTranslateApiInterface {

    @POST("api/v1.5/tr.json/translate")
    @FormUrlEncoded
    fun getTranslation(
        @Field("key") APIKey: String,
        @Field("text") textToTranslate: String,
        @Field("lang") lang: String?
    ): Call<ResponseBody>
}