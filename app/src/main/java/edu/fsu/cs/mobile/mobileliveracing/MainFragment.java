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
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getCanonicalName();
    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "MainFragment.onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        Button buttonShowMap = v.findViewById(R.id.buttonShowMap);
        buttonShowMap.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowMapClick();
            }
        });

        return v;
    }
    private void onShowMapClick(){
        EditText textLatitude = getView().findViewById(R.id.editTextLatitude);
        EditText textLongitude = getView().findViewById(R.id.editTextLongitude);
        textLatitude.setError(null);
        textLongitude.setError(null);
        boolean isValid = true;
        if(textLatitude.getText().toString().isEmpty()){
            textLatitude.setError(null);
            isValid = false;
        }
        if(textLongitude.getText().toString().isEmpty()){
            textLongitude.setError(null);
            isValid = false;
        }
        if(isValid){
            long latitude = Long.parseLong(textLatitude.getText().toString());
            long longitude = Long.parseLong(textLongitude.getText().toString());
            Log.i(TAG, "MainFragment.DrawMap() Latitude = "+latitude);
            Log.i(TAG, "MainFragment.DrawMap() Longitude = "+longitude);
            ((MainActivity)getActivity()).DrawMap(latitude,longitude);
        }
    }
}
