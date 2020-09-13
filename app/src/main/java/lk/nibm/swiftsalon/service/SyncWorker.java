package lk.nibm.swiftsalon.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.persistence.SwiftSalonDao;
import lk.nibm.swiftsalon.persistence.SwiftSalonDatabase;
import lk.nibm.swiftsalon.request.ServiceGenerator;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Session;
import retrofit2.Call;
import retrofit2.Response;

public class
SyncWorker extends Worker {

    private static final String TAG = "SyncWorker";
    private SwiftSalonDao swiftSalonDao;
    private Context context;
    private Session session;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        swiftSalonDao = SwiftSalonDatabase.getInstance(context).getDao();
    }

    @NonNull
    @Override
    public Result doWork() {
        return syncAppointments();
    }

    private Result syncAppointments() {

        try {

            int appointmentId = getInputData().getInt("appointment_id", 0);
            String status = getInputData().getString("status");

            Call<GenericResponse<Appointment>> call = ServiceGenerator.getAppointmentApi().getWorkerAppointment(appointmentId);
            Response<GenericResponse<Appointment>> response = call.execute();

            if (response.isSuccessful()) {
                Log.d(TAG, "syncAppointments: RESPONSE: " + response.body().getMessage());
                if (response.body().getStatus() == 1) {

                    if (response.body().getContent() != null) {

                        session = new Session(context);
                        Appointment appointment = response.body().getContent();

                        swiftSalonDao.insertAppointment(appointment);

                        if(appointment.getSalonId() == session.getSalonId()) {
                            if(Objects.equals(status, "not accepted")) {
                                NotificationHelper.showCancelNotification(context, appointment);
                            }
                            else {
                                NotificationHelper.showNotification(context, appointment);
                            }
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
