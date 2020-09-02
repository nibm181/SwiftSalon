package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.HashMap;

import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.repository.SalonRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;

public class EditProfileViewModel extends AndroidViewModel {

    private SalonRepository repository;
    private Session session;
    private MediatorLiveData<Resource<GenericResponse<Salon>>> salon = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericResponse>> verifyResponse = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericResponse>> confirmResponse = new MediatorLiveData<>();

    private boolean isFetching;

    public EditProfileViewModel(@NonNull Application application) {
        super(application);
        repository = SalonRepository.getInstance(application);
        session = new Session(application);
    }

    public LiveData<Resource<GenericResponse<Salon>>> updateSalon() {
        return salon;
    }

    public LiveData<Resource<GenericResponse>> verifyPassword() {
        return verifyResponse;
    }

    public LiveData<Resource<GenericResponse>> confirmPassword() {
        return confirmResponse;
    }

    public void updateApi(Salon objSalon) {
        if (!isFetching) {
            executeUpdate(objSalon);
        }
    }

    public void verifyApi(String password) {
        if (!isFetching) {
            HashMap<Object, Object> data = new HashMap<>();
            data.put("current_password", password);
            data.put("id", session.getSalonId());
            executeVerify(data);
        }
    }

    public void confirmApi(String password) {
        if (!isFetching) {
            HashMap<Object, Object> data = new HashMap<>();
            data.put("new_password", password);
            data.put("id", session.getSalonId());
            executeConfirm(data);
        }
    }

    private void executeUpdate(Salon objSalon) {
        isFetching = true;

        final LiveData<Resource<GenericResponse<Salon>>> repositorySource = repository.updateSalonApi(objSalon);

        salon.addSource(repositorySource, resource -> {
            if (resource != null) {
                salon.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS) {
                    isFetching = false;
                } else if (resource.status == Resource.Status.ERROR) {
                    isFetching = false;
                    salon.removeSource(repositorySource);
                }
            } else {
                salon.removeSource(repositorySource);
            }
        });
    }

    private void executeVerify(HashMap<Object, Object> data) {
        isFetching = true;

        final LiveData<Resource<GenericResponse>> repositorySource = repository.verifyPasswordApi(data);

        verifyResponse.addSource(repositorySource, resource -> {
            if (resource != null) {
                verifyResponse.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS) {
                    isFetching = false;
                } else if (resource.status == Resource.Status.ERROR) {
                    isFetching = false;
                    verifyResponse.removeSource(repositorySource);
                }
            } else {
                verifyResponse.removeSource(repositorySource);
            }
        });
    }

    private void executeConfirm(HashMap<Object, Object> data) {
        isFetching = true;

        final LiveData<Resource<GenericResponse>> repositorySource = repository.confirmPasswordApi(data);

        confirmResponse.addSource(repositorySource, resource -> {
            if (resource != null) {
                confirmResponse.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS) {
                    isFetching = false;
                } else if (resource.status == Resource.Status.ERROR) {
                    isFetching = false;
                    confirmResponse.removeSource(repositorySource);
                }
            } else {
                confirmResponse.removeSource(repositorySource);
            }
        });
    }
}
