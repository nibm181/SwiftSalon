package lk.nibm.swiftsalon.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.util.Session;
import lk.nibm.swiftsalon.viewmodel.AddStylistViewModel;

import static lk.nibm.swiftsalon.util.Constants.GENDERS;

public class AddStylistActivity extends AppCompatActivity {

    private static final String TAG = "AddStylistActivity";

    private TextInputLayout txtName, txtGender, txtJobs;
    private TextView txtSave;
    private RelativeLayout btnSave, btnImage;
    private ImageView imgStylist;
    private ProgressBar prgSave;
    private ImageButton btnBack;
    private AutoCompleteTextView dropdownGender;

    private Uri imageUri;
    private String stringUri = "";
    private List<Job> jobs;
    private CustomDialog dialog;
    private AddStylistViewModel viewModel;

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
        imgStylist = findViewById(R.id.img_stylist);
        btnImage = findViewById(R.id.layout_img);
        dropdownGender = findViewById(R.id.dropdown_gender);

        viewModel = new ViewModelProvider(this).get(AddStylistViewModel.class);
        dialog = new CustomDialog(AddStylistActivity.this);
        disableSave();
        subscribeObservers();

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

        //hide soft keyboard when dropdown focused
        dropdownGender.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }

        });

        txtJobs.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                startSelectJobsIntent();
                txtJobs.getEditText().clearFocus();
            }
        });

        txtJobs.getEditText().setOnClickListener(v -> {
            startSelectJobsIntent();
        });

        btnImage.setOnClickListener(v -> {
            if(imageUri != null) {
                stringUri = imageUri.toString();
            }

            Intent editImage = new Intent(AddStylistActivity.this, ImageActivity.class);
            editImage.putExtra("image", stringUri);

            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this, imgStylist, "image");

            startActivityForResult(editImage, ImageActivity.ADD, options.toBundle());
        });

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            saveApi();
        });
    }

    private void startSelectJobsIntent() {
        Intent intent = new Intent(AddStylistActivity.this, SelectJobActivity.class);
        intent.putExtra("jobs", (ArrayList<Job>) jobs);
        intent.putExtra("add", "add");
        startActivityForResult(intent, SelectJobActivity.SELECT_JOB_ADD);
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
                        || dropdownGender.getText().toString().trim().isEmpty()
                        || txtJobs.getEditText().getText().toString().trim().isEmpty()) {
                    btnSave.setEnabled(false);
                } else {
                    btnSave.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dropdownGender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtName.getEditText().getText().toString().trim().isEmpty()
                        || dropdownGender.getText().toString().trim().isEmpty()
                        || txtJobs.getEditText().getText().toString().trim().isEmpty()) {
                    btnSave.setEnabled(false);
                } else {
                    btnSave.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        txtJobs.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtName.getEditText().getText().toString().trim().isEmpty()
                        || dropdownGender.getText().toString().trim().isEmpty()
                        || txtJobs.getEditText().getText().toString().trim().isEmpty()) {
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

    private void subscribeObservers() {
        viewModel.saveStylist().observe(this, new Observer<Resource<GenericResponse<Stylist>>>() {
            @Override
            public void onChanged(Resource<GenericResponse<Stylist>> resource) {
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
            }
        });
    }

    private void saveApi() {
        if(isOnline()) {
            Session session = new Session(AddStylistActivity.this);
            String name = txtName.getEditText().getText().toString().trim().replaceAll("\\.*$", "");
            String gender = dropdownGender.getText().toString().trim().replaceAll("\\.*$", "");

            Stylist stylist = new Stylist();
            stylist.setSalonId(session.getSalonId());
            stylist.setName(name);
            stylist.setGender(gender);
            Log.d(TAG, "saveApi: STYLIST: " + stylist.toString());

            viewModel.saveApi(stylist, jobs, imageUri);
        }
        else {
            dialog.showToast("Check your connection and try again.");
        }
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
        else if (requestCode == ImageActivity.ADD && resultCode == RESULT_OK) {

            if (data != null) {

                if(data.hasExtra("image")) {

                    imageUri = Uri.parse(data.getStringExtra("image"));

                    Glide.with(AddStylistActivity.this)
                            .load(imageUri)
                            .apply(RequestOptions
                                    .circleCropTransform()
                                    .placeholder(R.drawable.sample_avatar)
                                    .error(R.drawable.sample_avatar)
                            )
                            .into(imgStylist);

                }
            }

        }
    }
}