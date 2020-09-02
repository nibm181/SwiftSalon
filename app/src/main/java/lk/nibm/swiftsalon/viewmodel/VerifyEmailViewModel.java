package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import lk.nibm.swiftsalon.repository.SalonRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Resource;

public class VerifyEmailViewModel extends AndroidViewModel {

    private SalonRepository repository;
    private MediatorLiveData<Resource<GenericResponse>> response = new MediatorLiveData<>();

    private boolean isFetching;

    public VerifyEmailViewModel(@NonNull Application application) {
        super(application);
        repository = SalonRepository.getInstance(application);
    }

    public LiveData<Resource<GenericResponse>> verifyEmail() {
        return response;
    }

    public void verifyEmailApi(String email, int code) {
        if(!isFetching) {
            executeFetch(email, code);
        }
    }

    private void executeFetch(String email, int code) {
        isFetching = true;

        final LiveData<Resource<GenericResponse>> repositorySource = repository.verifyEmail(email, code);

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
