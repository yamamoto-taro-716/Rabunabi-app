package rabunabi.freechat.com.utils

import org.json.JSONArray
import org.json.JSONObject

object ParserJsonUtils {

    fun <T> convertArray(jsonArray: JSONArray?, convertorSelector: ((JSONObject) -> T?)): ArrayList<T> {
        var result: ArrayList<T> = arrayListOf()
        jsonArray?.let {
            for (i in 0 until it.length()) {
                convertorSelector.invoke(it.getJSONObject(i))?.let { it1 -> result.add(it1) }
            }
        }
        return result
    }

}