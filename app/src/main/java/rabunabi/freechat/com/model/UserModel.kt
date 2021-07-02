package rabunabi.freechat.com.model

import org.json.JSONObject

data class UserModel(
    var id: Int?,
    var avatar: String?,
    var nickname: String?,
    var gender: Int?,
    var intro: String?,
    var nationality: String?,
    var age: Int?,
    var prefecture: String?,
    var maritalStatus: Int?,
    var state: State
) {
    companion object {
        fun initFrom(jsonObject: JSONObject?): UserModel? {
            jsonObject?.let {
                var id = jsonObject.optInt("id")
                var avatar = jsonObject.optString("avatar")
                var nickname = jsonObject.optString("nickname")
                var gender = jsonObject.optInt("gender")
                var intro = jsonObject.optString("intro")
                var nationality = jsonObject.optString("nationality")
                var age = jsonObject.optInt("age", 0)
                var prefecture = jsonObject.optString("prefecture")
                var maritalStatus = jsonObject.optInt("marital_status")
                var state = State.initFrom(
                    jsonObject?.optJSONObject("state")
                )
                return UserModel(
                    id,
                    avatar,
                    nickname,
                    gender,
                    intro,
                    nationality,
                    age,
                    prefecture,
                    maritalStatus,
                    state
                )
            }
            return null
        }
    }

    data class State(
        var is_blocked: Int?,
        var is_friend_blocked: Int?,
        var is_friend: Int?
    ) {
        companion object {
            fun initFrom(jsonObject: JSONObject?): State {
                var is_blocked = jsonObject?.optInt("is_blocked")
                var is_friend_blocked = jsonObject?.optInt("is_friend_blocked")
                var is_friend = jsonObject?.optInt("type")
                return State(
                    is_blocked,
                    is_friend_blocked,
                    is_friend
                )
            }
        }
    }
}