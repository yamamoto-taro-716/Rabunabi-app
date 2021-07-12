package com.rabunabi.friends.viewmodel

import com.rabunabi.friends.api.TalkApi
import com.rabunabi.friends.model.ContactusModel
import com.rabunabi.friends.utils.ParserJsonUtils
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
    fun sendMessageImage(friendId: Int, image: MultipartBody.Part,sendImage: Int, onCompleted: (String?) -> Unit) {
        TalkApi().sendMessageImage(friendId, image, sendImage) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
                var jsonObject = it.json()?.optJSONObject("data")
                chatModel = ContactusModel.initFrom(jsonObject)
            }
            onCompleted(it.getErrorMessage())
        }
    }

}