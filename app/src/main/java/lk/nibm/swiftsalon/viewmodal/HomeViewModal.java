package lk.nibm.swiftsalon.viewmodal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import lk.nibm.swiftsalon.service.modal.Appointment;
import lk.nibm.swiftsalon.service.repository.AppointmentRepo;

public class HomeViewModal extends AndroidViewModel {

    private AppointmentRepo repository;
    private LiveData<List<Appointment>> allAppointments;

    public HomeViewModal(@NonNull Application application) {
        super(application);
        repository = new AppointmentRepo(application);
        allAppointments = repository.getAllAppointments();
    }

    public LiveData<List<Appointment>> getAllAppointments() {
        return allAppointments;
    }
}
