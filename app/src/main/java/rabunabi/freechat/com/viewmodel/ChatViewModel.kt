package rabunabi.freechat.com.viewmodel

import rabunabi.freechat.com.api.TalkApi
import rabunabi.freechat.com.model.ChatModel
import rabunabi.freechat.com.utils.ParserJsonUtils
import okhttp3.MultipartBody
import java.util.*

class ChatViewModel {
    var list: MutableList<ChatModel>? = ArrayList()
    var is_blocked: Int? = 0
    var is_friend_blocked: Int? = 0
    var is_reported: Int? = 0
    var is_friend_reported: Int? = 0
    var is_friend: Int? = 0
    var linkImage: String? = null
    var chatModel : ChatModel? = null
    fun sendMessageImage(friendId: Int, image: MultipartBody.Part, sendImage: Int, onCompleted: (String?) -> Unit) {
        TalkApi().sendMessageImage(friendId, image, sendImage) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
//                linkImage = it.json()?.opt("data")
                var jsonObject = it.json()?.optJSONObject("data")
                chatModel = ChatModel.initFrom(jsonObject)
            }
            onCompleted(it.getErrorMessage())
        }
    }

    fun joinChat(friendId: Int, onCompleted: (String?) -> Unit) {
        TalkApi().joinChat(friendId) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
                var jsonObject = it.json()?.optJSONObject("data")
                var jsonStateObject = jsonObject?.optJSONObject("state")
                is_blocked = jsonStateObject?.optInt("is_blocked")
                is_friend_blocked = jsonStateObject?.optInt("is_friend_blocked")
                is_reported = jsonStateObject?.optInt("is_reported")
                is_friend_reported = jsonStateObject?.optInt("is_friend_reported")
                is_friend = jsonStateObject?.optInt("is_friend")
            }
            onCompleted(it.getErrorMessage())
        }
    }

    fun getHistoryChat(friendId: Int, onCompleted: (String?) -> Unit) {

        TalkApi().getHistoryChat(friendId) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
                var jsonObject = it.json()?.optJSONObject("data")
                var message = ParserJsonUtils.convertArray(jsonObject?.optJSONArray("messages")) {
                    ChatModel.initFrom(it)
                }
                //diep : fixbug duplicate message
                list?.clear()
                //end diep
                list?.addAll(message)
            }
            onCompleted(it.getErrorMessage())
        }
    }

}