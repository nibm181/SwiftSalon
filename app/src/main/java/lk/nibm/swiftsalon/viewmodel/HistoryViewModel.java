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

public class HistoryViewModel extends AndroidViewModel {

    private AppointmentRepository repository;
    private MediatorLiveData<Resource<List<Appointment>>>  appointments = new MediatorLiveData<>();

    private boolean isFetching;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        repository = AppointmentRepository.getInstance(application);
    }

    public LiveData<Resource<List<Appointment>>> getAppointments() {
        return appointments;
    }

    public void allAppointmentsApi() {
        if(!isFetching) {
            executeFetch();
        }
    }

    public void executeFetch() {
        isFetching = true;

        final LiveData<Resource<List<Appointment>>> repositorySource = repository.getAllAppointmentApi();

        appointments.addSource(repositorySource, new Observer<Resource<List<Appointment>>>() {
            @Override
            public void onChanged(Resource<List<Appointment>> listResource) {
                if(listResource != null) {
                    appointments.setValue(listResource);
                    if(listResource.status == Resource.Status.SUCCESS) {
                        isFetching = false;
                    }
                    else if(listResource.status == Resource.Status.ERROR) {
                        isFetching = false;
                        appointments.removeSource(repositorySource);
                    }
                }
                else {
                    appointments.removeSource(repositorySource);
                }
            }
        });
    }
}
