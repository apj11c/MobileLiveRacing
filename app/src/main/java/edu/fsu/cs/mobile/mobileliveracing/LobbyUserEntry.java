package edu.fsu.cs.mobile.mobileliveracing;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LobbyUserEntry {
    private static final String TAG = LobbyUserEntry.class.getCanonicalName() + " error checking";

    private static final String CURRENT_TIME = "currentTime";
    private static final String USER_NAME = "userName";
    private static final String EMAIL = "email";
    private static final String SESSION_NAME = "sessionName";
    private static final String IS_READY = "isReady";
    private static final String IN_SESSION = "inSession";

    private String userName;
    private String email;
    private boolean isReady;
    private String sessionName;
    private long currentTime;
    private boolean inSession;

    public LobbyUserEntry(@NonNull FirebaseUser mUser){

        if(mUser != null){

            userName = mUser.getDisplayName();
            email = mUser.getEmail();

        }else{

            userName = null;
            email = null;

        }
        isReady = false;
        inSession = false;
        sessionName = "NULL";
        currentTime = Calendar.getInstance().getTimeInMillis();

    }

    public LobbyUserEntry() {

        this(null);

    }

    @Override
    public String toString() {
        return "LobbyUserEntry{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", isReady=" + isReady +
                ", sessionName='" + sessionName + '\'' +
                ", currentTime=" + currentTime +
                ", inSession=" + inSession +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(SESSION_NAME, sessionName);
        result.put(IS_READY, isReady);
        result.put(USER_NAME, userName);
        result.put(EMAIL, email);
        result.put(CURRENT_TIME, currentTime);
        result.put(IN_SESSION, inSession);

        return result;
    }

    public static LobbyUserEntry fromDataSnapshot(DataSnapshot userSnapshot) {
        String key = (String) userSnapshot.getKey();
        String username = (String) userSnapshot.child(USER_NAME).getValue();
        String email = (String) userSnapshot.child(EMAIL).getValue();
        String sessionName = (String) userSnapshot.child(SESSION_NAME).getValue();
        boolean isReady = (boolean) userSnapshot.child(IS_READY).getValue();
        boolean inSession = (boolean) userSnapshot.child(IN_SESSION).getValue();
        long currentTime = (long) userSnapshot.child(CURRENT_TIME).getValue();

        return (new LobbyUserEntry(null).setCurrentTime(currentTime).setUserName(username)
                .setEmail(email).setSessionName(sessionName).setReady(isReady).setInSession(inSession));
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getKeyFromEmail(){

        return email.replace("@", "_a_t_").replace(".", "_d_o_t_");

    }

    public static String getKeyFromEmail(String email){

        return email.replace("@", "_a_t_").replace(".", "_d_o_t_");

    }

    public boolean isReady() {
        return isReady;
    }

    public boolean isInSession() {
        return inSession;
    }

    public String getSessionName() {
        return sessionName;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public LobbyUserEntry setEmail(String email) {
        this.email = email;
        return this;
    }

    public LobbyUserEntry setEmailFromKey(String key){

        email = key.replace("_a_t_", "@").replace("_d_o_t_", ".");
        return this;
    }

    public LobbyUserEntry setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
        return this;
    }

    public LobbyUserEntry setReady(boolean ready) {
        isReady = ready;
        return this;
    }

    public LobbyUserEntry setInSession(boolean inSession){

        this.inSession = inSession;
        return this;

    }

    public LobbyUserEntry setSessionName(String sessionName) {
        this.sessionName = sessionName;
        return this;
    }

    public LobbyUserEntry setUserName(String userName) {
        this.userName = userName;
        return this;
    }

}
