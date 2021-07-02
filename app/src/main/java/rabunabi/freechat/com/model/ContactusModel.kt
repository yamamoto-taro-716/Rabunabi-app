package rabunabi.freechat.com.model

import org.json.JSONObject

data class ContactusModel(
    var content: String?,
    var time: String?,
    var isMe: Boolean? = true,
    var send_id: Int
) {
    companion object {
        fun initFrom(jsonObject: JSONObject?): ContactusModel? {
            jsonObject?.let {
                var content = jsonObject.optString("message")
                var time = jsonObject.optString("created")
                var send_id = jsonObject.optInt("send_id")
                return ContactusModel(content, time, true, send_id)
            }
            return null
        }
    }
}