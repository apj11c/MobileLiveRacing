package edu.fsu.cs.mobile.mobileliveracing;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MLRLocationManager {

    private static final int UPDATE_INTERVAL = 1000;
    private static LocationRequest mLocationRequest;
    private static FusedLocationProviderClient mFusedLocationClient;
    private static LocationCallback mLocationCallback;

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
                    mActivity.onReceiveNewLoc(LocationEntry.toLocationEntry(location, mActivity));
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
