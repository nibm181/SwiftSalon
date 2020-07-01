package lk.nibm.swiftsalon.request;

import androidx.lifecycle.LiveData;

import java.util.List;

import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.request.response.ApiResponse;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
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

    @FormUrlEncoded
    @PUT("Salon")
    Call<ResponseBody> updateToken(@Field("id") int id, @Field("token") String token);

    @GET("Job")
    LiveData<ApiResponse<GenericResponse<List<Job>>>> getJobs(@Query("salon_id") int salonId);

    @GET("Job")
    LiveData<ApiResponse<GenericResponse<Job>>> getJob(@Query("id") int id);

    @PUT("Job")
    LiveData<ApiResponse<GenericResponse<Job>>> updateJob(@Body Job job);
}
