package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.repository.AppointmentRepository;
import lk.nibm.swiftsalon.util.Resource;

public class HomeViewModel extends AndroidViewModel {

    private AppointmentRepository repository;
    private MediatorLiveData<Resource<List<Appointment>>> newAppointments = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<Appointment>>> ongoingAppointments = new MediatorLiveData<>();

    private boolean isFetchingNew, isFetchingOngoing;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = AppointmentRepository.getInstance(application);
    }

    public LiveData<Resource<List<Appointment>>> getNewAppointments() {
        return newAppointments;
    }

    public LiveData<Resource<List<Appointment>>> getOngoingAppointments() {
        return ongoingAppointments;
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

    private void executeFetchOngoingAppointments() {
        isFetchingOngoing = true;

        final LiveData<Resource<List<Appointment>>> repositorySource = repository.getOngoingAppointmentApi();

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
                }
                else {
                    ongoingAppointments.removeSource(repositorySource);
                }
            }
        });
    }

    private void executeFetchNewAppointments() {
        isFetchingNew = true;

        final LiveData<Resource<List<Appointment>>> repositorySource = repository.getNewAppointmentApi();

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
                }
                else {
                    newAppointments.removeSource(repositorySource);
                }
            }
        });
    }
}
