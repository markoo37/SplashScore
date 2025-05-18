package com.example.splashscore;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService
        extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "news_channel";

    @Override
    public void onNewToken(String token) {
        Log.d("FCM", "New token: " + token);
        // (opcionális) elküldheted a szerveredre
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Ha a payloadban van notification blokk:
        String title = remoteMessage.getNotification() != null
                ? remoteMessage.getNotification().getTitle()
                : "Új hír";
        String body  = remoteMessage.getNotification() != null
                ? remoteMessage.getNotification().getBody()
                : "Nézd meg az újdonságokat!";
        sendNotification(title, body);
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        Uri defaultSound = RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_NOTIFICATION
        );
        NotificationManager nm =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Android O+ csatorna
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Hírek",
                    NotificationManager.IMPORTANCE_HIGH
            );
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder nb = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_news)  // legyen a drawable-dben
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        nm.notify((int)System.currentTimeMillis(), nb.build());
    }
}
