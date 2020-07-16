package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.repository.SalonRepository;
import lk.nibm.swiftsalon.service.RefreshTokenWorker;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;

public class ProfileViewModel extends AndroidViewModel {

    private SalonRepository repository;
    private MediatorLiveData<Resource<Salon>> salon = new MediatorLiveData<>();

    private WorkManager workManager;
    private Session session;
    private boolean isFetching;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = SalonRepository.getInstance(application);
        session = new Session(application);
        workManager = WorkManager.getInstance(application);
    }

    public LiveData<Resource<Salon>> getSalon() {
        return salon;
    }

    public void getSalonApi() {
        if(!isFetching) {
            executeSalonApi();
        }
    }

    public void executeSalonApi() {
        isFetching = true;

        final LiveData<Resource<Salon>> repositorySource = repository.getSalonApi(session.getSalonId());

        salon.addSource(repositorySource, new Observer<Resource<Salon>>() {
            @Override
            public void onChanged(Resource<Salon> salonResource) {
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
            }
        });

    }

    public void clearToken() {
        int id = session.getSalonId();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data.Builder data = new Data.Builder();
        data.putString("token", "0");
        data.putInt("id", id);

        WorkRequest request = new OneTimeWorkRequest.Builder(RefreshTokenWorker.class)
                .setInputData(data.build())
                .setConstraints(constraints)
                .build();

        workManager.enqueue(request);
    }
}
