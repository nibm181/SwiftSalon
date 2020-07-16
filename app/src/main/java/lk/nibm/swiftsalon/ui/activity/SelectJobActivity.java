package lk.nibm.swiftsalon.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.StylistJob;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.ui.adapter.OnItemListener;
import lk.nibm.swiftsalon.ui.adapter.SelectJobAdapter;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.viewmodel.SelectJobViewModel;

public class SelectJobActivity extends AppCompatActivity implements OnItemListener {
    private static final String TAG = "SelectJobActivity";

    public static final int SELECT_JOB_ADD = 1;
    public static final int SELECT_JOB_EDIT = 2;

    private ImageButton btnBack;
    private RecyclerView recyclerView;
    private RelativeLayout btnSave;
    private TextView txtSave, txtWarning;
    private ProgressBar prgSave;
    private ShimmerFrameLayout shimmerSelectJobs;

    private SelectJobAdapter adapter;
    private CustomDialog dialog;
    private SelectJobViewModel viewModel;

    private List<Job> selectedJobs = new ArrayList<>();
    private int stylistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_job);

        btnBack = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.rv_jobs);
        btnSave = findViewById(R.id.btn_save);
        txtSave = findViewById(R.id.btn_save_text);
        txtWarning = findViewById(R.id.txt_warning);
        prgSave = findViewById(R.id.btn_save_progress);
        shimmerSelectJobs = findViewById(R.id.layout_shimmer);

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            saveJobs();
        });

        viewModel = new ViewModelProvider(this).get(SelectJobViewModel.class);
        dialog = new CustomDialog(SelectJobActivity.this);

        getIncomingContent();
        initRecyclerView();
        subscribeObservers();
        jobsApi();
        Log.d(TAG, "onCreate: SELECTED JOBS " + selectedJobs.toString());
    }

    private void getIncomingContent() {
        if(getIntent().hasExtra("add")) {
            txtSave.setText("OK");
        }
        else if(getIntent().hasExtra("edit")) {
            stylistId = getIntent().getIntExtra("stylistId", 0);
        }

        if(getIntent().hasExtra("jobs") && getIntent().getParcelableArrayListExtra("jobs") != null) {
            selectedJobs = getIntent().getParcelableArrayListExtra("jobs");
        }

        disableSave();
    }

    private void initRecyclerView() {
        adapter = new SelectJobAdapter(this, selectedJobs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void subscribeObservers() {
        viewModel.getJobs().observe(this, listResource -> {
            if (listResource != null) {
                if (listResource.data != null) {

                    switch (listResource.status) {

                        case LOADING: {
                            Log.d(TAG, "onChanged: LOADING");
                            showRecyclerView(false);
                            break;
                        }

                        case ERROR: {
                            Log.d(TAG, "onChanged: ERROR");

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
                                txtWarning.setText("Add a job and come here to add a new stylist");
                                showEmpty();
                            } else {
                                showRecyclerView(true);
                                adapter.submitList(listResource.data);
                            }
                            break;
                        }
                    }
                }
            }
        });

        viewModel.updateStylist().observe(this, new Observer<Resource<GenericResponse<List<StylistJob>>>>() {
            @Override
            public void onChanged(Resource<GenericResponse<List<StylistJob>>> resource) {
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
                                    dialog.showToast("Successfully updated");

                                    Intent data = new Intent();
                                    data.putExtra("jobs", (ArrayList<Job>) selectedJobs);

                                    setResult(RESULT_OK, data);
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
            }
        });
    }

    private void jobsApi() {
        viewModel.jobsApi();
    }

    private void saveJobs() {
        if(selectedJobs.isEmpty()) {
            return;
        }

        if(getIntent().hasExtra("edit")) {
            if(isOnline()) {
                viewModel.updateApi(stylistId, selectedJobs);
            }
            else {
                dialog.showToast("Check your connection and try again.");
            }
        }
        else {
            Intent data = new Intent();
            data.putExtra("jobs", (ArrayList<Job>) selectedJobs);

            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void showRecyclerView(boolean show) {
        if (show) {
            recyclerView.setVisibility(View.VISIBLE);
            shimmerSelectJobs.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            shimmerSelectJobs.setVisibility(View.VISIBLE);
        }
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        shimmerSelectJobs.setVisibility(View.GONE);
        txtWarning.setVisibility(View.VISIBLE);
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

    private void disableSave() {
        if(selectedJobs.isEmpty()) {
            btnSave.setEnabled(false);
        }
        else {
            btnSave.setEnabled(true);
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onItemClick(int position) {
        Job job = adapter.getSelectedJob(position);

        if (!selectedJobs.contains(job)) {
            selectedJobs.add(job);
        } else {
            selectedJobs.remove(job);
        }

        disableSave();
    }
}