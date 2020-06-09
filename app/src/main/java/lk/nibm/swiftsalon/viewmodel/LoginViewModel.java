package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import lk.nibm.swiftsalon.repository.SalonRepository;

public class LoginViewModel extends AndroidViewModel {

    private SalonRepository repository;
    private MediatorLiveData<Boolean> isLoggedIn = new MediatorLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = SalonRepository.getInstance(application);
    }

    public LiveData<Boolean> isLoggedIn(String email, String password) {
        final LiveData<Boolean> repositorySource = repository.loginApi(email, password);

        isLoggedIn.addSource(repositorySource, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null) {
                    isLoggedIn.setValue(aBoolean);
                }
                else {
                    isLoggedIn.setValue(false);
                }
            }
        });

        return isLoggedIn;
    }
}
