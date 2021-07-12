package com.rabunabi.friends.model

import org.json.JSONObject

class CountryModel(
    var code: String,
    var name: String
) {

    companion object {
        fun initFrom(jsonObject: JSONObject?): CountryModel? {
            jsonObject?.let {
                var code = jsonObject.optString("code")
                var name = jsonObject.optString("name")
                return CountryModel(code, name)
            }
            return null
        }
    }
}