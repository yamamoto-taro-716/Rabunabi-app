package com.rabunabi.friends.utils

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import com.rabunabi.friends.BalloonchatApplication
import com.rabunabi.friends.common.Const
import com.rabunabi.friends.firebase.NotifyHelper
import org.json.JSONObject


class SocketSingleton private constructor() {
    var socket: Socket? = null
    var callbackSocket: SocketConnectCallback? = null;

    init {
        val opts = IO.Options()
        opts.forceNew = true
        socket = IO.socket(Const.SERVER_SOCKET_URL, opts)
    }

    companion object {

        private var s = SocketSingleton()
        fun getInstance(): SocketSingleton {
            if (s == null) {
                s = SocketSingleton()
            }
            return s
        }
    }

    fun socketStatus(): Boolean {
        return socket?.connected()!!
    }

    fun offSocket() {
        socket?.off(Socket.EVENT_CONNECT)
        socket?.off(Socket.EVENT_DISCONNECT)
        socket?.off(Socket.EVENT_ERROR)
        socket?.off(Socket.EVENT_CONNECT_ERROR)
        socket?.off("receive:message")
        socket?.off("join:room")
        socket?.off("send:message")
        socket?.off("send:contact")
        socket?.off("join:contact")
        socket?.off("response:room")
        socket?.disconnect();
    }

    fun connect(callback: SocketConnectCallback) {
        callbackSocket = callback;
        socket?.on(Socket.EVENT_CONNECT, eventConnect())
            ?.on(Socket.EVENT_DISCONNECT, eventDisconnect())
            ?.on("response:room", object : Emitter.Listener {
                override fun call(vararg args: Any?) {
                    val it = args.iterator()
                    while (it.hasNext()) {
                        val value = it.next()
                        System.out.println("diep ==============================Socket response:room value: " + value);
                    }
                }
            })?.on(Socket.EVENT_ERROR) {
                System.out.println("diep ==============================Socket EVENT_ERROR ")
            }?.on(Socket.EVENT_CONNECT_ERROR) {
                System.out.println("diep ==============================Socket EVENT_CONNECT_ERROR ")
            }
        socket?.connect()
    }

    private fun eventConnect() = Emitter.Listener {
        val token = SharePreferenceUtils.getInstances().getString(Const.AUTH)
        val obj = JSONObject()
        obj.put("token", token)
        socket
            ?.on("authenticated", object : Emitter.Listener {
                override fun call(vararg args: Any?) {
                    System.out.println("diep ==============================Socket authenticated: " + args.toString());
                    val it = args.iterator()
                    while (it.hasNext()) {
                        val value = it.next()
                        System.out.println("diep ==============================Socket authenticated: " + value);
                    }
                    callbackSocket?.socketConnected()
                }
            })
            ?.on("unauthorized", Emitter.Listener {
                System.out.println("diep ==============================Socket unauthorized 2")
            })
            ?.on("push:notification", object : Emitter.Listener {
                override fun call(vararg args: Any?) {
                    val it = args.iterator()
                    while (it.hasNext()) {
                        val value = it.next()
                        sendPush(value.toString())
                        /*var push = ChatPushEvent();
                        push.json = value.toString();
                        EventBus.getDefault().post(push);
                        */
                        System.out.println("diep ==============================push:notification value: " + value);
                    }
                }
                // System.out.println("diep ==============================push:notification")
            })
            ?.emit("authenticate", obj)
        System.out.println("diep ==============================Socket connected > Emit authenticate")
    }

    private fun eventDisconnect() = Emitter.Listener {
        System.out.println("diep ==============================Socket OFF")
    }

    fun joinRoom(friendId: Int, point: Int) {
        System.out.println("diep ================== joinRoom  friendId " + friendId + " connected()" + socket?.connected());
        val obj = JSONObject()
        obj.put("friend_id", friendId)
        obj.put("point", point)
        socket?.emit("join:room", obj)
    }

    fun sendMessage(friendId: Int, type: Int, point: Int, messageText: String) {
        System.out.println("diep sendMessage  socket.connected() " + socket?.connected());
        val obj = JSONObject()
        obj.put("type", type)
        obj.put("message", messageText)
        obj.put("friend_id", friendId)
        obj.put("point", point)
        socket?.emit("send:message", obj)
        System.out.println("diep sendMessage  " + obj.toString());
    }

    fun receiveMessage(event: Emitter.Listener) {
        System.out.println("diep ==============================do receiveMessage")
        socket?.on("receive:message", event)
    }

    fun joinContact() {
        val obj = JSONObject()
        obj.put("account_id", 0)
        socket?.emit("join:contact", obj)
    }

    fun sendContact(message: String) {
        val obj = JSONObject()
        obj.put("type", 1)
        obj.put("account_id", 0)
        obj.put("message", message)
        socket?.emit("send:contact", obj)
        Log.d("", "")
    }

    ///
    fun sendPush(json: String) {
        try {
            //{"id":9,"nickname":"diepmayao","message":"jjj","gender":1,"avatar":"","nationality":"JP","revision":5,"created":"2020-05-26 18:23:54"}
            val obj = JSONObject(json)
            var account_id = obj.optInt("account_id")
            var title = obj.optString("nickname")
            var message = obj.optString("message")
            var gender = obj.optInt("gender")
            var avatar = obj.optString("avatar")
            var nationality = obj.optString("nationality")
            var revision = obj.optString("revision")
            var created = obj.optString("created")

            var type = "chat"

            NotifyHelper.sendNotification(
                BalloonchatApplication.context, type, created, message, title, avatar,
                gender,
                account_id,
                revision,
                nationality
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

