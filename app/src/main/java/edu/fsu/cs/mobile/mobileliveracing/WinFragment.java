package edu.fsu.cs.mobile.mobileliveracing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WinFragment extends Fragment {
    public WinFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_win, container, false);

        Button buttonOpponentResult = v.findViewById(R.id.buttonOpponentResult);
        buttonOpponentResult.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = ((MainActivity)getActivity()).getfireBaseLocationEntries().get(0).getLat();
                double longitude = ((MainActivity)getActivity()).getfireBaseLocationEntries().get(0).getLng();
                ((MainActivity)getActivity()).DrawMap(latitude,longitude);

            }
        });
        Button buttonUserResults = v.findViewById(R.id.buttonUserResults);
        buttonUserResults.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double latitude = ((MainActivity)getActivity()).getLocationEntries().get(0).getLat();
                double longitude = ((MainActivity)getActivity()).getLocationEntries().get(0).getLng();
                ((MainActivity)getActivity()).DrawMap(latitude,longitude);

            }
        });

        return v;
    }
}
