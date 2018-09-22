package com.example.diprivi.g_hack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_LOCATION = 12347;
    private final int REQUEST_MICROPHONE = 12346;

    TextView dBMeter;
    MediaRecorder mRecorder;
    Thread runner;

    int i = 0;

    int val = 0;

    double lat,lon;

    final Runnable updater = new Runnable() {

        public void run() {
            updateTv();
        }
    };

    @SuppressLint("HandlerLeak")
    final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            updateTv();
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.sendToListing:
                Intent intent = new Intent(this, Listing.class);
                startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.RECORD_AUDIO},
                    REQUEST_LOCATION);
            return;
        }


        dBMeter = findViewById(R.id.dBMeter);

        if (runner == null) {
            runner = new Thread() {
                public void run() {
                    while (runner != null) {
                        try {
                            Thread.sleep(1000);
                            Log.i("Thread put to sleep", "  ");
                        } catch (InterruptedException ignored) {
                        }
                        if(i<6)
                            mHandler.post(updater);
                        else{
                            runner = null;
                        }
                    }
                }
            };
            runner.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRecorder();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE);
        } else startRecorder();
    }

    public void startRecorder() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " +
                        android.util.Log.getStackTraceString(ioe));

            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
            try {
                mRecorder.start();
            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
        }
    }

    public void stopRecorder() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE);
        } else if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void updateTv() {
        if(i<5){
            int curr = soundDb(getAmplitude());
            if(curr>0){
                dBMeter.setText(Integer.toString(curr));
                val+=curr;
                Log.i("Gommale google daaw", curr+" "+val+" "+i);
                i++;
            }
        }
        else if(i==5){
            updatecloud();
            dBMeter.setText(Integer.toString(val/5));
            i++;
        }
    }

    public int soundDb(double ampl) {
        return (int) (20 * Math.log10(ampl));
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude());
        else
            return 0;

    }

//    public double getAmplitudeEMA() {
//        double amp = getAmplitude();
//        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
//        return mEMA;
//    }

    public void updatecloud() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();

        if (!isConnected)
            return;
        else {
            //TODO Put data in server
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! do the
                    // calendar task you need to do.
                }
                else if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startRecorder();
                }
                else
                    finish();
            }
        }
    }

    private void getLocation() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();

                            String value = "lat="+String.valueOf(lat)+"&&lan="+String.valueOf(lon)+"&&deci="+val/5;

                            ApiInterface apiService =
                                    ApiClient.getClient().create(ApiInterface.class);

                            Call<EntireBody> exampleCall = apiService.postData(value);

                            exampleCall.enqueue(new Callback<EntireBody>()
                            {
                                @Override
                                public void onResponse (Call < EntireBody > call, Response< EntireBody > response){
                                    int res_code = response.body().getRes_code();
                                    if(res_code!=200){
                                        Log.v("server_status","SUCCESS");
                                    }
                                    else{
                                        Log.v("server_status","FAILURE");
                                    }
                                }

                                @Override
                                public void onFailure (Call < EntireBody > call, Throwable t){
                                    Log.i("Failed",t.getMessage());
                                }
                            });
                        } else
                            Log.i("TAG", "Location null");
                    }
                });
    }
}
