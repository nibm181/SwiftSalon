package lk.nibm.swiftsalon.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.model.AppointmentDetail;
import lk.nibm.swiftsalon.ui.adapter.AppointmentDetailsAdapter;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.viewmodel.AppointmentViewModel;

public class AppointmentActivity extends AppCompatActivity {

    private static final String TAG = "AppointmentActivity";

    private TextView txtTitle, txtDateTime, txtStatus, txtCustomer, txtStylist, txtEmpty;
    private ImageView imgCustomer;
    private RecyclerView rvAppointmentDetails;
    private ImageButton btnBack;
    private RelativeLayout btnAccept, btnCancel;
    private TextView txtAccept, txtCancel;
    private ProgressBar prgAccept, prgCancel;
    private LinearLayout layoutButtons;
    private ShimmerFrameLayout shimmerJobs;

    private Appointment appointment;
    private AppointmentDetailsAdapter adapter;

    private CustomDialog dialog;
    private AppointmentViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        txtTitle = findViewById(R.id.txt_title);
        txtDateTime = findViewById(R.id.txt_date_time);
        txtStatus = findViewById(R.id.txt_status);
        txtCustomer = findViewById(R.id.txt_customer);
        txtStylist = findViewById(R.id.txt_stylist);
        txtEmpty = findViewById(R.id.txt_empty);
        imgCustomer = findViewById(R.id.img_customer);
        rvAppointmentDetails = findViewById(R.id.rv_appointment_details);
        btnBack = findViewById(R.id.btn_back);
        btnAccept = findViewById(R.id.btn_accept);
        btnCancel = findViewById(R.id.btn_cancel);
        txtAccept = findViewById(R.id.btn_accept_text);
        txtCancel = findViewById(R.id.btn_cancel_text);
        prgAccept = findViewById(R.id.btn_accept_progress);
        prgCancel = findViewById(R.id.btn_cancel_progress);
        layoutButtons = findViewById(R.id.layout_buttons);
        shimmerJobs = findViewById(R.id.shimmer_jobs);

        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
        dialog = CustomDialog.getInstance(getApplicationContext());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAcceptProgress(true);
                btnCancel.setVisibility(View.GONE);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelProgress(true);
                btnAccept.setVisibility(View.GONE);
            }
        });

        getIncomingIntent();
        initRecyclerView();
        subscribeObservers();
        appointmentDetailsApi();
    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("appointment")) {

            appointment = getIntent().getParcelableExtra("appointment");

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.sample_avatar)
                    .error(R.drawable.sample_avatar);

            if(appointment != null) {

                String title = appointment.getCustomerFirstName() + " (" + appointment.getId() + ")";
                String dateTime = appointment.getDate() + " " + appointment.getTime();
                String status = appointment.getStatus();
                String customer = appointment.getCustomerFirstName() + " " + appointment.getCustomerLastName();
                String stylist = " Elakiri " + appointment.getStylistId();

                if(status.equals("pending")) {
                    layoutButtons.setVisibility(View.VISIBLE);
                }
                else {
                    layoutButtons.setVisibility(View.GONE);
                }

                txtTitle.setText(title);
                txtDateTime.setText(dateTime);
                txtStatus.setText(status);
                txtCustomer.setText(customer);
                txtStylist.setText(stylist);

                Glide.with(this)
                        .setDefaultRequestOptions(options)
                        .load(appointment.getCustomerImage())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imgCustomer);
            }
        }
    }

    private void initRecyclerView() {
        adapter = new AppointmentDetailsAdapter();
        rvAppointmentDetails.setLayoutManager(new LinearLayoutManager(this));
        rvAppointmentDetails.setAdapter(adapter);
    }

    private void subscribeObservers() {
        viewModel.getAppointmentDetails().observe(this, new Observer<Resource<List<AppointmentDetail>>>() {
            @Override
            public void onChanged(Resource<List<AppointmentDetail>> listResource) {

                if(listResource != null) {
                    if(listResource.data != null) {

                        switch (listResource.status) {

                            case LOADING: {
                                Log.d(TAG, "onChanged: LOADING");
                                showRecyclerView(false);
                                break;
                            }

                            case ERROR: {
                                Log.d(TAG, "onChanged: ERROR");
                                Log.d(TAG, "onChanged: ERROR MSG: " + listResource.message);

                                showRecyclerView(true);
                                adapter.submitList(listResource.data);
                                dialog.showToast(listResource.message);

                                if(listResource.data.isEmpty()) {
                                    showEmpty();
                                }
                                break;
                            }

                            case SUCCESS: {
                                Log.d(TAG, "onChanged: SUCCESS");

                                if(listResource.data.isEmpty()) {
                                    txtEmpty.setText("Oops! Something went wrong. Please try again later.");
                                    showEmpty();
                                }
                                else {
                                    showRecyclerView(true);
                                    adapter.submitList(listResource.data);
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
            rvAppointmentDetails.setVisibility(View.VISIBLE);
            shimmerJobs.setVisibility(View.GONE);
        }
        else {
            rvAppointmentDetails.setVisibility(View.GONE);
            shimmerJobs.setVisibility(View.VISIBLE);
        }
        txtEmpty.setVisibility(View.GONE);
    }

    private void showAcceptProgress(boolean show) {
        if(show) {
            txtAccept.setVisibility(View.GONE);
            prgAccept.setVisibility(View.VISIBLE);
        }
        else {
            txtAccept.setVisibility(View.VISIBLE);
            prgAccept.setVisibility(View.GONE);
        }
    }

    private void showCancelProgress(boolean show) {
        if(show) {
            txtCancel.setVisibility(View.GONE);
            prgCancel.setVisibility(View.VISIBLE);
        }
        else {
            txtCancel.setVisibility(View.VISIBLE);
            prgCancel.setVisibility(View.GONE);
        }
    }

    private void showEmpty() {
        txtEmpty.setVisibility(View.VISIBLE);
        rvAppointmentDetails.setVisibility(View.GONE);
        shimmerJobs.setVisibility(View.GONE);
    }

    private void appointmentDetailsApi() {
        viewModel.appointmentDetailsApi(appointment.getId());
    }
}