package lk.nibm.swiftsalon.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Appointment;

import static lk.nibm.swiftsalon.util.Constants.NEW_APPOINTMENT;

public class AppointmentAdapter extends ListAdapter<Appointment, RecyclerView.ViewHolder> {

    private Appointment appointment;
    private OnAppointmentListener onAppointmentListener;
    private RequestManager requestManager;
    private String type;
    private View view;

    public AppointmentAdapter(OnAppointmentListener onAppointmentListener, RequestManager requestManager, String type) {
        super(DIFF_CALLBACK);
        this.onAppointmentListener = onAppointmentListener;
        this.requestManager = requestManager;
        this.type = type;
    }

    private static final DiffUtil.ItemCallback<Appointment> DIFF_CALLBACK = new DiffUtil.ItemCallback<Appointment>() {
        @Override
        public boolean areItemsTheSame(@NonNull Appointment oldItem, @NonNull Appointment newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Appointment oldItem, @NonNull Appointment newItem) {
            return oldItem.getStatus().equals(newItem.getStatus());
        }
    };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (type.equals(NEW_APPOINTMENT)) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_new_appointment, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_appointment, parent, false);
        }
        return new AppointmentViewHolder(view, onAppointmentListener, requestManager, type);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        appointment = getItem(position);
        ((AppointmentViewHolder) holder).onBind(appointment);
    }

    public Appointment getSelectedAppointment(int position) {
        appointment = getItem(position);
        if (appointment != null) {
            return appointment;
        }
        return null;
    }
}
