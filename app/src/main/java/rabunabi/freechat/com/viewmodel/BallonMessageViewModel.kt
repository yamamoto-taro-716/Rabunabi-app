package rabunabi.freechat.com.viewmodel

import rabunabi.freechat.com.api.TalkApi

class BallonMessageViewModel {
    fun sendRandom(sex: String,message: String, nationality: String, onCompleted: (String?) -> Unit) {
        println("DIEP === sendRandom nationality "+nationality)
        TalkApi().sendRandom(message, nationality) {
            val isSuccess = it.isSuccess()
            if (isSuccess) {

            }
            onCompleted(it.getErrorMessage())
        }
    }

    fun sendRandomBalloonChat(
        sex: String,
        message: String,
        nationality: String,
        onCompleted: (String?) -> Unit
    ) {
        println("DIEP === sendRandomBalloonChat nationality "+nationality)
        TalkApi().sendRandomBalloonChat(sex, message, nationality) {
            val isSuccess = it.isSuccess()
            println("sendRandomBalloonChat it.getErrorMessage(): "+it.getErrorMessage() +" isSuccess: "+isSuccess)
            if (isSuccess) {

            }
            onCompleted(it.getErrorMessage())
        }
    }
}