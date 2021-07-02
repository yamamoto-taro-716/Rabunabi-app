package rabunabi.freechat.com.viewmodel

import android.util.Log
import rabunabi.freechat.com.api.AuthApi
import rabunabi.freechat.com.api.GoogleTranslateApi
import rabunabi.freechat.com.model.friends.FriendListModel
import rabunabi.freechat.com.model.friends.FriendListRequestModel
import rabunabi.freechat.com.utils.ParserJsonUtils

class ListfriendViewModel {
    var textAfterTranslate: String? = null
    var listFriend: ArrayList<FriendListModel>? = null
    var listFriendRequest: ArrayList<FriendListRequestModel>? = null

    init {
        listFriend = ArrayList()
        listFriendRequest = ArrayList()
    }

    fun getListFriends(onCompleted: (String?) -> Unit) {
        AuthApi().getListFriends() {
            val isSuccess = it.isSuccess()
            System.out.println("diep friends.isSuccess " + isSuccess)
            if (isSuccess) {
                var jsonObject = it.json()?.optJSONObject("data")
                var friends = ParserJsonUtils.convertArray(jsonObject?.optJSONArray("friend")) {
                    FriendListModel.initFrom(it)
                }
                listFriend?.addAll(friends)

                var friendRequests =
                    ParserJsonUtils.convertArray(jsonObject?.optJSONArray("pending")) {
                        FriendListRequestModel.initFrom(it)
                    }
                System.out.println("diep friends.size " + friends.size)
                listFriendRequest?.addAll(friendRequests)

            }
            onCompleted(it.getErrorMessage())
        }
    }

    fun apiTranslate(text: String, lang: String, onCompleted: (String?) -> Unit) {
        GoogleTranslateApi().translate(text, lang) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
                var jsonArray = it.json()?.optJSONArray("text")
                textAfterTranslate = jsonArray?.get(0).toString()
                Log.d("", "")
            }
            onCompleted(it.getErrorMessage())
        }
    }

    fun addFriend(id: Int, onCompleted: (String?) -> Unit) {
        AuthApi().addFriend(id) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
            }
            onCompleted(it.getErrorMessage())
        }
    }

    fun unFriend(id: Int, onCompleted: (String?) -> Unit) {
        AuthApi().unFriend(id) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
            }
            onCompleted(it.getErrorMessage())
        }
    }

    fun blockFriend(id: Int, onCompleted: (String?) -> Unit) {
        AuthApi().blockFriend(id) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
            }
            onCompleted(it.getErrorMessage())
        }
    }

    fun unBlockFriend(id: Int, onCompleted: (String?) -> Unit) {
        AuthApi().unBlockFriend(id) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
            }
            onCompleted(it.getErrorMessage())
        }
    }

    fun report(id: Int, onCompleted: (String?) -> Unit) {
        AuthApi().report(id) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
            }
            onCompleted(it.getErrorMessage())
        }
    }


    fun clearData() {
        listFriend?.clear()
        listFriendRequest?.clear()
    }
}