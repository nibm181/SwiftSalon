package lk.nibm.swiftsalon.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.CurrencyTextWatcher;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.viewmodel.EditJobViewModel;

import static lk.nibm.swiftsalon.util.Constants.EMAIL_REGEX;
import static lk.nibm.swiftsalon.util.Constants.MOBILE_NUMBER_REGEX;

public class EditJobActivity extends AppCompatActivity {
    private static final String TAG = "EditJobActivity";

    public static final String INPUT_NAME = "name";
    public static final String INPUT_DURATION = "duration";
    public static final String INPUT_PRICE = "price";

    private TextView txtTitle, txtWarning, txtSave;
    private EditText txtEdit;
    private ImageButton btnBack;
    private ProgressBar prgSave;
    private RelativeLayout btnSave;

    private EditJobViewModel viewModel;
    private CustomDialog dialog;
    private String edit;
    private Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);

        txtTitle = findViewById(R.id.txt_title);
        txtWarning = findViewById(R.id.txt_warning);
        txtSave = findViewById(R.id.btn_save_text);
        txtEdit = findViewById(R.id.txt_edit);
        prgSave = findViewById(R.id.btn_save_progress);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);

        viewModel = new ViewModelProvider(this).get(EditJobViewModel.class);
        dialog = CustomDialog.getInstance(EditJobActivity.this);

        btnBack.setOnClickListener(v -> finish());

        getIncomingContent();
        subscribeObservers();
        disableSave();

        btnSave.setOnClickListener(v -> {
            if (validate()) {
                updateApi();
            }
        });
    }

    private void getIncomingContent() {
        if(getIntent().hasExtra("job") && getIntent().hasExtra("edit")) {

            job = getIntent().getParcelableExtra("job");
            edit = getIntent().getStringExtra("edit");

            if(edit.equals(INPUT_NAME)) {
                txtTitle.setText("Name");
                txtEdit.setText(job.getName());
                txtEdit.setHint("Enter name");

                txtEdit.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            }
            else if(edit.equals(INPUT_DURATION)) {
                txtTitle.setText("Duration in Minutes");
                txtEdit.setText(String.valueOf(job.getDuration()));
                txtEdit.setHint("Enter duration");

                txtEdit.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
                txtEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            }
            else if(edit.equals(INPUT_PRICE)) {
                DecimalFormat df = new DecimalFormat("#.##");
                df.setMinimumFractionDigits(2);

                txtTitle.setText("Price");
                txtEdit.setText(df.format(job.getPrice()));
                txtEdit.setHint("Enter price");

                txtEdit.addTextChangedListener(new CurrencyTextWatcher());
                txtEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
            txtEdit.requestFocus();
            txtEdit.setSelection(txtEdit.getText().length());
        }
    }

    private void subscribeObservers() {
        viewModel.updateJob().observe(this, new Observer<Resource<GenericResponse<Job>>>() {
            @Override
            public void onChanged(Resource<GenericResponse<Job>> resource) {
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
                                    data.putExtra("edit", edit);
                                    data.putExtra("job", job);

                                    setResult(RESULT_OK, data);
                                    finish();
                                } else {
                                    showProgressBar(false);
                                    dialog.showAlert("Oops! Something went wrong. Please try again.");
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

    private void disableSave() {
        txtEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtEdit.getText().toString().trim().isEmpty()) {
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

    private void updateApi() {
        String text = txtEdit.getText().toString().trim().replaceAll("\\.*$", "");

        if(isOnline()) {
            switch (edit) {

                case INPUT_NAME: {
                    job.setName(text);
                    break;
                }

                case INPUT_DURATION: {
                    job.setDuration(Integer.parseInt(text));
                    break;
                }

                case INPUT_PRICE: {
                    job.setPrice(Float.parseFloat(text));
                    break;
                }
            }

            Log.d(TAG, "onCreate: SALON: " + job.toString());
            viewModel.updateApi(job);
        }
        else {
            dialog.showToast("Check your connection and try again.");
        }

    }

    private boolean validate() {
        txtWarning.setVisibility(View.GONE);
        String text = txtEdit.getText().toString();

        if (edit.equals(INPUT_DURATION) && Integer.parseInt(text) > 300) {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText("Duration must be within 300 minutes or 5 hours");
            return false;
        }
        else if (edit.equals(INPUT_DURATION) && Integer.parseInt(text) < 15) {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText("Duration must be at least 15 minutes");
            return false;
        }
        else if (edit.equals(INPUT_PRICE) && Float.parseFloat(text) < 100) {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText("Price must be greater than 100 rupees");
            return false;
        }

        return true;
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