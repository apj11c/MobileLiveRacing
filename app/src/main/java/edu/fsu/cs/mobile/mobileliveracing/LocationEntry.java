package edu.fsu.cs.mobile.mobileliveracing;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LocationEntry {
    private static final String TAG = LocationEntry.class.getCanonicalName();

    private static final int UPDATE_INTERVAL = 5000;
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String CURRENT_TIME = "currentTime";
    private static final String USER_NAME = "userName";

    private static LocationRequest mLocationRequest;
    private static FusedLocationProviderClient mFusedLocationClient;
    private static LocationCallback mLocationCallback;

    private double lat;
    private double lng;
    private String userName;
    private long currentTime;

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

    private static LocationEntry toLocationEntry(Location loc, MainActivity mActivity){

        return new LocationEntry(loc.getLatitude(), loc.getLongitude(), mActivity.getUser());

    }

    private static void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private static void createCallBack(final MainActivity mActivity){

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mActivity.onReceiveNewLoc(toLocationEntry(location, mActivity));
                }
            };
        };

    }

    public static void startLocationUpdates(final MainActivity mActivity){

        mActivity.checkLocationPermission();

        createLocationRequest();

        createCallBack(mActivity);

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null );

    }

    public static void stopLocationUpdates(final MainActivity mActivity) {

        if(mFusedLocationClient == null){

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);

        }

        if(mLocationCallback == null){

            createCallBack(mActivity);

        }

        mFusedLocationClient.removeLocationUpdates(mLocationCallback);

    }

}

/*
public static Location getLastLocation(MainActivity mActivity){

        mActivity.checkLocationPermission();
        mLastLoc = null;
        //return getLastKnownLocation(mActivity);

        getLastLocationNewMethod(mActivity);

        for(int i = 0; i < 100; i++){

            if (mLastLoc != null){

                Log.i(TAG, "Found lastLoc");
                return mLastLoc;

            }else{

                Log.i(TAG, "mLastLoc = null for iteration "+i);

            }

        }

        return null;

    }

    private static void getLastLocationNewMethod(MainActivity mainActivity){
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity);

        mLastLoc = null;

        try{

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                //getAddress(location);
                                mLastLoc = location;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });

        }catch(SecurityException s){

            Log.e(TAG, "Security Exception: Permission probably not found");

        }

    }
 */