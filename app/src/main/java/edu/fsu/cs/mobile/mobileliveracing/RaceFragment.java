package edu.fsu.cs.mobile.mobileliveracing;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RaceFragment extends Fragment {
    int myDist;
    int theirDist;
    int goal = 100;

    TextView myText;
    TextView theirText;

    public RaceFragment() {
        // Required empty public constructor
        // if time, add a way to change the goal distance
        myDist = 0;
        theirDist = 0;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_race, container, false);
        myText = v.findViewById(R.id.myDistance);
        theirText = v.findViewById(R.id.theirDist);
        return inflater.inflate(R.layout.fragment_race, container, false);

    }

    public void updateMyDistance(int x){
        // x is the distance travelled since last time this was called.
        // call this when location changes to update how far the user has run.
        Log.i("RaceFrag", "myDist = " + myDist);
        Log.i("RaceFrag","adding " + x);
        myDist += x;
        Log.i("RaceFrag", "myDist now is " + myDist);
        myText.setText("My distance: " + myDist);

    }
    public void updateOpponentDistance(int x){
        // x is the distance travelled since last time this was called.
        // call this when opponent's distance changes
        Log.i("RaceFrag", "theirDist = " + theirDist);
        Log.i("RaceFrag","adding " + x);
        theirDist += x;
        Log.i("RaceFrag", "theirDist now is " + theirDist);
        theirText.setText("Their distance is: " + theirDist);
    }

}
