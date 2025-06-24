package com.example.pushnotificationdemo.sendnotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FirebaseNotificationService {
    @POST("v1/projects/push-notification-demo-faf21/messages:send")
    Call<FcmResponse> sendNotification(@Body MessageRequest messageRequest);

}
