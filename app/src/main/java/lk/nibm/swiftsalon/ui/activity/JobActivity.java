package lk.nibm.swiftsalon.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.util.CustomDialog;

public class JobActivity extends AppCompatActivity {
    private static final String TAG = "JobActivity";
    public static final int EDIT_JOB = 1;

    private TextView txtTitle, txtName, txtDuration, txtPrice;
    private LinearLayout btnName, btnDuration, btnPrice;
    private ImageButton btnBack;

    private CustomDialog dialog;
    private Job job;

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

        dialog = CustomDialog.getInstance(JobActivity.this);
        getIncomingContent(getIntent());

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
}