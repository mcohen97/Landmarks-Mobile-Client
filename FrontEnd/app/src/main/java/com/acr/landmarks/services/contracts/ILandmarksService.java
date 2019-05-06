package com.acr.landmarks.services.contracts;

import android.arch.lifecycle.LiveData;

import com.acr.landmarks.models.Landmark;

import java.util.List;

public interface ILandmarksService {

    LiveData<List<Landmark>> getLandmarks();

    LiveData<Landmark> getLandmarkById(int id);
}
