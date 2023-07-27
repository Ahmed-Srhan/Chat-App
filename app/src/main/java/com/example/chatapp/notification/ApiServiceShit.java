package com.example.chatapp.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiServiceShit {
    @Headers({
            "Content-Type:application/json",
            "AAAAfLq-XGI:APA91bHLAa-U0Vd99pZCzcBjSgifCX3iOD8ioXKRAG1QRU9xFxOJJvTtfPQhTE9IfRL_U_qu6kAfcjP4W9VpV5jiLxd9bWkxm88AFxVAnGSUThdahzpDdFB9cahvnFV0N0xNLMLrhv-v"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
