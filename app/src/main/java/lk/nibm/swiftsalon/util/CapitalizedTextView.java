package lk.nibm.swiftsalon.util;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class CapitalizedTextView extends androidx.appcompat.widget.AppCompatTextView {


    public CapitalizedTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text.length() > 0) {
            text = String.valueOf(text.charAt(0)).toUpperCase() + text.subSequence(1, text.length());
        }
        super.setText(text, type);
    }
}
