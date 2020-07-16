package lk.nibm.swiftsalon.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.util.CustomDialog;

import static lk.nibm.swiftsalon.util.Constants.GENDERS;

public class AddStylistActivity extends AppCompatActivity {

    private static final String TAG = "AddStylistActivity";

    private TextInputLayout txtName, txtGender, txtJobs;
    private TextView txtSave;
    private RelativeLayout btnSave;
    private ProgressBar prgSave;
    private ImageButton btnBack;
    private AutoCompleteTextView dropdownGender;

    private List<Job> jobs;
    private CustomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stylist);

        txtName = findViewById(R.id.txt_name);
        txtGender = findViewById(R.id.txt_gender);
        txtJobs = findViewById(R.id.txt_jobs);
        txtSave = findViewById(R.id.btn_save_text);
        prgSave = findViewById(R.id.btn_save_progress);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
        dropdownGender = findViewById(R.id.dropdown_gender);

        dialog = new CustomDialog(AddStylistActivity.this);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        AddStylistActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        GENDERS);

        dropdownGender.setAdapter(adapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dropdownGender.setShowSoftInputOnFocus(false);
            txtJobs.getEditText().setShowSoftInputOnFocus(false);
        }
        dropdownGender.setKeyListener(null);
        txtJobs.getEditText().setKeyListener(null);

        txtJobs.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    startSelectJobsIntent();
                    txtJobs.getEditText().clearFocus();
                }
            }
        });

        txtJobs.getEditText().setOnClickListener(v -> {
            startSelectJobsIntent();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void startSelectJobsIntent() {
        Intent intent = new Intent(AddStylistActivity.this, SelectJobActivity.class);
        intent.putExtra("jobs", (ArrayList<Job>) jobs);
        intent.putExtra("add", "add");
        startActivityForResult(intent, SelectJobActivity.SELECT_JOB_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SelectJobActivity.SELECT_JOB_ADD && resultCode == RESULT_OK) {

            if (data != null) {

                if(data.hasExtra("jobs")) {

                    jobs = data.getParcelableArrayListExtra("jobs");
                    List<String> jobList = new ArrayList<>();

                    for(Job job : jobs) {
                        jobList.add(job.getName());
                    }

                    String jobValue = TextUtils.join(", ", jobList);
                    txtJobs.getEditText().setText(jobValue);

                }
            }

        }
    }
}