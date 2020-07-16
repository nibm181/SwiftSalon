package lk.nibm.swiftsalon.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.ui.adapter.JobAdapter;
import lk.nibm.swiftsalon.ui.adapter.OnItemListener;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.VerticalSpacingItemDecorator;
import lk.nibm.swiftsalon.viewmodel.JobsViewModel;

public class JobsActivity extends AppCompatActivity implements OnItemListener {

    private static final String TAG = "JobsActivity";

    private ImageButton btnBack;
    private FloatingActionButton btnAdd;
    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerJobs;
    private TextView txtEmpty;

    private JobsViewModel viewModel;
    private JobAdapter adapter;
    private CustomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        btnBack = findViewById(R.id.btn_back);
        btnAdd = findViewById(R.id.btn_add);
        recyclerView = findViewById(R.id.rv_jobs);
        shimmerJobs = findViewById(R.id.shimmer_jobs);
        txtEmpty = findViewById(R.id.txt_empty);

        viewModel = new ViewModelProvider(this).get(JobsViewModel.class);
        dialog = new CustomDialog(JobsActivity.this);

        btnBack.setOnClickListener(v -> finish());

        btnAdd.setOnClickListener(v -> {
            Intent addJob = new Intent(JobsActivity.this, AddJobActivity.class);
            startActivity(addJob);
        });

        initRecyclerView();
        subscribeObservers();
        jobsApi();
    }


    private void initRecyclerView() {
        adapter = new JobAdapter(this);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(15);

        recyclerView.addItemDecoration(itemDecorator);
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

                            showRecyclerView(true);
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
                                txtEmpty.setText("There are no jobs. Please add jobs to view your business to clients.");
                                showEmpty();
                            } else {
                                Log.d(TAG, "subscribeObservers: SUBMITTED");
                                Log.d(TAG, "subscribeObservers: DATA: " + listResource.data.size());
                                showRecyclerView(true);
                                adapter.submitList(listResource.data);
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

    private void showRecyclerView(boolean show) {
        if (show) {
            recyclerView.setVisibility(View.VISIBLE);
            shimmerJobs.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            shimmerJobs.setVisibility(View.VISIBLE);
        }
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        shimmerJobs.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(int position) {
        Job job = adapter.getSelectedJob(position);

        Intent intent = new Intent(JobsActivity.this, JobActivity.class);
        intent.putExtra("job", job);
        startActivity(intent);
    }
}