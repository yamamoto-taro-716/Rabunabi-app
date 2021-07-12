package com.rabunabi.friends.firebase;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.rabunabi.friends.BalloonchatApplication;
import com.rabunabi.friends.R;
import com.rabunabi.friends.model.friends.FriendListModel;
import com.rabunabi.friends.utils.DBManager;
import com.rabunabi.friends.view.home.HomeActivity;

public class NotifyHelper {
    public static void sendNotification(Context context,String type,
                                        String created,
                                        String messageBody,
                                        String title,
                                        String avatar,
                                        int gender,
                                        int account_id,
                                        String revision,
                                        String nationality
    ) {
        /**
         * json push
         * {avatar=, gender=1, account_id=9, revision=1, body=rr, type=chat, nationality=JP, sound=default, title=diepmayao, created=2020-05-19 16:54:26}
         */
        Context ct = BalloonchatApplication.Companion.getContext();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("avatar", avatar);
        intent.putExtra("gender", gender);
        intent.putExtra("account_id", account_id);
        intent.putExtra("revision", revision);
        intent.putExtra("nationality", nationality);


        intent.putExtra("type", type);
        intent.putExtra("created", created);
        intent.putExtra("title", title);
        intent.putExtra("message", messageBody);

        String displayTitle = "";
        // String LANGCODE = SharePreferenceUtils.Companion.getInstances().getString(Const.Companion.getLANG_CODE());
        String LANGCODE = Locale.getDefault().getLanguage();
        /*if ("vi".equals(LANGCODE)) {
            langCode = "VI"
            langText = "Việt Nam"
        } else if ("jp".equals(languageCode)) {
            langCode = "JP"
            langText = "日本語"
        } else {
            langCode = "EN"
            langText = "English"
        }*/
        System.out.println(" ==== DIEP 11111 LANGCODE " + LANGCODE);
        System.out.println(" ==== DIEP 11111 created " + created);
        if ("admin".equals(type)) {
            displayTitle = ct.getString(R.string.notice_from_admin);
        } else {
            displayTitle = title + "さんからメッセージが届いています。";

            // build object FriendListModel lưu vào message vào db local. để xử lý chức năng read/unread
            FriendListModel friendListModel = new FriendListModel();
            friendListModel.setCreated(created);
            friendListModel.setMessage(messageBody);
            friendListModel.setNickname(title);
            friendListModel.setAvatar(avatar);
            friendListModel.setGender(gender);
            friendListModel.setId(account_id);
            friendListModel.setRevision(revision);
            friendListModel.setNationality(nationality);

            DBManager dbManager = new DBManager(context);
            dbManager.updateMessage2(friendListModel);
            //end
        }


        if ("admin".equals(type) && appInForeground(ct)) {
            System.out.println(" ==== DIEP isRunning STATR ");
            intent.setClass(ct, PushResultActivity.class);
            ct.startActivity(intent);
        } else {
            System.out.println(" ==== DIEP isRunning CREATE ");
            intent.setClass(ct, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            String CHANNEL_ID = "CHANNEL_ID";

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(ct)
                            .setVibrate(new long[]{1000, 1000})
                            .setSound(defaultSoundUri)
                            .setAutoCancel(true)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(displayTitle)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setCategory(NotificationCompat.CATEGORY_ALARM)
                            .setContentText(messageBody);
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setColor(ct.getResources().getColor(R.color.blue));
            } else {
                builder.setSmallIcon(R.mipmap.ic_launcher);
            }*/
            builder.setSmallIcon(getNotificationIcon(builder));

            PendingIntent contentIntent = PendingIntent.getActivity(ct, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
            // Add as notification
            NotificationManager manager = (NotificationManager) ct.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel nChannel = new NotificationChannel(CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
                nChannel.enableLights(true);
                assert manager != null;
                builder.setChannelId(CHANNEL_ID);
                manager.createNotificationChannel(nChannel);

                NotificationChannel channel = manager.getNotificationChannel(CHANNEL_ID);
                channel.canBypassDnd();
            }
            assert manager != null;
            manager.notify(new Random().nextInt(), builder.build());
        }
    }

    private static int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = 0x008000;
            notificationBuilder.setColor(color);
            return R.drawable.ic_launcher;

        }
        return R.drawable.ic_launcher;
    }
    public static boolean appInForeground(@NonNull Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return false;
        }

        for (ActivityManager.RunningAppProcessInfo runningAppProcess : runningAppProcesses) {
            if (runningAppProcess.processName.equals(context.getPackageName()) &&
                    runningAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}
