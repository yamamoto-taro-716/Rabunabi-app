package rabunabi.freechat.com.model.friends

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

@SuppressLint("ParcelCreator")
data class FriendListModel(
    var id: Int? = null,
    var avatar: String? = null,
    var nickname: String? = null,
    var gender: Int? = null,
    var nationality: String? = null,
    var message: String? = null,
    var revision: String? = null,
    var notify: String? = null,
    var intro: String? = null,
    var prefecture: String? = null,
    var age: Int? = null,
    var maritalStatus: Int? = null,
    var created: String? = null,
    var avatar_status: Int? = null,
    var login_time: String? = null,
    var totalUnread: Int? = null,
    var purpose: String? = null,
    var marriage: String? = null,
    var isSelected: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(avatar)
        parcel.writeString(nickname)
        parcel.writeValue(gender)
        parcel.writeString(nationality)
        parcel.writeString(message)
        parcel.writeString(revision)
        parcel.writeString(notify)
        parcel.writeString(intro)
        parcel.writeString(prefecture)
        parcel.writeValue(age)
        parcel.writeValue(maritalStatus)
        parcel.writeString(created)
        parcel.writeValue(avatar_status)
        parcel.writeString(login_time)
        parcel.writeValue(totalUnread)
        parcel.writeString(purpose)
        parcel.writeString(marriage)
        parcel.writeValue(isSelected)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FriendListModel> {
        override fun createFromParcel(parcel: Parcel): FriendListModel {
            return FriendListModel(parcel)
        }

        override fun newArray(size: Int): Array<FriendListModel?> {
            return arrayOfNulls(size)
        }

        fun initFrom(jsonObject: JSONObject?): FriendListModel? {
            jsonObject?.let {
                var id = jsonObject.optInt("id")
                var avatar = jsonObject.optString("avatar")
                var nickname = jsonObject.optString("nickname")
                var gender = jsonObject.optInt("gender")
                var nationality = jsonObject.optString("nationality")
                var message = jsonObject.optString("message")
                var revision = jsonObject.optString("revision")
                var created = jsonObject.optString("created")
                var intro = jsonObject.optString("intro")
                var age = jsonObject.optInt("age")
                var prefecture = jsonObject.optString("prefecture")
                var maritalStatus = jsonObject.optInt("marital_status")
                var avatar_status = jsonObject.optInt("avatar_status")
                var login_time = jsonObject.optString("login_time")
                var purpose = jsonObject.optString("purpose")
                var marriage = jsonObject.optString("marriage")
                return FriendListModel(
                    id = id,
                    avatar = avatar,
                    nickname = nickname,
                    gender = gender,
                    nationality = nationality,
                    message = message,
                    revision = revision,
                    intro = intro,
                    prefecture = prefecture,
                    age = age,
                    maritalStatus = maritalStatus,
                    created = created,
                    avatar_status = avatar_status,
                    login_time = login_time,
                    purpose = purpose,
                    marriage = marriage
                )
            }
            return null
        }
    }
}