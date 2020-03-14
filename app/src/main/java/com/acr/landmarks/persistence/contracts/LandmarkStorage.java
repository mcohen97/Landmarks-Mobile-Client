package com.acr.landmarks.persistence.contracts;

import android.location.Location;

import com.acr.landmarks.models.Landmark;

import java.util.List;

public interface LandmarkStorage {

    List<Landmark> getSavedLandmarks(Location location, double radius);

    void deleteStorage();

    void insertLandmarks(List<Landmark> landmarks);
}
