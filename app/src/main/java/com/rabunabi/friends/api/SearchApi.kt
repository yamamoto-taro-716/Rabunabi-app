package com.rabunabi.friends.api

import android.text.TextUtils
import com.rabunabi.friends.api.base.OnApiResponseListener
import com.rabunabi.friends.api.base.ResponseApi
import com.rabunabi.friends.api.base.RetrofitSingleton
import com.rabunabi.friends.api.base.enqueue
import com.rabunabi.friends.common.Const
import com.rabunabi.friends.utils.SharePreferenceUtils

class SearchApi {

    fun searchFriend(
        nickname: String,
        gender: String,
        hasAvatar: String,
        prefecture: String,
        ageFrom: String,
        ageTo: String,
        mucdich: String,
        honnhan: String,
        limit: String,
        offset: String,
        onResponse: ((ResponseApi) -> Unit)?
    ) {
        val data: HashMap<String, String> = HashMap()
        data.put(
            "Authorization",
            SharePreferenceUtils.getInstances().getString(Const.AUTH).toString()
        )
        if (!TextUtils.isEmpty(nickname)) {
            data.put("nickname", nickname)
        }
        if (!TextUtils.isEmpty(gender)) {
            data.put("gender", gender)
        }
        if (!TextUtils.isEmpty(hasAvatar)) {
            data.put("has_avatar", hasAvatar)
        }
        if (!TextUtils.isEmpty(prefecture)) {
            data.put("prefecture", prefecture)
        }
        if (!TextUtils.isEmpty(ageFrom)) {
            data.put("age_from", ageFrom)
        }
        if (!TextUtils.isEmpty(ageTo)) {
            data.put("age_to", ageTo)
        }
        if (!TextUtils.isEmpty(mucdich)) {
            data.put("purpose", mucdich)
        }
        if (!TextUtils.isEmpty(honnhan)) {
            data.put("marriage", honnhan)
        }
        if (!TextUtils.isEmpty(limit)) {
            data.put("limit", limit)
        }
        System.out.println("OK offsetaaaaaaaa "+offset)
        if (!TextUtils.isEmpty(offset) && !"0".equals(offset)) {
            data.put("last_id", offset)
        }
        var historyChat = RetrofitSingleton.getInstance().create(SearchApiInterface::class.java)
            ?.searchFriend(data)
        historyChat?.enqueue(object : OnApiResponseListener {
            override fun onCompleted(response: ResponseApi) {
                onResponse?.invoke(response)
            }
        })
    }
}