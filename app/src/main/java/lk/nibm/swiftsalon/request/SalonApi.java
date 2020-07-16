package lk.nibm.swiftsalon.request;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;

import java.util.List;

import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.model.StylistJob;
import lk.nibm.swiftsalon.request.response.ApiResponse;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SalonApi {

    @GET("Salon")
    LiveData<ApiResponse<GenericResponse<Salon>>> getSalon(@Query("id") int id);

    @FormUrlEncoded
    @POST("Login")
    LiveData<ApiResponse<GenericResponse<Salon>>> login(@Field("identifier") String email, @Field("password") String password);

    @PUT("Salon")
    LiveData<ApiResponse<GenericResponse<Salon>>> updateSalon(@Body Salon salon);

    @PUT("Salon")
    Call<ResponseBody> updateToken(@Body Salon salon);

    @GET("Job")
    LiveData<ApiResponse<GenericResponse<List<Job>>>> getJobs(@Query("salon_id") int salonId);

    @GET("Job")
    LiveData<ApiResponse<GenericResponse<List<Job>>>> getJobsByStylist(@Query("stylist_id") int stylistId);

    @GET("Job")
    LiveData<ApiResponse<GenericResponse<Job>>> getJob(@Query("id") int id);

    @PUT("Job")
    LiveData<ApiResponse<GenericResponse<Job>>> updateJob(@Body Job job);

    @POST("Job")
    LiveData<ApiResponse<GenericResponse<Job>>> saveJob(@Body Job job);

    @DELETE("Job/{id}")
    LiveData<ApiResponse<GenericResponse<Job>>> deleteJob(@Path("id") int id);

    @GET("Stylist")
    LiveData<ApiResponse<GenericResponse<List<Stylist>>>> getStylists(@Query("salon_id") int salonId);

    @GET("Stylist")
    LiveData<ApiResponse<GenericResponse<Stylist>>> getStylist(@Query("id") int id);

    @PUT("Stylist")
    LiveData<ApiResponse<GenericResponse<Stylist>>> updateStylist(@Body Stylist stylist);

    @DELETE("Stylist/{id}")
    LiveData<ApiResponse<GenericResponse<Stylist>>> deleteStylist(@Path("id") int id);

    @GET("Stylist_job")
    LiveData<ApiResponse<GenericResponse<List<StylistJob>>>> getStylistJobsByStylist(@Query("stylist_id") int stylistId);

    @PUT("Stylist_job")
    LiveData<ApiResponse<GenericResponse<List<StylistJob>>>> updateStylistJobs(@Body List<StylistJob> stylistJobs);
}
