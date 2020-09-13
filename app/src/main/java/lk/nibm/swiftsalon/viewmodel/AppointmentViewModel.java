package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import java.util.List;

import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.model.AppointmentDetail;
import lk.nibm.swiftsalon.model.Customer;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.repository.AppointmentRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Resource;

import static lk.nibm.swiftsalon.util.Constants.STATUS_CANCELED;
import static lk.nibm.swiftsalon.util.Constants.STATUS_COMPLETED;
import static lk.nibm.swiftsalon.util.Constants.STATUS_ONSCHEDULE;

public class AppointmentViewModel extends AndroidViewModel {

    private static final String TAG = "AppointmentViewModel";
    private AppointmentRepository repository;
    private MediatorLiveData<Resource<List<AppointmentDetail>>> appointmentDetails = new MediatorLiveData<>();
    private MediatorLiveData<Resource<Stylist>> stylist = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericResponse<Appointment>>> acceptedAppointment = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericResponse<Appointment>>> canceledAppointment = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericResponse<Appointment>>> completedAppointment = new MediatorLiveData<>();

    private boolean isFetchingDetails;
    private boolean isFetchingStylist;
    private boolean isUpdating;

    public AppointmentViewModel(@NonNull Application application) {
        super(application);
        repository = AppointmentRepository.getInstance(application);
    }

    public LiveData<Resource<List<AppointmentDetail>>> getAppointmentDetails() {
        return appointmentDetails;
    }

    public LiveData<Resource<Stylist>> getStylist() {
        return stylist;
    }

    public LiveData<Resource<GenericResponse<Appointment>>> acceptAppointment() {
        return acceptedAppointment;
    }

    public LiveData<Resource<GenericResponse<Appointment>>> cancelAppointment() {
        return canceledAppointment;
    }

    public LiveData<Resource<GenericResponse<Appointment>>> completeAppointment() {
        return completedAppointment;
    }

    public void appointmentDetailsApi(int appointmentId) {
        if(!isFetchingDetails) {
            executeFetchAppointmentDetails(appointmentId);
        }
    }

    public void stylistApi(int stylistId) {
        if(!isFetchingStylist) {
            executeFetchStylist(stylistId);
        }
    }

    public void acceptAppointmentApi(int appointmentId) {
        if(!isUpdating) {
            Appointment appointment = new Appointment();
            appointment.setId(appointmentId);
            appointment.setStatus(STATUS_ONSCHEDULE);

            executeAcceptAppointment(appointment);
        }
    }

    public void cancelAppointmentApi(int appointmentId) {
        if(!isUpdating) {
            Appointment appointment = new Appointment();
            appointment.setId(appointmentId);
            appointment.setStatus(STATUS_CANCELED);

            executeCancelAppointment(appointment);
        }
    }

    public void completeAppointmentApi(int appointmentId) {
        if(!isUpdating) {
            Appointment appointment = new Appointment();
            appointment.setId(appointmentId);
            appointment.setStatus(STATUS_COMPLETED);

            executeCompleteAppointment(appointment);
        }
    }

    private void executeFetchAppointmentDetails(int appointmentId) {
        isFetchingDetails = true;

        final LiveData<Resource<List<AppointmentDetail>>> repositorySource = repository.getAppointmentDetailsApi(appointmentId);

        appointmentDetails.addSource(repositorySource, new Observer<Resource<List<AppointmentDetail>>>() {
            @Override
            public void onChanged(Resource<List<AppointmentDetail>> listResource) {
                if(listResource != null) {
                    appointmentDetails.setValue(listResource);
                    if(listResource.status == Resource.Status.SUCCESS) {
                        isFetchingDetails = false;
                    }
                    else if(listResource.status == Resource.Status.ERROR) {
                        isFetchingDetails = false;
                        appointmentDetails.removeSource(repositorySource);
                    }
                }
                else {
                    appointmentDetails.removeSource(repositorySource);
                }
            }
        });
    }



    private void executeFetchStylist(int stylistId) {
        isFetchingStylist = true;

        final LiveData<Resource<Stylist>> repositorySource = repository.getStylistApi(stylistId);

        stylist.addSource(repositorySource, new Observer<Resource<Stylist>>() {
            @Override
            public void onChanged(Resource<Stylist> stylistResource) {
                if(stylistResource != null) {

                    stylist.setValue(stylistResource);
                    if(stylistResource.status == Resource.Status.SUCCESS) {
                        isFetchingStylist = false;
                    }
                    else if(stylistResource.status == Resource.Status.ERROR) {
                        isFetchingStylist = false;
                        stylist.removeSource(repositorySource);
                    }
                }
                else {
                    stylist.removeSource(repositorySource);
                }
            }
        });
    }

    private void executeAcceptAppointment(Appointment appointment) {
        isUpdating = true;

        final LiveData<Resource<GenericResponse<Appointment>>> repositorySource = repository.acceptAppointmentApi(appointment);

        acceptedAppointment.addSource(repositorySource, new Observer<Resource<GenericResponse<Appointment>>>() {
            @Override
            public void onChanged(Resource<GenericResponse<Appointment>> appointmentResource) {
                if(appointmentResource != null) {
                    acceptedAppointment.setValue(appointmentResource);
                    Log.d(TAG, "onChanged: resource: " + appointmentResource.message);

                    if(appointmentResource.status == Resource.Status.ERROR) {
                        acceptedAppointment.removeSource(repositorySource);
                        isUpdating = false;
                    }
                    else if(appointmentResource.status == Resource.Status.SUCCESS) {
                        isUpdating = false;
                    }
                }
                else {
                    acceptedAppointment.removeSource(repositorySource);
                }
            }
        });
    }

    private void executeCancelAppointment(Appointment appointment) {
        isUpdating = true;

        final LiveData<Resource<GenericResponse<Appointment>>> repositorySource = repository.cancelAppointmentApi(appointment);

        canceledAppointment.addSource(repositorySource, new Observer<Resource<GenericResponse<Appointment>>>() {
            @Override
            public void onChanged(Resource<GenericResponse<Appointment>> appointmentResource) {
                if(appointmentResource != null) {
                    canceledAppointment.setValue(appointmentResource);
                    Log.d(TAG, "onChanged: resource: " + appointmentResource.message);

                    if(appointmentResource.status == Resource.Status.ERROR) {
                        canceledAppointment.removeSource(repositorySource);
                        isUpdating = false;
                    }
                    else if(appointmentResource.status == Resource.Status.SUCCESS) {
                        isUpdating = false;
                    }
                }
                else {
                    canceledAppointment.removeSource(repositorySource);
                }
            }
        });
    }

    private void executeCompleteAppointment(Appointment appointment) {
        isUpdating = true;

        final LiveData<Resource<GenericResponse<Appointment>>> repositorySource = repository.acceptAppointmentApi(appointment);

        completedAppointment.addSource(repositorySource, new Observer<Resource<GenericResponse<Appointment>>>() {
            @Override
            public void onChanged(Resource<GenericResponse<Appointment>> appointmentResource) {
                if(appointmentResource != null) {
                    completedAppointment.setValue(appointmentResource);
                    Log.d(TAG, "onChanged: resource: " + appointmentResource.message);

                    if(appointmentResource.status == Resource.Status.ERROR) {
                        completedAppointment.removeSource(repositorySource);
                        isUpdating = false;
                    }
                    else if(appointmentResource.status == Resource.Status.SUCCESS) {
                        isUpdating = false;
                    }
                }
                else {
                    completedAppointment.removeSource(repositorySource);
                }
            }
        });
    }
}
