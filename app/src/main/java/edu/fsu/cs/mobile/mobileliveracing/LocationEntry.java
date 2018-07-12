package edu.fsu.cs.mobile.mobileliveracing;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LocationEntry {
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String CURRENT_TIME = "currentTime";
    public static final String USER_NAME = "userName";

    public double lat;
    public double lng;
    private String userName;
    public long currentTime;

    public LocationEntry(double lat, double lng, FirebaseUser mUser){

        this.lat = lat;
        this.lng = lng;
        if(mUser == null){

            this.userName = "NULL";

        }else{

            this.userName = mUser.getDisplayName();

        }
        this.currentTime = Calendar.getInstance().getTimeInMillis();

    }

    public LocationEntry setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
        return this;
    }

    public LocationEntry setUserName(String userName){

        this.userName = userName;
        return this;
    }

    public double getLat(){

        return this.lat;
    }

    public double getLng() {
        return lng;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public String getUserName() {
        return userName;
    }

    public LocationEntry(){

        this(-1.0,-1.0,null);

    }

    @Override
    public String toString() {
        return (lat + "," + lng + "," + userName + "," + currentTime);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(LAT, lat);
        result.put(LNG, lng);
        result.put(USER_NAME, userName);
        result.put(CURRENT_TIME, currentTime);

        return result;
    }

    public static LocationEntry fromDataSnapshot(DataSnapshot userSnapshot) {
        String key = (String) userSnapshot.getKey();
        String username = (String) userSnapshot.child(USER_NAME).getValue();
        Double lat = (Double) userSnapshot.child(LAT).getValue();
        Double lng= (Double) userSnapshot.child(LNG).getValue();
        long currentTime = (long) userSnapshot.child(CURRENT_TIME).getValue();

        return (new LocationEntry(lat, lng, null).setCurrentTime(currentTime).setUserName(username));
    }

}
