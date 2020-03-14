package com.acr.landmarks.persistence;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.OnConflictStrategy;
import android.database.Cursor;


import java.util.List;

@Dao
public interface LandmarkDao {


    @Query("SELECT * FROM Landmark LIMIT 10")
    Cursor getStoredLandmarks();

    @Query("DELETE FROM Landmark")
    void clear();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<LandmarkEntity> landmarks);

}
