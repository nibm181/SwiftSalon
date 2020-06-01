package lk.nibm.swiftsalon.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.service.modal.Appointment;
import lk.nibm.swiftsalon.ui.adapter.NewAppointmentAdapter;
import lk.nibm.swiftsalon.viewmodal.HomeViewModal;

public class HomeFragment extends Fragment {

    private List<Appointment> newAppointments, ongoingAppointments;
    private RecyclerView rvNewApp, rvOngoingApp;
    private TextView txtNewApp, txtOngoingApp, txtSalonName;
    private LinearLayout layoutNewApp, layoutOngoingApp;
    private ShimmerFrameLayout shimmerNewApp, shimmerOngoingApp;
    private HomeViewModal viewModal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvNewApp = view.findViewById(R.id.rv_new_app);
        rvOngoingApp = view.findViewById(R.id.rv_ongoing_app);
        txtSalonName = view.findViewById(R.id.txt_salon_name);
        txtNewApp = view.findViewById(R.id.txt_new_app);
        txtOngoingApp = view.findViewById(R.id.txt_ongoing_app);
        shimmerNewApp = view.findViewById(R.id.shimmer_new_app);
        shimmerOngoingApp = view.findViewById(R.id.shimmer_ongoing_app);
        layoutNewApp = view.findViewById(R.id.layout_new_app);
        layoutOngoingApp = view.findViewById(R.id.layout_ongoing_app);

        rvNewApp.setLayoutManager(new LinearLayoutManager(getContext()));
        NewAppointmentAdapter newAppointmentAdapter = new NewAppointmentAdapter();
        rvNewApp.setAdapter(newAppointmentAdapter);

        viewModal = ViewModelProviders.of(getActivity()).get(HomeViewModal.class);
        viewModal.getAllAppointments().observe(getViewLifecycleOwner(), new Observer<List<Appointment>>() {
            @Override
            public void onChanged(List<Appointment> appointments) {
                Toast.makeText(getContext(), "onChange", Toast.LENGTH_SHORT).show();
                if(appointments.size() > 0) {
                    shimmerNewApp.setVisibility(View.GONE);
                }
                else {
                    layoutNewApp.setVisibility(View.GONE);
                }
                newAppointmentAdapter.submitList(appointments);
            }
        });

        return view;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
