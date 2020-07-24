package lk.nibm.swiftsalon.persistence;

import androidx.room.TypeConverter;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Converter {

    @TypeConverter
    public static String fromTime(Time time) {
        return time == null ? null : time.toString();
    }

    @TypeConverter
    public static Time fromString(String time) {
        return time == null ? null : Time.valueOf(time);
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
