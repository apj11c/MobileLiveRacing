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
    float myDist;
    float theirDist;
    float goal = 250;

    TextView myText;
    TextView theirText;

    public RaceFragment() {
        // Required empty public constructor
        // if time, add a way to change the goal distance
        Log.i("racefrag","constructor");
        myDist = 0;
        theirDist = 0;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("racefrag","onCreateView");
        View v = inflater.inflate(R.layout.fragment_race, container, false);
        myText = v.findViewById(R.id.myDistance);
        Log.i("racefrag","myText = "+ myText.getText().toString());
        theirText = v.findViewById(R.id.theirDist);
        return inflater.inflate(R.layout.fragment_race, container, false);

    }

    public boolean updateMyDistance(double x){
        // x is the distance travelled since last time this was called.
        // call this when location changes to update how far the user has run.
        Log.i("RaceFrag", "myDist = " + myDist);
        Log.i("RaceFrag","adding " + x);
        myDist += x;

        String newText = "My distance: " + myDist;
       // Log.i("RaceFrag", newText);
        myText.setText(newText);
        Log.i("racefrag","updated myText = "+ myText.getText().toString());
        if(myDist < goal){return false;}
        return true;
    }
    public void updateOpponentDistance(float x){
        // x is the distance travelled since last time this was called.
        // call this when opponent's distance changes
        Log.i("RaceFrag", "theirDist = " + theirDist);
        Log.i("RaceFrag","adding " + x);
        theirDist += x;
        Log.i("RaceFrag", "theirDist now is " + theirDist);
        theirText.setText("Their distance is: " + theirDist);
    }

}
