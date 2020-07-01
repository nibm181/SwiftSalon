package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import java.util.List;
import lk.nibm.swiftsalon.model.AppointmentDetail;
import lk.nibm.swiftsalon.repository.AppointmentRepository;
import lk.nibm.swiftsalon.util.Resource;

public class AppointmentViewModel extends AndroidViewModel {

    private AppointmentRepository repository;
    private MediatorLiveData<Resource<List<AppointmentDetail>>> appointmentDetails = new MediatorLiveData<>();

    private boolean isFetching;

    public AppointmentViewModel(@NonNull Application application) {
        super(application);
        repository = AppointmentRepository.getInstance(application);
    }

    public LiveData<Resource<List<AppointmentDetail>>> getAppointmentDetails() {
        return appointmentDetails;
    }

    public void appointmentDetailsApi(int appointmentId) {
        if(!isFetching) {
            executeFetch(appointmentId);
        }
    }

    private void executeFetch(int appointmentId) {
        isFetching = true;

        final LiveData<Resource<List<AppointmentDetail>>> repositorySource = repository.getAppointmentDetailsApi(appointmentId);

        appointmentDetails.addSource(repositorySource, new Observer<Resource<List<AppointmentDetail>>>() {
            @Override
            public void onChanged(Resource<List<AppointmentDetail>> listResource) {
                if(listResource != null) {
                    appointmentDetails.setValue(listResource);
                    if(listResource.status == Resource.Status.SUCCESS) {
                        isFetching = false;
                    }
                    else if(listResource.status == Resource.Status.ERROR) {
                        isFetching = false;
                        appointmentDetails.removeSource(repositorySource);
                    }
                }
                else {
                    appointmentDetails.removeSource(repositorySource);
                }
            }
        });
    }
}
