package com.timo.linemessageapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class SendNotification extends FirebaseMessagingService {

    private static final String TAG = "SendNotification";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendIt(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        Log.d(TAG, "I DID IT!");
    }

    public void sendIt(String title, String body){
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setContentTitle("SEND IT!!!!")
//                .setContentText(msg)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUR_DEVELOPMENT) {
            NotificationChannel channel = new NotificationChannel(
                    "Channel1",
                    "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setDescription("I DID IT!");
            channel.enableLights(true);
            channel.setLightColor(android.R.color.holo_blue_bright);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            channel.enableLights(true);
            notificationManager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "Channel1");
        notificationBuilder.setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_account_circle_black_24dp)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");


        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
