package lk.nibm.swiftsalon.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import lk.nibm.swiftsalon.model.Job;
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

public class JobRepository {

    private static final String TAG = "JobRepository";

    private static JobRepository instance;
    private SwiftSalonDao swiftSalonDao;

    public static JobRepository getInstance(Context context) {
        if (instance == null) {
            instance = new JobRepository(context);
        }
        return instance;
    }

    private JobRepository(Context context) {
        swiftSalonDao = SwiftSalonDatabase.getInstance(context).getDao();
    }

    public LiveData<Resource<List<Job>>> getJobsApi(int salonId) {
        return new NetworkBoundResource<List<Job>, GenericResponse<List<Job>>>(AppExecutor.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GenericResponse<List<Job>> item) {
                Log.d(TAG, "saveCallResult: DATA: " + item.getStatus());
                if (item.getContent() != null) {
                    Log.d(TAG, "saveCallResult: count: " + item.getContent().size());
                    Job[] jobs = new Job[item.getContent().size()];

                    //delete items for insert latest data
                    swiftSalonDao.deleteJobs();

                    int index = 0;
                    for (long rowId : swiftSalonDao.insertJobs(item.getContent().toArray(jobs))) {
                        Log.d(TAG, "saveCallResult: data: " + jobs[index].getName());
                        if (rowId == -1) {
                            Log.d(TAG, "saveCallResult: CONFLICT... This appointment is already in the cache");
                            swiftSalonDao.updateJob(
                                    jobs[index].getId(),
                                    jobs[index].getName(),
                                    jobs[index].getDuration(),
                                    jobs[index].getPrice());
                        }
                        index++;
                    }
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Job> data) {
//                if (data != null && data.isEmpty()) {
//                    return true;
//                }
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Job>> loadFromDb() {
                return swiftSalonDao.getJobs(salonId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<List<Job>>>> createCall() {
                return ServiceGenerator.getSalonApi().getJobs(salonId);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<Job>> getJobApi(int id) {
        return new NetworkBoundResource<Job, GenericResponse<Job>>(AppExecutor.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Job> item) {
                if (item.getContent() != null) {
                    swiftSalonDao.insertJob(item.getContent());
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Job data) {
                if (data == null) {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<Job> loadFromDb() {
                return swiftSalonDao.getJob(id);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Job>>> createCall() {
                return ServiceGenerator.getSalonApi().getJob(id);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericResponse<Job>>> updateJobApi(Job job) {
        return new NetworkOnlyBoundResource<Job, GenericResponse<Job>>(AppExecutor.getInstance()) {

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Job>>> createCall() {
                return ServiceGenerator.getSalonApi().updateJob(job);
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Job> item) {
                if(item.getContent() != null) {
                    swiftSalonDao.insertJob(item.getContent());
                }
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericResponse<Job>>> deleteJobApi(Job job) {
        return new NetworkOnlyBoundResource<Job, GenericResponse<Job>>(AppExecutor.getInstance()) {

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Job>>> createCall() {
                return ServiceGenerator.getSalonApi().deleteJob(job.getId());
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Job> item) {
                if(item.getContent() != null) {
                    Log.d(TAG, "saveCallResult: ITEM: " + item.getContent().toString());
                    swiftSalonDao.deleteJob(item.getContent());
                }
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericResponse<Job>>> saveJobApi(Job job) {
        return new NetworkOnlyBoundResource<Job, GenericResponse<Job>>(AppExecutor.getInstance()) {

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Job>>> createCall() {
                return ServiceGenerator.getSalonApi().saveJob(job);
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Job> item) {
                if(item.getContent() != null) {
                    Log.d(TAG, "saveCallResult: ITEM: " + item.getContent().toString());
                    swiftSalonDao.insertJob(item.getContent());
                }
            }
        }.getAsLiveData();
    }

    public void saveJobsApi(int salonId) {
        new NetworkBoundResource<List<Job>, GenericResponse<List<Job>>>(AppExecutor.getInstance()) {

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<List<Job>>>> createCall() {
                return ServiceGenerator.getSalonApi().getJobs(salonId);
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<List<Job>> item) {
                if(item.getContent() != null) {
                    Job[] jobs = new Job[item.getContent().size()];
                    swiftSalonDao.insertJobs(item.getContent().toArray(jobs));
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Job> data) {
                return data == null || data.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<List<Job>> loadFromDb() {
                return null;
            }
        };
    }
}
