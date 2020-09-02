package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.List;
import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.repository.AppointmentRepository;
import lk.nibm.swiftsalon.repository.SalonRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.service.RefreshTokenWorker;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;

public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = "HomeViewModel";
    private AppointmentRepository appointmentRepository;
    private SalonRepository salonRepository;
    private Session session;

    private MediatorLiveData<Resource<List<Appointment>>> newAppointments = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<Appointment>>> ongoingAppointments = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericResponse<Appointment>>> acceptedAppointment = new MediatorLiveData<>();
    private MediatorLiveData<Resource<Salon>> salon = new MediatorLiveData<>();

    private MediatorLiveData<Integer> countNewAppointments = new MediatorLiveData<>();
    private MediatorLiveData<Integer> countOngoingAppointments = new MediatorLiveData<>();

    private boolean isFetchingNew, isFetchingOngoing, isAccepting;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        session = new Session(application);
        appointmentRepository = AppointmentRepository.getInstance(application);
        salonRepository = SalonRepository.getInstance(application);
    }

    public LiveData<Resource<List<Appointment>>> getNewAppointments() {
        return newAppointments;
    }

    public LiveData<Resource<List<Appointment>>> getOngoingAppointments() {
        return ongoingAppointments;
    }

    public LiveData<Integer> getCountNewAppointments() {
        return countNewAppointments;
    }

    public LiveData<Integer> getCountOngoingAppointments() {
        return countOngoingAppointments;
    }

    public LiveData<Resource<Salon>> getSalon() {
        return salon;
    }

    public LiveData<Resource<GenericResponse<Appointment>>> acceptAppointment() {
        return acceptedAppointment;
    }

    public void newAppointmentApi() {
        if(!isFetchingNew) {
            executeFetchNewAppointments();
        }
    }

    public void ongoingAppointmentApi() {
        if(!isFetchingOngoing) {
            executeFetchOngoingAppointments();
        }
    }

    public void acceptAppointmentApi(int id) {
        if(!isAccepting) {
            Appointment appointment = new Appointment();
            appointment.setId(id);
            appointment.setStatus("on schedule");

            executeAcceptAppointment(appointment);
        }
    }

    private void executeFetchOngoingAppointments() {
        isFetchingOngoing = true;

        final LiveData<Resource<List<Appointment>>> repositorySource = appointmentRepository.getOngoingAppointmentApi(session.getSalonId());

        ongoingAppointments.addSource(repositorySource, new Observer<Resource<List<Appointment>>>() {
            @Override
            public void onChanged(Resource<List<Appointment>> listResource) {
                if(listResource != null) {
                    ongoingAppointments.setValue(listResource);
                    if(listResource.status == Resource.Status.SUCCESS) {
                        isFetchingOngoing = false;
                    }
                    else if(listResource.status == Resource.Status.ERROR) {
                        isFetchingOngoing = false;
                        ongoingAppointments.removeSource(repositorySource);
                    }

                    if(listResource.data != null) {
                        countOngoingAppointments.setValue(listResource.data.size());
                    }
                }
                else {
                    ongoingAppointments.removeSource(repositorySource);
                }
            }
        });
    }

    private void executeFetchNewAppointments() {
        isFetchingNew = true;

        final LiveData<Resource<List<Appointment>>> repositorySource = appointmentRepository.getNewAppointmentApi(session.getSalonId());

        newAppointments.addSource(repositorySource, new Observer<Resource<List<Appointment>>>() {
            @Override
            public void onChanged(Resource<List<Appointment>> listResource) {
                if(listResource != null) {
                    newAppointments.setValue(listResource);

                    if(listResource.status == Resource.Status.SUCCESS) {
                        isFetchingNew = false;
                    }
                    else if(listResource.status == Resource.Status.ERROR) {
                        isFetchingNew = false;
                        newAppointments.removeSource(repositorySource);
                    }

                    if(listResource.data != null) {
                        countNewAppointments.setValue(listResource.data.size());
                    }
                    else{
                        countNewAppointments.setValue(0);
                    }
                    
                }
                else {
                    newAppointments.removeSource(repositorySource);
                }
            }
        });
    }

    public void getSalonApi() {
        final LiveData<Resource<Salon>> repositorySource = salonRepository.getSalonApi(session.getSalonId());

        salon.addSource(repositorySource, new Observer<Resource<Salon>>() {
            @Override
            public void onChanged(Resource<Salon> salonResource) {
                if(salonResource != null) {
                    salon.setValue(salonResource);
                    Log.d(TAG, "onChanged: resource: " + salonResource.message);

                    if(salonResource.status == Resource.Status.ERROR) {
                        salon.removeSource(repositorySource);
                    }
                }
                else {
                    salon.removeSource(repositorySource);
                }
            }
        });

    }

    private void executeAcceptAppointment(Appointment appointment) {
        isAccepting = true;

        final LiveData<Resource<GenericResponse<Appointment>>> repositorySource = appointmentRepository.acceptAppointmentApi(appointment);

        acceptedAppointment.addSource(repositorySource, new Observer<Resource<GenericResponse<Appointment>>>() {
            @Override
            public void onChanged(Resource<GenericResponse<Appointment>> appointmentResource) {
                if(appointmentResource != null) {
                    acceptedAppointment.setValue(appointmentResource);
                    Log.d(TAG, "onChanged: resource: " + appointmentResource.status);

                    if(appointmentResource.status == Resource.Status.ERROR) {
                        acceptedAppointment.removeSource(repositorySource);
                        isAccepting = false;
                    }
                    else if(appointmentResource.status == Resource.Status.SUCCESS) {
                        isAccepting = false;
                    }
                }
                else {
                    acceptedAppointment.removeSource(repositorySource);
                }
            }
        });
    }

}
