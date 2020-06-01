package lk.nibm.swiftsalon.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.service.modal.Appointment;

public class NewAppointmentAdapter extends ListAdapter<Appointment, NewAppointmentAdapter.NewAppointmentVH> {

    private Appointment appointment;

    public NewAppointmentAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Appointment> DIFF_CALLBACK = new DiffUtil.ItemCallback<Appointment>() {
        @Override
        public boolean areItemsTheSame(@NonNull Appointment oldItem, @NonNull Appointment newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Appointment oldItem, @NonNull Appointment newItem) {
            return oldItem.getStatus().equals(newItem.getStatus()) &&
                    oldItem.getId() == newItem.getId();
        }
    };

    @NonNull
    @Override
    public NewAppointmentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_new_appointment, parent, false);
        return new NewAppointmentVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewAppointmentVH holder, int position) {
        appointment = getItem(position);

        String appointmentData = appointment.getCustomerFirstName() + " (" + appointment.getId() + ")";
        String dateTime = appointment.getDate() + " " + appointment.getTime();
        String image = appointment.getCustomerImage();

        //for recycler view
        holder.txtAppointmentData.setText( appointmentData );
        holder.txtDateTime.setText( dateTime );
    }

    //New appointment view holder
    static  class NewAppointmentVH extends RecyclerView.ViewHolder {

        LinearLayout appListLayout;
        TextView txtAppointmentData, txtDateTime;
        ImageView imgCustomer;
        Button btnAccept;

        NewAppointmentVH(@NonNull View view) {
            super(view);

            appListLayout = view.findViewById(R.id.layout_app_list);
            txtAppointmentData = view.findViewById(R.id.txt_app_data);
            txtDateTime = view.findViewById(R.id.txt_date_time);
            imgCustomer = view.findViewById(R.id.img_customer);
            btnAccept = view.findViewById(R.id.btn_accept);
        }
    }
}
