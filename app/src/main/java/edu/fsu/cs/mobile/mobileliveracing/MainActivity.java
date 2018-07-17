package edu.fsu.cs.mobile.mobileliveracing;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "error_checking";//MainActivity.class.getCanonicalName();
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    //firebase tests
    public static final int RC_SIGN_IN = 1234;

    public View.OnClickListener startSolo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           OnFragmentChanged(FRAGMENT_RACE);
        }
    };

    private static final String TEMP_RACE_NAME = "tempFastestRace";

    public static final String FRAGMENT_MAIN = "main";
    public static final String FRAGMENT_MAP = "map";
    public static final String FRAGMENT_FRIEND = "friend";
    public static final String FRAGMENT_RACE = "race";
    public static final String LOCATION_REFRESH_TIME = "map";
    public static final String LOCATION_REFRESH_DISTANCE = "map";
    private RaceFragment race;

    private LocationEntry oldLoc;

    //firebase tests
    private FirebaseManager mFirebase;

    public LocationManager mLocationManager;
    public Location location;

    EditText friendUser;
    Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "MainFragment.onCreate()");

        //OnFragmentChanged(FRAGMENT_MAIN);
        //OnFragmentChanged(FRAGMENT_FRIEND);
        OnFragmentChanged(FRAGMENT_RACE);
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



        //location permissions setup
        checkLocationPermission();

    }

    @Override
    protected void onResume(){

        super.onResume();

        //firebase tests

        if(mFirebase == null){

            //firebase tests
            //initialize manager
            mFirebase = new FirebaseManager(this);

            //start firebase session with login
            mFirebase.startAuth();

        }

        //assumes mFirebase has been initialized and startAuth() called

        //attach listener to react to new inserts
        mFirebase.attachListener();

        //create a LocationEntry object. Contains lat, lng, timeCreated and username as of now
        //might add or remove members
        LocationEntry tLoc = new LocationEntry(1.233424, 4.3233, mFirebase.getCurrentUser());

        //set race name after asking user
        mFirebase.setSessionName(TEMP_RACE_NAME);

        //add new LocationEntry objects to database
        //mFirebase.addToDatabase(tLoc);
        //mFirebase.addToDatabase(new LocationEntry(324.324324234, 33.3243423, mFirebase.getCurrentUser()));

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
            case FRAGMENT_FRIEND:
                Log.i(TAG,"starting friend fragment.");
                FindFriendFragment friend = new FindFriendFragment();
                fragmentTransaction.replace(R.id.frame,friend);
                fragmentTransaction.commit();
              //  Button ready = findViewById(R.id.find_friend);
                //ready.setOnClickListener(new View.OnClickListener() {
                  //  @Override
                    //public void onClick(View v) {
                      //  OnFragmentChanged(FRAGMENT_RACE);
                   // }
               // });
                //stop updates
                MLRLocationManager.stopLocationUpdates(this);

                break;
            case FRAGMENT_RACE:
                mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                Log.i(TAG,"case FRAGMENT_RACE");

                Log.i(TAG, "MainFragment.onCreate(): Checking Permission..");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            1);
                    //return;
                }

                Log.i(TAG, "race fragment: Permissions granted");

                if(isLocPermissionGranted()) {
                    Log.i(TAG,"racefrag: locPermissionGranted");
                    location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.i(TAG,"racefrag: location set");
                }
                else{

                    if(location == null){
                        Log.i(TAG,"Location is null.");
                    }

                    location = new Location(LocationManager.GPS_PROVIDER);
                    location.setLatitude(0);
                    location.setLongitude(0);

                }

                //starts receiving location updates through onReceiveNewLoc
                MLRLocationManager.startLocationUpdates(this);

                race = new RaceFragment();
                fragmentTransaction.replace(R.id.frame, race);
                fragmentTransaction.commit();

                break;
            case FRAGMENT_MAIN:
                Log.i(TAG,"case FRAGMENT_MAIN");
                MainFragment loginFragment = new MainFragment();
                fragmentTransaction.replace(R.id.frame, loginFragment);
                fragmentTransaction.commit();
                break;
            case FRAGMENT_MAP:
                mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                Log.i(TAG, "MainFragment.onCreate(): Checking Permission..");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            1);
                    //return;
                }

                Log.i(TAG, "MainFragment.onCreate(): Permissions granted");

                if(isLocPermissionGranted()) {

                    location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                }else{

                    if(location == null){
                        Log.i(TAG,"Location is null.");
                    }

                    location = new Location(LocationManager.GPS_PROVIDER);
                    location.setLatitude(0);
                    location.setLongitude(0);

                }

                double latitude = location.getLatitude();
                Log.i(TAG,"Latitude got.");
                double lng = location.getLongitude();
                DrawMap(latitude, lng);

                //starts receiving location updates through onReceiveNewLoc
                MLRLocationManager.startLocationUpdates(this);

                break;
            default:
                break;
        }
    }

    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.buttonJoin:
                friendUser = findViewById(R.id.friendName);
                joinButton = findViewById(R.id.buttonJoin);
                String friendName = friendUser.getText().toString().trim();
                if (friendName.length() == 0) {
                    friendUser.setError("Please enter a friend's email!");
                }
                else {
                    //search to make sure friend is in firebase
                    //if not, setError
                    //otherwise...
                    //do join game work
                }
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

    public void DrawMap(LocationEntry loc){
        DrawMap(loc.getLat(), loc.getLng());
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.locPermTitle)
                        .setMessage(R.string.locPermBody)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean isLocPermissionGranted(){

        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);

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
            case R.id.find_friend:
                OnFragmentChanged(FRAGMENT_FRIEND);
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //locationPermissionGranted = true;
                    }

                } else {

                    //denied
                    //locationPermissionGranted = false;

                }
                return;
            }

        }
    }

    public FirebaseUser getUser(){

        return mFirebase.getCurrentUser();

    }

    private String getUserName(){

        if(mFirebase.getCurrentUser() == null){

            return "NULL";

        }else{

            return mFirebase.getCurrentUser().getDisplayName();

        }

    }

    //firebase tests
    //called everytime a new LocationEntry object is inserted into the db
    public void onReceiveNewLocFirebase(LocationEntry loc){

        if(loc!= null){

            //TODO
            Log.i(TAG, "fireBase update "+loc.toString());
            //DrawMap(loc.getLat(), loc.getLng());

        }else{

            Log.e(TAG, "loc is null");

        }

    }

    public void onReceiveNewLoc(LocationEntry loc){

        if(loc!= null){
            if(race != null){
                if(oldLoc == null){oldLoc = loc;}
                double x = 0;
                x = (oldLoc.getLat() - loc.getLat()) * (oldLoc.getLat() - loc.getLat());
                x += (oldLoc.getLng() - loc.getLng()) * (oldLoc.getLng() - loc.getLng());
                x = Math.sqrt(x) * 100000;
                if(race.updateMyDistance(x)){
                    Log.i(TAG, "YOU WON THE RACE");
                    // switch to win screen.
                }
            }
            //TODO
            Log.i(TAG, "phone location " + loc.toString());
            //DrawMap(loc.getLat(), loc.getLng());

           // DrawMap(loc);

        }else{

            Log.e(TAG, "loc is null");

        }

    }

}
