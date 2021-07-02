package rabunabi.freechat.com.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface SearchApiInterface {

    //done
    @GET("accounts/search")
    fun searchFriend(@QueryMap options: Map<String, String>): Call<ResponseBody>

}