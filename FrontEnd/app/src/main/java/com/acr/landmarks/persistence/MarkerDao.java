package com.acr.landmarks.persistence;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.OnConflictStrategy;
import android.database.Cursor;


import java.util.List;

@Dao
public interface MarkerDao {


    @Query("SELECT * FROM markers LIMIT 10")
    Cursor getStoredLandmarks();

    @Query("DELETE FROM markers")
    void clear();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<LandmarkMarkerEntity> landmarks);

}
