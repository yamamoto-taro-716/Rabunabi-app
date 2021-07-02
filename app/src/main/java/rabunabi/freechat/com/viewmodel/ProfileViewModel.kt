package rabunabi.freechat.com.viewmodel

import rabunabi.freechat.com.api.AuthApi
import rabunabi.freechat.com.common.Const
import rabunabi.freechat.com.model.CountryModel
import rabunabi.freechat.com.model.ProfileModel
import rabunabi.freechat.com.utils.ParserJsonUtils
import rabunabi.freechat.com.utils.SharePreferenceUtils
import rabunabi.freechat.com.utils.Utils
import okhttp3.MultipartBody
import org.json.JSONArray

class ProfileViewModel {
    var profileModel: ProfileModel? = null
    var countryList: ArrayList<CountryModel>? = null

    init {
        countryList = ArrayList()
    }

    fun register(
        nickname: String,
        gender: Int,
        intro: String,
        age: String,
        prefecture: String,
        nationality: String,
        avatar: MultipartBody.Part,
        onCompleted: (String?) -> Unit
    ) {
        AuthApi().register(nickname, gender, intro, age, prefecture, nationality, avatar) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
                var jsonObject = it.json()?.optJSONObject("data")
                var auth = jsonObject?.optString("Authorization")
                SharePreferenceUtils.getInstances().saveString(Const.AUTH, auth)
                var profileInfor = ProfileModel.initFrom(jsonObject)
                SharePreferenceUtils.getInstances().saveUserInfo(profileInfor)
            }
            onCompleted(it.getErrorMessage())
        }
    }

    fun updateProfile(
        nickname: String,
        gender: Int,
        intro: String,
        age: String,
        prefecture: String,
        nationality: String,
        marriage: String,
        purpose: String,
        avatar: MultipartBody.Part,
        onCompleted: (String?) -> Unit
    ) {
        AuthApi()
            .updateProfile(
                nickname,
                gender,
                intro,
                age,
                prefecture,
                nationality,
                marriage,
                purpose,
                avatar
            ) {
                val isSuccess = it.isSuccess()
                if (isSuccess) {
                    var jsonObject = it.json()?.optJSONObject("data")
                    var auth = jsonObject?.optString("Authorization")
                    var userStartId = jsonObject?.optInt("id")
                    SharePreferenceUtils.getInstances().saveInt(Const.USER_START_ID, userStartId)
                    SharePreferenceUtils.getInstances().saveString(Const.AUTH, auth)
                    var profileInfor = ProfileModel.initFrom(jsonObject)
                    SharePreferenceUtils.getInstances().saveUserInfo(profileInfor)
                }
                onCompleted(it.getErrorMessage())
            }
    }

    fun getMyProfile(authorization: String, onCompleted: (String?) -> Unit) {
        AuthApi().getMyProfile(authorization) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
                var jsonObject = it.json()?.optJSONObject("data")
                profileModel = ProfileModel.initFrom(jsonObject)
            }
            onCompleted(it.getErrorMessage())
        }
    }

    fun initCountryData() {
        var jsonArray = JSONArray(Utils.readJSONFromAsset("countries.json"))
        var list = ParserJsonUtils.convertArray(jsonArray) {
            CountryModel.initFrom(it)
        }
        countryList?.addAll(list)
    }
}