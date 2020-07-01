package lk.nibm.swiftsalon.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.JsonToken;

public class Session {

    private SharedPreferences preferences;
    private int salonId;
    private JsonToken token;
    private boolean isSignedIn;

    public Session(Context context) {
        // TODO Auto-generated constructor stub
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        isSignedIn = false;
    }

    public int getSalonId() {
        salonId = preferences.getInt("salonId", 0);
        return salonId;
    }

    public void setSalonId(int salonId) {
        preferences.edit().putInt("salonId", salonId).apply();
    }

    public boolean isSignedIn() {
        isSignedIn = preferences.getBoolean("isSignedIn", false);
        return isSignedIn;
    }

    public void setSignedIn(boolean signedIn) {
        isSignedIn = signedIn;
        preferences.edit().putBoolean("isSignedIn", isSignedIn).apply();
    }

    public void clearSession() {
        preferences.edit().clear().apply();
    }
}
