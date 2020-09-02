package lk.nibm.swiftsalon.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.Promotion;
import lk.nibm.swiftsalon.util.CurrencyTextWatcher;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.DateRangeValidator;
import lk.nibm.swiftsalon.util.Session;
import lk.nibm.swiftsalon.viewmodel.AddPromotionViewModel;

public class AddPromotionActivity extends AppCompatActivity {
    private static final String TAG = "AddPromotionActivity";

    private TextInputLayout txtDescription, txtOffer, txtDate;
    private TextView txtTitle;
    private CardView btnImage;
    private Button btnSave;
    private ImageButton btnBack;
    private ImageView image;

    private MaterialDatePicker.Builder<Pair<Long, Long>> builder;
    private Calendar startDate, endDate;

    private AddPromotionViewModel viewModel;
    private Uri imageUri;
    private Job job;
    private CustomDialog dialog;
    private SweetAlertDialog confirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promotion);

        txtTitle = findViewById(R.id.txt_title);
        txtDate = findViewById(R.id.txt_date);
        txtDescription = findViewById(R.id.txt_description);
        txtOffer = findViewById(R.id.txt_offer);
        btnImage = findViewById(R.id.btn_image);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
        image = findViewById(R.id.image);

        txtDate.getEditText().setFocusable(false);
        txtDate.getEditText().setOnKeyListener(null);

        // initial date range for date picker
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, 1);
        endDate.add(Calendar.WEEK_OF_MONTH, 1);

        //init date picker builder
        builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar);
        builder.setCalendarConstraints(limitRange().build());

        viewModel = new ViewModelProvider(this).get(AddPromotionViewModel.class);
        dialog = new CustomDialog(AddPromotionActivity.this);

        btnBack.setOnClickListener(v -> finish());

        txtDate.getEditText().setOnClickListener(v -> {
            showDatePicker();
        });

        btnSave.setOnClickListener(v -> {
            if (validate()) {
                saveApi();
            }
        });

        btnImage.setOnClickListener(v -> {
            String stringUri = "";

            if (imageUri != null) {
                stringUri = imageUri.toString();
            }

            Intent addImage = new Intent(AddPromotionActivity.this, ImageActivity.class);
            addImage.putExtra("image", stringUri);
            addImage.putExtra("promo", "promo");

            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this, image, "image");

            startActivityForResult(addImage, ImageActivity.ADD, options.toBundle());
        });

        getIncomingContents();
        disableSave();
        subscribeObservers();
    }

    private void showDatePicker() {

        Pair<Long, Long> datePair = new Pair<>(startDate.getTimeInMillis(), endDate.getTimeInMillis());
        builder.setSelection(datePair);

        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
        datePicker.show(getSupportFragmentManager(), datePicker.toString());

        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection.first != null && selection.second != null) {

                startDate.setTimeInMillis(selection.first);
                endDate.setTimeInMillis(selection.second);

                long diff = selection.second - selection.first;
                long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;

                String startDateStr = new SimpleDateFormat("dd/MM/yyyy").format(selection.first);
                String endDateStr = new SimpleDateFormat("dd/MM/yyyy").format(selection.second);
                String dates = startDateStr + "    -    " + endDateStr;

                txtDate.setHelperText(days + " days");
                txtDate.getEditText().setText(dates);
            }
        });

    }

    /*
       Limit selectable Date range
     */
    private CalendarConstraints.Builder limitRange() {

        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();

        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.add(Calendar.MONTH, 1);

        long minDate = calendarStart.getTimeInMillis();
        long maxDate = calendarEnd.getTimeInMillis();

        constraintsBuilderRange.setStart(minDate);
        constraintsBuilderRange.setEnd(maxDate);
        constraintsBuilderRange.setValidator(new DateRangeValidator(minDate, maxDate));

        return constraintsBuilderRange;
    }

    private void getIncomingContents() {
        if (getIntent().hasExtra("job")) {
            job = getIntent().getParcelableExtra("job");

            if (job != null) {
                String jobStr = "New Promotion - " + "(" + job.getId() + ") " + job.getName();
                txtTitle.setText(jobStr);
            }
        } else {
            finish();
        }
    }

    private void subscribeObservers() {
        viewModel.savePromotion().observe(this, resource -> {
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

                            showProgressBar(false);
                            if (resource.data.getContent() != null) {
                                dialog.showToast("Successfully promotion created");
                                Intent intent = new Intent(AddPromotionActivity.this, PromotionsActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
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

        });
    }

    private void disableSave() {
        btnSave.setEnabled(false);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtDescription.getEditText().getText().toString().trim().isEmpty()
                        || txtDate.getEditText().getText().toString().trim().isEmpty()) {
                    btnSave.setEnabled(false);
                } else {
                    btnSave.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        txtDescription.getEditText().addTextChangedListener(textWatcher);
        txtDate.getEditText().addTextChangedListener(textWatcher);
        txtOffer.getEditText().addTextChangedListener(new CurrencyTextWatcher());
    }

    private boolean validate() {
        boolean isValid = true;
        txtOffer.setError(null);

        if (imageUri == null || imageUri.toString().equals("")) {
            isValid = false;
            dialog.showAlert("Add an attractive poster image for promotion");
        } else if (!txtOffer.getEditText().getText().toString().trim().isEmpty()) {

            float offer = Float.parseFloat(txtOffer.getEditText().getText().toString().trim());
            if (offer > job.getPrice()) {
                isValid = false;
                txtOffer.setError("Offer amount cannot exceed actual price");
            } else {
                isValid = true;
            }

        }

        return isValid;
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

    private void saveApi() {
        confirmDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        confirmDialog.setTitleText("Are you sure?")
                .setContentText("You can not change promotion details after confirmed.")
                .setConfirmText("Ok")
                .setConfirmClickListener(sDialog -> {
                    if (isOnline()) {

                        String description = txtDescription.getEditText().getText().toString().trim().replaceAll("\\.*$", "");

                        float offer = 0;
                        if(!txtOffer.getEditText().getText().toString().trim().isEmpty()) {
                            offer = Float.parseFloat(txtOffer.getEditText().getText().toString().trim().replaceAll("\\.*$", ""));
                        }

                        Promotion promotion = new Promotion();
                        promotion.setJobId(job.getId());
                        promotion.setDescription(description);
                        promotion.setOffAmount(offer);
                        promotion.setStartDate(startDate.getTime());
                        promotion.setEndDate(endDate.getTime());

                        Log.d(TAG, "saveApi: PROMOTION: " + promotion.toString());
                        viewModel.saveApi(promotion, imageUri);

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

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImageActivity.ADD && resultCode == RESULT_OK) {

            if (data != null) {

                if (data.hasExtra("image")) {

                    imageUri = Uri.parse(data.getStringExtra("image"));

                    RequestOptions options = new RequestOptions()
                            .error(R.drawable.sample_promo)
                            .placeholder(R.drawable.sample_promo);

                    Glide.with(AddPromotionActivity.this)
                            .load(imageUri)
                            .apply(options)
                            .into(image);

                }
            }

        }
    }
}