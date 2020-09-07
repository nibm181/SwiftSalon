package lk.nibm.swiftsalon.ui.activity;

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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.viewmodel.RegistrationViewModel;

public class Registration extends AppCompatActivity {

    private static final String TAG = "Registration";

    private ImageButton btnBack;
    private TextInputLayout txtEmail;
    private RelativeLayout btnNext;
    private TextView txtNext;
    private ProgressBar prgNext;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private String email;

    private RegistrationViewModel viewModel;
    private CustomDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        btnBack = findViewById(R.id.btn_back);
        txtEmail = findViewById(R.id.txt_email);
        btnNext = findViewById(R.id.btn_next);
        txtNext = findViewById(R.id.btn_next_text);
        prgNext = findViewById(R.id.btn_next_progress);
        txtEmail.requestFocus();

        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        subscribeObservers();
        dialog = new CustomDialog(Registration.this);

        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtEmail.setError(null);

                email = txtEmail.getEditText().getText().toString().trim();

                if (email.isEmpty()) {
                    txtEmail.setError("Please enter email address.");
                } else if (!txtEmail.getEditText().getText().toString().trim().matches(emailPattern)) {
                    txtEmail.setError("invalid email");
                } else {
                    sendEmailApi(email);
                }

            }
        });

    }

    private void subscribeObservers() {
        viewModel.sendEmail().observe(this, resource -> {
            switch (resource.status) {

                case LOADING: {
                    Log.d(TAG, "onChanged: LOADING");

                    showProgressBar(true);
                    break;
                }

                case ERROR: {
                    Log.d(TAG, "onChanged: ERROR");
                    Log.d(TAG, "onChanged: ERROR MSG: " + resource.data);

                    showProgressBar(false);

                    if (resource.data != null) {
                        dialog.showAlert(resource.data.getMessage());
                    } else {
                        dialog.showToast("Oops! Something went wrong. Try again later. ");
                    }
                    break;
                }

                case SUCCESS: {
                    Log.d(TAG, "onChanged: SUCCESS");

                    if (resource.data.getStatus() == 1) {

                        Intent intent = new Intent(Registration.this, Registration_Part2.class);
                        intent.putExtra("email", email);
                        startActivity(intent);

                    } else {
                        dialog.showAlert(resource.data.getMessage());
                    }
                    showProgressBar(false);
                    break;
                }

            }
        });
    }

    private void sendEmailApi(String email) {
        if (isOnline()) {
            viewModel.sendEmailApi(email);
        } else {
            showProgressBar(false);
            dialog.showToast("Check your connection and try again.");
        }
    }


    private void showProgressBar(boolean show) {
        if (show) {
            txtNext.setVisibility(View.GONE);
            prgNext.setVisibility(View.VISIBLE);
        } else {
            txtNext.setVisibility(View.VISIBLE);
            prgNext.setVisibility(View.GONE);
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
