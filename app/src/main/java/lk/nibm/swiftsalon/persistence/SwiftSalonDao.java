package lk.nibm.swiftsalon.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.model.AppointmentDetail;
import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.Promotion;
import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.model.StylistJob;

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

    @Query("UPDATE tbl_appointment SET status = :status, customer_first_name = :customerFirstName, customer_last_name = :customerLastName, customer_image = :customerImage, modified_on = :modifiedOn WHERE id = :id")
    void updateAppointmentStatus(int id, String status, String customerFirstName, String customerLastName, String customerImage, String modifiedOn);

    @Query("SELECT * FROM tbl_appointment WHERE id = :id")
    LiveData<Appointment> getAppointment(int id);

    @Query("SELECT * FROM tbl_appointment WHERE salon_id = :salonId ORDER BY date DESC, time DESC")
    LiveData<List<Appointment>> getAppointments(int salonId);

    @Query("SELECT * FROM tbl_appointment WHERE salon_id = :salonId AND status = 'pending' ORDER BY date DESC, time DESC")
    LiveData<List<Appointment>> getNewAppointments(int salonId);

    @Query("SELECT * FROM tbl_appointment WHERE salon_id = :salonId AND status = 'on schedule' ORDER BY date DESC, time DESC")
    LiveData<List<Appointment>> getOngoingAppointments(int salonId);

    @Query("SELECT * FROM tbl_appointment WHERE salon_id = :salonId AND status IN ('completed', 'canceled') ORDER BY date DESC, time DESC")
    LiveData<List<Appointment>> getOldAppointments(int salonId);

    @Update
    void updateAppointmentLoading(Appointment appointment);

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

    @Delete
    void deleteJob(Job job);

    @Query("DELETE FROM tbl_job")
    void deleteJobs();

    // Stylist
    @Insert(onConflict = IGNORE)
    long[] insertStylists(Stylist... stylists);

    @Insert(onConflict = REPLACE)
    void insertStylist(Stylist stylist);

    @Query("UPDATE tbl_stylist SET salon_id = :salonId, name = :name, gender = :gender, image = :image, status = :status WHERE id = :id")
    void updateStylist(int id, int salonId, String name, String gender, String image, int status);

    @Query("SELECT * FROM tbl_stylist WHERE salon_id = :salonId")
    LiveData<List<Stylist>> getStylists(int salonId);

    @Query("SELECT * FROM tbl_stylist WHERE id = :id")
    LiveData<Stylist> getStylist(int id);

    @Delete
    void deleteStylist(Stylist stylist);

    @Query("DELETE FROM tbl_stylist WHERE salon_id = :salonId")
    void deleteStylists(int salonId);

    // StylistJob
    @Insert(onConflict = REPLACE)
    void insertStylistJobs(StylistJob... stylistJobs);

    @Query("DELETE FROM tbl_stylist_job WHERE stylist_id = :stylistId")
    void deleteStylistJobsByStylist(int stylistId);

    @Query("SELECT J.* FROM tbl_job J INNER JOIN tbl_stylist_job SJ ON J.id = SJ.job_id WHERE SJ.stylist_id = :stylistId")
    LiveData<List<Job>> getJobsByStylist(int stylistId);

    //Promotion
    @Insert(onConflict = REPLACE)
    long[] insertPromotions(Promotion... promotions);

    @Insert(onConflict = REPLACE)
    void insertPromotion(Promotion promotion);

    @Query("SELECT * FROM tbl_promotion WHERE salon_id = :salonId")
    LiveData<List<Promotion>> getPromotions(int salonId);

    @Query("SELECT * FROM tbl_promotion WHERE job_id = :jobId")
    LiveData<Promotion> getPromotionByJob(int jobId);

    @Query("DELETE FROM tbl_promotion WHERE salon_id = :salonId")
    void deletePromotions(int salonId);
}
