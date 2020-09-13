package lk.nibm.swiftsalon.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Appointment;
import lk.nibm.swiftsalon.model.AppointmentDetail;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.ui.adapter.AppointmentDetailsAdapter;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.viewmodel.AppointmentViewModel;

import static lk.nibm.swiftsalon.util.Constants.STATUS_CANCELED;
import static lk.nibm.swiftsalon.util.Constants.STATUS_COMPLETED;
import static lk.nibm.swiftsalon.util.Constants.STATUS_ONSCHEDULE;
import static lk.nibm.swiftsalon.util.Constants.STATUS_PENDING;

public class AppointmentActivity extends AppCompatActivity {

    private static final String TAG = "AppointmentActivity";

    private TextView txtTitle, txtDateTime, txtStatus, txtCustomer, txtStylist, txtEmpty, txtTotal;
    private ImageView imgCustomer;
    private RecyclerView rvAppointmentDetails;
    private ImageButton btnBack, btnPhone;
    private RelativeLayout btnAccept, btnCancel;
    private TextView txtAccept, txtCancel;
    private ProgressBar prgAccept, prgCancel;
    private LinearLayout layoutButtons;
    private RelativeLayout layoutTotal;
    private ShimmerFrameLayout shimmerJobs;

    private Appointment appointment;
    private AppointmentDetailsAdapter adapter;

    private CustomDialog dialog;
    private AppointmentViewModel viewModel;
    private boolean toComplete;

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
        txtTotal = findViewById(R.id.txt_total);
        imgCustomer = findViewById(R.id.img_customer);
        rvAppointmentDetails = findViewById(R.id.rv_appointment_details);
        btnBack = findViewById(R.id.btn_back);
        btnAccept = findViewById(R.id.btn_accept);
        btnCancel = findViewById(R.id.btn_cancel);
        txtAccept = findViewById(R.id.btn_accept_text);
        txtCancel = findViewById(R.id.btn_cancel_text);
        prgAccept = findViewById(R.id.btn_accept_progress);
        prgCancel = findViewById(R.id.btn_cancel_progress);
        layoutTotal = findViewById(R.id.layout_total);
        layoutButtons = findViewById(R.id.layout_buttons);
        shimmerJobs = findViewById(R.id.shimmer_jobs);
        btnPhone = findViewById(R.id.btn_phone);

        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
        dialog = new CustomDialog(AppointmentActivity.this);

        btnBack.setOnClickListener(v -> finish());

        btnAccept.setOnClickListener(v -> {
            if(toComplete) {
                completeAppointmentApi();
            }
            else {
                acceptAppointmentApi();
            }
        });

        btnCancel.setOnClickListener(v -> cancelAppointmentApi());

        getIncomingIntent();
        initRecyclerView();
        subscribeObservers();
        appointmentDetailsApi();
        stylistApi();
    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("appointment")) {

            appointment = getIntent().getParcelableExtra("appointment");

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.sample_avatar)
                    .error(R.drawable.sample_avatar);

            if (appointment != null) {

                String title = appointment.getCustomerFirstName() + " (" + appointment.getId() + ")";
                String dateTime = appointment.getDate() + " " + appointment.getTime();
                String status = appointment.getStatus();
                String customer = appointment.getCustomerFirstName() + " " + appointment.getCustomerLastName();
                //String stylist = " (" + appointment.getStylistId() + ")";

                if (status.equals(STATUS_PENDING)) {
                    layoutButtons.setVisibility(View.VISIBLE);
                }
                else if(status.equals(STATUS_ONSCHEDULE) && isGreaterThanNow(dateTime.substring(0, 16))) {
                    showComplete();
                    toComplete = true;
                }
                else {
                    layoutButtons.setVisibility(View.GONE);
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

                if(appointment.getCustomerMobile() != null) {
                    showPhone(appointment.getCustomerMobile());
                }
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

                if (listResource != null) {
                    if (listResource.data != null) {

                        switch (listResource.status) {

                            case LOADING: {
                                Log.d(TAG, "onChanged: LOADING");
                                showRecyclerView(false, listResource.data);
                                break;
                            }

                            case ERROR: {
                                Log.d(TAG, "onChanged: ERROR");
                                Log.d(TAG, "onChanged: ERROR MSG: " + listResource.message);

                                showRecyclerView(true, listResource.data);
                                adapter.submitList(listResource.data);
                                dialog.showToast(listResource.message);

                                if (listResource.data.isEmpty()) {
                                    showEmpty();
                                }
                                break;
                            }

                            case SUCCESS: {
                                Log.d(TAG, "onChanged: SUCCESS");

                                if (listResource.data.isEmpty()) {
                                    txtEmpty.setText("Oops! Something went wrong. Please try again later.");
                                    showEmpty();
                                } else {
                                    showRecyclerView(true, listResource.data);
                                    adapter.submitList(listResource.data);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        });

        viewModel.acceptAppointment().observe(this, new Observer<Resource<GenericResponse<Appointment>>>() {
            @Override
            public void onChanged(Resource<GenericResponse<Appointment>> resource) {
                if (resource != null) {
                    switch (resource.status) {

                        case LOADING: {
                            Log.d(TAG, "onChanged: LOADING");
                            showAcceptProgress();
                            break;
                        }

                        case ERROR: {
                            Log.d(TAG, "onChanged: ERROR");
                            showButtons();
                            dialog.showToast(resource.message);
                            break;
                        }

                        case SUCCESS: {
                            if (resource.data.getStatus() == 1) {

                                if (resource.data.getContent() != null) {
                                    layoutButtons.setVisibility(View.GONE);
                                    txtStatus.setText(STATUS_ONSCHEDULE);
                                    dialog.showToast("Accepted");
                                } else {
                                    dialog.showAlert("Oops! Something went wrong. Try again later.");
                                }

                            } else {
                                dialog.showAlert(resource.data.getMessage());
                            }
                            showButtons();
                            break;
                        }
                    }
                }
            }
        });

        viewModel.cancelAppointment().observe(this, new Observer<Resource<GenericResponse<Appointment>>>() {
            @Override
            public void onChanged(Resource<GenericResponse<Appointment>> resource) {
                if (resource != null) {
                    switch (resource.status) {

                        case LOADING: {
                            Log.d(TAG, "onChanged: LOADING");
                            showCancelProgress();
                            break;
                        }

                        case ERROR: {
                            Log.d(TAG, "onChanged: ERROR");
                            showButtons();
                            dialog.showToast(resource.message);
                            break;
                        }

                        case SUCCESS: {
                            if (resource.data.getStatus() == 1) {

                                if (resource.data.getContent() != null) {
                                    layoutButtons.setVisibility(View.GONE);
                                    txtStatus.setText(STATUS_CANCELED);
                                    dialog.showToast("Canceled");
                                } else {
                                    dialog.showAlert("Oops! Something went wrong. Try again later.");
                                }

                            } else {
                                dialog.showAlert(resource.data.getMessage());
                            }
                            showButtons();
                            break;
                        }
                    }
                }
            }
        });

        viewModel.completeAppointment().observe(this, new Observer<Resource<GenericResponse<Appointment>>>() {
            @Override
            public void onChanged(Resource<GenericResponse<Appointment>> resource) {
                if (resource != null) {
                    switch (resource.status) {

                        case LOADING: {
                            Log.d(TAG, "onChanged: LOADING");
                            showAcceptProgress();
                            break;
                        }

                        case ERROR: {
                            Log.d(TAG, "onChanged: ERROR");
                            showCompleteButton();
                            dialog.showToast(resource.message);
                            break;
                        }

                        case SUCCESS: {
                            if (resource.data.getStatus() == 1) {

                                if (resource.data.getContent() != null) {
                                    layoutButtons.setVisibility(View.GONE);
                                    txtStatus.setText(STATUS_COMPLETED);
                                    dialog.showToast("Completed");
                                } else {
                                    dialog.showAlert("Oops! Something went wrong. Try again later.");
                                }

                            } else {
                                dialog.showAlert(resource.data.getMessage());
                            }
                            showCompleteButton();
                            break;
                        }
                    }
                }
            }
        });

        viewModel.getStylist().observe(this, new Observer<Resource<Stylist>>() {
            @Override
            public void onChanged(Resource<Stylist> stylistResource) {
                if (stylistResource != null) {
                    if (stylistResource.data != null) {

                        switch (stylistResource.status) {

                            case LOADING: {
                                Log.d(TAG, "onChanged: STYLIST LOADING");
                                break;
                            }

                            case ERROR:

                            case SUCCESS: {
                                String stylist = " " + stylistResource.data.getName() + " (" + stylistResource.data.getId() + ")";
                                txtStylist.setText(stylist);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    private void showRecyclerView(boolean show, List<AppointmentDetail> appointmentDetails) {
        if (show) {
            rvAppointmentDetails.setVisibility(View.VISIBLE);
            shimmerJobs.setVisibility(View.GONE);
            layoutTotal.setVisibility(View.VISIBLE);

            float total = 0;
            for (AppointmentDetail appointmentDetail : appointmentDetails) {
                total = total + appointmentDetail.getPrice();
            }
            txtTotal.setText(String.valueOf(total));
        } else {
            rvAppointmentDetails.setVisibility(View.GONE);
            shimmerJobs.setVisibility(View.VISIBLE);
            layoutTotal.setVisibility(View.GONE);
        }
        txtEmpty.setVisibility(View.GONE);
    }

    private void showAcceptProgress() {
        txtAccept.setVisibility(View.GONE);
        prgAccept.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);
    }

    private void showCancelProgress() {
        txtCancel.setVisibility(View.GONE);
        prgCancel.setVisibility(View.VISIBLE);
        btnAccept.setVisibility(View.GONE);
    }

    private void showButtons() {
        txtCancel.setVisibility(View.VISIBLE);
        prgCancel.setVisibility(View.GONE);
        btnAccept.setVisibility(View.VISIBLE);


        txtAccept.setVisibility(View.VISIBLE);
        prgAccept.setVisibility(View.GONE);
        btnCancel.setVisibility(View.VISIBLE);
    }

    private void showEmpty() {
        txtEmpty.setVisibility(View.VISIBLE);
        rvAppointmentDetails.setVisibility(View.GONE);
        shimmerJobs.setVisibility(View.GONE);
        layoutTotal.setVisibility(View.GONE);
    }

    private void showComplete() {
        layoutButtons.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);
        txtAccept.setText("Complete");
    }

    private void showCompleteButton() {
        txtAccept.setVisibility(View.VISIBLE);
        prgAccept.setVisibility(View.GONE);
    }

    private void showPhone(String mobileNo) {
        btnPhone.setVisibility(View.VISIBLE);
        btnPhone.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mobileNo));
                startActivity(intent);
            }
            catch (Exception e) {
                dialog.showToast("Can not make phone call");
            }
        });
    }

    private void appointmentDetailsApi() {
        viewModel.appointmentDetailsApi(appointment.getId());
    }

    private void stylistApi() {
        viewModel.stylistApi(appointment.getStylistId());
    }

    private void acceptAppointmentApi() {
        if(isOnline()) {
            viewModel.acceptAppointmentApi(appointment.getId());
        }
        else {
            dialog.showToast("Check your connection and try again.");
        }
    }

    private void cancelAppointmentApi() {
        if(isOnline()) {
            viewModel.cancelAppointmentApi(appointment.getId());
        }
        else {
            dialog.showToast("Check your connection and try again.");
        }
    }

    private void completeAppointmentApi() {
        if(isOnline()) {
            viewModel.completeAppointmentApi(appointment.getId());
        }
        else {
            dialog.showToast("Check your connection and try again.");
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGreaterThanNow(String appointmentTime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = df.parse(appointmentTime);
            return new Date().after(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}