package edu.fsu.cs.mobile.mobileliveracing;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class RaceFragment extends Fragment {


    public RaceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_race, container, false);
    }

    public void updateMyDistance(int x){
        // x is the distance travelled since last time this was called.
        // call this when location changes to update how far the user has run.
    }
    public void updateOpponentDistance(int x){
        // x is the distance travelled since last time this was called.
        // call this when opponent's distance changes
    }

}
