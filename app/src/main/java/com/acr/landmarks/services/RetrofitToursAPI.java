package com.acr.landmarks.services;

import com.acr.landmarks.models.Tour;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitToursAPI {

    @GET("tours")
    Call<List<Tour>> getToursInRange(@Query("lat") double latitude, @Query("lng") double longitude, @Query("dist") double distance);

    @GET("tours/{id}")
    Call<Tour> getTour(@Path("id") int id);
}
