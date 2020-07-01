package lk.nibm.swiftsalon.request;

import java.util.concurrent.TimeUnit;
import lk.nibm.swiftsalon.util.Constants;
import lk.nibm.swiftsalon.util.LiveDataCallAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static lk.nibm.swiftsalon.util.Constants.CONNECTION_TIMEOUT;
import static lk.nibm.swiftsalon.util.Constants.READ_TIMEOUT;
import static lk.nibm.swiftsalon.util.Constants.WRITE_TIMEOUT;

public class ServiceGenerator {

    private static OkHttpClient client = new OkHttpClient.Builder()

            //establish connection to server
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)

            //time between each byte read from the server
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)

            //time between each byte sent to the server
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

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
}
