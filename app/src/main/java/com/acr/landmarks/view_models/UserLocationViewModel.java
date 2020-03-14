package com.acr.landmarks.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;

import com.acr.landmarks.services.contracts.ILocationService;

import javax.inject.Inject;


public class UserLocationViewModel extends ViewModel {

    private final ILocationService locationService;
    private final LiveData<Location> current;

    @Inject
    public UserLocationViewModel(ILocationService service) {
        //locationService = LocationService.getInstance();
        locationService = service;
        current = locationService.getLocation();
    }

    public void setLocation(Location update) {
        locationService.setLocation(update);
    }

    public LiveData<Location> getLocation() {
        return current;
    }
}
