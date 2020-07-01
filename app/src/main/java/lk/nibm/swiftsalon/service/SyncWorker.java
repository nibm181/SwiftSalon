package lk.nibm.swiftsalon.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.util.List;

import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.persistence.SwiftSalonDao;
import lk.nibm.swiftsalon.persistence.SwiftSalonDatabase;
import lk.nibm.swiftsalon.request.ServiceGenerator;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Session;
import retrofit2.Call;
import retrofit2.Response;

public class SyncWorker extends Worker {

    private static final String TAG = "SyncWorker";
    private SwiftSalonDao swiftSalonDao;
    private Session session;
    private int salonId;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        swiftSalonDao = SwiftSalonDatabase.getInstance(context).getDao();

        session = new Session(context);
        salonId = session.getSalonId();
    }

    @NonNull
    @Override
    public Result doWork() {
        return syncAppointments();
    }

    private Result syncAppointments() {

        try {
            Call<GenericResponse<List<Appointment>>> call = ServiceGenerator.getAppointmentApi().getWorkerNewAppointments(salonId);
            Response<GenericResponse<List<Appointment>>> response = call.execute();

            if (response.isSuccessful()) {
                if (response.body().getStatus() == 1) {

                    if (response.body().getContent() != null) {

                        Appointment[] appointments = new Appointment[response.body().getContent().size()];

                        int index = 0;
                        for (long rowId : swiftSalonDao.insertAppointments((Appointment[]) (response.body().getContent().toArray(appointments)))) {

                            if (rowId == -1) {
                                swiftSalonDao.updateAppointmentStatus(
                                        appointments[index].getId(),
                                        appointments[index].getStatus(),
                                        appointments[index].getModifiedOn());
                            }
                            index++;
                        }

                        return Result.success();
                    }

                }
            }
            return Result.failure();

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }

}
