package com.acr.landmarks.services;

import android.location.Location;
import android.util.Log;

import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitLocationService {

    private Retrofit retrofit;
    private RetrofitLocationAPI webService;

    public RetrofitLocationService(String apiBaseUrl){
        retrofit = new Retrofit.Builder()
                .baseUrl(apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build();
        webService = retrofit.create(RetrofitLocationAPI.class);
    }

    public void updateLocation(Location currentLocation, String token){
        Call<OkResult> landmark = webService.updateLocation(token,currentLocation.getLatitude(), currentLocation.getLongitude());
        landmark.enqueue(new Callback<OkResult>() {
            @Override
            public void onResponse(Call<OkResult> call, Response<OkResult> response) {
                if (response.isSuccessful()) {
                    Log.d("Retrofit","Llego location");
                }
            }
            @Override
            public void onFailure(Call<OkResult> call, Throwable t) {
                Log.d("Retrofit", "request failed");
            }
        });

    }
}