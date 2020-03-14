package com.acr.landmarks.models;

import java.util.Comparator;

public class LandmarkDistanceComparator implements Comparator<Landmark> {

    @Override
    public int compare(Landmark o1, Landmark o2) {
        return o1.compareTo(o2);
    }
}
