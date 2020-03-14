package com.acr.landmarks.background_services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.acr.landmarks.R;
import com.acr.landmarks.ui.MainActivity;
import com.acr.landmarks.util.Config;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MessageService extends FirebaseMessagingService {
    String TAG = "MessageService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
    }

    private void sendNotification(RemoteMessage message) {
        Map<String, String> data = message.getData();
        String channelId = Config.getConfigValue(getApplicationContext(), "default_notification_channel_id");

        NotificationCompat.Builder notificationBuilder = createNotificationBuilder(data, channelId);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Landmarks Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    private NotificationCompat.Builder createNotificationBuilder(Map<String, String> data, String channelId) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("landmarkId", Integer.parseInt(data.get("landmarkId")));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String dataBody = data.get("body");
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_statue_accent)
                        .setContentTitle(data.get("title"))
                        .setContentText(dataBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle());
        return notificationBuilder;
    }
}

