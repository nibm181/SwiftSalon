package lk.nibm.swiftsalon.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;
import lk.nibm.swiftsalon.viewmodel.AddStylistViewModel;
import lk.nibm.swiftsalon.viewmodel.RegistrationViewModel;

public class Registration_Part4 extends AppCompatActivity {

    public static final int OPEN_LOCATION = 1;
    private static final String TAG = "Registration_Part4";
    private CustomDialog dialog;

    private  TextInputLayout location,open_time,close_time,password,confirm_password;
    private  SimpleDateFormat dateFormat;
    private RelativeLayout register;
    private RegistrationViewModel viewModel;
    private TextView txtRegister;
    private ProgressBar prgRegister;
    private ImageButton btnBack;
    Salon salon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration__part4);
        getIntentContents();
        location=findViewById(R.id.txt_location);
        open_time=findViewById(R.id.time_open);
        close_time=findViewById(R.id.time_close);
        password=findViewById(R.id.txt_pwd);
        confirm_password=findViewById(R.id.txt_cpwd);
        register=findViewById(R.id.btn_register);
        txtRegister=findViewById(R.id.btn_register_text);
        prgRegister=findViewById(R.id.btn_register_progress);
        btnBack=findViewById(R.id.btn_back);

        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        dialog = new CustomDialog(Registration_Part4.this);
        //disableSave();
        subscribeObservers();

        dateFormat = new SimpleDateFormat("HH:mm");

        open_time.getEditText().setFocusable(false);
        close_time.getEditText().setFocusable(false);
        location.getEditText().setFocusable(false);
        btnBack.setOnClickListener(v -> finish());
        location.getEditText().setOnClickListener(v -> {
            Intent intent = new Intent(Registration_Part4.this, MapsActivity.class);
            startActivityForResult(intent, OPEN_LOCATION);
        });

        open_time.getEditText().setOnClickListener(v -> {
            String txtOpen = open_time.getEditText().getText().toString();
            if (txtOpen.isEmpty()) {
                txtOpen = "00:00";
            }
            Date openTime = null;
            try {
                openTime = dateFormat.parse(txtOpen);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            new SingleDateAndTimePickerDialog.Builder(this)
                    .displayMinutes(true)
                    .displayHours(true)
                    .displayDays(false)
                    .displayMonth(false)
                    .displayYears(false)
                    .displayDaysOfMonth(false)
                    .defaultDate(openTime)
                    .title("Open Time")
                    .titleTextSize(17)
                    .titleTextColor(getResources().getColor(R.color.dark))
                    .backgroundColor(getResources().getColor(R.color.dark))
                    .mainColor(getResources().getColor(R.color.light_grey))
                    .listener(date -> {
                        String time = dateFormat.format(date);
                        open_time.getEditText().setText(time);
                    })
                    .display();
        });

        close_time.getEditText().setOnClickListener(v -> {
            String txtClose = close_time.getEditText().getText().toString();
            if (txtClose.isEmpty()) {
                txtClose = "00:00";
            }
            Date closeTime = null;
            try {
                closeTime = dateFormat.parse(txtClose);
                Log.d(TAG, "showTimePicker: close: " + closeTime.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            new SingleDateAndTimePickerDialog.Builder(this)
                    .displayMinutes(true)
                    .displayHours(true)
                    .displayDays(false)
                    .displayMonth(false)
                    .displayYears(false)
                    .displayDaysOfMonth(false)
                    .defaultDate(closeTime)
                    .title("Close Time")
                    .titleTextSize(17)
                    .titleTextColor(getResources().getColor(R.color.dark))
                    .backgroundColor(getResources().getColor(R.color.dark))
                    .mainColor(getResources().getColor(R.color.light_grey))
                    .listener(date -> {
                        String time = dateFormat.format(date);
                        close_time.getEditText().setText(time);
                    })
                    .display();
        });
        register.setOnClickListener(v -> {
            try{
                location.setError(null);
                open_time.setError(null);
                close_time.setError(null);
                password.setError(null);
                confirm_password.setError(null);
                if (location.getEditText().getText().toString().trim().isEmpty()) {
                    location.setError("Please select the location");
                } else if (open_time.getEditText().getText().toString().trim().isEmpty()) {
                    open_time.setError("Please select the open time");
                } else if (close_time.getEditText().getText().toString().trim().isEmpty()) {
                    close_time.setError("please select the close time");
                } else if (!dateFormat.parse(open_time.getEditText().getText().toString().trim()).before(dateFormat.parse(close_time.getEditText().getText().toString().trim()))) {
                    open_time.setError("Open time should be before close time");
                    close_time.setError("Close time should be after open time");
                } else if (password.getEditText().getText().toString().trim().isEmpty()) {
                    password.setError("Please enter password");
                } else if (password.getEditText().getText().toString().trim().length()<8) {
                    password.setError("password too short");
                } else if (confirm_password.getEditText().getText().toString().trim().isEmpty()) {
                    confirm_password.setError("Please confirm the password");
                }  else if (!password.getEditText().getText().toString().trim().equals(confirm_password.getEditText().getText().toString().trim())) {
                    confirm_password.setError("confirmed password not matched");
                } else {
                    saveApi();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == OPEN_LOCATION) {
            if(data.hasExtra("location")) {
                String address = data.getStringExtra("location");
                Double longitude = data.getDoubleExtra("longitude", 0);
                Double latitude = data.getDoubleExtra("latitude", 0);
                location.getEditText().setText(address);

                salon.setLongitude(longitude);
                salon.setLatitude(latitude);

            }
        }
    }

    private void getIntentContents() {
        if(getIntent().hasExtra("salon")) {
            salon = getIntent().getParcelableExtra("salon");
            Log.d(TAG, "getIntentContents: Salon: " + salon.toString());
        }
    }
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    private void saveApi() {
        if(isOnline()) {

            salon.setLocation(location.getEditText().getText().toString().trim());
            salon.setOpenTime(open_time.getEditText().getText().toString().trim());
            salon.setCloseTime(close_time.getEditText().getText().toString().trim());
            salon.setPassword(password.getEditText().getText().toString());
            Log.d(TAG, "saveApi: SALON: " + salon.toString());

            viewModel.saveApi(salon);
        }
        else {
            dialog.showToast("Check your connection and try again.");
        }
    }
    private void showProgressBar(boolean show) {
        if (show) {
            txtRegister.setVisibility(View.GONE);
            prgRegister.setVisibility(View.VISIBLE);
        } else {
            txtRegister.setVisibility(View.VISIBLE);
            prgRegister.setVisibility(View.GONE);
        }
    }
    private void subscribeObservers() {
        viewModel.saveSalon().observe(this, new Observer<Resource<GenericResponse<Salon>>>() {
            @Override
            public void onChanged(Resource<GenericResponse<Salon>> resource) {
                if (resource != null) {

                    switch (resource.status) {

                        case LOADING: {
                            Log.d(TAG, "onChanged: LOADING");
                            showProgressBar(true);
                            break;
                        }

                        case ERROR: {
                            Log.d(TAG, "onChanged: ERROR");
                            showProgressBar(false);
                            dialog.showToast(resource.message);
                            break;
                        }

                        case SUCCESS: {
                            Log.d(TAG, "onChanged: SUCCESS");

                            if (resource.data.getStatus() == 1) {

                                if (resource.data.getContent() != null) {
                                    dialog.showToast("Salon Successfully Registered");
                                    Intent mainIntent = new Intent(Registration_Part4.this, LoginActivity.class);
                                    startActivity(mainIntent);
                                    finish();
                                } else {
                                    showProgressBar(false);
                                    dialog.showAlert("Oops! Something went wrong. Try again later.");
                                }

                            } else {
                                Log.d(TAG, "subscribeObservers: MESSAGE: " + resource.data.getMessage());
                                showProgressBar(false);
                                dialog.showAlert(resource.data.getMessage());
                            }
                            break;
                        }

                    }

                }
            }
        });
    }
}