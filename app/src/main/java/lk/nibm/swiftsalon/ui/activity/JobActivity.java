package lk.nibm.swiftsalon.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import cn.pedant.SweetAlert.SweetAlertDialog;
import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.viewmodel.JobViewModel;

public class JobActivity extends AppCompatActivity {
    private static final String TAG = "JobActivity";
    public static final int EDIT_JOB = 1;

    private TextView txtTitle, txtName, txtDuration, txtPrice;
    private LinearLayout btnName, btnDuration, btnPrice;
    private ImageButton btnBack;
    private FloatingActionButton btnDelete, btnPromote;

    private CustomDialog dialog;
    private Job job;
    private JobViewModel viewModel;
    private SweetAlertDialog confirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        txtTitle = findViewById(R.id.txt_title);
        txtName = findViewById(R.id.txt_name);
        txtDuration = findViewById(R.id.txt_duration);
        txtPrice = findViewById(R.id.txt_price);
        btnBack = findViewById(R.id.btn_back);
        btnName = findViewById(R.id.btn_name);
        btnDuration = findViewById(R.id.btn_duration);
        btnPrice = findViewById(R.id.btn_price);
        btnDelete = findViewById(R.id.btn_delete);
        btnPromote = findViewById(R.id.btn_promote);

        viewModel = new ViewModelProvider(this).get(JobViewModel.class);
        dialog = new CustomDialog(JobActivity.this);
        getIncomingContent(getIntent());
        subscribeObservers();

        btnBack.setOnClickListener(v -> finish());

        btnName.setOnClickListener(v -> {
            Intent editJob = new Intent(JobActivity.this, EditJobActivity.class);
            editJob.putExtra("job", job);
            editJob.putExtra("edit", EditJobActivity.INPUT_NAME);

            startActivityForResult(editJob, EDIT_JOB);
        });

        btnDuration.setOnClickListener(v -> {
            Intent editJob = new Intent(JobActivity.this, EditJobActivity.class);
            editJob.putExtra("job", job);
            editJob.putExtra("edit", EditJobActivity.INPUT_DURATION);

            startActivityForResult(editJob, EDIT_JOB);
        });

        btnPrice.setOnClickListener(v -> {
            Intent editJob = new Intent(JobActivity.this, EditJobActivity.class);
            editJob.putExtra("job", job);
            editJob.putExtra("edit", EditJobActivity.INPUT_PRICE);

            startActivityForResult(editJob, EDIT_JOB);
        });

        btnDelete.setOnClickListener(v -> {
            deleteApi();
        });

        btnPromote.setOnClickListener(v -> {
            //dialog.showAlert("Promote option coming soon!");
            Intent intent = new Intent(JobActivity.this, AddPromotionActivity.class);
            intent.putExtra("job", job);
            startActivity(intent);
        });
    }

    private void getIncomingContent(Intent intent) {
        if (intent.hasExtra("job")) {
            job = intent.getParcelableExtra("job");

            if (job != null) {
                String title = job.getName() + " (" + job.getId() + ")";
                String duration = job.getDuration() + " minutes";
                String price = "Rs. " + String.format("%,.2f", job.getPrice());

                txtTitle.setText(title);
                txtName.setText(job.getName());
                txtDuration.setText(duration);
                txtPrice.setText(price);
            } else {
                dialog.showToast("Oops! Something went wrong.");
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_JOB && resultCode == RESULT_OK) {

            if (data != null) {
                getIncomingContent(data);
            }

        }
    }

    private void subscribeObservers() {
        viewModel.deleteJob().observe(this, resource -> {
            if (resource != null) {

                switch (resource.status) {

                    case LOADING: {
                        Log.d(TAG, "onChanged: LOADING");
                        showProgressBar(true);
                        break;
                    }

                    case ERROR: {
                        Log.d(TAG, "onChanged: ERROR ");
                        showProgressBar(false);
                        dialog.showToast(resource.message);
//                        Snackbar.make(btnDelete, resource.message, Snackbar.LENGTH_LONG)
//                                .setBackgroundTint(getResources().getColor(R.color.dark))
//                                .show();
                        break;
                    }

                    case SUCCESS: {
                        Log.d(TAG, "onChanged: SUCCESS");

                        if (resource.data.getStatus() == 1) {

                            if (resource.data.getContent() != null) {
                                showProgressBar(false);
                                dialog.showToast("Successfully deleted.");
                                finish();
                            } else {
                                showProgressBar(false);
                                dialog.showAlert("Oops! Something went wrong. Try again later.");
                            }

                        } else {
                            showProgressBar(false);
                            dialog.showAlert(resource.data.getMessage());
                        }
                        break;
                    }

                }

            }
        });
    }

    private void deleteApi() {
        confirmDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        confirmDialog.setTitleText("Are you sure?")
                .setContentText("Deleting a job will remove the job from every stylist's job list.")
                .setConfirmText("Ok")
                .setConfirmClickListener(sDialog -> {
                    if (isOnline()) {
                        viewModel.deleteApi(job);
                    } else {
                        sDialog.dismiss();
                        dialog.showToast("Check your connection and try again.");
                    }
                })
                .setCancelButton("Cancel", SweetAlertDialog::dismissWithAnimation)
                .show();

        Button btnConfirm = confirmDialog.findViewById(R.id.confirm_button);
        Button btnCancel = confirmDialog.findViewById(R.id.cancel_button);
        btnConfirm.setBackgroundResource(R.drawable.button_shape);
        btnCancel.setBackgroundResource(R.drawable.button_shape);

    }

    private void showProgressBar(boolean show) {
        if (confirmDialog != null) {
            if (show) {
                confirmDialog.setCancelable(false);
                confirmDialog.setTitle("");
                confirmDialog.setContentText("");
                confirmDialog.getProgressHelper().setBarColor(R.color.dark);
                confirmDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);

                Button btnCancel = confirmDialog.findViewById(R.id.cancel_button);
                btnCancel.setVisibility(View.GONE);
            } else {
                confirmDialog.dismiss();
            }
        }

    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}