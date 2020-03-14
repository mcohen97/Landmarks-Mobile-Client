package com.acr.landmarks.services.contracts;

import android.arch.lifecycle.LiveData;
import android.location.Location;

import com.acr.landmarks.models.Landmark;

import java.util.List;

public interface ILandmarksService {

    LiveData<List<Landmark>> getLandmarks(Location location, double radius);

    LiveData<Landmark> getLandmarkById(int id);

    LiveData<Landmark> getSelectedLandmark();
}
