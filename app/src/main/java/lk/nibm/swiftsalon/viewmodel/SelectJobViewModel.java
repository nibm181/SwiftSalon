package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.model.StylistJob;
import lk.nibm.swiftsalon.repository.JobRepository;
import lk.nibm.swiftsalon.repository.StylistJobRepository;
import lk.nibm.swiftsalon.repository.StylistRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;

public class SelectJobViewModel extends AndroidViewModel {
    private static final String TAG = "SelectJobViewModel";

    private MediatorLiveData<Resource<List<Job>>> jobs = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericResponse<List<StylistJob>>>> stylistJobs = new MediatorLiveData<>();
    private JobRepository jobRepository;
    private StylistJobRepository stylistJobRepository;
    private Session session;

    private boolean isFetching;
    private boolean isUpdating;

    public SelectJobViewModel(@NonNull Application application) {
        super(application);
        jobRepository = JobRepository.getInstance(application);
        stylistJobRepository = StylistJobRepository.getInstance(application);
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

    public LiveData<Resource<GenericResponse<List<StylistJob>>>> updateStylist() {
        return stylistJobs;
    }


    public void updateApi(int stylistId, List<Job> objJobs) {
        if (!isUpdating) {
            List<StylistJob> objStylistJobs = new ArrayList<>();
            for(Job job: objJobs) {
                StylistJob stylistJob = new StylistJob();
                stylistJob.setStylistId(stylistId);
                stylistJob.setJobId(job.getId());

                objStylistJobs.add(stylistJob);
            }

            executeUpdate(objStylistJobs);
        }
    }

    private void executeFetch() {
        isFetching = true;

        final LiveData<Resource<List<Job>>> repositorySource = jobRepository.getJobsApi(session.getSalonId());

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

    private void executeUpdate(List<StylistJob> objStylistJobs) {
        isUpdating = true;

        final LiveData<Resource<GenericResponse<List<StylistJob>>>> repositorySource = stylistJobRepository.updateStylistJobsApi(objStylistJobs);

        stylistJobs.addSource(repositorySource, new Observer<Resource<GenericResponse<List<StylistJob>>>>() {
            @Override
            public void onChanged(Resource<GenericResponse<List<StylistJob>>> listResource) {
                if (listResource != null) {
                    stylistJobs.setValue(listResource);
                    if (listResource.status == Resource.Status.SUCCESS) {
                        isUpdating = false;
                    } else if (listResource.status == Resource.Status.ERROR) {
                        isUpdating = false;
                        stylistJobs.removeSource(repositorySource);
                    }
                } else {
                    stylistJobs.removeSource(repositorySource);
                }
            }
        });
    }
}
