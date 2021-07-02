package rabunabi.freechat.com.view.home.ui.withdraw

import rabunabi.freechat.com.api.AuthApi

class DisableUserViewModel {

    fun disableUser(onCompleted: (String?) -> Unit) {
        AuthApi().disableUser() {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
            }
            onCompleted(it.getErrorMessage())
        }
    }
}