package lk.nibm.swiftsalon.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import lk.nibm.swiftsalon.model.Appointment;

@Database(entities = {Appointment.class}, version = 1)
public abstract class SwiftSalonDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "swiftsalon";

    private static SwiftSalonDatabase instance;

    public static SwiftSalonDatabase getInstance(final Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    SwiftSalonDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract SwiftSalonDao getDao();

}
