package lk.nibm.swiftsalon.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Appointment;

import static lk.nibm.swiftsalon.util.Constants.NEW_APPOINTMENT;
import static lk.nibm.swiftsalon.util.Constants.NORMAL_APPOINTMENT;

public class AppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView txtAppointmentData, txtDateTime, txtStatus;
    private ImageView imgCustomer;
    private RelativeLayout btnAccept;

    private OnAppointmentListener onAppointmentListener;
    private RequestManager requestManager;
    private String type;

    public AppointmentViewHolder(@NonNull View itemView, OnAppointmentListener onAppointmentListener, RequestManager requestManager, String type) {
        super(itemView);

        this.onAppointmentListener = onAppointmentListener;
        this.requestManager = requestManager;
        this.type = type;

        txtAppointmentData = itemView.findViewById(R.id.txt_app_data);
        txtDateTime = itemView.findViewById(R.id.txt_date_time);
        imgCustomer = itemView.findViewById(R.id.img_customer);

        itemView.setOnClickListener(this);

        if (type.equals(NEW_APPOINTMENT)) {
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAppointmentListener.onAppointmentAccept(getAdapterPosition());
                }
            });
        }

        if (type.equals(NORMAL_APPOINTMENT)) {
            txtStatus = itemView.findViewById(R.id.txt_status);
            txtStatus.setVisibility(View.VISIBLE);
        }
    }

    public void onBind(Appointment appointment) {

        String appointmentData = appointment.getCustomerFirstName() + " (" + appointment.getId() + ")";
        String dateTime = appointment.getDate() + " " + appointment.getTime();
        String status = appointment.getStatus();

        txtAppointmentData.setText(appointmentData);
        txtDateTime.setText(dateTime);

        if (type.equals(NORMAL_APPOINTMENT)) {
            txtStatus.setText(status);
        }

        requestManager
                .load(appointment.getCustomerImage())
                .apply(RequestOptions.circleCropTransform())
                .into(imgCustomer);
    }

    @Override
    public void onClick(View v) {
        onAppointmentListener.onAppointmentClick(getAdapterPosition(), type);
    }
}
