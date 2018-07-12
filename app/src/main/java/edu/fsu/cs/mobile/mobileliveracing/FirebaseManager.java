package edu.fsu.cs.mobile.mobileliveracing;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {

    private static String TAG = FirebaseManager.class.getCanonicalName();
    private static String ANON = "anon";
    private static String MAIN_TABLE = "raceSessions";

    MainActivity mActivity;
    private boolean firebaseSignedIn;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    private DatabaseReference mTable;
    private String mSessionName;

    public FirebaseManager(MainActivity m) {

        this(m, ANON + Calendar.getInstance().getTimeInMillis());

    }

    public FirebaseManager(MainActivity m, String sessionName){

        this.mActivity = m;
        this.firebaseSignedIn = false;
        this.mUser = null;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mSessionName = sessionName;

    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == MainActivity.RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == MainActivity.RESULT_OK) {
                // Successfully signed in
                mUser = FirebaseAuth.getInstance().getCurrentUser();
                firebaseSignedIn = true;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                return true;
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...

                if(response == null){

                    Log.e(TAG, "Sign In failed: User canceled");

                }else{

                    Log.e(TAG, "Sign In failed:" + response.getError().getErrorCode());

                }

                return false;

            }
        }

        return false;

    }

    public void setSessionName(String s){

        this.mSessionName = s;

    }

    private void setTable() throws Exception{

        if(!mDatabase.child(mSessionName).equals(null)){

            Log.i(TAG, "session already exists");

        }else{

            //mDatabase.

        }

    }

    public void startAuth(){

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        mActivity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                MainActivity.RC_SIGN_IN);

    }

    public void endSession(){

        AuthUI.getInstance()
                .signOut(mActivity)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Log.i(TAG, "Firebase Session Ended");
                    }
                });

        firebaseSignedIn = false;
        mDatabase = null;
        mUser = null;
        mTable = null;
        mSessionName = null;

    }

    //not tested yet
    public void deleteUser(){

        AuthUI.getInstance()
                .delete(mActivity)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });

    }

    public FirebaseUser getCurrentUser(){

        return mUser;

    }

    public void addToDatabase(LocationEntry loc){
        String key = mDatabase.child(MAIN_TABLE).push().getKey();

        Map<String, Object> postValues = loc.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + MAIN_TABLE + "/" + mSessionName + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);


        //mTable = mDatabase.child(MAIN_TABLE).child(mSessionName);

        //mTable.setValue(loc);

    }

    public void attachListener(){

        mDatabase.child(MAIN_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                for(DataSnapshot userSnapshot : dataSnapshot.child(mSessionName).getChildren()) {
                    String key = userSnapshot.getKey();
                    LocationEntry newLoc = LocationEntry.fromDataSnapshot(userSnapshot);
                    mActivity.onReceiveNewLoc(newLoc);
                }


                //LocationEntry newLoc = dataSnapshot.getValue(LocationEntry.class);



                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

    }

}
