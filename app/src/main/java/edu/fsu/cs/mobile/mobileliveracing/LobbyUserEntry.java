package edu.fsu.cs.mobile.mobileliveracing;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

public class LobbyUserEntry {
    private static final String TAG = LobbyUserEntry.class.getCanonicalName();

    private static final String CURRENT_TIME = "currentTime";
    private static final String USER_NAME = "userName";

    private String userName;
    private String email;
    private boolean isReady;
    private String sessionName;

    public LobbyUserEntry(@NonNull FirebaseUser mUser){

        if(mUser != null){

            userName = mUser.getDisplayName();
            email = mUser.getEmail();

        }else{

            userName = null;
            email = null;

        }
        isReady = false;
        sessionName = "NULL";

    }

    public LobbyUserEntry() {



    }

}
