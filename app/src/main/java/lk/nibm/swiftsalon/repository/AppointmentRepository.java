package lk.nibm.swiftsalon.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.persistence.SwiftSalonDao;
import lk.nibm.swiftsalon.persistence.SwiftSalonDatabase;
import lk.nibm.swiftsalon.request.ServiceGenerator;
import lk.nibm.swiftsalon.request.response.ApiResponse;
import lk.nibm.swiftsalon.request.response.GenericListResponse;
import lk.nibm.swiftsalon.request.response.GenericObjectResponse;
import lk.nibm.swiftsalon.util.AppExecutor;
import lk.nibm.swiftsalon.util.NetworkBoundResource;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;

import static lk.nibm.swiftsalon.util.Constants.REFRESH_TIME;

public class AppointmentRepository {

    private static final String TAG = "AppointmentRepository";

    private static AppointmentRepository instance;
    private SwiftSalonDao swiftSalonDao;

    private MutableLiveData<List<Appointment>> newAppointments, ongoingAppointments;
    Application application;
    int salonId;
    Session session;

    public static AppointmentRepository getInstance(Context context) {
        if(instance == null) {
            instance = new AppointmentRepository(context);
        }
        return instance;
    }

    private AppointmentRepository(Context context) {
        swiftSalonDao = SwiftSalonDatabase.getInstance(context).getDao();

        session = new Session(context);
        salonId = session.getSalonId();
    }

    public LiveData<Resource<Appointment>> getAppointmentApi(int id) {
        return new NetworkBoundResource<Appointment, GenericObjectResponse<Appointment>>(AppExecutor.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GenericObjectResponse<Appointment> item) {
                if(item.getContent() != null) {
                    swiftSalonDao.insertAppointment(item.getContent());
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Appointment data) {
                int currentTime = (int) (System.currentTimeMillis() / 1000);

                if(data != null) {
                    if((currentTime - data.getModifiedOn()) >= REFRESH_TIME)
                    {
                        return true;
                    }
                }
                else {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<Appointment> loadFromDb() {
                return swiftSalonDao.getAppointment(id);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericObjectResponse<Appointment>>> createCall() {
                return ServiceGenerator.getAppointmentApi()
                        .getAppointment(id);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<Appointment>>> getAllAppointmentApi() {
        return new NetworkBoundResource<List<Appointment>, GenericListResponse<Appointment>>(AppExecutor.getInstance()) {
            @Override
            protected void saveCallResult(@NonNull GenericListResponse<Appointment> item) {
                if(item.getContents() != null) {
                    Log.d(TAG, "saveCallResult: count: " + item.getContents().size());
                    Appointment[] appointments = new Appointment[item.getContents().size()];

                    int index = 0;
                    for(long rowId : swiftSalonDao.insertAppointments((Appointment[]) (item.getContents().toArray(appointments)))) {
                        Log.d(TAG, "saveCallResult: data: " + appointments[index].getCustomerFirstName());
                        if(rowId == -1) {
                            Log.d(TAG, "saveCallResult: CONFLICT... This appointment is already in the cache");
                            swiftSalonDao.updateAppointmentStatus(
                                    appointments[index].getId(),
                                    appointments[index].getStatus(),
                                    appointments[index].getModifiedOn());
                        }
                        index++;
                    }
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Appointment> data) {
                int currentTime = (int) (System.currentTimeMillis() / 1000);
                Log.d(TAG, "shouldFetch: Data: " + data);

                if(data.size() > 0) {
                    Log.d(TAG, "shouldFetch: current time: " + currentTime);
                    Log.d(TAG, "shouldFetch: last refresh: " + data.get(data.size() - 1).getModifiedOn());
                    if((currentTime - data.get(data.size() - 1).getModifiedOn()) >= REFRESH_TIME)
                    {
                        Log.d(TAG, "shouldFetch: SHOULD REFRESH? " + true);
                        return true;
                    }
                }
                else {
                    return true;
                }
                Log.d(TAG, "shouldFetch: SHOULD REFRESH? " + false);
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Appointment>> loadFromDb() {
                return swiftSalonDao.getAppointments(salonId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericListResponse<Appointment>>> createCall() {
                return ServiceGenerator.getAppointmentApi()
                        .getAllAppointments();
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<Appointment>>> getNewAppointmentApi() {
        return new NetworkBoundResource<List<Appointment>, GenericListResponse<Appointment>>(AppExecutor.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GenericListResponse<Appointment> item) {
                if(item.getContents() != null) {
                    Appointment[] appointments = new Appointment[item.getContents().size()];

                    int index = 0;
                    for(long rowId: swiftSalonDao.insertAppointments((Appointment[]) (item.getContents().toArray(appointments)))) {

                        if(rowId == -1) {
                            swiftSalonDao.updateAppointmentStatus(
                                    appointments[index].getId(),
                                    appointments[index].getStatus(),
                                    appointments[index].getModifiedOn());
                        }
                        index++;
                    }
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Appointment> data) {
                int currentTime = (int) (System.currentTimeMillis() / 1000);

                if(data.size() > 0) {
                    if((currentTime - data.get(data.size() - 1).getModifiedOn()) >= REFRESH_TIME)
                    {
                        return true;
                    }
                }
                else {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Appointment>> loadFromDb() {
                return swiftSalonDao.getNewAppointments(salonId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericListResponse<Appointment>>> createCall() {
                return ServiceGenerator.getAppointmentApi()
                        .getNewAppointments();
            }
        }.getAsLiveData();
    }


    public LiveData<Resource<List<Appointment>>> getOngoingAppointmentApi() {
        return new NetworkBoundResource<List<Appointment>, GenericListResponse<Appointment>>(AppExecutor.getInstance()) {
            
            @Override
            protected void saveCallResult(@NonNull GenericListResponse<Appointment> item) {
                if(item.getContents() != null) {
                    Appointment[] appointments = new Appointment[item.getContents().size()];

                    int index = 0;
                    for(long rowId: swiftSalonDao.insertAppointments((Appointment[]) (item.getContents().toArray(appointments)))) {
                        
                        if(rowId == -1) {
                            swiftSalonDao.updateAppointmentStatus(
                                    appointments[index].getId(),
                                    appointments[index].getStatus(),
                                    appointments[index].getModifiedOn());
                        }
                        index++;
                    }
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Appointment> data) {
                int currentTime = (int) (System.currentTimeMillis() / 1000);

                if(data.size() > 0) {
                    if((currentTime - data.get(data.size() - 1).getModifiedOn()) >= REFRESH_TIME)
                    {
                        return true;
                    }
                }
                else {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Appointment>> loadFromDb() {
                return swiftSalonDao.getOngoingAppointments(salonId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericListResponse<Appointment>>> createCall() {
                return ServiceGenerator.getAppointmentApi()
                        .getOngoingAppointments();
            }
        }.getAsLiveData();
    }

}
