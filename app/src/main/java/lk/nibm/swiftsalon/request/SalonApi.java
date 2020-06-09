package lk.nibm.swiftsalon.request;

import androidx.lifecycle.LiveData;

import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.request.response.ApiResponse;
import lk.nibm.swiftsalon.request.response.GenericObjectResponse;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SalonApi {

    @GET("salon/{id}")
    LiveData<ApiResponse<GenericObjectResponse<Salon>>> getSalon(@Path("id") int id);

    @FormUrlEncoded
    @POST("salon/login")
    LiveData<ApiResponse<GenericObjectResponse<Salon>>> login(@Field("email") String email, @Field("password") String password);
}
