package lk.nibm.swiftsalon.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.AppointmentDetail;

public class AppointmentDetailsAdapter extends ListAdapter<AppointmentDetail, RecyclerView.ViewHolder> {

    private View view;
    AppointmentDetail appointmentDetail;

    public AppointmentDetailsAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<AppointmentDetail> DIFF_CALLBACK = new DiffUtil.ItemCallback<AppointmentDetail>() {
        @Override
        public boolean areItemsTheSame(@NonNull AppointmentDetail oldItem, @NonNull AppointmentDetail newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull AppointmentDetail oldItem, @NonNull AppointmentDetail newItem) {
            return oldItem.getAppointmentId() == newItem.getAppointmentId()
                    && oldItem.getJobId() == newItem.getJobId()
                    && oldItem.getPrice() == newItem.getPrice();
        }
    };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_appointment_details, parent, false);
        return new AppointmentDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        appointmentDetail = getItem(position);
        ((AppointmentDetailsViewHolder) holder).onBind(appointmentDetail);
    }

    public AppointmentDetail getSelectedAppointmentDetails(int position) {
        appointmentDetail = getItem(position);
        if(appointmentDetail != null) {
            return appointmentDetail;
        }
        return null;
    }
}
