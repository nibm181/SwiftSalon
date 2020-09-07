package lk.nibm.swiftsalon.request;

import androidx.lifecycle.LiveData;

import java.util.List;

import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.model.AppointmentDetail;
import lk.nibm.swiftsalon.request.response.ApiResponse;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface AppointmentApi {

    // get an appointment by id
    @GET("Appointment")
    LiveData<ApiResponse<GenericResponse<Appointment>>> getAppointment(@Query("id") int id);

    // get all appointments
    @GET("Appointment")
    LiveData<ApiResponse<GenericResponse<List<Appointment>>>> getAllAppointments(@Query("salon_id") int salonId);

    // get all appointments
    @GET("Appointment?old=old")
    LiveData<ApiResponse<GenericResponse<List<Appointment>>>> getOldAppointments(@Query("salon_id") int salonId);

    // get new appointments
    @GET("Appointment")
    LiveData<ApiResponse<GenericResponse<List<Appointment>>>> getAppointmentsByStatus(@Query("salon_id") int salonId, @Query("status") String status);

    @GET("Appointment_details")
    LiveData<ApiResponse<GenericResponse<List<AppointmentDetail>>>> getAppointmentDetails(@Query("appointment_id") int appointmentId);

    @GET("Appointment")
    Call<GenericResponse<List<Appointment>>> getWorkerAppointments(@Query("salon_id") int salon_id);

    @GET("Appointment")
    Call<GenericResponse<Appointment>> getWorkerAppointment(@Query("appointment_id") int id);

    @PUT("Appointment?who=salon")
    LiveData<ApiResponse<GenericResponse<Appointment>>> updateAppointmentStatus(@Body Appointment appointment);

}
