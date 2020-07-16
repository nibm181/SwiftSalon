package lk.nibm.swiftsalon.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;

import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.request.ServiceGenerator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class RefreshTokenWorker extends Worker {

    private static final String TAG = "RefreshTokenWorker";

    public RefreshTokenWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int id = getInputData().getInt("id", 0);
        String token = getInputData().getString("token");

        Salon salon = new Salon();
        salon.setId(id);
        salon.setToken(token);

        try {
            Log.d(TAG, "doWork: 37");
            Call<ResponseBody> call = ServiceGenerator.getSalonApi().updateToken(salon);
            Response<ResponseBody> response = call.execute();

            Log.d(TAG, "doWork: " + response.code());
            if(response.body() != null) {
                Log.d(TAG, "doWork: BODY: " + response.body().toString());
            }

            if (response.isSuccessful() && response.body() != null && !response.body().string().isEmpty()) {
                Log.d(TAG, "doWork: 42");
                return Result.success();
            } else {
                Log.d(TAG, "doWork: 45");
                return Result.retry();
            }
        } catch (IOException e) {
            Log.d(TAG, "doWork: FAILED");
            return Result.failure();
        }
    }
}
