package com.acr.landmarks.models;

import java.util.ArrayList;

public class Tour {
    public ArrayList<Integer> landmarksIds;
    public String imageFile;
    public int id;
    public String title;
    public String description;
    public String category;

    public Tour() {
    }

    @Override
    public boolean equals(Object o) {
        return ((Tour) o).id == id;
    }
}
