package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.persistence.SwiftSalonDao;
import lk.nibm.swiftsalon.repository.SalonRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Resource;

public class RegistrationViewModel extends AndroidViewModel {

    private static final String TAG = "RegistrationViewModel";

    private SalonRepository repository;
    private MediatorLiveData<Resource<GenericResponse>> response = new MediatorLiveData<>();

    private boolean isFetching;

    public RegistrationViewModel(@NonNull Application application) {
        super(application);
        repository = SalonRepository.getInstance(application);
    }

    public LiveData<Resource<GenericResponse>> sendEmail() {
        return response;
    }

    public void sendEmailApi(String email) {
        if(!isFetching) {
            executeFetch(email);
        }
    }

    private void executeFetch(String email) {
        isFetching = true;

        final LiveData<Resource<GenericResponse>> repositorySource = repository.sendEmail(email);

        response.addSource(repositorySource, resource -> {
            if (resource != null) {
                response.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS) {
                    isFetching = false;
                } else if (resource.status == Resource.Status.ERROR) {
                    isFetching = false;
                    response.removeSource(repositorySource);
                }
            } else {
                response.removeSource(repositorySource);
            }
        });
    }
}
