package lk.nibm.swiftsalon.util;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.material.datepicker.CalendarConstraints;

public class DateRangeValidator implements CalendarConstraints.DateValidator, Parcelable {

    long minDate, maxDate;

    public DateRangeValidator(long minDate, long maxDate) {
        this.minDate = minDate;
        this.maxDate = maxDate;
    }

    @Override
    public boolean isValid(long date) {
        return !(minDate > date || maxDate < date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(minDate);
        dest.writeLong(maxDate);
    }

    protected DateRangeValidator(Parcel in) {
        minDate = in.readLong();
        maxDate = in.readLong();
    }

    public static final Creator<DateRangeValidator> CREATOR = new Creator<DateRangeValidator>() {
        @Override
        public DateRangeValidator createFromParcel(Parcel in) {
            return new DateRangeValidator(in);
        }

        @Override
        public DateRangeValidator[] newArray(int size) {
            return new DateRangeValidator[size];
        }
    };
}
