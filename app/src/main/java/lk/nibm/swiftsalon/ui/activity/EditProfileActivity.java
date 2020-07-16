package lk.nibm.swiftsalon.ui.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.viewmodel.EditProfileViewModel;

import static lk.nibm.swiftsalon.util.Constants.EMAIL_REGEX;
import static lk.nibm.swiftsalon.util.Constants.MOBILE_NUMBER_REGEX;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    public static final String INPUT_NAME = "name";
    public static final String INPUT_TYPE = "type";
    public static final String INPUT_EMAIL = "email";
    public static final String INPUT_MOBILE = "mobile";
    public static final String INPUT_ADDRESS = "address";
    public static final String INPUT_TIME = "time";
    public static final String INPUT_PASSWORD = "password";

    public static final int GENTS = 0;
    public static final int UNISEX = 1;
    public static final int LADIES = 2;

    private String edit;
    private Salon salon;
    private SimpleDateFormat dateFormat;
    private EditProfileViewModel viewModel;
    private CustomDialog dialog;

    private ImageButton btnBack;
    private TextView txtTitle, txtTitle2, txtOpenTime, txtCloseTime, txtSave, txtWarning;
    private EditText txtEdit, txtEdit2;
    private RelativeLayout btnSave;
    private SegmentedButtonGroup sgmType;
    private LinearLayout layoutTime;
    private ProgressBar prgSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        btnBack = findViewById(R.id.btn_back);
        txtTitle = findViewById(R.id.txt_title);
        txtTitle2 = findViewById(R.id.txt_title2);
        txtEdit = findViewById(R.id.txt_edit);
        txtEdit2 = findViewById(R.id.txt_edit2);
        txtWarning = findViewById(R.id.txt_warning);
        btnSave = findViewById(R.id.btn_save);
        txtSave = findViewById(R.id.btn_save_text);
        prgSave = findViewById(R.id.btn_save_progress);
        sgmType = findViewById(R.id.sgm_type);
        txtOpenTime = findViewById(R.id.time_open);
        txtCloseTime = findViewById(R.id.time_close);
        layoutTime = findViewById(R.id.layout_time);

        dateFormat = new SimpleDateFormat("HH:mm");
        dialog = new CustomDialog(EditProfileActivity.this);
        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);

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
        if (getIntent().hasExtra("salon") && getIntent().hasExtra("edit")) {

            salon = getIntent().getParcelableExtra("salon");
            edit = getIntent().getStringExtra("edit");

            if (edit.equals(INPUT_NAME)) {
                txtEdit.requestFocus();
                txtTitle.setText("Name");
                txtEdit.setText(salon.getName());
                txtEdit.setHint("Enter name");
                txtEdit.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                txtEdit.setSelection(txtEdit.getText().length());
            } else if (edit.equals(INPUT_TYPE)) {
                txtTitle.setText("Salon Type");
                showSpinner();
                if (salon.getType().toLowerCase().equals("gents")) {
                    sgmType.setPosition(GENTS, false);
                } else if (salon.getType().toLowerCase().equals("unisex")) {
                    sgmType.setPosition(UNISEX, false);
                } else {
                    sgmType.setPosition(LADIES, false);
                }

            } else if (edit.equals(INPUT_EMAIL)) {
                txtEdit.requestFocus();
                txtTitle.setText("Email");
                txtEdit.setText(salon.getEmail());
                txtEdit.setHint("Enter email");
                txtEdit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                txtEdit.setSelection(txtEdit.getText().length());
            } else if (edit.equals(INPUT_MOBILE)) {
                txtEdit.requestFocus();
                txtTitle.setText("Mobile Number");
                txtEdit.setText(salon.getMobileNo());
                txtEdit.setHint("Enter mobile");
                txtEdit.setInputType(InputType.TYPE_CLASS_PHONE);
                txtEdit.setSelection(txtEdit.getText().length());
            } else if (edit.equals(INPUT_TIME)) {
                showTimeLayout();
                String openTime = salon.getOpenTime().substring(0, salon.getOpenTime().length() - 3);
                String closeTime = salon.getCloseTime().substring(0, salon.getCloseTime().length() - 3);

                txtOpenTime.setText(openTime);
                txtCloseTime.setText(closeTime);
                showTimePicker();
            } else if (edit.equals(INPUT_ADDRESS)) {
                showAddressLayout();
                txtEdit.requestFocus();
                txtTitle.setText("Address Line 1");
                txtEdit.setText(salon.getAddr1());
                txtEdit.setHint("Enter address 1");
                txtEdit.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
                txtEdit.setSelection(txtEdit.getText().length());

                txtTitle2.setText("Address Line 2");
                txtEdit2.setText(salon.getAddr2());
                txtEdit2.setHint("Enter address 2");
                txtEdit2.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
            } else if (edit.equals(INPUT_PASSWORD)) {
                txtEdit.requestFocus();
                txtTitle.setText("Verify Password");
                btnSave.setEnabled(false);
                txtEdit.setHint("Enter current password");
                txtEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                txtEdit.setTransformationMethod(new PasswordTransformationMethod());
                txtSave.setText("Verify");
            }
        }
    }

    private void showSpinner() {
        sgmType.setVisibility(View.VISIBLE);
        txtEdit.setVisibility(View.GONE);
        layoutTime.setVisibility(View.GONE);
    }

    private void showTimeLayout() {
        sgmType.setVisibility(View.GONE);
        txtEdit.setVisibility(View.GONE);
        txtTitle.setVisibility(View.GONE);
        layoutTime.setVisibility(View.VISIBLE);
    }

    private void showAddressLayout() {
        txtTitle2.setVisibility(View.VISIBLE);
        txtEdit2.setVisibility(View.VISIBLE);
    }

    private void showTimePicker() {

        txtOpenTime.setOnClickListener(v -> {

            String txtOpen = txtOpenTime.getText().toString();
            if (txtOpen.isEmpty()) {
                txtOpen = "00:00";
            }
            Date openTime = null;
            try {
                openTime = dateFormat.parse(txtOpen);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            new SingleDateAndTimePickerDialog.Builder(this)
                    .displayMinutes(true)
                    .displayHours(true)
                    .displayDays(false)
                    .displayMonth(false)
                    .displayYears(false)
                    .displayDaysOfMonth(false)
                    .defaultDate(openTime)
                    .title("Open Time")
                    .titleTextSize(17)
                    .titleTextColor(getResources().getColor(R.color.dark))
                    .backgroundColor(getResources().getColor(R.color.dark))
                    .mainColor(getResources().getColor(R.color.light_grey))
                    .listener(date -> {
                        String time = dateFormat.format(date);
                        txtOpenTime.setText(time);
                    })
                    .display();
        });

        txtCloseTime.setOnClickListener(v -> {

            String txtClose = txtCloseTime.getText().toString();
            if (txtClose.isEmpty()) {
                txtClose = "00:00";
            }
            Date closeTime = null;
            try {
                closeTime = dateFormat.parse(txtClose);
                Log.d(TAG, "showTimePicker: close: " + closeTime.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            new SingleDateAndTimePickerDialog.Builder(this)
                    .displayMinutes(true)
                    .displayHours(true)
                    .displayDays(false)
                    .displayMonth(false)
                    .displayYears(false)
                    .displayDaysOfMonth(false)
                    .defaultDate(closeTime)
                    .title("Close Time")
                    .titleTextSize(17)
                    .titleTextColor(getResources().getColor(R.color.dark))
                    .backgroundColor(getResources().getColor(R.color.dark))
                    .mainColor(getResources().getColor(R.color.light_grey))
                    .listener(date -> {
                        String time = dateFormat.format(date);
                        txtCloseTime.setText(time);
                    })
                    .display();
        });
    }

    private void disableSave() {
        if (Arrays.asList(INPUT_NAME, INPUT_EMAIL, INPUT_MOBILE, INPUT_ADDRESS, INPUT_PASSWORD).contains(edit)) {
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
    }

    private boolean validate() {
        txtWarning.setVisibility(View.GONE);
        String text = txtEdit.getText().toString();

        if (edit.equals(INPUT_EMAIL) && !text.matches(EMAIL_REGEX)) {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText("Enter a valid email address");
            return false;
        } else if (edit.equals(INPUT_MOBILE) && !text.matches(MOBILE_NUMBER_REGEX)) {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText("Enter a valid mobile number");
            return false;
        } else if (edit.equals(INPUT_TIME)) {
            String openTime = txtOpenTime.getText().toString();
            String closeTime = txtCloseTime.getText().toString();

            try {
                if (!dateFormat.parse(openTime).before(dateFormat.parse(closeTime))) {
                    txtWarning.setVisibility(View.VISIBLE);
                    txtWarning.setText("Close time should be after Open time");
                    return false;
                }
            } catch (ParseException e) {
                Log.d(TAG, "validate: time: " + e.getMessage());
                return false;
            }

        }

        return true;
    }

    private void subscribeObservers() {
        viewModel.updateSalon().observe(this, new Observer<Resource<GenericResponse<Salon>>>() {
            @Override
            public void onChanged(Resource<GenericResponse<Salon>> resource) {

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

    private void updateApi() {
        String text = txtEdit.getText().toString().trim();

        if(isOnline()) {
            switch (edit) {

                case INPUT_NAME: {
                    salon.setName(text);
                    break;
                }

                case INPUT_EMAIL: {
                    salon.setEmail(text);
                    break;
                }

                case INPUT_MOBILE: {
                    salon.setMobileNo(text);
                    break;
                }

                case INPUT_TYPE: {
                    String type = sgmType.getButton(sgmType.getPosition()).getText();
                    salon.setType(type);
                    break;
                }

                case INPUT_ADDRESS: {
                    String text2 = txtEdit2.getText().toString().trim();
                    salon.setAddr1(text);
                    salon.setAddr2(text2);
                    break;
                }

                case INPUT_TIME: {
                    String openTime = txtOpenTime.getText().toString().trim();
                    String closeTime = txtCloseTime.getText().toString().trim();
                    salon.setOpenTime(openTime);
                    salon.setCloseTime(closeTime);
                    break;
                }

                case INPUT_PASSWORD: {
                    //update password
                    break;
                }
            }

            Log.d(TAG, "onCreate: SALON: " + salon.toString());
            viewModel.updateApi(salon);
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

}