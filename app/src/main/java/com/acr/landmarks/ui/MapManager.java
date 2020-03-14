package com.acr.landmarks.ui;

import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.services.contracts.DebugConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

public class MapManager implements GoogleMap.OnPolylineClickListener {

    private GoogleMap mMap;
    private GeoApiContext mGeoApiContext;
    private ArrayList<PolylineData> mPolyLinesData;
    private ArrayList<PolylineData> mTourPolylinesData;
    private int mPrimaryColor;
    private int mSecondaryColor;
    private static final int ROUTE_PADDING = 120;
    private static final int DEFAULT_ZOOM = 15;


    public MapManager(GoogleMap map, GeoApiContext geoApiContext, int selectedPolylineColor, int unselectedPolylineColor) {
        mMap = map;
        mGeoApiContext = geoApiContext;
        mPolyLinesData = new ArrayList<>();
        mPrimaryColor = selectedPolylineColor;
        mSecondaryColor = unselectedPolylineColor;
        mTourPolylinesData = new ArrayList<>();
    }

    public float getMapRangeRadius() {
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        Location center = latLngToLocation(bounds.getCenter());
        Location northEast = latLngToLocation(bounds.northeast);

        float radiusInMeters = center.distanceTo(northEast);
        float radiusInKm = radiusInMeters / 1000;

        return radiusInKm;
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) {
            return;
        }

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute) {
            boundsBuilder.include(latLngPoint);
        }

        int routePadding = ROUTE_PADDING;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    public void drawPolyline(List<Landmark> landmarks) {
        PolylineOptions options = new PolylineOptions();
        options.color(Color.RED);

        for (Landmark lm : landmarks) {
            options.add(new LatLng(lm.latitude, lm.longitude));
        }

        Polyline polyline = mMap.addPolyline(options);
        polyline.setClickable(true);
        mTourPolylinesData.add(new PolylineData(polyline, null));

        zoomRoute(polyline.getPoints());
        Log.d(DebugConstants.AP_DEX, "Selected tour drawn in map, time: " + System.currentTimeMillis());
    }

    public static Location latLngToLocation(LatLng googleData) {
        Location conversion = new Location(new String());
        conversion.setLatitude(googleData.latitude);
        conversion.setLongitude(googleData.longitude);
        return conversion;
    }

    public void showDirections(LandmarkClusterMarker marker, Location mUserLocation, int color) {
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        mUserLocation.getLatitude(),
                        mUserLocation.getLongitude()
                )
        );
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                addRoutesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.d("Directions", e.getMessage());
            }
        });
    }

    private void addRoutesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(() -> {

            clearRoutes();
            double minDuration = Integer.MAX_VALUE;
            Polyline shortestPath = null;
            List<Polyline> drawnPolylines = new ArrayList<>();
            for (DirectionsRoute route : result.routes) {
                Polyline polyline = drawRoute(route);
                drawnPolylines.add(polyline);
                double tempDuration = route.legs[0].duration.inSeconds;
                if (tempDuration < minDuration) {
                    minDuration = tempDuration;
                    shortestPath = polyline;
                }
            }
            highlightPolyline(shortestPath);
            zoomRoute(shortestPath.getPoints());
            Log.d(DebugConstants.AP_DEX, "Route to landmark drawn in map, time: " + System.currentTimeMillis());
        });
    }

    private Polyline drawRoute(DirectionsRoute route) {
        List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

        List<LatLng> newDecodedPath = transformPathLatLng(decodedPath);

        Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
        polyline.setColor(mPrimaryColor);
        polyline.setClickable(true);
        mPolyLinesData.add(new PolylineData(polyline, route.legs[0]));
        return polyline;
    }

    private List<LatLng> transformPathLatLng(List<com.google.maps.model.LatLng> decodedPath) {
        List<LatLng> newDecodedPath = new ArrayList<>();
        // This loops through all the LatLng coordinates of ONE polyline.
        for (com.google.maps.model.LatLng latLng : decodedPath) {
            newDecodedPath.add(new LatLng(latLng.lat, latLng.lng));
        }
        return newDecodedPath;
    }

    public void resetMapPolylines() {
        if (mPolyLinesData.size() > 0) {
            mPolyLinesData.clear();
            mPolyLinesData = new ArrayList<>();
        }
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        highlightPolyline(polyline);
    }

    private void highlightPolyline(Polyline polyline) {
        if (polyline == null) {
            return;
        }
        for (PolylineData polylineData : mPolyLinesData) {
            if (polyline.getId().equals(polylineData.getPolyline().getId())) {
                polylineData.getPolyline().setColor(mPrimaryColor);
                polylineData.getPolyline().setZIndex(1);

                DirectionsLeg leg = polylineData.getLeg();
                LatLng endLocation = new LatLng(leg.endLocation.lat, leg.endLocation.lng);
            } else {
                polylineData.getPolyline().setColor(mSecondaryColor);
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

    public void setCameraDefaultView(Location mUserLocation) {
        setCameraViewWithZoom(DEFAULT_ZOOM, mUserLocation);
    }

    private void setCameraViewWithZoom(float zoom, Location mUserLocation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mUserLocation.getLatitude(),
                        mUserLocation.getLongitude()), zoom));
    }

    public void clearRoutes() {
        for (PolylineData data : mPolyLinesData) {
            data.getPolyline().remove();
        }
        resetMapPolylines();
    }

    public void clearTours(){
        for (PolylineData data : mTourPolylinesData){
            data.getPolyline().remove();
        }
        mTourPolylinesData.clear();
        mTourPolylinesData = new ArrayList<>();
    }

}
