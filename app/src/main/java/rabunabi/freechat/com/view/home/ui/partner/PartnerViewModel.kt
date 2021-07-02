package rabunabi.freechat.com.view.home.ui.partner

import rabunabi.freechat.com.api.AuthApi

class PartnerViewModel {
    fun blockUser(
        partnerId: Int, onCompleted: (String?) -> Unit
    ) {
        AuthApi().blockFriend(partnerId) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
            }
            onCompleted(it.getErrorMessage())
        }
    }

    fun reportUser(
        partnerId: Int, onCompleted: (String?) -> Unit
    ) {
        AuthApi().report(partnerId) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
            }
            onCompleted(it.getErrorMessage())
        }
    }
}