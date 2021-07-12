package com.rabunabi.friends.viewmodel

import com.rabunabi.friends.api.AuthApi
import com.rabunabi.friends.common.Const
import com.rabunabi.friends.model.CountryModel
import com.rabunabi.friends.model.ProfileModel
import com.rabunabi.friends.utils.ParserJsonUtils
import com.rabunabi.friends.utils.SharePreferenceUtils
import com.rabunabi.friends.utils.Utils
import okhttp3.MultipartBody
import org.json.JSONArray
import com.rabunabi.friends.model.PointModel

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
                var profileInfor = ProfileModel.initFrom(jsonObject?.optJSONObject("payload")?.optJSONObject("profile"))
                var pointInfor = PointModel.initFrom(jsonObject?.optJSONObject("points"))

                SharePreferenceUtils.getInstances().saveUserInfo(profileInfor)
                SharePreferenceUtils.getInstances().savePointInfo(pointInfor)
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
                    var pointInfo = PointModel.initFrom(it.json()?.optJSONObject("point"))
                    SharePreferenceUtils.getInstances().savePointInfo(pointInfo)

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