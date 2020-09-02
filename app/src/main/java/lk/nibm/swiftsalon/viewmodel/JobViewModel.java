package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.Promotion;
import lk.nibm.swiftsalon.repository.JobRepository;
import lk.nibm.swiftsalon.repository.PromotionRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Resource;

public class JobViewModel extends AndroidViewModel {
    private static final String TAG = "JobViewModel";

    private JobRepository repository;
    private PromotionRepository promotionRepository;
    private MediatorLiveData<Resource<GenericResponse<Job>>> job = new MediatorLiveData<>();
    private MediatorLiveData<Resource<Promotion>> promotion = new MediatorLiveData<>();

    private boolean isFetching;

    public JobViewModel(@NonNull Application application) {
        super(application);
        repository = JobRepository.getInstance(application);
        promotionRepository = PromotionRepository.getInstance(application);
    }

    public LiveData<Resource<GenericResponse<Job>>> deleteJob() {
        return job;
    }

    public LiveData<Resource<Promotion>> checkPromotionForJob() {
        return promotion;
    }

    public void deleteApi(Job objJob) {
        if (!isFetching) {
            executeDelete(objJob);
        }
    }

    public void checkPromotionForJobApi(int jobId) {
        if (!isFetching) {
            executeFetchPromotion(jobId);
        }
    }

    private void executeDelete(Job objJob) {
        isFetching = true;

        final LiveData<Resource<GenericResponse<Job>>> repositorySource = repository.deleteJobApi(objJob);

        job.addSource(repositorySource, resource -> {
            if (resource != null) {
                job.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS) {
                    isFetching = false;
                } else if (resource.status == Resource.Status.ERROR) {
                    isFetching = false;
                    job.removeSource(repositorySource);
                }
            } else {
                job.removeSource(repositorySource);
            }
        });
    }

    private void executeFetchPromotion(int jobId) {
        isFetching = true;

        final LiveData<Resource<Promotion>> repositorySource = promotionRepository.getPromotionForJobApi(jobId);

        promotion.addSource(repositorySource, resource -> {
            if (resource != null) {
                promotion.setValue(resource);
                if (resource.status != Resource.Status.LOADING) {
                    isFetching = false;
                    promotion.removeSource(repositorySource);
                }
            } else {
                promotion.removeSource(repositorySource);
            }
        });
    }
}
