package edu.fsu.cs.mobile.mobileliveracing;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();

    public static final String FRAGMENT_MAIN = "main";
    public static final String FRAGMENT_MAP = "map";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "MainFragment.onCreate()");
        OnFragmentChanged(FRAGMENT_MAIN);
    }
    public void OnFragmentChanged(String key){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (key){
            case FRAGMENT_MAIN:
                MainFragment loginFragment = new MainFragment();
                fragmentTransaction.replace(R.id.frame, loginFragment);
                fragmentTransaction.commit();
                break;
            case FRAGMENT_MAP:
                /*MapFragment mapFragment = new MapFragment();
                fragmentTransaction.replace(R.id.frame, mapFragment);
                fragmentTransaction.commit();*/
                break;
            default:
                break;
        }
    }

    public void DrawMap(long latitude, long longitude){
        Log.i(TAG, "MainActivity.DrawMap() Latitude = "+latitude);
        Log.i(TAG, "MainActivity.DrawMap() Longitude = "+longitude);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        args.putLong(MapFragment.ARG_LATITUDE,latitude);
        args.putLong(MapFragment.ARG_LONGITUDE,longitude);

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
        }

        return true;
    }

}
