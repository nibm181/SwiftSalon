package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lk.nibm.swiftsalon.model.Earnings;
import lk.nibm.swiftsalon.model.StylistEarning;
import lk.nibm.swiftsalon.repository.EarningsRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;

public class EarningsViewModel extends AndroidViewModel {

    private static final String TAG = "EarningsViewModel";

    private EarningsRepository repository;
    private MediatorLiveData<Resource<GenericResponse<Earnings>>> monthlyEarnings = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericResponse<Earnings>>> dailyEarnings = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericResponse<List<StylistEarning>>>> stylistEarnings = new MediatorLiveData<>();

    private Session session;
    private boolean isFetchingMonth, isFetchingDay, isFetchingStylist;
    private SimpleDateFormat dateFormat;

    public EarningsViewModel(@NonNull Application application) {
        super(application);
        repository = EarningsRepository.getInstance(application);
        session = new Session(application);
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    }

    public LiveData<Resource<GenericResponse<Earnings>>> getMonthlyEarning() {
        return monthlyEarnings;
    }

    public LiveData<Resource<GenericResponse<Earnings>>> getDailyEarning() {
        return dailyEarnings;
    }

    public LiveData<Resource<GenericResponse<List<StylistEarning>>>> getStylistEarning() {
        return stylistEarnings;
    }

    public void MonthlyEarningsApi(Date date) {
        if (!isFetchingMonth) {
            executeFetchMonthlyEarnings(dateFormat.format(date));
        }
    }

    public void DailyEarningsApi(Date date) {
        if (!isFetchingDay) {
            executeFetchDailyEarnings(dateFormat.format(date));
        }
    }

    public void StylistEarningsApi(Date date) {
        if (!isFetchingStylist) {
            executeFetchStylistEarnings(dateFormat.format(date));
        }
    }

    private void executeFetchMonthlyEarnings(String date) {
        isFetchingMonth = true;

        final LiveData<Resource<GenericResponse<Earnings>>> repositorySource = repository.getMonthlyEarningsApi(session.getSalonId(), date);

        monthlyEarnings.addSource(repositorySource, resource -> {
            if (resource != null) {
                monthlyEarnings.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS) {
                    isFetchingMonth = false;
                } else if (resource.status == Resource.Status.ERROR) {
                    isFetchingMonth = false;
                    monthlyEarnings.removeSource(repositorySource);
                }
            } else {
                monthlyEarnings.removeSource(repositorySource);
            }
        });
    }

    private void executeFetchDailyEarnings(String date) {
        isFetchingDay = true;

        final LiveData<Resource<GenericResponse<Earnings>>> repositorySource = repository.getDailyEarningsApi(session.getSalonId(), date);

        dailyEarnings.addSource(repositorySource, resource -> {
            if (resource != null) {
                dailyEarnings.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS) {
                    isFetchingDay = false;
                } else if (resource.status == Resource.Status.ERROR) {
                    isFetchingDay = false;
                    dailyEarnings.removeSource(repositorySource);
                }
            } else {
                dailyEarnings.removeSource(repositorySource);
            }
        });
    }

    private void executeFetchStylistEarnings(String date) {
        isFetchingStylist = true;

        final LiveData<Resource<GenericResponse<List<StylistEarning>>>> repositorySource = repository.getStylistEarningsApi(session.getSalonId(), date);

        stylistEarnings.addSource(repositorySource, resource -> {
            if (resource != null) {
                stylistEarnings.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS) {
                    isFetchingStylist = false;
                } else if (resource.status == Resource.Status.ERROR) {
                    isFetchingStylist = false;
                    stylistEarnings.removeSource(repositorySource);
                }
            } else {
                stylistEarnings.removeSource(repositorySource);
            }
        });
    }
}
