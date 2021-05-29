package com.jiyong.apps.earthquake;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity
public class Earthquake {
    @NonNull
    @PrimaryKey
    private final String mId;
    private final Date mDate;
    private final String mDetails;
    private final Location mLocation;
    private final double mMagnitude;
    private final String mLink;

    public String getId(){
        return mId;
    }
    public Date getDate(){
        return mDate;
    }
    public String getDetails(){
        return mDetails;
    }
    public Location getLocation(){
        return mLocation;
    }
    public double getMagnitude(){
        return mMagnitude;
    }
    public String getLink(){
        return mLink;
    }
    public Earthquake(String id, Date date, String details, Location location, double magnitude, String link){
        mId=id;
        mDate=date;
        mDetails=details;
        mLocation=location;
        mMagnitude=magnitude;
        mLink=link;
    }
    @Override
    public String toString(){
        SimpleDateFormat sdf=new SimpleDateFormat("MM/dd    HH.mm", Locale.US);
        String dateString=sdf.format(mDate);
        return dateString+": "+mMagnitude+": "+mDetails;
    }
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Earthquake){
            return (((Earthquake)obj).getId().contentEquals(mId));
        }
        else{
            return false;
        }
    }
}
