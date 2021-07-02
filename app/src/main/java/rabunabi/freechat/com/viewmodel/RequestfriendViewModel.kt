package rabunabi.freechat.com.viewmodel

import rabunabi.freechat.com.model.RequestfriendModel


class RequestfriendViewModel {
    var list:MutableList <RequestfriendModel>?=null
    fun getRequestfriend(): MutableList<RequestfriendModel>?{
        list =ArrayList()
        list?.add(
            RequestfriendModel(
                "http://2sao.vietnamnetjsc.vn/images/2017/09/03/06/48/hot-girl-3.jpg",
                "R.drawable.flag_an",
                "VN",
                1,
                "Hai Anh",
                "Hello do you not make friend...."
            )
        )
        list?.add(
            RequestfriendModel(
                "https://media.tintucvietnam.vn/uploads/medias/2018/05/08/1024x1024/thuy-vi-hot-girl-noi-tieng-nho-tai-tieng-bb-baaabL9yKL.jpg",
                "R.drawable.flag_vn",
                "JP",
                2,
                "Thúy Vi",
                "Hello do you not make friend...."
            )
        )
        list?.add(
            RequestfriendModel(
                "https://www.myfun88.com/wp-content/uploads/2018/02/girl-xinh-thai-lan-fun88-7.jpg",
                "R.drawable.flag_vn",
                "US",
                1,
                "Dieu Linh",
                "Hello do you not make friend...."
            )
        )
        return list
    }

}