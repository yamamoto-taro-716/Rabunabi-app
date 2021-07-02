package rabunabi.freechat.com.viewmodel

import rabunabi.freechat.com.api.StartApi

class HomeViewModel {
    var countFriendPendding: Int? = 0
    fun getFriendPendding(onCompleted: (String?) -> Unit) {
        StartApi().getFriendPendding() {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
                var jsonObject = it.json()?.optJSONObject("data")
                countFriendPendding = jsonObject?.getInt("count")
            }
            onCompleted(it.getErrorMessage())
        }
    }
}