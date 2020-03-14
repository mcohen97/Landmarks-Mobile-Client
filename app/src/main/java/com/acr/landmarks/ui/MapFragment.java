package com.acr.landmarks.ui;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acr.landmarks.R;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.models.Tour;
import com.acr.landmarks.services.contracts.DebugConstants;
import com.acr.landmarks.services.contracts.IImageService;
import com.acr.landmarks.view_models.LandmarksViewModel;
import com.acr.landmarks.view_models.ToursViewModel;
import com.acr.landmarks.view_models.UserLocationViewModel;
import com.acr.landmarks.view_models.ViewModelProviderFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.GeoApiContext;
import com.google.maps.android.clustering.ClusterManager;


import java.util.ArrayList;
import java.util.List;


import javax.inject.Inject;

import dagger.android.support.DaggerFragment;


public class MapFragment extends DaggerFragment implements OnMapReadyCallback, ClusterManager.OnClusterItemInfoWindowClickListener<LandmarkClusterMarker>,
        GoogleMap.OnCameraIdleListener, ClusterManager.OnClusterItemClickListener<LandmarkClusterMarker> {

    private final String TAG = "MapFragment";
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private MapView mMapView;

    //location y camera update
    private static GoogleMap mMap;
    public static Location mUserLocation;
    private MapManager mMapManager;
    private boolean firstCameraMovement;

    //Clustering
    private ClusterManager<LandmarkClusterMarker> mClusterManager;
    private ClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<LandmarkClusterMarker> mClusterMarkers;

    private List<Landmark> mLandmarks;

    //ViewModels
    @Inject
    ViewModelProviderFactory viewModelFactory;
    private LandmarksViewModel landmarksViewModel;
    private UserLocationViewModel locationViewModel;
    private ToursViewModel toursViewModel;

    private LandmarkClusterMarker mSelectedMarker;
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    @Inject
    public IImageService imageService;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //imageService = new PicassoImageService(Config.getConfigValue(getContext(),"api_url"));
        mUserLocation = null;
        mClusterMarkers = new ArrayList<>();
        mLandmarks = new ArrayList<Landmark>();

        landmarksViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(LandmarksViewModel.class);
        toursViewModel = ViewModelProviders.of(getActivity()).get(ToursViewModel.class);
        locationViewModel = ViewModelProviders.of(getActivity()).get(UserLocationViewModel.class);
        firstCameraMovement = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = view.findViewById(R.id.fragmented_map);
        initGoogleMap(savedInstanceState);
        return view;
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (mUserLocation != null) {
            mMapManager.setCameraDefaultView(mUserLocation);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        GeoApiContext mGeoApiContext = new GeoApiContext.Builder()
                .apiKey(getString(R.string.google_map_api_key))
                .build();

        int colorForSelected = ContextCompat.getColor(getActivity(), R.color.blue1);
        int colorForUnselected = ContextCompat.getColor(getActivity(), R.color.darkGrey);
        mMap = map;
        mMapManager = new MapManager(mMap, mGeoApiContext, colorForSelected, colorForUnselected);
        loadMapStyle();
        observeUserLocation();
        observeLandmarksInRange();
        observeSelectedTour();
        observeDirectionsAsked();
    }

    private void observeUserLocation() {
        locationViewModel.getLocation().observe(this, location -> {
            boolean firstLocation = mUserLocation == null;

            mUserLocation = location;

            if (firstLocation) {
                mMapManager.setCameraDefaultView(mUserLocation);
                Double radius = new Double(mMapManager.getMapRangeRadius());
                landmarksViewModel.setGeofence(location, radius);
                toursViewModel.setGeofence(location, radius);
                mMap.setOnCameraIdleListener(this);
            }
        });
    }

    private void observeLandmarksInRange() {
        landmarksViewModel.getLandmarks().observe(this, landmarks -> {
            mLandmarks = landmarks;
            Log.d(DebugConstants.AP_DEX, "Receiving new landmarks, time: " + System.currentTimeMillis());
            Log.d(TAG, "Received " + landmarks.size() + " landmarks");
            if (!landmarks.isEmpty()) {
                addMapMarkers();
            }
        });
    }

    private void observeSelectedTour() {
        toursViewModel.getSelectedTour().observe(this, tour -> {
            if (tour != null) {
                drawTour(tour);
            } else {
                mMapManager.clearTours();
                mMapManager.clearRoutes();
            }

        });
    }

    private void observeDirectionsAsked() {
        landmarksViewModel.getAskedForDirections().observe(this, isAsked -> {

            if (isAsked) {
                LandmarkClusterMarker marker = mSelectedMarker;
                mClusterManagerRenderer.getMarker(marker).hideInfoWindow();
                Landmark selected = landmarksViewModel.getSelectedLandmark().getValue();
                marker = getLandmarksMarker(selected);
                mClusterManagerRenderer.getMarker(marker).showInfoWindow();
                if(marker != null) {
                    mMapManager.showDirections(marker,mUserLocation,ContextCompat.getColor(getActivity(), R.color.darkGrey));
                }
            }
            else{
                mMapManager.clearRoutes();
            }

        });
    }

    private void loadMapStyle() {
        boolean success = false;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        try {
            SharedPreferences preferences = getActivity().getSharedPreferences("PREFS", 0);
            String mapStyle = preferences.getString("mapStyle", "map_style_light");
            switch (mapStyle) {
                case "map_style_light":
                    success = mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this.getContext(), R.raw.map_style_light));
                    break;
                case "map_style_night":
                    success = mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this.getContext(), R.raw.map_style_night));
                    break;
            }

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    private boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private LandmarkClusterMarker getLandmarksMarker(Landmark landmark) {
        for (LandmarkClusterMarker marker : mClusterMarkers) {
            if (marker.getLandmark().equals(landmark)) {
                return marker;
            }
        }
        return null;
    }

    private boolean isTourSelected() {
        return toursViewModel.isTourSelected();
    }

    private void addMapMarkers() {
        if (mMap == null) {
            return;
        }
        boolean firstTime = mClusterManager == null;
        if (firstTime) {
            setUpClusterManager();
        }
        if (mClusterManagerRenderer == null) {
            setUpClusterManagerRenderer();
        }
        updateMapMarkers();
    }

    private void setUpClusterManager() {
        mClusterManager = new ClusterManager<LandmarkClusterMarker>(getActivity().getApplicationContext(), mMap);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
    }

    private void setUpClusterManagerRenderer() {
        mClusterManagerRenderer = new ClusterManagerRenderer(
                getActivity(),
                mMap,
                mClusterManager
        );
        mClusterManager.setRenderer(mClusterManagerRenderer);
    }

    private void updateMapMarkers() {

        int landmarkIndex = 0;
        for (Landmark landmark : mLandmarks) {
            try {
                addMarker(landmark, mLandmarks.size(), landmarkIndex);
                landmarkIndex++;
            } catch (NullPointerException e) {
                Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage());
            }
        }
    }

    private void addMarker(Landmark landmark, int size, int landmarkIndex) {

        boolean alreadyInMap = isMarkerInMap(landmark);
        Log.d(TAG, "ya se encuentra en el mapa: " + alreadyInMap);
        if (!alreadyInMap) {
            Log.d(TAG, "Loading marker " + landmarkIndex);
            String snippet = "Determine route to " + landmark.title + "?";
            LandmarkClusterMarker newClusterMarker = new LandmarkClusterMarker(
                    new LatLng(landmark.latitude, landmark.longitude),
                    landmark.title,
                    snippet,
                    null,
                    landmark
            );
            mClusterMarkers.add(newClusterMarker);
            imageService.loadBitmap(landmark.imageFiles[0], new IImageService.ImageLoadListener() {
                @Override
                public void onImageLoaded(Bitmap img) {
                    newClusterMarker.setIconPicture(img);
                    mClusterManager.addItem(newClusterMarker);
                    Log.d(TAG, "Loaded marker " + landmarkIndex);
                    //if(landmarkIndex == (size-1) ){
                    mClusterManager.cluster();
                    //Log.d(TAG,"Markers loaded");
                    //}
                }
            });
        }
    }

    private void removeUselessMarkers() {
        List<LandmarkClusterMarker> auxLandmarks = (List<LandmarkClusterMarker>) mClusterMarkers.clone();
        for (LandmarkClusterMarker marker : auxLandmarks) {
            if (!markerContainsLandmarkInRange(marker)) {
                mClusterMarkers.remove(marker);
                mClusterManager.removeItem(marker);
            }
        }
    }

    private boolean markerContainsLandmarkInRange(LandmarkClusterMarker marker) {
        return mLandmarks.contains(marker.getLandmark());
    }

    private boolean isMarkerInMap(Landmark landmark) {
        for (LandmarkClusterMarker marker : mClusterMarkers) {
            if (marker.getLandmark().equals(landmark)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(LandmarkClusterMarker landmarkClusterMarker) {
        landmarksViewModel.setSelectedLandmark(landmarkClusterMarker.getLandmark().id);
        mSelectedMarker = landmarkClusterMarker;
    }

    @Override
    public boolean onClusterItemClick(LandmarkClusterMarker landmarkClusterMarker) {
        landmarksViewModel.setSelectedLandmark(landmarkClusterMarker.getLandmark().id);
        mSelectedMarker = landmarkClusterMarker;
        if (isTourSelected() && isPartOfSelectedTour(landmarkClusterMarker)) {
            mMapManager.showDirections(landmarkClusterMarker, mUserLocation, ContextCompat.getColor(getActivity(), R.color.darkGrey));
            drawTour(toursViewModel.getSelectedTour().getValue());
        }
        return false;
    }

    private boolean isPartOfSelectedTour(LandmarkClusterMarker landmarkClusterMarker) {
        Tour selectedTour = toursViewModel.getSelectedTour().getValue();

        for (int landmarkId : selectedTour.landmarksIds) {
            if (landmarkId == landmarkClusterMarker.getLandmark().id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCameraIdle() {
        if (firstCameraMovement) {
            float newRadius = mMapManager.getMapRangeRadius();
            LatLng center = mMap.getCameraPosition().target;
            Location centerLocation = MapManager.latLngToLocation(center);
            Double radius = new Double(newRadius);
            Log.d(DebugConstants.AP_DEX, "Requesting new landmarks, time: " + System.currentTimeMillis());
            Log.d(DebugConstants.AP_DEX, "Requesting new tours, time: " + System.currentTimeMillis());
            landmarksViewModel.setGeofence(centerLocation, radius);
            toursViewModel.setGeofence(centerLocation, radius);
        } else {
            firstCameraMovement = true;
        }
    }

    public void drawTour(Tour selected) {

        ArrayList<Integer> landmarksIds = selected.landmarksIds;
        ArrayList<Landmark> landmarks = new ArrayList<Landmark>();

        for (Integer id : landmarksIds) {
            for (Landmark landmark : mLandmarks) {
                if (landmark.id == id) {
                    landmarks.add(landmark);
                }
            }
        }
        mMapManager.drawPolyline(landmarks);
    }

    public void resetTheMap() {
        if (mMap != null) {
            mMap.clear();

            if (mClusterManager != null) {
                mClusterManager.clearItems();
            }
            if (mClusterMarkers.size() > 0) {
                mClusterMarkers.clear();
                mClusterMarkers = new ArrayList<>();
            }
            mMapManager.resetMapPolylines();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
