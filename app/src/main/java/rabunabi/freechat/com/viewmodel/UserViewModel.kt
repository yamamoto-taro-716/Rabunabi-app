package rabunabi.freechat.com.viewmodel

import rabunabi.freechat.com.api.AuthApi
import rabunabi.freechat.com.model.UserModel


class UserViewModel {
    var userModel: UserModel? = null

    fun getUserProfile(id: Int, onCompleted: (String?) -> Unit) {
        AuthApi().getUserProfile(id) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
                var jsonObject = it.json()?.optJSONObject("data")
                userModel = UserModel.initFrom(jsonObject)
            }
            onCompleted(it.getErrorMessage())
        }
    }
}