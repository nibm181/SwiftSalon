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
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.service.RefreshTokenWorker;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;

public class LoginViewModel extends AndroidViewModel {

    private static final String TAG = "LoginViewModel";
    private SalonRepository repository;
    private MediatorLiveData<Resource<GenericResponse<Salon>>> salon = new MediatorLiveData<>();

    private Session session;
    private WorkManager workManager;
    private boolean isFetching;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = SalonRepository.getInstance(application);
        session = new Session(application);
        workManager = WorkManager.getInstance(application);
    }

    public LiveData<Resource<GenericResponse<Salon>>> getSalon() {
        return salon;
    }

    public void loginApi(String email, String password) {
        if(!isFetching) {
            executeLogin(email, password);
        }
    }

    private void executeLogin(String email, String password) {
        isFetching = true;

        final LiveData<Resource<GenericResponse<Salon>>> repositorySource = repository.getLoginApi(email, password);

        salon.addSource(repositorySource, resource -> {
            if(resource != null) {
                salon.setValue(resource);
                if(resource.status == Resource.Status.SUCCESS ) {
                    isFetching = false;
                }
                else if(resource.status == Resource.Status.ERROR){
                    isFetching = false;
                    salon.removeSource(repositorySource);
                }
            }
            else {
                salon.removeSource(repositorySource);
            }
        });
    }

    public void updateToken() {

        // TODO: Implement this method to send token to your app server.
        int id = session.getSalonId();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        FirebaseInstanceId.getInstance().getInstanceId().
                addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d(TAG, "onComplete: TOKEN: " + token);

                        Data.Builder data = new Data.Builder();
                        data.putString("token", token);
                        data.putInt("id", id);

                        Log.d(TAG, "onComplete: 101");
                        WorkRequest request = new OneTimeWorkRequest.Builder(RefreshTokenWorker.class)
                                .setInputData(data.build())
                                .setConstraints(constraints)
                                .build();

                        workManager.enqueue(request);
                    }
                });

    }
}
