package rabunabi.freechat.com.model

import android.text.TextUtils
import org.json.JSONObject

class PointModel(
  var points: Int,
  var sendMessage: Int,
  var readMessage: Int,
  var sendImage: Int) {

    companion object {
        fun initFrom(jsonObject: JSONObject?): PointModel? {
            jsonObject?.let {
                var points = jsonObject.optInt("points")
                var sendMessage = jsonObject.optInt("sendMessage")
                var readMessage = jsonObject.optInt("readMessage")
                var sendImage = jsonObject.optInt("sendImage")

                return PointModel(
                    points,
                    sendMessage,
                    readMessage,
                    sendImage
                )
            }
            return null
        }
    }
}