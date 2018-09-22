package com.example.diprivi.g_hack;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Listing extends AppCompatActivity {

    double lat, lon;
    ListView polList;
    boolean i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        polList = (findViewById(R.id.polList));
    }

    @Override
    protected void onResume() {
        super.onResume();
        i = true;
        getLocation();
    }

    private void getLocation() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Listing.this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            String value = "lat=" + String.valueOf(lat) + "&&lan=" + String.valueOf(lon);

                            if (i) {
                                ApiInterface apiService =
                                        ApiClient.getClient().create(ApiInterface.class);
                                getLocation();

                                Call<EntireBody> get_data = apiService.getData(value);

                                get_data.enqueue(new Callback<EntireBody>() {
                                    @Override
                                    public void onResponse(Call<EntireBody> call, Response<EntireBody> response) {
                                        List<ItemList> items = response.body().getItems();
                                        CustomAdapter adapter = new CustomAdapter(Listing.this, items);
                                        Log.i("Size", items.size() + " ");
                                        polList.setAdapter(adapter);
                                    }

                                    @Override
                                    public void onFailure(Call<EntireBody> call, Throwable t) {

                                    }
                                });
                                i = false;
                            }
                        } else
                            Log.i("TAG", "Location null");
                    }
                });
    }
}
