package lk.nibm.swiftsalon.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;

import lk.nibm.swiftsalon.request.ServiceGenerator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class RefreshTokenWorker extends Worker {

    private static final String TAG = "RefreshTokenWorker";

    public RefreshTokenWorker(@NonNull Context context, @NonNull WorkerParameters workerParams, int id, String token) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int id = getInputData().getInt("id", 0);
        String token = getInputData().getString("token");

        try {
            Call<ResponseBody> call = ServiceGenerator.getSalonApi().updateToken(id, token);
            Response<ResponseBody> response = call.execute();

            if (response.isSuccessful() && response.body() != null && !response.body().string().isEmpty()) {
                return Result.success();
            } else {
                return Result.retry();
            }
        } catch (IOException e) {
            return Result.failure();
        }
    }
}
