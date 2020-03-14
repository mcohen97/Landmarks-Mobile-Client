package com.acr.landmarks.services;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;

import com.acr.landmarks.services.contracts.ILocationService;


public class LocationService implements ILocationService {

    private final MutableLiveData<Location> currentLocation;

    private static final LocationService instance = new LocationService();

    public static LocationService getInstance() {
        return instance;
    }

    private LocationService() {
        currentLocation = new MutableLiveData<>();
    }

    @Override
    public void setLocation(Location current) {
        boolean hasChanged = (currentLocation.getValue() == null) || (currentLocation.getValue().distanceTo(current) > 20);
        if (hasChanged) {
            currentLocation.setValue(current);
        }
    }

    @Override
    public LiveData<Location> getLocation() {
        return currentLocation;
    }

}