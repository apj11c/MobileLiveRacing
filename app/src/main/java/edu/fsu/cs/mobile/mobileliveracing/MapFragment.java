package edu.fsu.cs.mobile.mobileliveracing;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {
    private static final String TAG = MapFragment.class.getCanonicalName();
    public static final String ARG_LATITUDE = "latitude";
    public static final String ARG_LONGITUDE = "longitude";

    private Double mParamLatitude;
    private Double mParamLongitude;
    MapView mMapView;
    private GoogleMap googleMap;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);


        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                //googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                mParamLatitude = getArguments().getDouble(ARG_LATITUDE);
                mParamLongitude = getArguments().getDouble(ARG_LONGITUDE);

                Log.i(TAG, "MapFragment.OnMapReadyCallback(): latitude = "+mParamLatitude);
                Log.i(TAG, "MapFragment.OnMapReadyCallback(): longitude = "+mParamLongitude);
                LatLng location = new LatLng(mParamLatitude, mParamLongitude);
                googleMap.addMarker(new MarkerOptions().position(location).title("Latitude = "+mParamLatitude).snippet("Longitude = "+mParamLongitude));




                ArrayList<LocationEntry> locationEntries = ((MainActivity)getActivity()).getLocationEntries();
                if(locationEntries != null && locationEntries.size()  > 0){
                    LatLng[] latLngs = new LatLng[locationEntries.size()];
                    int i = 0;
                    for (LocationEntry locationEntry: locationEntries) {
                        latLngs[i++] = new LatLng(locationEntry.getLat(), locationEntry.getLng());
                    }
                    googleMap.addPolyline(new PolylineOptions().clickable(true).add(latLngs));
                }

                ArrayList<LocationEntry> fireBaselocationEntries = ((MainActivity)getActivity()).getfireBaseLocationEntries();
                if(fireBaselocationEntries != null && fireBaselocationEntries.size()  > 0){
                    LatLng[] firebaselatLngs = new LatLng[fireBaselocationEntries.size()];
                    int x = 0;
                    for (LocationEntry locationEntry: fireBaselocationEntries) {
                        firebaselatLngs[x++] = new LatLng(locationEntry.getLat(), locationEntry.getLng());
                    }
                    googleMap.addPolyline(new PolylineOptions().clickable(true).add(firebaselatLngs));
                }

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(18).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.getUiSettings().setZoomControlsEnabled(true);


            }
        });


        Button buttonOpponentRoute = rootView.findViewById(R.id.buttonOpponentRoute);
        buttonOpponentRoute.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = ((MainActivity)getActivity()).getfireBaseLocationEntries().get(0).getLat();
                double longitude = ((MainActivity)getActivity()).getfireBaseLocationEntries().get(0).getLng();
                ((MainActivity)getActivity()).DrawMap(latitude,longitude);

            }
        });
        Button buttonMyRoute = rootView.findViewById(R.id.buttonMyRoute);
        buttonMyRoute.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double latitude = ((MainActivity)getActivity()).getLocationEntries().get(0).getLat();
                double longitude = ((MainActivity)getActivity()).getLocationEntries().get(0).getLng();
                ((MainActivity)getActivity()).DrawMap(latitude,longitude);

            }
        });


        ArrayList<LocationEntry> locationEntries = ((MainActivity)getActivity()).getLocationEntries();
        if(locationEntries != null && locationEntries.size()  > 0){
            buttonMyRoute.setVisibility(View.VISIBLE);
        }else{
            buttonMyRoute.setVisibility(View.INVISIBLE);
        }

        ArrayList<LocationEntry> fireBaselocationEntries = ((MainActivity)getActivity()).getLocationEntries();
        if(fireBaselocationEntries != null && fireBaselocationEntries.size()  > 0){
            buttonOpponentRoute.setVisibility(View.VISIBLE);
        }else{
            buttonOpponentRoute.setVisibility(View.INVISIBLE);
        }


        return rootView;

    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
