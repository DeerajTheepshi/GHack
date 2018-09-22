package com.example.diprivi.g_hack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Listing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);

        ListView polList = (findViewById(R.id.polList));

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<EntireBody> get_data = apiService.getData("DUMMY");

        get_data.enqueue(new Callback<EntireBody>() {
            @Override
            public void onResponse(Call<EntireBody> call, Response<EntireBody> response) {
                List<ItemList> items = response.body().getItems();


            }

            @Override
            public void onFailure(Call<EntireBody> call, Throwable t) {

            }
        });

    }
}
