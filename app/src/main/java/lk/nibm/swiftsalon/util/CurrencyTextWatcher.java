package lk.nibm.swiftsalon.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class CurrencyTextWatcher implements TextWatcher {
    private static final String TAG = "CurrencyTextWatcher";

    boolean mEditing;

    public CurrencyTextWatcher() {
        mEditing = false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public synchronized void afterTextChanged(Editable s) {

        if(!mEditing) {
            mEditing = true;

            try{
                String digits = s.toString();
                if(digits.contains(".") && digits.length() == 1) {
                    s.replace(0, s.length(), "");
                }
                else if(digits.contains(".") && digits.substring(digits.indexOf(".") + 1).length() >= 2) {
                    String value = digits.substring(0, digits.indexOf(".") + 3);

                    s.replace(0, s.length(), value);
                }
                if(digits.length() > 9) {
                    digits = digits.substring(0, 9);
                    s.replace(0, s.length(), digits);
                }
            }
            catch (Exception ignored) {}

            mEditing = false;
        }
    }
}
