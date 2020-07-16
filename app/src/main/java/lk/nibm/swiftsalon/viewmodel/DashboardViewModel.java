package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.repository.SalonRepository;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;

public class DashboardViewModel extends AndroidViewModel {

    private SalonRepository repository;
    private MediatorLiveData<Resource<Salon>> salon = new MediatorLiveData<>();
    private Session session;
    private boolean isFetching;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        repository = SalonRepository.getInstance(application);
        session = new Session(application);
    }

    public LiveData<Resource<Salon>> getSalon() {
        return salon;
    }

    public void getSalonApi() {
        if(!isFetching) {
            executeSalonApi();
        }
    }

    private void executeSalonApi() {
        isFetching = true;

        final LiveData<Resource<Salon>> repositorySource = repository.getSalonApi(session.getSalonId());

        salon.addSource(repositorySource, salonResource -> {
            if(salonResource != null) {
                salon.setValue(salonResource);

                isFetching = false;

                if(salonResource.status == Resource.Status.ERROR) {
                    salon.removeSource(repositorySource);
                }
            }
            else {
                salon.removeSource(repositorySource);
            }
        });

    }
}
