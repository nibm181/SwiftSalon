package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.repository.StylistRepository;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;

public class StylistsViewModel extends AndroidViewModel {
    private static final String TAG = "StylistsViewModel";

    private StylistRepository repository;
    private MediatorLiveData<Resource<List<Stylist>>> stylists = new MediatorLiveData<>();
    private Session session;

    private boolean isFetching;

    public StylistsViewModel(@NonNull Application application) {
        super(application);
        repository = StylistRepository.getInstance(application);
        session = new Session(application);
    }

    public LiveData<Resource<List<Stylist>>> getStylists() {
        return stylists;
    }

    public void stylistsApi() {
        if (!isFetching) {
            executeFetch();
        }
    }

    private void executeFetch() {
        isFetching = true;

        final LiveData<Resource<List<Stylist>>> repositorySource = repository.getStylistsApi(session.getSalonId());

        stylists.addSource(repositorySource, listResource -> {
            if (listResource != null) {
                stylists.setValue(listResource);
                if (listResource.status == Resource.Status.SUCCESS) {
                    isFetching = false;
                } else if (listResource.status == Resource.Status.ERROR) {
                    isFetching = false;
                    stylists.removeSource(repositorySource);
                }
            } else {
                stylists.removeSource(repositorySource);
            }
        });

    }
}
