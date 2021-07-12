package com.rabunabi.friends.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import com.rabunabi.friends.BalloonchatApplication;
import com.rabunabi.friends.api.StartApi;
import com.rabunabi.friends.api.base.ResponseApi;
import com.rabunabi.friends.common.Const;
import com.rabunabi.friends.model.friends.FriendListModel;
import com.rabunabi.friends.utils.SharePreferenceUtils;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class NotificationService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        System.out.println(" ==== DIEP onNewToken " + s);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken)
        StartApi startApi = new StartApi();
        startApi.startSync(s, new Function1<ResponseApi, Unit>() {
            @Override
            public Unit invoke(ResponseApi responseApi) {
                if (responseApi != null && responseApi.isSuccess()) {
                    if (responseApi.json() != null) {
                        try {
                            JSONObject jsonObject = responseApi.json().getJSONObject("data");
                            if (jsonObject != null) {
                                String auth = jsonObject.getString("Authorization");
                                Log.d("DIEP Authorization", auth);
                                SharePreferenceUtils.Companion.getInstances().saveString(Const.Companion.getAUTH(), auth);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.println(" ==== DIEP remoteMessage.getNotification() onMessageReceived");
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) { // from firebase console
            System.out.println(" ==== DIEP remoteMessage.getNotification() " + remoteMessage.getNotification().getBody());
            com.rabunabi.friends.firebase.NotifyHelper.sendNotification(BalloonchatApplication.Companion.getContext(), "", "", remoteMessage.getNotification().getBody()
                    , remoteMessage.getNotification().getTitle(), "", 0, 0, "", "");
        } else {
            String json = remoteMessage.getData().toString();
            Map<String, String> datas = remoteMessage.getData();
            Log.e("dataChat", json);
            String title = "";
            String message = "";
            String type = "";
            String created = "";
          //  String friendId = "";

            String avatar = "";
            int gender = 0;
            int account_id = 0;
            String revision = "";
            String nationality = "";
            try {
                if (json.contains("type=chat")) {
                    String[] arr = json.split(",");
                    for (String str : arr) {
                        if (str.contains("title=")) {
                            title = (String) str.subSequence("title=".length() + 1,
                                    str.length());
                        }
                        if (str.contains("body=")) {
                            message = (String) str.subSequence("body=".length() + 1,
                                    str.length());
                        }
                        if (str.contains("type=")) {
                            type = (String) str.subSequence("type=".length() + 1,
                                    str.length());
                        }
                        if (str.contains("created=")) {
                            created = (String) str.subSequence("created=".length() + 1,
                                    str.length());
                        }
                        //
                        if (str.contains("avatar=")) {
                            avatar = (String) str.subSequence("avatar=".length() + 1,
                                    str.length());
                        }
                        if (str.contains("gender=")) {
                            gender = Integer.parseInt((String) str.subSequence("gender=".length() + 1,
                                    str.length()));
                        }
                        if (str.contains("account_id=")) {
                            account_id = Integer.parseInt((String) str.subSequence("account_id=".length() + 1,
                                    str.length()));
                        }
                        if (str.contains("revision=")) {
                            revision = (String) str.subSequence("revision=".length() + 1,
                                    str.length());
                        }
                        if (str.contains("nationality=")) {
                            nationality = (String) str.subSequence("nationality=".length() + 1,
                                    str.length());
                        }
                    }
                    FriendListModel model = new FriendListModel();
                    model.setId(account_id);
                    model.setAvatar(avatar);
                    model.setGender(gender);
                    model.setRevision(revision);
                    model.setNationality(nationality);
                    //diep commentout code vi dang update o NotifyHelper.sendNotification
                //    DBManager dbManager = new DBManager(BalloonchatApplication.Companion.getContext());
                  //  dbManager.updateMessage2(model);
                } else {
                    JSONObject object = new JSONObject(remoteMessage.getData().toString());
                    JSONObject jsonContent = object.getJSONObject("content");
                    title = jsonContent.getString("title");
                    message = jsonContent.getString("message");
                    type = jsonContent.getString("type");
                    created = jsonContent.getString("created");
                }
            } catch (Exception e) {
                // {avatar=, gender=2, account_id=860, revision=2, body=rrfd, type=chat, nationality=JP, sound=default, title=maytab, created=2019-11-01 08:07:54}
                e.printStackTrace();
            }
            NotifyHelper.sendNotification(BalloonchatApplication.Companion.getContext(), type, created, message, title, avatar,
                    gender,
                    account_id,
                    revision,
                    nationality);
        }
    }
}