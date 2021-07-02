package rabunabi.freechat.com.model

import org.json.JSONObject

data class StartModel(
    var title_notify: String?,
    var title_notify_en: String?,
    var show_notify: Boolean?,
    var count_ads: Int?
) {
    companion object {
        fun initFrom(jsonObject: JSONObject?): StartModel? {
            jsonObject?.let {
                var titleNotify = jsonObject.optString("title_notify")
                var titleNotifyen = jsonObject.optString("title_notify_en")
                var showNotify = jsonObject.optBoolean("show_notify")
                var countAds = jsonObject.optInt("count_ads")
                return StartModel(
                    titleNotify,
                    titleNotifyen,
                    showNotify,
                    countAds
                )
            }
            return null
        }
    }
}