package com.example.buzzchat3.network;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService {

    @GET("session")
    Call<GetSessionResponse> getSession();
}
