package lk.nibm.swiftsalon.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.model.AppointmentDetail;
import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.model.StylistJob;

@Database(entities = {Appointment.class, AppointmentDetail.class, Salon.class, Job.class, Stylist.class, StylistJob.class}, version = 10)
@TypeConverters({Converter.class})
public abstract class SwiftSalonDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "swiftsalon";

    private static SwiftSalonDatabase instance;

    public static SwiftSalonDatabase getInstance(final Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    SwiftSalonDatabase.class,
                    DATABASE_NAME
            )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract SwiftSalonDao getDao();

}
