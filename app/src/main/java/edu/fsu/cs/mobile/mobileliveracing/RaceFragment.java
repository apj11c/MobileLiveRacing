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
    float goal = 500;

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
        myDist += x;
        String newText = "My distance: " + (int)(myDist / goal * 100) + "%";
        ((TextView)getView().findViewById(R.id.myDistance)).setText(newText);
        if(myDist < goal){return false;}
        return true;
    }
    public boolean updateOpponentDistance(double x){
        // x is the distance travelled since last time this was called.
        theirDist += x;
        String newText = "My distance: " + (int)theirDist / goal * 100 + "%";
        ((TextView)getView().findViewById(R.id.theirDist)).setText(newText);
        if(myDist < goal){return false;}
        return true;
    }

}
