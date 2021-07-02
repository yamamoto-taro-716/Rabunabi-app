package rabunabi.freechat.com.utils

import android.content.Context
import android.net.ConnectivityManager

class NetworkUtils private constructor() {
    companion object {
        fun isNetworkConnected(context: Context?): Boolean {
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm != null) {
                val activeNetwork = cm.activeNetworkInfo
                return activeNetwork != null && activeNetwork.isConnectedOrConnecting
            }
            return false
        }
    }
}