package lk.nibm.swiftsalon.persistence;

import androidx.room.TypeConverter;

import java.sql.Time;
import java.text.SimpleDateFormat;

public class Converter {

    @TypeConverter
    public static String fromTime(Time time) {
        return time == null ? null : time.toString();
    }

    @TypeConverter
    public static Time fromString(String time) {
        return time == null ? null : Time.valueOf(time);
    }
}
