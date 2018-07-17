package edu.fsu.cs.mobile.mobileliveracing;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class FindFriendFragment extends Fragment {

    public FindFriendFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_friend, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();

        ((Button)getView().findViewById(R.id.buttonReady)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).onClickListener(v);
            }
        });

    }

}
