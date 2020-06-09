package lk.nibm.swiftsalon.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import java.util.List;
import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.ui.activity.AppointmentActivity;
import lk.nibm.swiftsalon.ui.adapter.AppointmentAdapter;
import lk.nibm.swiftsalon.ui.adapter.OnAppointmentListener;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.viewmodel.HomeViewModel;

import static lk.nibm.swiftsalon.util.Constants.NEW_APPOINTMENT;
import static lk.nibm.swiftsalon.util.Constants.NORMAL_APPOINTMENT;
import static lk.nibm.swiftsalon.util.Constants.SCHEDULED_APPOINTMENT;

public class HomeFragment extends Fragment implements OnAppointmentListener {

    private static final String TAG = "HomeFragment";

    private RecyclerView rvNewApp, rvOngoingApp;
    private TextView txtNewApp, txtOngoingApp, txtSalonName;
    private LinearLayout layoutNewApp, layoutOngoingApp;
    private RelativeLayout layoutEmpty;
    private ShimmerFrameLayout shimmerNewApp, shimmerOngoingApp;

    private CustomDialog dialog;
    private AppointmentAdapter newAppointmentAdapter, ongoingAppointmentAdapter;

    private HomeViewModel viewModal;

    private boolean isNew, isOn;

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
        layoutEmpty = view.findViewById(R.id.layout_empty);

        dialog = new CustomDialog(getContext());
        viewModal = new ViewModelProvider(this).get(HomeViewModel.class);

        initRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        subscribeObservers();
        newAppointmentApi();
        ongoingAppointmentApi();
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.sample_avatar)
                .error(R.drawable.sample_avatar);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    private void initRecyclerView() {
        rvNewApp.setLayoutManager(new LinearLayoutManager(getContext()));
        newAppointmentAdapter = new AppointmentAdapter(this, initGlide(), NEW_APPOINTMENT);
        rvNewApp.setAdapter(newAppointmentAdapter);

        rvOngoingApp.setLayoutManager(new LinearLayoutManager(getContext()));
        ongoingAppointmentAdapter = new AppointmentAdapter(this, initGlide(), SCHEDULED_APPOINTMENT);
        rvOngoingApp.setAdapter(ongoingAppointmentAdapter);
    }

    private void showNewRecyclerView(boolean show) {
        if(show) {
            rvNewApp.setVisibility(View.VISIBLE);
            shimmerNewApp.setVisibility(View.GONE);
        }
        else {
            rvNewApp.setVisibility(View.GONE);
            shimmerNewApp.setVisibility(View.VISIBLE);
        }
    }

    private void showOngoingRecyclerView(boolean show) {
        if(show) {
            rvOngoingApp.setVisibility(View.VISIBLE);
            shimmerOngoingApp.setVisibility(View.GONE);
        }
        else {
            rvOngoingApp.setVisibility(View.GONE);
            shimmerOngoingApp.setVisibility(View.VISIBLE);
        }
    }

    private void emptyNewRecyclerView() {
        layoutNewApp.setVisibility(View.GONE);
    }

    private void showRecyclerView(boolean newApp, boolean onApp) {
        if(!newApp && !onApp) {
            layoutEmpty.setVisibility(View.VISIBLE);
            layoutNewApp.setVisibility(View.GONE);
            layoutOngoingApp.setVisibility(View.GONE);
        }
        else if(!newApp) {
            layoutEmpty.setVisibility(View.GONE);
            layoutNewApp.setVisibility(View.GONE);
            layoutOngoingApp.setVisibility(View.VISIBLE);
        }
        else if(!onApp) {
            layoutEmpty.setVisibility(View.GONE);
            layoutNewApp.setVisibility(View.VISIBLE);
            layoutOngoingApp.setVisibility(View.GONE);
        }
        else {
            layoutEmpty.setVisibility(View.GONE);
            layoutNewApp.setVisibility(View.VISIBLE);
            layoutOngoingApp.setVisibility(View.VISIBLE);
        }
    }

    private void subscribeObservers() {
        viewModal.getNewAppointments().observe(getViewLifecycleOwner(), new Observer<Resource<List<Appointment>>>() {
            @Override
            public void onChanged(Resource<List<Appointment>> listResource) {
                if(listResource != null) {

                    if(listResource.data != null) {

                        switch (listResource.status) {

                            case LOADING: {
                                showNewRecyclerView(false);
                                break;
                            }

                            case ERROR: {
                                showNewRecyclerView(true);
                                dialog.showToast(listResource.message);
                                newAppointmentAdapter.submitList(listResource.data);
                                break;
                            }

                            case SUCCESS: {
                                if(listResource.data.size() > 0) {
                                    showNewRecyclerView(true);
                                }
                                newAppointmentAdapter.submitList(listResource.data);
                                break;
                            }
                        }
                    }
                }
            }
        });

        viewModal.getOngoingAppointments().observe(getViewLifecycleOwner(), new Observer<Resource<List<Appointment>>>() {
            @Override
            public void onChanged(Resource<List<Appointment>> listResource) {
                if(listResource != null) {

                    if(listResource.data != null) {

                        switch (listResource.status) {

                            case LOADING: {
                                showOngoingRecyclerView(false);
                                break;
                            }

                            case ERROR: {
                                showOngoingRecyclerView(true);;
                                dialog.showToast(listResource.message);
                                ongoingAppointmentAdapter.submitList(listResource.data);
                                break;
                            }

                            case SUCCESS: {
                                if(listResource.data.size() > 0) {
                                    showOngoingRecyclerView(true);
                                    ongoingAppointmentAdapter.submitList(listResource.data);
                                }
                                else {

                                }
                                break;
                            }
                        }
                    }
                }
            }
        });

        viewModal.getCountNewAppointments().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {

                if(count <= 0) {
                    txtNewApp.setText("-");
                    isNew = false;
                }
                else {
                    txtNewApp.setText("" + count);
                    isNew = true;
                }

                showRecyclerView(isNew, isOn);
            }
        });

        viewModal.getCountOngoingAppointments().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {

                if(count <= 0) {
                    txtOngoingApp.setText("-");
                    isOn = false;
                }
                else {
                    txtOngoingApp.setText("" + count);
                    isOn = true;
                }

                showRecyclerView(isNew, isOn);
            }
        });
    }

    private void newAppointmentApi() {
        viewModal.newAppointmentApi();
    }

    private void ongoingAppointmentApi() {
        viewModal.ongoingAppointmentApi();
    }

    @Override
    public void onAppointmentClick(int position, String type) {
        Intent intent = new Intent(getContext(), AppointmentActivity.class);
        if(type.equals(NEW_APPOINTMENT)) {
            intent.putExtra("appointment", newAppointmentAdapter.getSelectedAppointment(position));
            startActivity(intent);
        }
        else if(type.equals(SCHEDULED_APPOINTMENT)) {
            intent.putExtra("appointment", ongoingAppointmentAdapter.getSelectedAppointment(position));
            startActivity(intent);
        }
    }

    @Override
    public void onAppointmentAccept(int position) {
        dialog.showToast("Clicked: " + newAppointmentAdapter.getSelectedAppointment(position).getCustomerFirstName());
        viewModal.acceptAppointment(newAppointmentAdapter.getSelectedAppointment(position));
    }
}
