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
import lk.nibm.swiftsalon.util.Session;

public class HistoryViewModel extends AndroidViewModel {

    private AppointmentRepository repository;
    private MediatorLiveData<Resource<List<Appointment>>> appointments = new MediatorLiveData<>();
    private Session session;

    private boolean isFetching;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        repository = AppointmentRepository.getInstance(application);
        session = new Session(application);
    }

    public LiveData<Resource<List<Appointment>>> getAppointments() {
        return appointments;
    }

    public void allAppointmentsApi() {
        if (!isFetching) {
            executeFetch();
        }
    }

    public void executeFetch() {
        isFetching = true;

        final LiveData<Resource<List<Appointment>>> repositorySource = repository.getOldAppointmentApi(session.getSalonId());

        appointments.addSource(repositorySource, listResource -> {
            if (listResource != null) {
                appointments.setValue(listResource);
                if (listResource.status == Resource.Status.SUCCESS) {
                    isFetching = false;
                } else if (listResource.status == Resource.Status.ERROR) {
                    isFetching = false;
                    appointments.removeSource(repositorySource);
                }
            } else {
                appointments.removeSource(repositorySource);
            }
        });
    }
}
