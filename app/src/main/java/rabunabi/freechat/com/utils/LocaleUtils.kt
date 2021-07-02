package rabunabi.freechat.com.utils

import rabunabi.freechat.com.R
import rabunabi.freechat.com.common.Const
import java.util.*

class LocaleUtils {
    companion object {
        //phase 2 xoa di
        var jpIcon = R.drawable.flag_jp
        var jpTex = R.string.japan_language
        //////////////////

        fun setDefaultLangue() {
            var langCode = "JP"
            var langText = "日本語"
            var languageCode = Locale.getDefault().getLanguage()
            System.out.println("diep setDefaultLangue : " + languageCode)
            if ("vi".equals(languageCode)) {
                langCode = "VI"
                langText = "Việt Nam"
            } else if ("jp".equals(languageCode)||"ja".equals(languageCode)) {
                langCode = "JP"
                langText = "日本語"
            } else {
                langCode = "EN"
                langText = "English"
            }
            SharePreferenceUtils.getInstances()
                .saveString(Const.LANG_CODE, langCode)
            SharePreferenceUtils.getInstances()
                .saveString(Const.LANG_TEXT, langText)
        }
    }
}