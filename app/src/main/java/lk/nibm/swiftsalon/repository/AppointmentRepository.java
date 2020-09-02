package lk.nibm.swiftsalon.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.model.AppointmentDetail;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.persistence.SwiftSalonDao;
import lk.nibm.swiftsalon.persistence.SwiftSalonDatabase;
import lk.nibm.swiftsalon.request.ServiceGenerator;
import lk.nibm.swiftsalon.request.response.ApiResponse;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.AppExecutor;
import lk.nibm.swiftsalon.util.NetworkBoundResource;
import lk.nibm.swiftsalon.util.NetworkOnlyBoundResource;
import lk.nibm.swiftsalon.util.Resource;

public class AppointmentRepository {

    private static final String TAG = "AppointmentRepository";

    private static AppointmentRepository instance;
    private SwiftSalonDao swiftSalonDao;

    public static AppointmentRepository getInstance(Context context) {
        if (instance == null) {
            instance = new AppointmentRepository(context);
        }
        return instance;
    }

    private AppointmentRepository(Context context) {
        swiftSalonDao = SwiftSalonDatabase.getInstance(context).getDao();
    }

    public LiveData<Resource<Appointment>> getAppointmentApi(int id) {
        return new NetworkBoundResource<Appointment, GenericResponse<Appointment>>(AppExecutor.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Appointment> item) {
                if (item.getContent() != null) {
                    swiftSalonDao.insertAppointment(item.getContent());
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Appointment data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Appointment> loadFromDb() {
                return swiftSalonDao.getAppointment(id);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Appointment>>> createCall() {
                return ServiceGenerator.getAppointmentApi()
                        .getAppointment(id);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<Appointment>>> getAllAppointmentApi(int salonId) {
        return new NetworkBoundResource<List<Appointment>, GenericResponse<List<Appointment>>>(AppExecutor.getInstance()) {
            @Override
            protected void saveCallResult(@NonNull GenericResponse<List<Appointment>> item) {
                if (item.getContent() != null) {
                    Log.d(TAG, "saveCallResult: count: " + item.getContent().size());
                    Appointment[] appointments = new Appointment[item.getContent().size()];

                    int index = 0;
                    for (long rowId : swiftSalonDao.insertAppointments(item.getContent().toArray(appointments))) {
                        Log.d(TAG, "saveCallResult: data: " + appointments[index].getCustomerFirstName());
                        if (rowId == -1) {
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
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Appointment>> loadFromDb() {
                return swiftSalonDao.getAppointments(salonId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<List<Appointment>>>> createCall() {
                return ServiceGenerator.getAppointmentApi()
                        .getAllAppointments(salonId);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<Appointment>>> getOldAppointmentApi(int salonId) {
        return new NetworkBoundResource<List<Appointment>, GenericResponse<List<Appointment>>>(AppExecutor.getInstance()) {
            @Override
            protected void saveCallResult(@NonNull GenericResponse<List<Appointment>> item) {
                if (item.getContent() != null) {
                    Log.d(TAG, "saveCallResult: count: " + item.getContent().size());
                    Appointment[] appointments = new Appointment[item.getContent().size()];

                    int index = 0;
                    for (long rowId : swiftSalonDao.insertAppointments(item.getContent().toArray(appointments))) {
                        Log.d(TAG, "saveCallResult: data: " + appointments[index].getCustomerFirstName());
                        if (rowId == -1) {
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
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Appointment>> loadFromDb() {
                return swiftSalonDao.getOldAppointments(salonId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<List<Appointment>>>> createCall() {
                return ServiceGenerator.getAppointmentApi()
                        .getOldAppointments(salonId);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<Appointment>>> getNewAppointmentApi(int salonId) {
        return new NetworkBoundResource<List<Appointment>, GenericResponse<List<Appointment>>>(AppExecutor.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GenericResponse<List<Appointment>> item) {
                if (item.getContent() != null) {
                    Appointment[] appointments = new Appointment[item.getContent().size()];

                    int index = 0;
                    for (long rowId : swiftSalonDao.insertAppointments(item.getContent().toArray(appointments))) {

                        if (rowId == -1) {
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
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Appointment>> loadFromDb() {
                return swiftSalonDao.getNewAppointments(salonId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<List<Appointment>>>> createCall() {
                return ServiceGenerator.getAppointmentApi()
                        .getAppointmentsByStatus(salonId, "pending");
            }
        }.getAsLiveData();
    }


    public LiveData<Resource<List<Appointment>>> getOngoingAppointmentApi(int salonId) {
        return new NetworkBoundResource<List<Appointment>, GenericResponse<List<Appointment>>>(AppExecutor.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GenericResponse<List<Appointment>> item) {
                if (item.getContent() != null) {
                    Appointment[] appointments = new Appointment[item.getContent().size()];

                    int index = 0;
                    for (long rowId : swiftSalonDao.insertAppointments(item.getContent().toArray(appointments))) {

                        if (rowId == -1) {
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
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Appointment>> loadFromDb() {
                return swiftSalonDao.getOngoingAppointments(salonId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<List<Appointment>>>> createCall() {
                return ServiceGenerator.getAppointmentApi()
                        .getAppointmentsByStatus(salonId, "on schedule");
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<AppointmentDetail>>> getAppointmentDetailsApi(int appointmentId) {
        return new NetworkBoundResource<List<AppointmentDetail>, GenericResponse<List<AppointmentDetail>>>(AppExecutor.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GenericResponse<List<AppointmentDetail>> item) {
                if (item.getContent() != null) {
                    AppointmentDetail[] details = new AppointmentDetail[item.getContent().size()];
                    swiftSalonDao.insertAppointmentDetails(item.getContent().toArray(details));
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<AppointmentDetail> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<AppointmentDetail>> loadFromDb() {
                return swiftSalonDao.getAppointmentDetails(appointmentId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<List<AppointmentDetail>>>> createCall() {
                return ServiceGenerator.getAppointmentApi()
                        .getAppointmentDetails(appointmentId);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericResponse<Appointment>>> acceptAppointmentApi(Appointment appointment) {
        return new NetworkOnlyBoundResource<Appointment, GenericResponse<Appointment>>(AppExecutor.getInstance()) {

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Appointment>>> createCall() {
                return ServiceGenerator.getAppointmentApi().updateAppointmentStatus(appointment);
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Appointment> item) {
                if (item.getContent() != null) {
                    swiftSalonDao.insertAppointment(item.getContent());
                }
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericResponse<Appointment>>> cancelAppointmentApi(Appointment appointment) {
        return new NetworkOnlyBoundResource<Appointment, GenericResponse<Appointment>>(AppExecutor.getInstance()) {

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Appointment>>> createCall() {
                return ServiceGenerator.getAppointmentApi().updateAppointmentStatus(appointment);
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Appointment> item) {
                if (item.getContent() != null) {
                    swiftSalonDao.insertAppointment(item.getContent());
                }
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<Stylist>> getStylistApi(int stylistId) {
        return new NetworkBoundResource<Stylist, GenericResponse<Stylist>>(AppExecutor.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Stylist> item) {
                if (item.getContent() != null) {
                    swiftSalonDao.insertStylist(item.getContent());
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Stylist data) {
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<Stylist> loadFromDb() {
                return swiftSalonDao.getStylist(stylistId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Stylist>>> createCall() {
                return ServiceGenerator.getSalonApi().getStylist(stylistId);
            }
        }.getAsLiveData();
    }

}
