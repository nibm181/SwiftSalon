package lk.nibm.swiftsalon.service.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.JsonToken;

public class Session {

    private SharedPreferences preferences;
    private String salonNo;
    private JsonToken token;
    private boolean isSignedIn;

    public Session(Context context) {
        // TODO Auto-generated constructor stub
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        isSignedIn = false;
    }

    public void setSalonNo(String salonNo) {
        preferences.edit().putString("salonNo", salonNo).apply();
    }

    public String getSalonNo() {
        salonNo = preferences.getString("salonNo","");
        return salonNo;
    }

    public boolean isSignedIn() {
        isSignedIn = preferences.getBoolean("isSignedIn",false);
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
