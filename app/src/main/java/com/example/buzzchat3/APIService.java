package com.example.buzzchat3;

import com.example.buzzchat3.Notifications.MyResponse;
import com.example.buzzchat3.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAkyS-xMg:APA91bFyVddZTi7kRSNbm3DR-woa4EPJGIwv2IpZGouU2FXvYC392fO_9nKgo1EKRTUWz_O0CBCh5iuEeWwmb6GN2kaJrDUE_CBAdMNUyflXuumXctGX_BxE533CyZghoh_9NLgd6pAb"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
