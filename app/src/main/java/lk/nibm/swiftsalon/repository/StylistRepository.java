package lk.nibm.swiftsalon.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.model.StylistJob;
import lk.nibm.swiftsalon.persistence.SwiftSalonDao;
import lk.nibm.swiftsalon.persistence.SwiftSalonDatabase;
import lk.nibm.swiftsalon.request.ServiceGenerator;
import lk.nibm.swiftsalon.request.response.ApiResponse;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.AppExecutor;
import lk.nibm.swiftsalon.util.NetworkBoundResource;
import lk.nibm.swiftsalon.util.NetworkOnlyBoundResource;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;

public class StylistRepository {

    private static final String TAG = "StylistRepository";

    private static StylistRepository instance;
    private SwiftSalonDao swiftSalonDao;

    public static StylistRepository getInstance(Context context) {
        if (instance == null) {
            instance = new StylistRepository(context);
        }
        return instance;
    }

    private StylistRepository(Context context) {
        swiftSalonDao = SwiftSalonDatabase.getInstance(context).getDao();
    }

    public LiveData<Resource<List<Stylist>>> getStylistsApi(int salonId) {
        return new NetworkBoundResource<List<Stylist>, GenericResponse<List<Stylist>>>(AppExecutor.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GenericResponse<List<Stylist>> item) {
                Log.d(TAG, "saveCallResult: DATA: " + item.toString());
                if (item.getContent() != null) {
                    Stylist[] stylists = new Stylist[item.getContent().size()];

                    //delete items for insert latest data
                    swiftSalonDao.deleteStylists(salonId);

                    int index = 0;
                    for (long rowId : swiftSalonDao.insertStylists(item.getContent().toArray(stylists))) {
                        if (rowId == -1) {
                            Log.d(TAG, "saveCallResult: CONFLICT... This appointment is already in the cache");
                            swiftSalonDao.updateStylist(
                                    stylists[index].getId(),
                                    stylists[index].getSalonId(),
                                    stylists[index].getName(),
                                    stylists[index].getGender(),
                                    stylists[index].getImage(),
                                    stylists[index].getStatus());
                        }
                        index++;
                    }
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Stylist> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Stylist>> loadFromDb() {
                return swiftSalonDao.getStylists(salonId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<List<Stylist>>>> createCall() {
                return ServiceGenerator.getSalonApi().getStylists(salonId);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericResponse<Stylist>>> updateStylistApi(Stylist stylist) {
        return new NetworkOnlyBoundResource<Stylist, GenericResponse<Stylist>>(AppExecutor.getInstance()) {

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Stylist>>> createCall() {
                return ServiceGenerator.getSalonApi().updateStylist(stylist);
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Stylist> item) {
                if (item.getContent() != null) {
                    swiftSalonDao.insertStylist(item.getContent());
                }
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericResponse<Stylist>>> deleteStylistApi(Stylist stylist) {
        return new NetworkOnlyBoundResource<Stylist, GenericResponse<Stylist>>(AppExecutor.getInstance()) {

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Stylist>>> createCall() {
                return ServiceGenerator.getSalonApi().deleteStylist(stylist.getId());
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Stylist> item) {
                if(item.getContent() != null) {
                    swiftSalonDao.deleteStylist(item.getContent());
                }
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<Job>>> getJobsByStylistApi(int id) {
        return new NetworkBoundResource<List<Job>, GenericResponse<List<StylistJob>>>(AppExecutor.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GenericResponse<List<StylistJob>> item) {
                if(item.getContent() != null) {
                    StylistJob[] stylistJobs =  new StylistJob[item.getContent().size()];
                    swiftSalonDao.insertStylistJobs(item.getContent().toArray(stylistJobs));
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Job> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Job>> loadFromDb() {
                return swiftSalonDao.getJobsByStylist(id);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<List<StylistJob>>>> createCall() {
                return ServiceGenerator.getSalonApi().getStylistJobsByStylist(id);
            }
        }.getAsLiveData();
    }
}
