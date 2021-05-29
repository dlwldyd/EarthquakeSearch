package com.jiyong.apps.earthquake;


import android.content.Context;

import androidx.room.Room;

public class EarthquakeDatabaseAccessor {
    private static EarthquakeDatabase EarthquakeDatabaseInstance;
    private static final String EARTHQUAKE_DB_NAME="earthquake_db";

    private EarthquakeDatabaseAccessor(){

    }
    public static EarthquakeDatabase getInstance(Context context){
        if(EarthquakeDatabaseInstance==null){
            //SQLite 데이터베이스 생성 or 연다, 해당 room 데이터베이스 인스턴스 반환
            EarthquakeDatabaseInstance= Room.databaseBuilder(context, EarthquakeDatabase.class, EARTHQUAKE_DB_NAME).build();
        }
        return EarthquakeDatabaseInstance;
    }
}
