package com.rabunabi.friends.model.friends

import org.json.JSONObject

data class FriendListRequestModel(
    var id: Int?,
    var avatar: String?,
    var nickname: String?,
    var gender: Int?,
    var nationality: String?,
    var message: String?,
    var revision: String?,
    var created: String?
) {
    companion object {
        fun initFrom(jsonObject: JSONObject?): FriendListRequestModel? {
            jsonObject?.let {
                var id = jsonObject.optInt("id")
                var avatar = jsonObject.optString("avatar")
                var nickname = jsonObject.optString("nickname")
                var gender = jsonObject.optInt("gender")
                var nationality = jsonObject.optString("nationality")
                var message = jsonObject.optString("message")
                var revision = jsonObject.optString("revision")
                var created = jsonObject.optString("created")
                return FriendListRequestModel(
                    id,
                    avatar,
                    nickname,
                    gender,
                    nationality,
                    message,
                    revision,
                    created
                )
            }
            return null
        }
    }
}