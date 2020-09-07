package lk.nibm.swiftsalon.request;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lk.nibm.swiftsalon.model.Earnings;
import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.Promotion;
import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.model.StylistEarning;
import lk.nibm.swiftsalon.model.StylistJob;
import lk.nibm.swiftsalon.request.response.ApiResponse;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    @PUT("Salon")
    LiveData<ApiResponse<GenericResponse>> verifyPassword(@Body HashMap<Object, Object> data);

    @PUT("Salon")
    LiveData<ApiResponse<GenericResponse>> confirmPassword(@Body HashMap<Object, Object> data);

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

    @Multipart
    @POST("Stylist")
    LiveData<ApiResponse<GenericResponse<Stylist>>> saveStylist(@Part("data") HashMap<String, Object> hashMap, @Part MultipartBody.Part image);

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

    @Multipart
    @POST("Upload")
    LiveData<ApiResponse<GenericResponse<Salon>>> updateSalonImage(@Part("salon_id") int salonId, @Part MultipartBody.Part image);

    @Multipart
    @POST("Upload")
    LiveData<ApiResponse<GenericResponse<Stylist>>> updateStylistImage(@Part("stylist_id") int stylistId, @Part MultipartBody.Part image);

    @Multipart
    @POST("Promotion")
    LiveData<ApiResponse<GenericResponse<Promotion>>> savePromotion(@Part("promotion") Promotion promotion, @Part MultipartBody.Part image);

    @GET("Promotion")
    LiveData<ApiResponse<GenericResponse<List<Promotion>>>> getPromotions(@Query("salon_id") int salonId);

    @GET("Promotion")
    LiveData<ApiResponse<GenericResponse<Promotion>>> getPromotionByJob(@Query("job_id") int jobId);

    @Multipart
    @POST("Upload")
    Call<ResponseBody> uploadImage(@Part("stylist") Stylist stylist,
                                   @Part MultipartBody .Part image);

    @GET("Earning")
    LiveData<ApiResponse<GenericResponse<Earnings>>> getEarnings(@Query("salon_id") int salonId, @Query("time") String date, @Query("type") String type);

    @GET("Earning")
    LiveData<ApiResponse<GenericResponse<List<StylistEarning>>>> getStylistEarnings(@Query("salon_id") int salonId, @Query("time") String date, @Query("type") String type);


    @FormUrlEncoded
    @POST("Salon")
    LiveData<ApiResponse<GenericResponse>> sendEmail(@Field("email") String email);

    @FormUrlEncoded
    @POST("Salon")
    LiveData<ApiResponse<GenericResponse>> verifyEmail(@Field("email") String email, @Field("rand_id") int code);

    @Multipart
    @POST("Salon")
    LiveData<ApiResponse<GenericResponse<Salon>>> saveSalon(@Part("data") Salon salon, @Part MultipartBody.Part image);
}
