package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.repository.JobRepository;
import lk.nibm.swiftsalon.repository.StylistRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;

public class StylistViewModel extends AndroidViewModel {
    private static final String TAG = "StylistViewModel";

    private StylistRepository repository;
    private JobRepository jobRepository;
    private MediatorLiveData<Resource<GenericResponse<Stylist>>> stylist = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<Job>>> jobs = new MediatorLiveData<>();
    private Session session;

    private boolean isFetching;
    private boolean isFetchingJobs;

    public StylistViewModel(@NonNull Application application) {
        super(application);
        session = new Session(application);
        repository = StylistRepository.getInstance(application);
        jobRepository = JobRepository.getInstance(application);
    }

    public LiveData<Resource<GenericResponse<Stylist>>> deleteStylist() {
        return stylist;
    }

    public LiveData<Resource<List<Job>>> getJobs() {
        return jobs;
    }

    public void deleteApi(Stylist objStylist) {
        if (!isFetching) {
            executeDelete(objStylist);
        }
    }

    public void getJobsApi(int id) {
        if(!isFetchingJobs) {
            executeFetchJobs(id);
        }
    }

    private void executeFetchJobs(int id) {
        isFetchingJobs = true;

        jobRepository.saveJobsApi(session.getSalonId());

        final LiveData<Resource<List<Job>>> repositorySource = repository.getJobsByStylistApi(id);

        jobs.addSource(repositorySource, new Observer<Resource<List<Job>>>() {
            @Override
            public void onChanged(Resource<List<Job>> listResource) {
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
            }
        });
    }

    private void executeDelete(Stylist objStylist) {
        isFetching = true;

        final LiveData<Resource<GenericResponse<Stylist>>> repositorySource = repository.deleteStylistApi(objStylist);

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
