package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.repository.SalonRepository;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;

public class MainViewModel extends AndroidViewModel {


    private SalonRepository salonRepository;
    private Session session;
    private MediatorLiveData<Salon> salon = new MediatorLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        session = new Session(application);
        salonRepository = SalonRepository.getInstance(application);
    }

    public LiveData<Salon> getSalon() {
        return salon;
    }

    public void getSalonApi() {
        final LiveData<Resource<Salon>> repositorySource = salonRepository.getSalonApi(session.getSalonId());
    }
}
