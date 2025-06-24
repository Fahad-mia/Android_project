package com.example.pushnotificationdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SenderActivity extends AppCompatActivity {

    EditText messageEditText;
    Button getListBtn;
    ListView userList;
    ArrayList<String> tokenList;
    ArrayAdapter<String> listAdapter;
    String path; // Declare here but initialize in onCreate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sender);

        path = getIntent().getStringExtra("user"); // ✅ Move it here

        messageEditText = findViewById(R.id.massageId);
        getListBtn = findViewById(R.id.getListBtnId);
        userList = findViewById(R.id.userListId);

        tokenList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tokenList);
        userList.setAdapter(listAdapter);

        // Optional: show message if opened from FCM
        String message = getIntent().getStringExtra("message_body");
        if (message != null) {
            messageEditText.setText(message);
        }

        getListBtn.setOnClickListener(v -> fetchUsersFromFirebase());
    }

    private void fetchUsersFromFirebase() {
        if (path == null || path.isEmpty()) {
            Toast.makeText(this, "Invalid database path", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                tokenList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    UserInfo userInfo = userSnapshot.getValue(UserInfo.class);
                    if (userInfo != null) {
                        String item = userInfo.userName + ": " + userInfo.userToken;
                        tokenList.add(item);
                    }
                }
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SenderActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
