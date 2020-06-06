package lk.nibm.swiftsalon.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Appointment;

import static lk.nibm.swiftsalon.util.Constants.NEW_APPOINTMENT;

public class AppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView appointmentData, dateTime;
    private ImageView image;
    private RelativeLayout btnAccept;
    private OnAppointmentListener onAppointmentListener;
    private RequestManager requestManager;
    private String type;

    public AppointmentViewHolder(@NonNull View itemView, OnAppointmentListener onAppointmentListener, RequestManager requestManager, String type) {
        super(itemView);

        this.onAppointmentListener = onAppointmentListener;
        this.requestManager = requestManager;
        this.type = type;

        appointmentData = itemView.findViewById(R.id.txt_app_data);
        dateTime = itemView.findViewById(R.id.txt_date_time);
        image = itemView.findViewById(R.id.img_customer);

        itemView.setOnClickListener(this);

        if(type.equals(NEW_APPOINTMENT)) {
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onBind(Appointment appointment) {
        appointmentData.setText(appointment.getCustomerFirstName());
        dateTime.setText(appointment.getDate());

        requestManager
                .load(appointment.getCustomerImage())
                .apply(RequestOptions.circleCropTransform())
                .into(image);
    }

    @Override
    public void onClick(View v) {
        onAppointmentListener.onAppointmentClick(getAdapterPosition(), type);
    }
}
