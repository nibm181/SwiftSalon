package lk.nibm.swiftsalon.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setsalonNo(String salonNo) {
        prefs.edit().putString("salonNo", salonNo).commit();
    }

    public String getsalonNo() {
        String salonNo = prefs.getString("salonNo","");
        return salonNo;
    }

    public void clearSession() {
        prefs.edit().clear().commit();
    }
}
