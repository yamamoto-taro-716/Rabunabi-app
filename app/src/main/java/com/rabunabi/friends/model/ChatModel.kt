package com.rabunabi.friends.model

import org.json.JSONObject

data class ChatModel(
    var id: Int? = null,
    var content: String? = null,
    var sendId: Int? = null,
    var receiveId: Int? = null,
    var time: String? = null,
    var type: String? = null,
    var isMe: Boolean? = null
) {

    companion object {
        fun initFrom(jsonObject: JSONObject?): ChatModel? {
            jsonObject?.let {
                var id = jsonObject.optInt("id")
                var message = jsonObject.optString("message")
                var sendId = jsonObject.optInt("send_id")
                var receiveiId = jsonObject.optInt("receive_id")
                var created = jsonObject.optString("created")
                var type = jsonObject.optString("type")
                return ChatModel(
                    id,
                    message,
                    sendId,
                    receiveiId,
                    created,
                    type
                )
            }
            return null
        }
    }
}