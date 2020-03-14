package com.acr.landmarks.services.contracts;

import android.location.Location;

public interface ILocationUpdatesService {
    void updateLocation(Location currentLocation, String token);
}
