package lk.nibm.swiftsalon.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.model.AppointmentDetail;
import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.Salon;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface SwiftSalonDao {

    // Appointment
    @Insert(onConflict = IGNORE)
    long[] insertAppointments(Appointment... appointments);

    @Insert(onConflict = REPLACE)
    void insertAppointment(Appointment appointment);

    @Update
    void updateAppointment(Appointment appointment);

    @Query("UPDATE tbl_appointment SET status = :status, modified_on = :modifiedOn WHERE id = :id")
    void updateAppointmentStatus(int id, String status, int modifiedOn);

    @Query("SELECT * FROM tbl_appointment WHERE id = :id")
    LiveData<Appointment> getAppointment(int id);

    @Query("SELECT * FROM tbl_appointment WHERE salon_id = :salonId")
    LiveData<List<Appointment>> getAppointments(int salonId);

    @Query("SELECT * FROM tbl_appointment WHERE salon_id = :salonId AND status = 'pending'")
    LiveData<List<Appointment>> getNewAppointments(int salonId);

    @Query("SELECT * FROM tbl_appointment WHERE salon_id = :salonId AND status = 'on schedule'")
    LiveData<List<Appointment>> getOngoingAppointments(int salonId);

    // Appointment Detail
    @Insert(onConflict = REPLACE)
    void insertAppointmentDetails(AppointmentDetail... details);

    @Query("SELECT * FROM tbl_appointment_detail WHERE appointment_id = :appointmentId")
    LiveData<List<AppointmentDetail>> getAppointmentDetails(int appointmentId);

    // Salon
    @Insert(onConflict = REPLACE)
    void insertSalon(Salon salon);

    @Query("SELECT * FROM tbl_salon WHERE id = :id")
    LiveData<Salon> getSalon(int id);

    @Query("SELECT * FROM tbl_salon WHERE id = :id")
    Salon getRawSalon(int id);

    // Job
    @Insert(onConflict = IGNORE)
    long[] insertJobs(Job... jobs);

    @Insert(onConflict = REPLACE)
    void insertJob(Job job);

    @Query("SELECT * FROM tbl_job WHERE salon_id = :salonId")
    LiveData<List<Job>> getJobs(int salonId);

    @Query("SELECT * FROM tbl_job WHERE id = :id")
    LiveData<Job> getJob(int id);

    @Query("UPDATE tbl_job SET name = :name, duration = :duration, price = :price WHERE id = :id")
    void updateJob(int id, String name, int duration, float price);
}
