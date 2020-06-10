package lk.nibm.swiftsalon.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.model.AppointmentDetail;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.viewmodel.AppointmentViewModel;

public class AppointmentActivity extends AppCompatActivity {

    private static final String TAG = "AppointmentActivity";

    private TextView txtTitle, txtDateTime, txtStatus, txtCustomer, txtEmpty;
    private ImageView imgCustomer;
    private RecyclerView recyclerViewJobs;
    private ImageButton btnBack;
    private LinearLayout layoutButtons;
    private ShimmerFrameLayout shimmerJobs;

    private Appointment appointment;

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
        txtEmpty = findViewById(R.id.txt_empty);
        imgCustomer = findViewById(R.id.img_customer);
        recyclerViewJobs = findViewById(R.id.rv_jobs);
        btnBack = findViewById(R.id.btn_back);
        layoutButtons = findViewById(R.id.layout_buttons);
        shimmerJobs = findViewById(R.id.shimmer_jobs);

        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
        dialog = new CustomDialog(getApplicationContext());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getIncomingIntent();
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

                if(status.equals("pending")) {
                    layoutButtons.setVisibility(View.VISIBLE);
                }

                txtTitle.setText(title);
                txtDateTime.setText(dateTime);
                txtStatus.setText(status);
                txtCustomer.setText(customer);

                Glide.with(this)
                        .setDefaultRequestOptions(options)
                        .load(appointment.getCustomerImage())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imgCustomer);
            }
        }
    }

    private void subscribeObservers() {
        viewModel.getAppointmentDetails().observe(this, new Observer<Resource<List<AppointmentDetail>>>() {
            @Override
            public void onChanged(Resource<List<AppointmentDetail>> listResource) {
                Log.d(TAG, "onChanged: listResource: " + listResource);
                if(listResource != null) {
                    if(listResource.data != null) {
                        Log.d(TAG, "onChanged: data: " + listResource.data);
                        switch (listResource.status) {

                            case LOADING: {
                                Log.d(TAG, "onChanged: LOADING");
                                showRecyclerView(false);
                                break;
                            }

                            case ERROR: {
                                Log.d(TAG, "onChanged: ERROR");
                                showRecyclerView(true);
                                dialog.showToast(listResource.message);

                                if(listResource.data.isEmpty()) {
                                    showEmpty();
                                }
                                break;
                            }

                            case SUCCESS: {
                                Log.d(TAG, "onChanged: SUCCESS");
                                showRecyclerView(true);

                                if(listResource.data.isEmpty()) {
                                    txtEmpty.setText("Oops! Something went wrong. Please try again later.");
                                    showEmpty();
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
            recyclerViewJobs.setVisibility(View.VISIBLE);
            shimmerJobs.setVisibility(View.GONE);
        }
        else {
            recyclerViewJobs.setVisibility(View.GONE);
            shimmerJobs.setVisibility(View.VISIBLE);
        }
        txtEmpty.setVisibility(View.GONE);
    }

    private void showEmpty() {
        txtEmpty.setVisibility(View.VISIBLE);
        recyclerViewJobs.setVisibility(View.GONE);
        shimmerJobs.setVisibility(View.GONE);
    }

    private void appointmentDetailsApi() {
        viewModel.appointmentDetailsApi(appointment.getId());
    }
}