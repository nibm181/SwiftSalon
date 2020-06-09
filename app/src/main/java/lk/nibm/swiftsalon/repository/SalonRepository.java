package lk.nibm.swiftsalon.repository;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.persistence.SwiftSalonDao;
import lk.nibm.swiftsalon.persistence.SwiftSalonDatabase;
import lk.nibm.swiftsalon.request.ServiceGenerator;
import lk.nibm.swiftsalon.request.response.ApiResponse;
import lk.nibm.swiftsalon.request.response.GenericObjectResponse;
import lk.nibm.swiftsalon.util.AppExecutor;
import lk.nibm.swiftsalon.util.NetworkBoundResource;

public class SalonRepository {

    private static SalonRepository instance;
    private SwiftSalonDao swiftSalonDao;

    private MediatorLiveData<Boolean> isLoggedIn = new MediatorLiveData<>();

    public static SalonRepository getInstance(Context context) {
        if(instance == null) {
            instance = new SalonRepository(context);
        }
        return instance;
    }

    public SalonRepository(Context context) {
        swiftSalonDao = SwiftSalonDatabase.getInstance(context).getDao();
    }

    public LiveData<Boolean> loginApi(String email, String password) {
        new NetworkBoundResource<Salon, GenericObjectResponse<Salon>>(AppExecutor.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GenericObjectResponse<Salon> item) {
                if(item.getStatus() == 1) {
                    if(item.getContent() != null) {
                        swiftSalonDao.insertSalon(item.getContent());
                        isLoggedIn.postValue(true);
                    }
                    else {
                        isLoggedIn.postValue(false);
                    }
                }
                else {
                    isLoggedIn.postValue(false);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Salon data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Salon> loadFromDb() {
                return null;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericObjectResponse<Salon>>> createCall() {
                return ServiceGenerator.getSalonApi()
                        .login(email, password);
            }
        };

        return isLoggedIn;
    }
}
