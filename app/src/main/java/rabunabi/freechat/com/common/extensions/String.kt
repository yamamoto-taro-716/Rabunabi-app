package rabunabi.freechat.com.common.extensions

import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat

fun String.encodeSH256(): String {
    var md: MessageDigest? = null
    var oauthToken = ""
    try {
        md = MessageDigest.getInstance("SHA-256")
        md!!.update(this.toByteArray(charset("UTF-8")))
        val digest = md.digest()
        oauthToken = String.format("%064x", BigInteger(1, digest))
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }

    return oauthToken
}

fun String.invalidUserName(): Boolean {
    if (this.isNullOrBlank()) {
        return true
    } else {
        if (this.length >= 1) {
            return false
        }
        return true
    }
}

fun String.getLastIdFromNextPage(): String? {
    if (this.contains("=")) {
        var s = this!!.split("=")
        return s[1]
    }
    return ""
}

fun String.toTimeFomatHHmm(): String {
    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = df.parse(this)
    //val dfOutput = SimpleDateFormat("HH:mm") //MM/dd HH:mm
    val dfOutput = SimpleDateFormat("MM/dd HH:mm") //
    return dfOutput.format(date)
}

fun String.toTexthhMMssddMMyyyy(): String {
    var arr = this.split(" ")
    var arrTime = arr[1].split(":")
    var arrDate = arr[0].split("-")
    var result = ""
    result += "${arrTime[0]} giờ "
    if (arrTime[1] != "00") {
        result += "${arrTime[1]} phút "
    }
    result += "ngày ${arrDate[2]} tháng ${arrDate[1]} năm ${arrDate[0]}"
    return result
}