package com.rabunabi.friends.api

import com.rabunabi.friends.api.base.OnApiResponseListener
import com.rabunabi.friends.api.base.ResponseApi
import com.rabunabi.friends.api.base.enqueue
import com.rabunabi.friends.common.Const
import com.rabunabi.friends.utils.SharePreferenceUtils
import com.rabunabi.friends.utils.Utils
import org.json.JSONArray
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class GoogleTranslateApi {
    fun translate(text: String, lang: String?, onResponse: ((ResponseApi) -> Unit)?) {
        var langCode: String? = lang
        var countryCode = SharePreferenceUtils.getInstances().getString(
            Const.COUNTRY_CODE)
        if (!countryCode.equals("00")) {
            val data: HashMap<String, String> = HashMap()
            var jsonArray = JSONArray(Utils.readJSONFromAsset("countries.json"))
            for (i in 0 until jsonArray?.length()) {
                data.put(
                    jsonArray?.getJSONObject(i)?.optString("code")!!,
                    jsonArray?.getJSONObject(i)?.optString("lang")!!
                )
            }
            langCode = data.get(countryCode)?.toLowerCase()
        }
        val APIKey = "trnsl.1.1.20181204T151148Z.c9ac0cb6993225c4.9b50bb7c0bf63f97252efdd89f3c79a467d11984"
        val query = Retrofit.Builder().baseUrl("https://translate.yandex.net/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val call = query.create(GoogleTranslateApiInterface::class.java)
            ?.getTranslation(APIKey, text, langCode)
        call?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }
}