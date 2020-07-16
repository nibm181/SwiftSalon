package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.repository.JobRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Resource;

public class AddJobViewModel extends AndroidViewModel {
    private static final String TAG = "AddJobViewModel";

    private JobRepository repository;
    private MediatorLiveData<Resource<GenericResponse<Job>>> job = new MediatorLiveData<>();

    private boolean isFetching;

    public AddJobViewModel(@NonNull Application application) {
        super(application);
        repository = JobRepository.getInstance(application);
    }

    public LiveData<Resource<GenericResponse<Job>>> saveJob() {
        return job;
    }

    public void saveApi(Job objJob) {
        if (!isFetching) {
            executeSave(objJob);
        }
    }

    private void executeSave(Job objJob) {
        isFetching = true;

        final LiveData<Resource<GenericResponse<Job>>> repositorySource = repository.saveJobApi(objJob);

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
}
