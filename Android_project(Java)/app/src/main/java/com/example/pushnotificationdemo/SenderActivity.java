package com.example.pushnotificationdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pushnotificationdemo.messagingService.FirebaseMessingServices;
import com.example.pushnotificationdemo.sendnotification.FcmResponse;
import com.example.pushnotificationdemo.sendnotification.FirebaseNotificationService;
import com.example.pushnotificationdemo.sendnotification.Message;
import com.example.pushnotificationdemo.sendnotification.MessageRequest;
import com.example.pushnotificationdemo.sendnotification.Notification;
import com.example.pushnotificationdemo.sendnotification.RetrofitClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SenderActivity extends AppCompatActivity {

    EditText messageEditText;
    Button getListBtn;
    ListView userList;

    ArrayList<UserInfo> userInfoList;
    ArrayList<String> userNameList; // just for displaying names
    ArrayAdapter<String> listAdapter;

    String path = "UsersList"; // Firebase node
    String TAG = "Fahad";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sender);

        messageEditText = findViewById(R.id.massageId);
        getListBtn = findViewById(R.id.getListBtnId);
        userList = findViewById(R.id.userListId);

        userInfoList = new ArrayList<>();
        userNameList = new ArrayList<>();

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNameList);
        userList.setAdapter(listAdapter);

        // Optional: prefill message if opened from notification
        String message = getIntent().getStringExtra("message_body");
        if (message != null) {
            messageEditText.setText(message);
        }

        getListBtn.setOnClickListener(v -> fetchUsersFromFirebase());

        // Click listener for list items
        userList.setOnItemClickListener((parent, view, position, id) -> {
            UserInfo selectedUser = userInfoList.get(position);
            String userName = selectedUser.userName;
            String userToken = selectedUser.userToken;

            sendPushNotification(userName, userToken);

            // You can send the FCM message here if needed
            // sendNotificationToUser(userToken, messageEditText.getText().toString());
        });
    }

    private void sendPushNotification(String userName, String userToken) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String token = FirebaseMessingServices.getServerKey(SenderActivity.this);
            FirebaseNotificationService service = RetrofitClient.getInstance(token)
                    .create(FirebaseNotificationService.class);
            String title = "Push Notification from app";
            String body = "Successful Push Notification ";
            Notification notification = new Notification(title, body);
            Message message = new Message(userToken, notification);
            MessageRequest request = new MessageRequest(message);

            service.sendNotification(request).enqueue(new Callback<FcmResponse>() {
                @Override
                public void onResponse(Call<FcmResponse> call, Response<FcmResponse> response) {
                    Log.d(TAG, "onResponse: "+new Gson().toJson(response));
                    if (response.isSuccessful()) {
                        Toast.makeText(SenderActivity.this, "Notification has sent to User: " + userName, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SenderActivity.this, "Failed to Send Notification ", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<FcmResponse> call, Throwable t) {
                    Toast.makeText(SenderActivity.this, "Failed "+ t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onFailure: "+ "Failed" + t.getMessage());
                }
            });

        });

    }

    private void fetchUsersFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userInfoList.clear();
                userNameList.clear();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    UserInfo userInfo = userSnapshot.getValue(UserInfo.class);
                    if (userInfo != null) {
                        userInfoList.add(userInfo);
                        userNameList.add(userInfo.userName); // show names in the ListView
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
