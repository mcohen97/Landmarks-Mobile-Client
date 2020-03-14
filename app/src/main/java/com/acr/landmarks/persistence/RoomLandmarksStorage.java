package com.acr.landmarks.persistence;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;


import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.persistence.contracts.LandmarkStorage;

import java.util.ArrayList;
import java.util.List;

public class RoomLandmarksStorage implements LandmarkStorage {

    AppDatabase db;

    public RoomLandmarksStorage(Context c) {
        db = Room.databaseBuilder(c,
                AppDatabase.class, "landmarks")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Override
    public List<Landmark> getSavedLandmarks(Location location, double radius) {
        ArrayList<Landmark> markers = new ArrayList<>();
        Cursor rawData = db.landmarksDao().getStoredLandmarks();
        while (rawData.moveToNext()) {

            double lat = getDouble(rawData, "latitude");
            double lng = getDouble(rawData, "longitude");
            Location actual = new Location(new String());
            actual.setLatitude(lat);
            actual.setLongitude(lng);
            double distance = location.distanceTo(actual) / 1000;

            if (distance <= radius) {
                Landmark built = buildMarker(rawData, lat, lng);
                markers.add(built);
            }
        }
        return markers;
    }

    private Landmark buildMarker(Cursor cursor, double lat, double lng) {
        int id = getInt(cursor, "id");
        String title = getString(cursor, "title");
        String description = getString(cursor, "description");
        String[] images = getStringArray(cursor, "images");
        String[] audios = getStringArray(cursor, "audios");

        return new Landmark(id, title, description, lat, lng, images, audios);
    }

    private double getDouble(Cursor cursor, String name) {
        int index = cursor.getColumnIndexOrThrow(name);
        return cursor.getDouble(index);
    }

    private int getInt(Cursor cursor, String name) {
        int index = cursor.getColumnIndexOrThrow(name);
        return cursor.getInt(index);
    }

    private String getString(Cursor cursor, String name) {
        int index = cursor.getColumnIndexOrThrow(name);
        return cursor.getString(index);
    }

    @Override
    public void deleteStorage() {
        db.landmarksDao().clear();
    }

    @Override
    public void insertLandmarks(List<Landmark> landmarks) {
        List<LandmarkEntity> converted = convertLandmarks(landmarks);
        db.landmarksDao().insertAll(converted);
    }

    private String[] getStringArray(Cursor cursor, String images) {
        int index = cursor.getColumnIndexOrThrow(images);
        return Converter.fromString(cursor.getString(index));
    }

    private List<LandmarkEntity> convertLandmarks(List<Landmark> landmarks) {
        List<LandmarkEntity> converted = new ArrayList<>();
        for (Landmark l : new ArrayList<>(landmarks)) {
            LandmarkEntity entity = new LandmarkEntity();
            entity.id = l.id;
            entity.latitude = l.latitude;
            entity.longitude = l.longitude;
            entity.title = l.title;
            entity.images = l.imageFiles;
            entity.audios = l.audioFiles;
            converted.add(entity);
        }
        return converted;
    }


}


