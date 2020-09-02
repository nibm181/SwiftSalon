package lk.nibm.swiftsalon.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.HashMap;

import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.persistence.SwiftSalonDao;
import lk.nibm.swiftsalon.persistence.SwiftSalonDatabase;
import lk.nibm.swiftsalon.request.ServiceGenerator;
import lk.nibm.swiftsalon.request.response.ApiResponse;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.AppExecutor;
import lk.nibm.swiftsalon.util.NetworkBoundResource;
import lk.nibm.swiftsalon.util.NetworkOnlyBoundResource;
import lk.nibm.swiftsalon.util.Resource;
import okhttp3.MultipartBody;

public class SalonRepository {

    private static final String TAG = "SalonRepository";

    private static SalonRepository instance;
    private SwiftSalonDao swiftSalonDao;

    public static SalonRepository getInstance(Context context) {
        if(instance == null) {
            instance = new SalonRepository(context);
        }
        return instance;
    }

    private SalonRepository(Context context) {
        swiftSalonDao = SwiftSalonDatabase.getInstance(context).getDao();
    }

    public LiveData<Resource<GenericResponse<Salon>>> getLoginApi(String email, String password) {
        return new NetworkOnlyBoundResource<Salon, GenericResponse<Salon>>(AppExecutor.getInstance()) {
            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Salon>>> createCall() {
                return ServiceGenerator.getSalonApi().login(email, password);
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Salon> item) {
                swiftSalonDao.insertSalon(item.getContent());
            }

        }.getAsLiveData();
    }

    public LiveData<Resource<Salon>> getSalonApi(int id) {
        return new NetworkBoundResource<Salon, GenericResponse<Salon>>(AppExecutor.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Salon> item) {
                if(item.getContent() != null) {
                    swiftSalonDao.insertSalon(item.getContent());
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Salon data) {
                if(data == null) {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<Salon> loadFromDb() {
                return swiftSalonDao.getSalon(id);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Salon>>> createCall() {
                return ServiceGenerator.getSalonApi().getSalon(id);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericResponse<Salon>>> updateSalonApi(Salon salon) {
        return new NetworkOnlyBoundResource<Salon, GenericResponse<Salon>>(AppExecutor.getInstance()) {
            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Salon>>> createCall() {
                return ServiceGenerator.getSalonApi().updateSalon(salon);
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Salon> item) {
                if(item.getContent() != null) {
                    swiftSalonDao.insertSalon(item.getContent());
                }
            }

        }.getAsLiveData();
    }

    public LiveData<Resource<GenericResponse<Salon>>> updateSalonImageApi(int salonId, MultipartBody.Part image) {
        return new NetworkOnlyBoundResource<Salon, GenericResponse<Salon>>(AppExecutor.getInstance()) {
            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Salon>>> createCall() {
                return ServiceGenerator.getSalonApi().updateSalonImage(salonId, image);
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Salon> item) {
                if(item.getContent() != null) {
                    swiftSalonDao.insertSalon(item.getContent());
                }
            }

        }.getAsLiveData();
    }

    public LiveData<Resource<GenericResponse>> verifyPasswordApi(HashMap<Object, Object> data) {
        return new NetworkOnlyBoundResource<Object, GenericResponse>(AppExecutor.getInstance()) {
            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse>> createCall() {
                return ServiceGenerator.getSalonApi().verifyPassword(data);
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse item) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericResponse>> confirmPasswordApi(HashMap<Object, Object> data) {
        return new NetworkOnlyBoundResource<Object, GenericResponse>(AppExecutor.getInstance()) {
            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse>> createCall() {
                return ServiceGenerator.getSalonApi().confirmPassword(data);
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse item) {
            }
        }.getAsLiveData();
    }
}
