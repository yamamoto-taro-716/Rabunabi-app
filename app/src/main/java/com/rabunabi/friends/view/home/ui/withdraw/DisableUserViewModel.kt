package com.rabunabi.friends.view.home.ui.withdraw

import com.rabunabi.friends.api.AuthApi

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