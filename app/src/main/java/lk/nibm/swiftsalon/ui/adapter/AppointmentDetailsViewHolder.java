package lk.nibm.swiftsalon.ui.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.AppointmentDetail;

public class AppointmentDetailsViewHolder extends RecyclerView.ViewHolder {

    private TextView txtJob, txtPrice;

    public AppointmentDetailsViewHolder(@NonNull View itemView) {
        super(itemView);

        txtJob = itemView.findViewById(R.id.txt_job_name);
        txtPrice = itemView.findViewById(R.id.txt_price);
    }

    public void onBind(AppointmentDetail appointmentDetail) {
        String job = appointmentDetail.getJobName() + " (" + appointmentDetail.getJobId() + ")";
        String price = String.valueOf(appointmentDetail.getPrice());

        txtJob.setText(job);
        txtPrice.setText(price);
    }

}
