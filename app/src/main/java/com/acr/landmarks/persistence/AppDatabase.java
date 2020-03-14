package com.acr.landmarks.persistence;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;


@Database(entities = {LandmarkEntity.class}, version = 2, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract LandmarkDao landmarksDao();
}
