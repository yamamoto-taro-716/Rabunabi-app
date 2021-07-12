package com.rabunabi.friends.common

class Const {
    companion object {
        // val SERVER_VERIFY = "http://blc.steadfast-inc.com/api/"
//        val SERVER_VERIFY = "http://52.194.166.45/api/"
        //var HOST = "http://52.69.103.221"
        var HOST = "http://18.183.150.95"
        val SERVER_VERIFY = HOST + "/rabunabi/api/"
        var LOCALHOST = "http://10.0.2.2"
//        val SERVER_SOCKET_URL = HOST + ":3001/chat-rabunabi"
        val SERVER_SOCKET_URL = LOCALHOST + ":3001/chat-rabunabi"
        val ERROR_NETWORK = 1000
        val DISCONNECT_NETWORK = 999
        val DEVICE_ID = "DEVICE_ID"
        val name = "Balloonchat"
        val KEY_EXTRA_DATA: String = "data"
        val AUTH = "Authorization"
        val USER_START_ID = "user_start_id"
        val NOTIFICATION_STATUS = "notification_status"
        val CAMERA_PERMISSION_CODE = 100
        val CAMERA_PERMISSION_CODE_PROFILE = 200
        val READ_STORAGE_PERMISSION_CODE = 101
        val READ_STORAGE_PERMISSION_CODE_PROFILE = 201
        val REQUEST_CODE_GALLERY = 12
        val REQUEST_CODE_CAMERA = 13

        val FRIEND_ID = "friend_id"
        val FRIEND_NAME = "friend_name"
        val FRIEND_AVATAR = "friend_avatar"
        val FRIEND_PREFECTURE = "FRIEND_PREFECTURE"
        val FRIEND_INTRO = "FRIEND_INTRO"
        val FRIEND_AGE = "FRIEND_AGE"
        val FRIEND_GENDER = "FRIEND_GENDER"
        val LANG_CODE = "lang_code"
        val LANG_TEXT = "lang_text"
        val SHOW_POPUP_TERMS = "show_popup_terms"
        val NAME_ID = "NAME_ID"
        val KEY_FRIEND = "friend"
        val KEY_ACTYVITY = "KEY_ACTYVITY"
        val COUNTRY_CODE = "country_code"

    }
}