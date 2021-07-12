package com.rabunabi.friends.api.base

import android.util.Log
import com.rabunabi.friends.BalloonchatApplication
import com.rabunabi.friends.R
import com.rabunabi.friends.common.Const
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class ResponseApi {

    var throwable: Throwable? = null
    var responseContent: String? = null
    var successfulApi: Boolean = false
    var code: Int = 0

    constructor(throwable: Throwable) {
        this.throwable = throwable
        code = Const.ERROR_NETWORK
    }

    constructor() {
        code = Const.DISCONNECT_NETWORK
    }

    constructor(response: Response<ResponseBody>) {
        this.responseContent = response?.body()?.string()
        successfulApi = response.isSuccessful
    }

    fun isSuccess(): Boolean {
        var isSuccess = false
        if (successfulApi) {
            this.json()?.let {
                isSuccess = it.optBoolean("status", false)
                if (it.optInt("code") == 200) {
                    isSuccess = true
                }
            }
        }
        return isSuccess
    }

    fun getErrorCode(): Int {
        if (successfulApi) {
            this.json()?.let {
                code = it.optInt("error_code")
            }
        }
        return code
    }

    fun getErrorMessage(): String? {
        Log.d("ERROR_CODE", getErrorCode().toString())
        when (getErrorCode()) {
            404 -> return BalloonchatApplication.context?.getString(
                R.string.code_404)
            600 -> return BalloonchatApplication.context?.getString(R.string.code_600)
            101 -> return BalloonchatApplication.context?.getString(R.string.code_101)
            Const.DISCONNECT_NETWORK -> return BalloonchatApplication.context?.getString(R.string.err_disconnect)
        }
        return null
    }


    fun json(): JSONObject? {
        var ret: JSONObject? = null
        if (!this.responseContent.isNullOrEmpty()) {
            try {
                ret = JSONObject(this.responseContent)
            }catch (e : JSONException) {
                e.printStackTrace()
            }
        }
        return ret
    }

    fun jsonArray(): JSONArray? {
        var ret: JSONArray? = null
        if (!this.responseContent.isNullOrEmpty()) {
            try {
                ret = JSONArray(this.responseContent)
            }catch (e : JSONException) {
                e.printStackTrace()
            }
        }
        return ret
    }
}