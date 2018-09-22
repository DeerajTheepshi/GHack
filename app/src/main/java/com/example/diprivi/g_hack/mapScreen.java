package com.example.diprivi.g_hack;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class mapScreen extends AppCompatActivity implements OnMapReadyCallback {

    boolean i;
    double lat,lon;
    List<ItemList> mainList;
    int length;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        i = true;
        getLocation();
    }

    private void getLocation() {

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mapScreen.this);
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
                                        mainList = items;
                                        length = mainList.size();
                                        for(int i=0;i<length;i++){
                                            ItemList cur = mainList.get(i);
                                            LatLng sydney = new LatLng(cur.getLat(),cur.getLon());
                                            googleMap.addMarker(new MarkerOptions().position(sydney)
                                                    .title(cur.getDecibel()+""));
                                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                                        }
                                        googleMap.setMinZoomPreference(20);
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
