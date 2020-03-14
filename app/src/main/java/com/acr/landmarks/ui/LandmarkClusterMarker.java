package com.acr.landmarks.ui;


import android.graphics.Bitmap;

import com.acr.landmarks.models.Landmark;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class LandmarkClusterMarker implements ClusterItem {
    private LatLng position;
    private String title;
    private String snippet;
    private Bitmap iconPicture;
    private Landmark landmark;

    public LandmarkClusterMarker(LatLng position, String title, String snippet, Bitmap iconPicture, Landmark lm) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.landmark = lm;
    }

    @Override
    public LatLng getPosition() {
        return this.position;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getSnippet() {
        return this.snippet;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getIconPicture() {
        return iconPicture;
    }

    public Landmark getLandmark() {
        return landmark;
    }

    public void setIconPicture(Bitmap b) {
        iconPicture = b;
    }

    @Override
    public boolean equals(Object o) {
        return (o == null) || ((LandmarkClusterMarker) o).getLandmark().equals(getLandmark());
    }
}
