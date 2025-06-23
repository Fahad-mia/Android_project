package com.example.pushnotificationdemo.messagingService;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.pushnotificationdemo.MainActivity;
import com.example.pushnotificationdemo.R;
import com.example.pushnotificationdemo.SenderActivity;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseMessingServices extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM_TOKEN", "Refreshed token: " + token);
        System.out.println("Token is" + token);
        // You can send this token to your server if needed
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "Notification Title";
        String message = "Notification Message";

        // Handle notification payload (if present)
        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getTitle() != null) {
                title = remoteMessage.getNotification().getTitle();
            }
            if (remoteMessage.getNotification().getBody() != null) {
                message = remoteMessage.getNotification().getBody();
            }
        }

        // Handle data payload (in case notification payload is absent)
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData().containsKey("title")) {
                title = remoteMessage.getData().get("title");
            }
            if (remoteMessage.getData().containsKey("message")) {
                message = remoteMessage.getData().get("message");
            }
        }

        Log.d("FCM_TITLE", title);
        Log.d("FCM_MESSAGE", message);

        showNotification(title, message);
    }


    public void showNotification(String title, String message) {
        String channelId = "customNotificationChannel";
        String channelName = "Notification Test Channel";
        Log.d("Shown", "showNotification: "+"");

        // ✅ Create notification channel BEFORE building the notification (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // ✅ Prepare intent to launch when user taps the notification
        Intent intent = new Intent(this, SenderActivity.class);
        intent.putExtra("message_body", message);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        // ✅ Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notificationbadge) // Make sure this icon exists
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        // ✅ Check and request permission for Android 13+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {

            // ✅ Use a unique ID so each notification appears separately
            int notificationId = (int) System.currentTimeMillis();
            manager.notify(notificationId, builder.build());
        } else {
            Log.w("FCM", "Notification permission not granted.");
        }
    }


    private static String generateKey(InputStream serviceAccountStream) throws Exception {
        List<String> scrops = Arrays.asList("https://www.googleapis.com/auth/userinfo.email",
                "https://www.googleapis.com/auth/firebase.database",
                "https://www.googleapis.com/auth/firebase.messaging");
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccountStream)
                .createScoped(scrops);
        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }
    public static String getServerKey(Context context) {
        try
        {
            InputStream stream = context.getAssets().open("serverAccount.json");
            return generateKey(stream);
        } catch (Exception e) {
             e.printStackTrace();
             return "";
        }


    }
}
