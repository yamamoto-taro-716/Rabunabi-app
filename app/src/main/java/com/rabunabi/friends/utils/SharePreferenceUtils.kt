package com.rabunabi.friends.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rabunabi.friends.BalloonchatApplication
import com.rabunabi.friends.model.PointModel
import com.rabunabi.friends.model.ProfileModel
import com.rabunabi.friends.model.StartModel


class SharePreferenceUtils {
    val name = "BalloonChat"
    val startInfor = "start_info"
    val user = "user_info"
    var point = "point_info"
    private constructor() {
        mPrefs = BalloonchatApplication.context?.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    companion object {
        private var mPrefs: SharedPreferences? = null
        private var instance: SharePreferenceUtils =
            SharePreferenceUtils()
        fun getInstances(): SharePreferenceUtils {
            if (instance == null) {
                instance =
                    SharePreferenceUtils()
            }
            return instance
        }
    }

    fun saveString(key: String, content: String?) {
        mPrefs?.edit()?.putString(key, content)?.apply()
    }

    fun removeString(key: String) {
        mPrefs?.edit()?.remove(key)?.apply()
    }

    fun getString(key: String): String? {
        return mPrefs?.getString(key, "")
    }

    fun saveBoolean(key: String, content: Boolean?) {
        content?.let {
            mPrefs?.edit()?.putBoolean(key, content)?.apply()
        }
    }

    fun getBoolean(key: String): Boolean {
        return mPrefs?.getBoolean(key, true) ?: false
    }

    fun saveInt(key: String, content: Int?) {
        content?.let {
            mPrefs?.edit()?.putInt(key, content)?.apply()
        }
    }

    fun getInt(key: String): Int {
        return mPrefs?.getInt(key, -1) ?: -1
    }

    fun saveStartInfo(startModel: StartModel?) {
        val json = Gson().toJson(startModel)
        mPrefs?.edit()?.putString(this.startInfor, json)?.apply()
    }

    fun getStartInfo(): StartModel {
        try {
            val json = mPrefs?.getString(this.startInfor, null)
            val type = object : TypeToken<StartModel>() {

            }.type
            return Gson().fromJson(json, type)
        }catch (e: Exception){
            return  StartModel("","",false,0);
        }
    }

    fun saveUserInfo(profileModel: ProfileModel?) {
        val json = Gson().toJson(profileModel)
        mPrefs?.edit()?.putString(this.user, json)?.apply()
    }

    fun savePointInfo(pointModel: PointModel?) {
        val json = Gson().toJson(pointModel)
        mPrefs?.edit()?.putString(this.point, json)?.apply()
    }

    fun updatePointInfo(point: Int) {
        var pointModel = getPointInfo()
        pointModel?.points = point
        val json = Gson().toJson(pointModel)
        mPrefs?.edit()?.putString(this.point, json)?.apply()
    }

    fun getUserInfo(): ProfileModel? {
        try {
            val json = mPrefs?.getString(this.user, null)
            val type = object : TypeToken<ProfileModel>() {

            }.type
            return Gson().fromJson(json, type)
        }catch (e: Exception){
            return null;
        }
    }
    fun getPointInfo(): PointModel? {
        try {
            val json = mPrefs?.getString(this.point, null)
            val type = object : TypeToken<PointModel>() {

            }.type
            return Gson().fromJson(json, type)
        }catch (e: Exception){
            return null;
        }
    }
}