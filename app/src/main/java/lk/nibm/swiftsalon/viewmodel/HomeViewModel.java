package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import java.util.List;
import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.repository.AppointmentRepository;
import lk.nibm.swiftsalon.util.Resource;

public class HomeViewModel extends AndroidViewModel {

    private AppointmentRepository repository;

    private MediatorLiveData<Resource<List<Appointment>>> newAppointments = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<Appointment>>> ongoingAppointments = new MediatorLiveData<>();

    private MediatorLiveData<Integer> countNewAppointments = new MediatorLiveData<>();
    private MediatorLiveData<Integer> countOngoingAppointments = new MediatorLiveData<>();

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

    public LiveData<Integer> getCountNewAppointments() {
        return countNewAppointments;
    }

    public LiveData<Integer> getCountOngoingAppointments() {
        return countOngoingAppointments;
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

                    if(listResource.data != null) {
                        countNewAppointments.setValue(listResource.data.size());
                    }
                }
                else {
                    newAppointments.removeSource(repositorySource);
                }
            }
        });
    }

    public void acceptAppointment(Appointment appointment) {
        repository.acceptAppointmentApi(appointment);
    }
}
