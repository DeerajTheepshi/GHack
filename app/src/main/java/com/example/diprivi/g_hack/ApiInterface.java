package com.example.diprivi.g_hack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("cod")
    Call<EntireBody> postData(@Body String values);

    @POST("getCod")
    Call<EntireBody> getData(@Body String values);


}
