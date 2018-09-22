package com.example.diprivi.g_hack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("cod")
    Call<String> postData(@Body String a);
}
