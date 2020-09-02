package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import lk.nibm.swiftsalon.model.Promotion;
import lk.nibm.swiftsalon.repository.PromotionRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;
import okhttp3.MultipartBody;

public class PromotionsViewModel extends AndroidViewModel {
    private static final String TAG = "PromotionsViewModel";

    private PromotionRepository repository;
    private MediatorLiveData<Resource<List<Promotion>>> promotions = new MediatorLiveData<>();
    private Session session;

    private boolean isFetching;

    public PromotionsViewModel(@NonNull Application application) {
        super(application);
        repository = PromotionRepository.getInstance(application);
        session = new Session(application);
    }

    public LiveData<Resource<List<Promotion>>> getPromotions() {
        return promotions;
    }

    public void promotionsApi() {
        if (!isFetching) {
            executeFetch();
        }
    }

    private void executeFetch() {
        isFetching = true;

        final LiveData<Resource<List<Promotion>>> repositorySource = repository.getPromotionsApi(session.getSalonId());

        promotions.addSource(repositorySource, resource -> {
            if (resource != null) {
                promotions.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS) {
                    isFetching = false;
                } else if (resource.status == Resource.Status.ERROR) {
                    isFetching = false;
                    promotions.removeSource(repositorySource);
                }
            } else {
                promotions.removeSource(repositorySource);
            }
        });
    }
}
