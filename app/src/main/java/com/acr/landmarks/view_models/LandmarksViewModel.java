package com.acr.landmarks.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;
import android.util.Log;
import android.util.Pair;

import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.persistence.contracts.LandmarkStorage;
import com.acr.landmarks.services.contracts.DebugConstants;
import com.acr.landmarks.services.contracts.ILandmarksService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

public class LandmarksViewModel extends ViewModel {

    private ILandmarksService landmarksService;
    private LandmarkStorage markersStorage;

    private LiveData<Landmark> selectedLandmark;

    private MutableLiveData<Boolean> askedForDirections;

    private Double lastRadius;
    private Location lastCenterLocation;
    private LiveData<List<Landmark>> landmarksInRange;
    private MediatorLiveData liveDataMerger;
    private final MutableLiveData<Pair<Location, Double>> geoFence;
    private final AtomicBoolean lastDataRetrieved;
    private boolean firstGeofenceAssigned;
    private static final double SERVERSIDE_MIN_DISTANCE = 2.5;


    @Inject
    public LandmarksViewModel(ILandmarksService service, LandmarkStorage cache) {
        markersStorage = cache;
        landmarksService = service;
        liveDataMerger = new MediatorLiveData();
        geoFence = new MutableLiveData<Pair<Location, Double>>();
        askedForDirections = new MutableLiveData<Boolean>();
        lastDataRetrieved = new AtomicBoolean(false);
        firstGeofenceAssigned = false;
        setDefaultData();
    }

    private Location generateDefaultLocation() {
        Location defaultLocation = new Location(new String());
        defaultLocation.setLatitude(-34.858757);//harcoded,default location, take to config file
        defaultLocation.setLongitude(-56.032397);
        return defaultLocation;
    }


    private void setDefaultData() {
        geoFence.setValue(new Pair<>(generateDefaultLocation(), new Double(SERVERSIDE_MIN_DISTANCE)));
        lastCenterLocation = null;
        lastRadius = null;
        selectedLandmark = landmarksService.getSelectedLandmark();
        landmarksInRange = landmarksService.getLandmarks(geoFence.getValue().first, geoFence.getValue().second);
        liveDataMerger = new MediatorLiveData<>();
        liveDataMerger.addSource(geoFence, centerRadius ->
                updateGeofence((Pair<Location, Double>) centerRadius));
        liveDataMerger.addSource(landmarksInRange,
                value -> updateLandmarks(value));
    }

    private void updateLandmarks(Object value) {
        lastDataRetrieved.set(true);
        liveDataMerger.setValue(value);
        new Thread(() -> {
            markersStorage.insertLandmarks((List<Landmark>) value);
        }).start();
    }

    private void updateGeofence(Pair<Location, Double> value) {
        if ((lastCenterLocation == null) || (lastRadius == null)
                || (!value.first.equals(lastCenterLocation)) || !value.second.equals(lastRadius)) {
            lastCenterLocation = value.first;
            lastRadius = value.second;
            //avoiding the asignation of first (default value) affect the landmarks collection
            if (firstGeofenceAssigned) {
                reload();
            } else {
                firstGeofenceAssigned = true;
            }
        }
    }

    private void reload() {
        lastDataRetrieved.set(false);
        landmarksService.getLandmarks(geoFence.getValue().first, geoFence.getValue().second);

        new Thread(() -> {
            List<Landmark> cachedLandmarks = new ArrayList<>();
            if (!lastDataRetrieved.get()) {
                cachedLandmarks = markersStorage.getSavedLandmarks(geoFence.getValue().first, geoFence.getValue().second);
            }
            if (!lastDataRetrieved.get()) {
                Log.d(DebugConstants.AP_DEX, "Retrieved landmarks from cache, time: " + System.currentTimeMillis());
                liveDataMerger.postValue(cachedLandmarks);
            }
        }).start();
    }

    public void setGeofence(Location aLocation, Double aRadius) {
        if (aRadius < SERVERSIDE_MIN_DISTANCE) {
            aRadius = SERVERSIDE_MIN_DISTANCE;
        }
        geoFence.setValue(new Pair<>(aLocation, aRadius));
    }

    public LiveData<List<Landmark>> getLandmarks() {
        return liveDataMerger;
    }

    public void setSelectedLandmark(int id) {
        selectedLandmark = landmarksService.getLandmarkById(id);
    }

    public LiveData<Landmark> getSelectedLandmark() {
        return selectedLandmark;
    }

    public MutableLiveData<Boolean> getAskedForDirections() {
        return askedForDirections;
    }

    public void setAskedForDirections(MutableLiveData<Boolean> askedForDirections) {
        this.askedForDirections = askedForDirections;
    }

}
