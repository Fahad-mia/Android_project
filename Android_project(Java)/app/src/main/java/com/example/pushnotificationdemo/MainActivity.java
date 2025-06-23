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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pushnotificationdemo.messagingService.FirebaseMessingServices;
import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final org.apache.commons.logging.Log log = LogFactory.getLog(MainActivity.class);
    EditText userName;
    EditText password;
    Button loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

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
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM_TOKEN", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    // ✅ Got the FCM token
                    String token = task.getResult();
                    Log.d("FCM_TOKEN", "Device: " + token);
//                    System.out.println("Token is: "+token);
                });
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String token = FirebaseMessingServices.getServerKey(MainActivity.this);

            // Log or use the token safely here
            Log.d("Serverkey", "Server token: " + token);
        });

        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginButton);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SenderActivity.class);
                startActivity(intent);

            }
        });
    }
}