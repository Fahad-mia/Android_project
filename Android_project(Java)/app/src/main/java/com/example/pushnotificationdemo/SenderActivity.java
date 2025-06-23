package com.example.pushnotificationdemo;

import android.os.Bundle;
import android.widget.EditText;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SenderActivity extends AppCompatActivity {

    EditText messageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sender);

        messageEditText = findViewById(R.id.massageId);

        // ✅ Get intent extra and set to EditText
        String message = getIntent().getStringExtra("message_body");
        if (message != null) {
            messageEditText.setText(message);
        }
    }
}
