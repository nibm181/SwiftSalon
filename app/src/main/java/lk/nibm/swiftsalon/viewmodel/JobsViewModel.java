package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.repository.JobRepository;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;

public class JobsViewModel extends AndroidViewModel {
    private static final String TAG = "JobsViewModel";

    private MediatorLiveData<Resource<List<Job>>> jobs = new MediatorLiveData<>();
    private JobRepository repository;
    private Session session;

    private boolean isFetching;

    public JobsViewModel(@NonNull Application application) {
        super(application);
        repository = JobRepository.getInstance(application);
        session = new Session(application);
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

        final LiveData<Resource<List<Job>>> repositorySource = repository.getJobsApi(session.getSalonId());

        jobs.addSource(repositorySource, listResource -> {
            if (listResource != null) {
                jobs.setValue(listResource);

                if (listResource.status == Resource.Status.ERROR) {
                    jobs.removeSource(repositorySource);
                }

                isFetching = false;
            } else {
                jobs.removeSource(repositorySource);
            }
        });
    }
}
