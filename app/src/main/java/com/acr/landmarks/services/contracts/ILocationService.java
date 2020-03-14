package com.acr.landmarks.services.contracts;

import android.arch.lifecycle.LiveData;
import android.location.Location;

public interface ILocationService {

    void setLocation(Location current);

    LiveData<Location> getLocation();


}
