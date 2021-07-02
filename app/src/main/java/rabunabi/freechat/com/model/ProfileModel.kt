package rabunabi.freechat.com.model

import android.text.TextUtils
import org.json.JSONObject

data class ProfileModel(
    var id: Int?,
    var avatar: String?,
    var nickname: String?,
    var gender: Int?,
    var intro: String?,
    var age: String?,
    var prefecture: String?,
    var nationality: String?,
//    var marital_status: Int?,
    var marriage: String?,
    var purpose: String?,
    var point: Int?
) {
    companion object {
        fun initFrom(jsonObject: JSONObject?): ProfileModel? {
            jsonObject?.let {
                var id = jsonObject.optInt("id")
                var avatar = jsonObject.optString("avatar")
                var nickname = jsonObject.optString("nickname")
                var gender = jsonObject.optInt("gender")
                var intro = jsonObject.optString("intro")

                var age = jsonObject.optString("age")
                var prefecture = jsonObject.optString("prefecture")

                var nationality = jsonObject.optString("nationality")

                var marriage =
                    if (!TextUtils.isEmpty(jsonObject.optString("marriage"))) jsonObject.optString("marriage") else "ヒミツ"
                var purpose =
                    if (!TextUtils.isEmpty(jsonObject.optString("marriage"))) jsonObject.optString("purpose") else "ヒミツ"
                var point = jsonObject.optInt("point")

                return ProfileModel(
                    id,
                    avatar,
                    nickname,
                    gender,
                    intro,
                    age,
                    prefecture,
                    nationality,
                    marriage,
                    purpose,
                    point
                )
            }
            return null
        }
    }
}