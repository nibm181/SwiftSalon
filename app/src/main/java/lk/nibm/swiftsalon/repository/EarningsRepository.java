package lk.nibm.swiftsalon.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import lk.nibm.swiftsalon.model.Earnings;
import lk.nibm.swiftsalon.model.StylistEarning;
import lk.nibm.swiftsalon.persistence.SwiftSalonDao;
import lk.nibm.swiftsalon.persistence.SwiftSalonDatabase;
import lk.nibm.swiftsalon.request.ServiceGenerator;
import lk.nibm.swiftsalon.request.response.ApiResponse;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.AppExecutor;
import lk.nibm.swiftsalon.util.NetworkOnlyBoundResource;
import lk.nibm.swiftsalon.util.Resource;

public class EarningsRepository {

    private static final String TAG = "EarningsRepository";

    private static EarningsRepository instance;
    private SwiftSalonDao swiftSalonDao;

    public static EarningsRepository getInstance(Context context) {
        if (instance == null) {
            instance = new EarningsRepository(context);
        }
        return instance;
    }

    public EarningsRepository(Context context) {
        swiftSalonDao = SwiftSalonDatabase.getInstance(context).getDao();
    }

    public LiveData<Resource<GenericResponse<Earnings>>> getMonthlyEarningsApi(int salonId, String date) {
        return new NetworkOnlyBoundResource<Earnings, GenericResponse<Earnings>>(AppExecutor.getInstance()) {
            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Earnings>>> createCall() {
                return ServiceGenerator.getSalonApi().getEarnings(salonId, date, "month");
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Earnings> item) {
            }
        }.getAsLiveData();

    }

    public LiveData<Resource<GenericResponse<Earnings>>> getDailyEarningsApi(int salonId, String date) {
        return new NetworkOnlyBoundResource<Earnings, GenericResponse<Earnings>>(AppExecutor.getInstance()) {
            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Earnings>>> createCall() {
                return ServiceGenerator.getSalonApi().getEarnings(salonId, date, "day");
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Earnings> item) {
            }
        }.getAsLiveData();

    }

    public LiveData<Resource<GenericResponse<List<StylistEarning>>>> getStylistEarningsApi(int salonId, String date) {
        return new NetworkOnlyBoundResource<List<StylistEarning>, GenericResponse<List<StylistEarning>>>(AppExecutor.getInstance()) {
            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<List<StylistEarning>>>> createCall() {
                return ServiceGenerator.getSalonApi().getStylistEarnings(salonId, date, "stylist");
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<List<StylistEarning>> items) {
            }
        }.getAsLiveData();

    }
}
