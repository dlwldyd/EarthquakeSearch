package com.jiyong.apps.earthquake;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EarthquakeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEarthquakes(List<Earthquake> earthquakes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEarthquake(Earthquake earthquake);

    @Delete
    void deleteEarthquake(Earthquake earthquake);

    @Query("SELECT * FROM earthquake ORDER BY mDate DESC")
    LiveData<List<Earthquake>> loadAllEarthquakes();

    @Query("SELECT mId as _id, "+"mDetails as suggest_text_1, "+ "mId as suggest_intent_data_id "+"FROM earthquake "+"WHERE mDetails LIKE :query "+"ORDER BY mdate DESC")
    Cursor generateSearchSuggestions(String query);

    @Query("SELECT * "+"FROM earthquake "+"WHERE mDetails LIKE :query "+"ORDER BY mdate DESC")
    LiveData<List<Earthquake>> searchEarthquakes(String query);

    //지진 id 받아 id와 일치하는 지진이 포함된 라이브데이터 반환
    @Query("SELECT * "+"FROM earthquake "+"WHERE mId=:id "+"LIMIT 1")
    LiveData<Earthquake> getEarthquake(String id);
}