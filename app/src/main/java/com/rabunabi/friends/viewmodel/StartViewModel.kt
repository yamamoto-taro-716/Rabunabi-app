package com.rabunabi.friends.viewmodel

import com.rabunabi.friends.BalloonchatApplication
import com.rabunabi.friends.api.StartApi
import com.rabunabi.friends.common.Const
import com.rabunabi.friends.model.PointModel
import com.rabunabi.friends.model.StartModel
import com.rabunabi.friends.utils.SharePreferenceUtils


class StartViewModel {
    fun startUp(onCompleted: (String?) -> Unit) {
        StartApi().startUp() {
            val isSuccess = it.isSuccess()
            println("isSuccess "+isSuccess)
            if (isSuccess) {
                var jsonObject = it.json()?.optJSONObject("data")
                var auth = jsonObject?.optString("Authorization")
                SharePreferenceUtils.getInstances().saveString(Const.AUTH, auth)
                var startInfor = StartModel.initFrom(jsonObject)
                var pointInfor = PointModel.initFrom(jsonObject?.optJSONObject("points"))

                SharePreferenceUtils.getInstances().savePointInfo(pointInfor)


                //check admob count config
                var local_count_ads = 0
                if(SharePreferenceUtils.getInstances().getStartInfo() != null) {
                    local_count_ads  = SharePreferenceUtils.getInstances().getStartInfo().count_ads!!
                }

                var server_count_ads = 0
                startInfor?.let {
                    server_count_ads = startInfor.count_ads!!
                }
                if(local_count_ads != server_count_ads){
                    SharePreferenceUtils.getInstances().saveInt(BalloonchatApplication.AD_DISPLAY_COUNT, 0)
                }
                //end check

                SharePreferenceUtils.getInstances().saveStartInfo(startInfor)
            }
            onCompleted(it.getErrorMessage())
        }
    }
}