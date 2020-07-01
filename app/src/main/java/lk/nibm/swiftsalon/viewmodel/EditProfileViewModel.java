package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.repository.SalonRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Resource;

public class EditProfileViewModel extends AndroidViewModel {

    private SalonRepository repository;
    private MediatorLiveData<Resource<GenericResponse<Salon>>> salon = new MediatorLiveData<>();

    private boolean isFetching;

    public EditProfileViewModel(@NonNull Application application) {
        super(application);
        repository = SalonRepository.getInstance(application);
    }

    public LiveData<Resource<GenericResponse<Salon>>> updateSalon() {
        return salon;
    }

    public void updateApi(Salon objSalon) {
        if (!isFetching) {
            executeUpdate(objSalon);
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
}
