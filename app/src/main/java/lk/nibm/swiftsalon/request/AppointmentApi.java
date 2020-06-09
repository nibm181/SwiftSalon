package lk.nibm.swiftsalon.request;

import androidx.lifecycle.LiveData;

import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.model.AppointmentDetail;
import lk.nibm.swiftsalon.request.response.ApiResponse;
import lk.nibm.swiftsalon.request.response.GenericListResponse;
import lk.nibm.swiftsalon.request.response.GenericObjectResponse;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AppointmentApi {

    // get an appointment by id
    @GET("Appointment/{id}")
    LiveData<ApiResponse<GenericObjectResponse<Appointment>>> getAppointment(@Path("id") int id);

    // get all appointments
    @GET("appTest.php")
    LiveData<ApiResponse<GenericListResponse<Appointment>>> getAllAppointments();

    // get new appointments
    @GET("appTest.php")
    LiveData<ApiResponse<GenericListResponse<Appointment>>> getNewAppointments();

    // get ongoing appointments
    @GET("appTest.php")
    LiveData<ApiResponse<GenericListResponse<Appointment>>> getOngoingAppointments();

    @GET("")
    LiveData<ApiResponse<GenericListResponse<AppointmentDetail>>> getAppointmentDetails(@Path("appointmentId") int appointmentId);


}
