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

    private static String TAG = FirebaseManager.class.getCanonicalName() + " error checking";
    private static String ANON = "anon";
    private static String MAIN_TABLE = "raceSessions";
    private static String LOBBY = "lobby";

    private MainActivity mActivity;
    private boolean firebaseSignedIn;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    private DatabaseReference mTable;
    private String mSessionName;

    private boolean inQueue;
    private boolean waitingToStart;
    private ValueEventListener mLocationListener;
    private ValueEventListener mLobbyListener;
    private boolean isFriend;
    private String friendEmail;
    private LobbyUserEntry user1, user2;

    public FirebaseManager(MainActivity m) {

        this(m, ANON + Calendar.getInstance().getTimeInMillis());

    }

    public FirebaseManager(MainActivity m, String sessionName){

        this.mActivity = m;
        this.firebaseSignedIn = false;
        this.mUser = null;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mSessionName = sessionName;
        this.inQueue = false;
        this.waitingToStart = false;
        this.mLobbyListener = null;
        this.mLocationListener = null;
        this.isFriend = false;
        this.friendEmail = "NULL";

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

        detachLobbyListener();
        detachLocationListener();

        addToDatabase(new LobbyUserEntry(getCurrentUser()).setReady(false).setInSession(false));

        mDatabase.child(MAIN_TABLE).child(mSessionName).removeValue();
        mDatabase.child(LOBBY).child(LobbyUserEntry.getKeyFromEmail(getCurrentUser().getEmail())).removeValue();

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

    public void startRace(){

        getRandomReadyUser();

    }

    public void startRace(String friendEmail){

        getFriendReadyUser(friendEmail);

    }

    private void getFriendReadyUser(final String email){

        isFriend = true;

        friendEmail = email;

        inQueue = true;

        attachLobbyListener();

        addToDatabase(new LobbyUserEntry(getCurrentUser()).setReady(true));

        mDatabase.child(LOBBY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String key = userSnapshot.getKey();
                    LobbyUserEntry newLob = LobbyUserEntry.fromDataSnapshot(userSnapshot);

                    if (newLob.getKeyFromEmail().equals(LobbyUserEntry.getKeyFromEmail(email))){
                        //friend
                        if (newLob.isReady()){

                            inQueue = false;

                            LobbyUserEntry thisLob = new LobbyUserEntry(getCurrentUser());

                            setUpRaceSession(thisLob, newLob);
                            startRaceSession();

                        }

                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    private void getRandomReadyUser(){

        isFriend = false;

        inQueue = true;

        attachLobbyListener();

        addToDatabase(new LobbyUserEntry(getCurrentUser()).setReady(true));

        mDatabase.child(LOBBY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String key = userSnapshot.getKey();
                    LobbyUserEntry newLob = LobbyUserEntry.fromDataSnapshot(userSnapshot);

                    if (!newLob.getKeyFromEmail().equals(LobbyUserEntry.getKeyFromEmail(getCurrentUser().getEmail()))){
                    //not self

                        if (newLob.isReady()){

                            inQueue = false;

                            LobbyUserEntry thisLob = new LobbyUserEntry(getCurrentUser());

                            setUpRaceSession(thisLob, newLob);

                        }


                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    private String sessionNameGen(){

        return ANON + Calendar.getInstance().getTimeInMillis();

    }

    private void setUpRaceSession(LobbyUserEntry user1, LobbyUserEntry user2){

        inQueue = false;

        String randSessionName = sessionNameGen();

        user1.setReady(false).setSessionName(randSessionName).setInSession(true);
        user2.setReady(false).setSessionName(randSessionName).setInSession(true);

        addToDatabase(user1);
        addToDatabase(user2);

        setSessionName(randSessionName);

    }

    private void updateSessionNameFromDB(){

        mDatabase.child(LOBBY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String key = userSnapshot.getKey();
                    LobbyUserEntry newLob = LobbyUserEntry.fromDataSnapshot(userSnapshot);

                    if (newLob.getKeyFromEmail().equals(LobbyUserEntry.getKeyFromEmail(getCurrentUser().getEmail()))){
                        //self

                        setSessionName(newLob.getSessionName());

                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void startRaceSession(){

        updateSessionNameFromDB();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mActivity.onStartRace();
        //callback to main activity: race started

        detachLobbyListener();
        attachLocationListener();
        MLRLocationManager.startLocationUpdates(mActivity);

    }

    public void stopRace(){

        isFriend = false;

        detachLobbyListener();
        detachLocationListener();
        MLRLocationManager.stopLocationUpdates(mActivity);

    }

    public void addToDatabase(LobbyUserEntry lob){
        //String key = mDatabase.child(MAIN_TABLE).push().getKey();
        String key = lob.getKeyFromEmail();

        Map<String, Object> postValues = lob.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + LOBBY + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);


        //mTable = mDatabase.child(MAIN_TABLE).child(mSessionName);

        //mTable.setValue(loc);

    }

    public void attachLocationListener(){

        mLocationListener = mDatabase.child(MAIN_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                for(DataSnapshot userSnapshot : dataSnapshot.child(mSessionName).getChildren()) {
                    String key = userSnapshot.getKey();
                    LocationEntry newLoc = LocationEntry.fromDataSnapshot(userSnapshot);
                    mActivity.onReceiveNewLocFirebase(newLoc);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

    }

    public void detachLocationListener(){

        if (mLocationListener != null){

            mDatabase.child(MAIN_TABLE).removeEventListener(mLocationListener);

        }

    }

    public void attachLobbyListener(){

        mLobbyListener = mDatabase.child(LOBBY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String key = userSnapshot.getKey();
                    LobbyUserEntry newLob = LobbyUserEntry.fromDataSnapshot(userSnapshot);

                    if (!newLob.getKeyFromEmail().equals(LobbyUserEntry.getKeyFromEmail(getCurrentUser().getEmail()))) {
                    //not self
                        if (inQueue) {

                            if (isFriend) {

                                if (newLob.getKeyFromEmail() != LobbyUserEntry.getKeyFromEmail(friendEmail)) {

                                    continue;

                                }

                            }

                            if (newLob.isReady()) {

                                inQueue = false;

                                LobbyUserEntry thisLob = new LobbyUserEntry(getCurrentUser());

                                setUpRaceSession(thisLob, newLob);

                                startRaceSession();

                            }

                        }

                    }else if (newLob.getKeyFromEmail().equals(LobbyUserEntry.getKeyFromEmail(getCurrentUser().getEmail()))){
                    //this user
                        if(newLob.isInSession()){

                            inQueue = false;

                            setSessionName(newLob.getSessionName());

                            startRaceSession();

                        }

                    }

                    mActivity.onReceiveNewLobbyUserFirebase(newLob);
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

    public void detachLobbyListener(){

        if (mLobbyListener != null){

            mDatabase.child(LOBBY).removeEventListener(mLobbyListener);

        }

    }

}
