package com.rabunabi.friends.view.home.ui.search

import com.rabunabi.friends.api.SearchApi
import com.rabunabi.friends.model.friends.FriendListModel
import com.rabunabi.friends.utils.ParserJsonUtils
import java.lang.Exception

class SearchViewModel {
    var listUser: ArrayList<FriendListModel>? = null
    var isLoadMore: Boolean = false
    var offset: Int = 0

    init {
        listUser = ArrayList()
    }

    fun searchUser(
        nickname: String,
        gender: String,
        hasAvatar: String,
        prefecture: String,
        ageFrom: String,
        ageTo: String,
        mucdich: String,
        honnhan: String,
        isRefresh: Boolean,
        limit: String, onCompleted: (String?) -> Unit
    ) {
        SearchApi().searchFriend(
            nickname,
            gender,
            hasAvatar,
            prefecture,
            ageFrom,
            ageTo,
            mucdich,
            honnhan,
            limit,
            offset.toString()
        ) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {
                var jsonObject = it.json()?.optJSONObject("data")
                System.out.println("diep search " + jsonObject);

                //   "next_page": "last_id=1223&last_date=1604044770"
                try {
                    var next_page: String? = jsonObject?.getString("next_page")
                    System.out.println("Ok next_page " + next_page);
                    val strs = next_page?.split("&")?.toTypedArray()
                    val arr2 = strs!![0].split("=")?.toTypedArray()
                    //System.out.println("Ok arr2[0] " + arr2[1]);
                    try{
                        offset = arr2[1].toInt();
                    }catch (e:Exception){}
                   // System.out.println("Ok offset " + offset);
                    isLoadMore = true;
                }catch (e:Exception){
                    e.printStackTrace()
                    isLoadMore = false
                }

                var user = ParserJsonUtils.convertArray(jsonObject?.optJSONArray("accounts")) {
                    FriendListModel.initFrom(it)
                }
                //     isLoadMore = (user.size > 0)
                if (isRefresh) {
                    listUser?.clear()
                }
                listUser?.addAll(user)
                System.out.println("Ok isRefresh " + isRefresh + "  listUser "+listUser?.size);
                //  offset = listUser?.size!!
                // kiểm tra xem avata đã confirm chưa. nếu chưa confirm thì "" để ko hiển thị
                // avatar_status = 1 là đã được confirm bởi admin
                if (listUser != null) {
                    for (item: FriendListModel in listUser!!) {
                        // nếu avatar chưa được confirm thì set "" để ko hiển thị lên
                        if (item.avatar_status != 1) {
                            item.avatar = "";
                        }
                    }
                }
                // end
            }
            onCompleted(it.getErrorMessage())
        }
    }
}