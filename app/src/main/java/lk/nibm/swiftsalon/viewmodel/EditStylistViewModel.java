package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.repository.StylistRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Resource;

public class EditStylistViewModel extends AndroidViewModel {

    private StylistRepository repository;
    private MediatorLiveData<Resource<GenericResponse<Stylist>>> stylist = new MediatorLiveData<>();

    private boolean isFetching;

    public EditStylistViewModel(@NonNull Application application) {
        super(application);
        repository = StylistRepository.getInstance(application);
    }


    public LiveData<Resource<GenericResponse<Stylist>>> updateStylist() {
        return stylist;
    }

    public void updateApi(Stylist objStylist) {
        if (!isFetching) {
            executeUpdate(objStylist);
        }
    }

    private void executeUpdate(Stylist objStylist) {
        isFetching = true;

        final LiveData<Resource<GenericResponse<Stylist>>> repositorySource = repository.updateStylistApi(objStylist);

        stylist.addSource(repositorySource, resource -> {
            if (resource != null) {
                stylist.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS) {
                    isFetching = false;
                } else if (resource.status == Resource.Status.ERROR) {
                    isFetching = false;
                    stylist.removeSource(repositorySource);
                }
            } else {
                stylist.removeSource(repositorySource);
            }
        });
    }
}
