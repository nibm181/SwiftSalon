package lk.nibm.swiftsalon.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.util.CurrencyTextWatcher;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.Session;
import lk.nibm.swiftsalon.viewmodel.AddJobViewModel;

public class AddJobActivity extends AppCompatActivity {
    private static final String TAG = "AddJobActivity";

    private ImageButton btnBack;
    private TextInputLayout txtName, txtDuration, txtPrice;
    private TextView txtSave;
    private ProgressBar prgSave;
    private RelativeLayout btnSave;

    private CustomDialog dialog;
    private AddJobViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        txtName = findViewById(R.id.txt_name);
        txtDuration = findViewById(R.id.txt_duration);
        txtPrice = findViewById(R.id.txt_price);
        txtSave = findViewById(R.id.btn_save_text);
        prgSave = findViewById(R.id.btn_save_progress);
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);

        dialog = new CustomDialog(AddJobActivity.this);
        viewModel = new ViewModelProvider(this).get(AddJobViewModel.class);
        txtPrice.getEditText().addTextChangedListener(new CurrencyTextWatcher());

        btnBack.setOnClickListener(v -> finish());

        subscribeObservers();
        disableSave();

        btnSave.setOnClickListener(v -> {
            if(validate()) {
                saveApi();
            }
        });
    }

    private void subscribeObservers() {
        viewModel.saveJob().observe(this, resource -> {
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
                                dialog.showToast("Successfully saved");
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
        });
    }

    private void saveApi() {
        if(isOnline()) {
            Session session = new Session(AddJobActivity.this);
            String name = txtName.getEditText().getText().toString().trim().replaceAll("\\.*$", "");
            String duration = txtDuration.getEditText().getText().toString().trim().replaceAll("\\.*$", "");
            String price = txtPrice.getEditText().getText().toString().trim().replaceAll("\\.*$", "");

            Job job = new Job();
            job.setSalonId(session.getSalonId());
            job.setName(name);
            job.setDuration(Integer.parseInt(duration));
            job.setPrice(Float.parseFloat(price));
            Log.d(TAG, "saveApi: JOB: " + job.toString());

            viewModel.saveApi(job);
        }
        else {
            dialog.showToast("Check your connection and try again.");
        }
    }

    private void disableSave() {
        btnSave.setEnabled(false);
        txtName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtName.setError(null);
                if (txtName.getEditText().getText().toString().trim().isEmpty()
                        || txtDuration.getEditText().getText().toString().trim().isEmpty()
                        || txtPrice.getEditText().getText().toString().trim().isEmpty()) {
                    btnSave.setEnabled(false);
                } else {
                    btnSave.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        txtDuration.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtDuration.setError(null);
                if (txtName.getEditText().getText().toString().trim().isEmpty()
                        || txtDuration.getEditText().getText().toString().trim().isEmpty()
                        || txtPrice.getEditText().getText().toString().trim().isEmpty()) {
                    btnSave.setEnabled(false);
                } else {
                    btnSave.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        txtPrice.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtPrice.setError(null);
                if (txtName.getEditText().getText().toString().trim().isEmpty()
                        || txtDuration.getEditText().getText().toString().trim().isEmpty()
                        || txtPrice.getEditText().getText().toString().trim().isEmpty()) {
                    btnSave.setEnabled(false);
                } else {
                    btnSave.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private boolean validate() {
        boolean valid = true;

        txtName.setError(null);
        txtDuration.setError(null);
        txtPrice.setError(null);

        if (Integer.parseInt(txtDuration.getEditText().getText().toString().trim()) > 300) {
            txtDuration.setError("Duration must be within 300 minutes or 5 hours");
            valid = false;
        } else if (Integer.parseInt(txtDuration.getEditText().getText().toString().trim()) < 15) {
            txtDuration.setError("Duration must be at least 15 minutes");
            valid = false;
        }
        if (Float.parseFloat(txtPrice.getEditText().getText().toString().trim()) < 100) {
            txtPrice.setError("Price must be greater than 100 rupees");
            valid = false;
        }

        return valid;
    }

    private void showProgressBar(boolean show) {
        if (show) {
            txtSave.setVisibility(View.GONE);
            prgSave.setVisibility(View.VISIBLE);
        } else {
            txtSave.setVisibility(View.VISIBLE);
            prgSave.setVisibility(View.GONE);
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}