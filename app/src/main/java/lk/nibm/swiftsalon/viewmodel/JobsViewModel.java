package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.repository.JobRepository;
import lk.nibm.swiftsalon.util.Resource;

public class JobsViewModel extends AndroidViewModel {
    private static final String TAG = "JobsViewModel";

    private MediatorLiveData<Resource<List<Job>>> jobs = new MediatorLiveData<>();
    private JobRepository repository;

    private boolean isFetching;

    public JobsViewModel(@NonNull Application application) {
        super(application);
        repository = JobRepository.getInstance(application);
    }

    public LiveData<Resource<List<Job>>> getJobs() {
        return jobs;
    }

    public void jobsApi() {
        if (!isFetching) {
            executeFetch();
        }
    }

    private void executeFetch() {
        isFetching = true;

        final LiveData<Resource<List<Job>>> repositorySource = repository.getJobsApi();

        jobs.addSource(repositorySource, listResource -> {
            if (listResource != null) {
                jobs.setValue(listResource);
                if (listResource.status == Resource.Status.SUCCESS) {
                    isFetching = false;
                } else if (listResource.status == Resource.Status.ERROR) {
                    isFetching = false;
                    jobs.removeSource(repositorySource);
                }
            } else {
                jobs.removeSource(repositorySource);
            }
        });

    }
}
