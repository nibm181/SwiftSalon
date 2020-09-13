package lk.nibm.swiftsalon.request;

import java.util.concurrent.TimeUnit;

import lk.nibm.swiftsalon.util.Constants;
import lk.nibm.swiftsalon.util.LiveDataCallAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static lk.nibm.swiftsalon.util.Constants.CONNECTION_TIMEOUT;
import static lk.nibm.swiftsalon.util.Constants.READ_TIMEOUT;
import static lk.nibm.swiftsalon.util.Constants.READ_TIMEOUT2;
import static lk.nibm.swiftsalon.util.Constants.WRITE_TIMEOUT;
import static lk.nibm.swiftsalon.util.Constants.WRITE_TIMEOUT2;

public class ServiceGenerator {

    private static OkHttpClient client = new OkHttpClient.Builder()
            //establish connection to server
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            //time between each byte read from the server
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            //time between each byte sent to the server
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();


    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create());


    private static Retrofit retrofit = retrofitBuilder.build();


    private static AppointmentApi appointmentApi = retrofit.create(AppointmentApi.class);

    private static SalonApi salonApi = retrofit.create(SalonApi.class);

    public static AppointmentApi getAppointmentApi() {
        return appointmentApi;
    }

    public static SalonApi getSalonApi() {
        return salonApi;
    }


    /**
     * for request taking longer read and write time
     */
    private static OkHttpClient client2 = new OkHttpClient.Builder()
            //establish connection to server
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            //time between each byte read from the server
            .readTimeout(READ_TIMEOUT2, TimeUnit.SECONDS)
            //time between each byte sent to the server
            .writeTimeout(WRITE_TIMEOUT2, TimeUnit.SECONDS)
            .build();

    private static Retrofit.Builder retrofitBuilder2 =
            new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client2)
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit2 = retrofitBuilder2.build();

    private static SalonApi salonApi2 = retrofit2.create(SalonApi.class);

    public static SalonApi getSalonApi2() {
        return salonApi2;
    }
}
