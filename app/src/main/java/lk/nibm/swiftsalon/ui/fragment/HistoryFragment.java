package lk.nibm.swiftsalon.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
import lk.nibm.swiftsalon.viewmodel.HistoryViewModel;

import static lk.nibm.swiftsalon.util.Constants.NORMAL_APPOINTMENT;

public class HistoryFragment extends Fragment implements OnAppointmentListener {

    private View view;
    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private ShimmerFrameLayout shimmer;
    private RelativeLayout layoutEmpty;

    private CustomDialog dialog;
    private HistoryViewModel viewModal;

    private static final String TAG = "HistoryFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.rv_previous);
        shimmer = view.findViewById(R.id.layout_shimmer);
        layoutEmpty = view.findViewById(R.id.layout_empty);

        dialog = new CustomDialog(getContext());
        viewModal = new ViewModelProvider(this).get(HistoryViewModel.class);

        initRecyclerView();
        subscribeObservers();
        allAppointmentsApi();
        return view;
    }

    private void subscribeObservers() {
        viewModal.getAppointments().observe(getViewLifecycleOwner(), new Observer<Resource<List<Appointment>>>() {
            @Override
            public void onChanged(Resource<List<Appointment>> listResource) {
                if(listResource != null) {
                    Log.d(TAG, "onChanged: status: " + listResource.status);
                    if(listResource.data != null) {
                        Log.d(TAG, "onChanged: data: " + listResource.data);
                        switch (listResource.status) {

                            case LOADING: {
                                showRecyclerView(false);
                                break;
                            }

                            case ERROR: {
                                showRecyclerView(true);
                                Log.d(TAG, "onChanged: error: " + listResource.message);
                                dialog.showToast(listResource.message);
                                adapter.submitList(listResource.data);
                                break;
                            }

                            case SUCCESS: {
                                if(listResource.data.size() > 0) {
                                    showRecyclerView(true);
                                    adapter.submitList(listResource.data);
                                }
                                else {
                                    showEmptyAppointmentLayout(true);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    private void showRecyclerView(boolean show) {
        if(show) {
            shimmer.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else {
            shimmer.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void showEmptyAppointmentLayout(boolean show) {
        if(show) {
            layoutEmpty.setVisibility(View.VISIBLE);
            shimmer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.sample_avatar)
                .error(R.drawable.sample_avatar);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    private void allAppointmentsApi() {
        viewModal.allAppointmentsApi();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AppointmentAdapter(this, initGlide(), NORMAL_APPOINTMENT);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAppointmentClick(int position, String type) {
        Intent intent = new Intent(getContext(), AppointmentActivity.class);
        intent.putExtra("appointment", adapter.getSelectedAppointment(position));

        startActivity(intent);
    }
}
