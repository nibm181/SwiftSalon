package lk.nibm.swiftsalon.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.ui.activity.AppointmentActivity;
import lk.nibm.swiftsalon.ui.activity.MainActivity;

import static lk.nibm.swiftsalon.util.Constants.STATUS_CANCELED;
import static lk.nibm.swiftsalon.util.Constants.STATUS_COMPLETED;
import static lk.nibm.swiftsalon.util.Constants.STATUS_PENDING;

public class NotificationHelper {

    public static void showNotification(Context context, Appointment appointment) {

        String title = "";
        String body = "";

        if(appointment.getStatus().equals(STATUS_PENDING)) {
            title = "New appointment";
            body = "You got a new appointment from " + appointment.getCustomerLastName() + " on " + appointment.getDate();
        }
        else if(appointment.getStatus().equals(STATUS_CANCELED)) {
            title = "Appointment Canceled";
            body = "An appointment from " + appointment.getCustomerLastName() + " has been canceled by himself";
        }
        else if(appointment.getStatus().equals(STATUS_COMPLETED)) {
            title = "Appointment completed";
            body = "You have successfully completed an appointment from " + appointment.getCustomerLastName();
        }

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(context, AppointmentActivity.class);
        resultIntent.putExtra("appointment", appointment);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        builder.setContentIntent(resultPendingIntent);
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        builder.setAutoCancel(true);
        manager.notify(1, builder.build());
    }

    public static void showNotification(Context context, String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle());

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(1, builder.build());
    }

    public static void showCancelNotification(Context context, Appointment appointment) {

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(context, AppointmentActivity.class);
        resultIntent.putExtra("appointment", appointment);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Missed an appointment")
                .setContentText("An appointment that has been not accepted, canceled automatically.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle());

        builder.setContentIntent(resultPendingIntent);
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        builder.setAutoCancel(true);
        manager.notify(1, builder.build());
    }
}
