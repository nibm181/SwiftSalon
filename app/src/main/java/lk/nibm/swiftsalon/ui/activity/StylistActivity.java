package lk.nibm.swiftsalon.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.viewmodel.StylistViewModel;

public class StylistActivity extends AppCompatActivity {
    private static final String TAG = "StylistActivity";
    public static final int EDIT_STYLIST = 1;

    public static final String AVAILABLE = "Available";
    public static final String UNAVAILABLE = "Unavailable";

    private TextView txtName, txtGender, txtStatus, txtJobs, txtTitle;
    private LinearLayout btnName, btnGender, btnJobs, btnStatus;
    private ImageView imgStylist;
    private ImageButton btnBack, btnEditImage;
    private FloatingActionButton btnDelete;

    private CustomDialog dialog;
    private Stylist stylist;
    private RequestOptions options;
    private StylistViewModel viewModel;
    private SweetAlertDialog confirmDialog;
    private List<Job> selectedJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stylist);

        txtTitle = findViewById(R.id.txt_title);
        txtName = findViewById(R.id.txt_name);
        txtGender = findViewById(R.id.txt_gender);
        txtJobs = findViewById(R.id.txt_jobs);
        txtStatus = findViewById(R.id.txt_status);
        imgStylist = findViewById(R.id.img_stylist);
        btnName = findViewById(R.id.btn_name);
        btnGender = findViewById(R.id.btn_gender);
        btnJobs = findViewById(R.id.btn_jobs);
        btnStatus = findViewById(R.id.btn_status);
        btnBack = findViewById(R.id.btn_back);
        btnEditImage = findViewById(R.id.btn_edit_image);
        btnDelete = findViewById(R.id.btn_delete);

        viewModel = new ViewModelProvider(this).get(StylistViewModel.class);
        selectedJobs = new ArrayList<>();
        dialog = new CustomDialog(StylistActivity.this);
        options = new RequestOptions()
                .placeholder(R.drawable.sample_avatar)
                .error(R.drawable.sample_avatar)
                .circleCrop();

        getIncomingContent(getIntent());
        subscribeObservers();
        getJobsApi(stylist.getId());

        btnBack.setOnClickListener(v -> finish());

        btnName.setOnClickListener(v -> {
            Intent editJob = new Intent(StylistActivity.this, EditStylistActivity.class);
            editJob.putExtra("stylist", stylist);
            editJob.putExtra("edit", EditStylistActivity.INPUT_NAME);

            startActivityForResult(editJob, EDIT_STYLIST);
        });

        btnGender.setOnClickListener(v -> {
            Intent editJob = new Intent(StylistActivity.this, EditStylistActivity.class);
            editJob.putExtra("stylist", stylist);
            editJob.putExtra("edit", EditStylistActivity.INPUT_GENDER);

            startActivityForResult(editJob, EDIT_STYLIST);
        });

        btnStatus.setOnClickListener(v -> {
            Intent editJob = new Intent(StylistActivity.this, EditStylistActivity.class);
            editJob.putExtra("stylist", stylist);
            editJob.putExtra("edit", EditStylistActivity.INPUT_STATUS);

            startActivityForResult(editJob, EDIT_STYLIST);
        });

        imgStylist.setOnClickListener(v -> {
            Intent editImage = new Intent(StylistActivity.this, ImageActivity.class);
            editImage.putExtra("stylist", stylist);

            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this, imgStylist, "image");

            startActivityForResult(editImage, EDIT_STYLIST,options.toBundle());
        });

        btnJobs.setOnClickListener(v -> {
            Intent intent = new Intent(StylistActivity.this, SelectJobActivity.class);
            intent.putExtra("jobs", (ArrayList<Job>) selectedJobs);
            intent.putExtra("stylistId", stylist.getId());
            intent.putExtra("edit", "edit");
            startActivityForResult(intent, SelectJobActivity.SELECT_JOB_EDIT);
        });

        btnDelete.setOnClickListener(v -> {
            deleteApi();
        });
    }

    private void getIncomingContent(Intent intent) {
        if (intent.hasExtra("stylist")) {
            stylist = intent.getParcelableExtra("stylist");

            if (stylist != null) {
                String title = stylist.getName() + " (" + stylist.getId() + ")";
                String status;

                Log.d(TAG, "getIncomingContent: STATUS: " + stylist.getStatus());
                if(stylist.getStatus() == 1) {
                    status = AVAILABLE;
                }
                else {
                    status = UNAVAILABLE;
                }

                txtTitle.setText(title);
                txtName.setText(stylist.getName());
                txtGender.setText(stylist.getGender());
                txtStatus.setText(status);

                Glide.with(this)
                        .setDefaultRequestOptions(options)
                        .load(stylist.getImage())
                        .into(imgStylist);
            } else {
                dialog.showToast("Oops! Something went wrong.");
                finish();
            }
        }
    }

    private void subscribeObservers() {
        viewModel.deleteStylist().observe(this, resource -> {
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

        viewModel.getJobs().observe(this, new Observer<Resource<List<Job>>>() {
            @Override
            public void onChanged(Resource<List<Job>> listResource) {
                if (listResource != null) {
                    if(listResource.data != null) {
                        switch (listResource.status) {

                            case LOADING: {
                                Log.d(TAG, "onChanged: LOADING");
                                txtJobs.setText("Loading...");
                                break;
                            }

                            case ERROR: {
                                Log.d(TAG, "onChanged: ERROR ");
                                Log.d(TAG, "onChanged: MSG: " + listResource.message);
                                selectedJobs = listResource.data;
                                txtJobs.setText(getJobNames(listResource.data));

                                if(selectedJobs.isEmpty()) {
                                    btnJobs.setEnabled(false);
                                    txtJobs.setText("We couldn't load the jobs. Check your connection.");
                                    txtJobs.setTextColor(getResources().getColor(R.color.default_text_color));
                                }
                                break;
                            }

                            case SUCCESS: {
                                Log.d(TAG, "onChanged: SUCCESS");
                                Log.d(TAG, "onChanged: DATA: " + listResource.data.size());
                                btnJobs.setEnabled(true);
                                selectedJobs = listResource.data;
                                txtJobs.setText(getJobNames(listResource.data));
                                break;
                            }

                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_STYLIST && resultCode == RESULT_OK) {

            if (data != null) {
                getIncomingContent(data);
            }

        }
        else if(requestCode == SelectJobActivity.SELECT_JOB_EDIT && requestCode == RESULT_OK) {

            if(data != null) {
                txtJobs.setText(getJobNames(data.getParcelableArrayListExtra("jobs")));
            }
        }
    }

    private void deleteApi() {
        confirmDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        confirmDialog.setTitleText("Are you sure?")
                .setContentText("You want to delete.")
                .setConfirmText("Ok")
                .setConfirmClickListener(sDialog -> {
                    if (isOnline()) {
                        viewModel.deleteApi(stylist);
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

    private String getJobNames(List<Job> jobs) {
        List<String> jobList = new ArrayList<>();
        for(Job job : jobs) {
            jobList.add(job.getName());
        }

        return TextUtils.join(", ", jobList);
    }

    private void getJobsApi(int id) {
        viewModel.getJobsApi(id);
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