package com.example.pushnotificationdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pushnotificationdemo.messagingService.FirebaseMessingServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    EditText userName;
    EditText password;
    Button loginBtn;
    String fcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // ✅ Initialize Firebase
        FirebaseApp.initializeApp(this);

        // ✅ Notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );
            }
        }

        // ✅ Get FCM token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM_TOKEN", "Fetching FCM token failed", task.getException());
                        return;
                    }
                    fcmToken = task.getResult();
                    Log.d("FCM_TOKEN", "Device token: " + fcmToken);
                });

        // ✅ (Optional) Get server key in background
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String token = FirebaseMessingServices.getServerKey(MainActivity.this);
            Log.d("ServerKey", "Server token: " + token);
        });

        // ✅ UI Elements
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginButton);

        // ✅ On Click
        loginBtn.setOnClickListener(v -> {
            String user = userName.getText().toString().trim();

            if (user.isEmpty()) {
                Toast.makeText(MainActivity.this, "User Name must be provided", Toast.LENGTH_LONG).show();
                return;
            }

            if (fcmToken == null || fcmToken.isEmpty()) {
                Toast.makeText(MainActivity.this, "FCM Token not generated yet. Please wait.", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(getApplicationContext(), SenderActivity.class);
            startActivity(intent);

            saveToken(user, fcmToken);
        });
    }

    // ✅ Save token to Firebase
    void saveToken(String user, String token) {
        try {
            Log.d("SAVE_TOKEN", "User: " + user + ", Token: " + token);

            UserInfo userInfo = new UserInfo(user, token);
            DatabaseReference ref = FirebaseDatabase
                    .getInstance()
                    .getReference("UsersList");

            ref.push().setValue(userInfo);

            Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("SAVE_TOKEN", "Failed to save", e);
            Toast.makeText(getApplicationContext(), "Failed To Save", Toast.LENGTH_LONG).show();
        }
    }
}
