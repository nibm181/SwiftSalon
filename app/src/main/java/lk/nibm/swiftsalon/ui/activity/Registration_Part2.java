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
import lk.nibm.swiftsalon.viewmodel.VerifyEmailViewModel;

public class Registration_Part2 extends AppCompatActivity {

    private static final String TAG = "Registration_Part2";

    private TextInputLayout txtVCode;
    private ImageButton btnBack;
    private RelativeLayout btnVerify;
    private TextView txtVerify;
    private ProgressBar prgVerify;

    private VerifyEmailViewModel viewModel;
    private CustomDialog dialog;
    private String email;
    private int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration__part2);

        txtVCode = findViewById(R.id.txt_vcode);
        btnBack = findViewById(R.id.btn_back);
        btnVerify = findViewById(R.id.btn_verify);
        txtVerify = findViewById(R.id.btn_verify_text);
        prgVerify = findViewById(R.id.btn_verify_progress);
        txtVCode.requestFocus();

        viewModel = new ViewModelProvider(this).get(VerifyEmailViewModel.class);
        dialog = new CustomDialog(Registration_Part2.this);

        btnBack.setOnClickListener(v -> finish());

        btnVerify.setOnClickListener(v -> verifyEmailApi());

        getIncomingContent();
        subscribeObservers();

    }

    private void getIncomingContent() {
        if(getIntent().hasExtra("email")) {
            email = getIntent().getStringExtra("email");
        }
    }

    private void subscribeObservers() {
        viewModel.verifyEmail().observe(this, resource -> {
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
                        dialog.showToast("Oops! Something went wrong. Try again later. " + resource.status);
                    }
                    break;
                }

                case SUCCESS: {
                    Log.d(TAG, "onChanged: SUCCESS");

                    if (resource.data.getStatus() == 1) {

                        Intent intent = new Intent(Registration_Part2.this, Registration_Part3.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();

                    } else {
                        dialog.showAlert("Something went wrong.");
                    }
                    showProgressBar(false);
                    break;
                }

            }
        });
    }

    private void showProgressBar(boolean show) {
        if (show) {
            txtVerify.setVisibility(View.GONE);
            prgVerify.setVisibility(View.VISIBLE);
        } else {
            txtVerify.setVisibility(View.VISIBLE);
            prgVerify.setVisibility(View.GONE);
        }
    }

    private void verifyEmailApi() {
        if (isOnline()) {
            code = Integer.parseInt(txtVCode.getEditText().getText().toString().trim());
            viewModel.verifyEmailApi(email, code);
        } else {
            showProgressBar(false);
            dialog.showToast("Check your connection and try again.");
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
