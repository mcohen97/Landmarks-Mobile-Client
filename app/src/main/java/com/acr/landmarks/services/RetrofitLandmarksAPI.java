package com.acr.landmarks.services;

import com.acr.landmarks.models.Landmark;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitLandmarksAPI {

    @GET("landmarks")
    Call<List<Landmark>> getLandmarksInRange(@Query("lat") double latitude, @Query("lng") double longitude, @Query("dist") double distance);

    @GET("landmarks/{id}")
    Call<Landmark> getLandmark(@Path("id") int id);
}
