package rabunabi.freechat.com.viewmodel

import rabunabi.freechat.com.api.TalkApi
import rabunabi.freechat.com.model.ContactusModel
import rabunabi.freechat.com.utils.ParserJsonUtils
import okhttp3.MultipartBody

class ContactusViewModel {
    var list: ArrayList<ContactusModel>? = null
    var linkImage: String? = null
    var chatModel : ContactusModel? = null

    init {
        list = ArrayList()
    }

    fun getContactMessage(onCompleted: (String?) -> Unit) {
        TalkApi().getContactMessage() {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
                var jsonObject = it.json()?.optJSONObject("data")

                var message = ParserJsonUtils.convertArray(jsonObject?.optJSONArray("messages")) {
                    ContactusModel.initFrom(it)
                }
                if (message != null) {
                    message.sortBy { it.time }
                }
                list?.addAll(message)
            }
            onCompleted(it.getErrorMessage())
        }
    }
    fun sendMessageImage(friendId: Int, image: MultipartBody.Part, onCompleted: (String?) -> Unit) {
        TalkApi().sendMessageImage(friendId, image) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
                var jsonObject = it.json()?.optJSONObject("data")
                chatModel = ContactusModel.initFrom(jsonObject)
            }
            onCompleted(it.getErrorMessage())
        }
    }

}