package lk.nibm.swiftsalon.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.model.StylistJob;
import lk.nibm.swiftsalon.persistence.SwiftSalonDao;
import lk.nibm.swiftsalon.persistence.SwiftSalonDatabase;
import lk.nibm.swiftsalon.request.ServiceGenerator;
import lk.nibm.swiftsalon.request.response.ApiResponse;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.AppExecutor;
import lk.nibm.swiftsalon.util.NetworkOnlyBoundResource;
import lk.nibm.swiftsalon.util.Resource;

public class StylistJobRepository  {

    private static final String TAG = "StylistJobRepository";

    private static StylistJobRepository instance;
    private SwiftSalonDao swiftSalonDao;

    public static StylistJobRepository getInstance(Context context) {
        if(instance == null) {
            instance = new StylistJobRepository(context);
        }
        return instance;
    }

    private StylistJobRepository(Context context) {
        swiftSalonDao = SwiftSalonDatabase.getInstance(context).getDao();
    }

    public LiveData<Resource<GenericResponse<List<StylistJob>>>> updateStylistJobsApi(List<StylistJob> stylistJobs) {
        return new NetworkOnlyBoundResource<List<StylistJob>, GenericResponse<List<StylistJob>>>(AppExecutor.getInstance()) {


            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<List<StylistJob>>>> createCall() {
                return ServiceGenerator.getSalonApi().updateStylistJobs(stylistJobs);
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<List<StylistJob>> item) {
                if(item.getContent() != null && !item.getContent().isEmpty()) {
                    StylistJob[] arrayStylistJobs = new StylistJob[item.getContent().size()];
                    swiftSalonDao.insertStylistJobs(item.getContent().toArray(item.getContent().toArray(arrayStylistJobs)));
                }
            }
        }.getAsLiveData();
    }
}
