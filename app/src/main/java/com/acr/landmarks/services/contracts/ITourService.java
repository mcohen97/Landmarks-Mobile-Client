package com.acr.landmarks.services.contracts;

import android.arch.lifecycle.LiveData;
import android.location.Location;

import com.acr.landmarks.models.Tour;

import java.util.List;

public interface ITourService {

    LiveData<List<Tour>> getTours(Location currentLocation, double currentRadius);

    LiveData<Tour> getTourById(int id);
}
