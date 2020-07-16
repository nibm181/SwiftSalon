package lk.nibm.swiftsalon.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.viewmodel.EditStylistViewModel;

public class EditStylistActivity extends AppCompatActivity {

    private static final String TAG = "EditStylistActivity";

    public static final String INPUT_NAME = "name";
    public static final String INPUT_GENDER = "gender";
    public static final String INPUT_STATUS = "status";

    public static final int MALE = 0;
    public static final int FEMALE = 1;
    public static final int NEUTRAL = 2;

    public static final int AVAILABLE = 0;
    public static final int UNAVAILABLE = 1;

    private ImageButton btnBack;
    private TextView txtTitle, txtWarning, txtSave;
    private EditText txtEdit;
    private RelativeLayout btnSave;
    private ProgressBar prgSave;
    private SegmentedButtonGroup sgmGender, sgmStatus;

    private Stylist stylist;
    private String edit;
    private CustomDialog dialog;
    private EditStylistViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stylist);

        txtTitle = findViewById(R.id.txt_title);
        txtEdit = findViewById(R.id.txt_edit);
        txtWarning = findViewById(R.id.txt_warning);
        txtSave = findViewById(R.id.btn_save_text);
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        prgSave = findViewById(R.id.btn_save_progress);
        sgmGender = findViewById(R.id.sgm_gender);
        sgmStatus = findViewById(R.id.sgm_status);

        viewModel = new ViewModelProvider(this).get(EditStylistViewModel.class);
        dialog = new CustomDialog(EditStylistActivity.this);

        btnBack.setOnClickListener(v -> finish());

        getIncomingContent();
        subscribeObservers();
        disableSave();

        btnSave.setOnClickListener(v -> {
            updateApi();
        });
    }

    private void getIncomingContent() {
        if (getIntent().hasExtra("stylist") && getIntent().hasExtra("edit")) {

            stylist = getIntent().getParcelableExtra("stylist");
            edit = getIntent().getStringExtra("edit");

            if (edit.equals(INPUT_NAME)) {
                txtTitle.setText("Name");
                txtEdit.setText(stylist.getName());
                txtEdit.setHint("Enter name");

                txtEdit.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            } else if (edit.equals(INPUT_GENDER)) {
                txtTitle.setText("Gender");
                showGender(stylist.getGender());
            } else if (edit.equals(INPUT_STATUS)) {
                txtTitle.setText("Availability");
                showStatus(stylist.getStatus());
            }

            txtEdit.requestFocus();
            txtEdit.setSelection(txtEdit.getText().length());
        }
    }

    private void subscribeObservers() {
        viewModel.updateStylist().observe(this, resource -> {
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
                                data.putExtra("stylist", stylist);

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
        });
    }

    private void showGender(String gender) {
        txtEdit.setVisibility(View.GONE);
        sgmGender.setVisibility(View.VISIBLE);
        sgmStatus.setVisibility(View.GONE);

        if (gender.toLowerCase().equals("male")) {
            sgmGender.setPosition(MALE, false);
        } else if (gender.toLowerCase().equals("female")) {
            sgmGender.setPosition(FEMALE, false);
        } else {
            sgmGender.setPosition(NEUTRAL, false);
        }
    }

    private void showStatus(int status) {
        Log.d(TAG, "showStatus: STATUS: " + status);
        txtEdit.setVisibility(View.GONE);
        sgmGender.setVisibility(View.GONE);
        sgmStatus.setVisibility(View.VISIBLE);

        if (status == 1) {
            sgmStatus.setPosition(AVAILABLE, false);
        } else {
            sgmStatus.setPosition(UNAVAILABLE, false);
        }
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

        if (isOnline()) {
            switch (edit) {

                case INPUT_NAME: {
                    stylist.setName(text);
                    break;
                }

                case INPUT_GENDER: {
                    String gender = sgmGender.getButton(sgmGender.getPosition()).getText();
                    stylist.setGender(gender);
                    break;
                }

                case INPUT_STATUS: {
                    int status = sgmStatus.getButton(sgmStatus.getPosition()).getText().toLowerCase().equals("available") ? 1 : 0;
                    stylist.setStatus(status);
                    break;
                }
            }

            Log.d(TAG, "onCreate: SALON: " + stylist.toString());
            viewModel.updateApi(stylist);
        } else {
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
}