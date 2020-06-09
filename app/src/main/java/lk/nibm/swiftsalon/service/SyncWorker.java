package lk.nibm.swiftsalon.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.ui.activity.MainActivity;

public class SyncWorker extends Worker {

    private static final String TAG = "SyncWorker";
    Context context;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Result doWork() {
        syncAppointments();
        Log.d(TAG, "doWork: running.");
        return Result.success();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void syncAppointments() {

        String channelId = "my_channel_1";

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(channelId, "my_channel", NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        manager.createNotificationChannel(channel);

        Notification.Builder notification = new Notification.Builder(context, channelId)
                .setContentTitle("Test Notification")
                .setContentText("Hello there, this is a test notification for yourself.")
                .setSmallIcon(R.drawable.ic_launcher_circular);

        manager.notify(0, notification.build());
    }

}
