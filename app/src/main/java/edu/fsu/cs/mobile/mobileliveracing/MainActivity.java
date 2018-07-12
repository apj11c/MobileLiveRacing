package edu.fsu.cs.mobile.mobileliveracing;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();
    //firebase tests
    public static final int RC_SIGN_IN = 1234;
    private static final String TEMP_RACE_NAME = "tempFastestRace";

    public static final String FRAGMENT_MAIN = "main";
    public static final String FRAGMENT_MAP = "map";
    public static final String LOCATION_REFRESH_TIME = "map";
    public static final String LOCATION_REFRESH_DISTANCE = "map";

    //firebase tests
    private FirebaseManager mFirebase;

    public LocationManager mLocationManager;
    public Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "MainFragment.onCreate()");

        OnFragmentChanged(FRAGMENT_MAIN);

        String jsonStr = getJSONFromAssets();


        Log.i(TAG, "MainFragment.onCreate(): jString = "+jsonStr);
        /*JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonStr);
            // Getting JSON Array node
            JSONArray locations = jsonObj.getJSONArray("data");
            for (int i = 0; i < locations.length(); i++){
                JSONObject c = locations.getJSONObject(i);
                Log.i(TAG, "MainFragment.onCreate(): jString = "+c.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        //firebase tests
        //initialize manager
        mFirebase = new FirebaseManager(this);

        //start firebase session with login
        mFirebase.startAuth();

    }

    @Override
    protected void onResume(){

        super.onResume();

        //firebase tests
        //assumes mFirebase has been initialized and startAuth() called in onCreate()

        //attach listener to react to new inserts
        mFirebase.attachListener();

        //create a LocationEntry object. Contains lat, lng, timeCreated and username as of now
        //might add or remove members
        LocationEntry tLoc = new LocationEntry(1.233424, 4.3233, mFirebase.getCurrentUser());

        //set race name after asking user
        mFirebase.setSessionName(TEMP_RACE_NAME);

        //add new LocationEntry objects to database
        mFirebase.addToDatabase(tLoc);
        mFirebase.addToDatabase(new LocationEntry(324.324324234, 33.3243423, mFirebase.getCurrentUser()));

    }

    @Override
    protected void onDestroy(){

        super.onDestroy();

        //firebase tests

        //end firebase session
        mFirebase.endSession();

    }

    public String getJSONFromAssets() {
        String json = null;
        try {
            InputStream inputData = getApplicationContext().getResources().openRawResource(R.raw.locations);//am.get("locations.json");
            int size = inputData.available();
            byte[] buffer = new byte[size];
            inputData.read(buffer);
            inputData.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void OnFragmentChanged(String key) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (key) {
            case FRAGMENT_MAIN:
                MainFragment loginFragment = new MainFragment();
                fragmentTransaction.replace(R.id.frame, loginFragment);
                fragmentTransaction.commit();
                break;
            case FRAGMENT_MAP:
                mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                Log.i(TAG, "MainFragment.onCreate(): Checking Permission..");

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            1);
                    //return;
                }
                Log.i(TAG, "MainFragment.onCreate(): Permissions granted");
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                double latitude = location.getLatitude();
                double lng = location.getLongitude();
                DrawMap(latitude, lng);
                break;
            default:
                break;
        }
    }

    public void DrawMap(double latitude, double longitude){
        Log.i(TAG, "MainActivity.DrawMap() Latitude = "+latitude);
        Log.i(TAG, "MainActivity.DrawMap() Longitude = "+longitude);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        args.putDouble(MapFragment.ARG_LATITUDE,latitude);
        args.putDouble(MapFragment.ARG_LONGITUDE,longitude);

        MapFragment mapFragment = new MapFragment();
        mapFragment.setArguments(args);
        Log.i(TAG, "MainActivity.DrawMap(): Set args in bundle");
        fragmentTransaction.replace(R.id.frame, mapFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_main:
                OnFragmentChanged(FRAGMENT_MAIN);
                break;
            case R.id.option_current_location:
                OnFragmentChanged(FRAGMENT_MAP);
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //firebase tests
        //send result to FirebaseManager
        if(mFirebase != null){

            mFirebase.handleActivityResult(requestCode, resultCode, data);

        }

    }

    //firebase tests
    //called everytime a new LocationEntry object is inserted into the db
    public void onReceiveNewLoc(LocationEntry loc){

        if(loc!= null){

            //TODO
            Log.i(TAG, loc.toString());

        }else{

            Log.e(TAG, "loc is null");

        }

    }


}
