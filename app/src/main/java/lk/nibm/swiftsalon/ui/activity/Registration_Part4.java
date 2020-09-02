package lk.nibm.swiftsalon.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Salon;

public class Registration_Part4 extends AppCompatActivity {

    public static final int OPEN_LOCATION = 1;
    private static final String TAG = "Registration_Part4";

    TextInputLayout location,open_time,close_time,password,confirm_password;
    SimpleDateFormat dateFormat;
    Button register;
    Salon salon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration__part4);
        getIntentContents();
        location=findViewById(R.id.txt_location);
        open_time=findViewById(R.id.time_open);
        close_time=findViewById(R.id.time_close);
        password=findViewById(R.id.txt_pwd);
        confirm_password=findViewById(R.id.txt_cpwd);
        register=findViewById(R.id.btn_register);
        dateFormat = new SimpleDateFormat("HH:mm");

        open_time.getEditText().setFocusable(false);
        close_time.getEditText().setFocusable(false);
        location.getEditText().setFocusable(false);

        location.getEditText().setOnClickListener(v -> {
            Intent intent = new Intent(Registration_Part4.this, MapsActivity.class);
            startActivityForResult(intent, OPEN_LOCATION);
        });

        open_time.getEditText().setOnClickListener(v -> {
            String txtOpen = open_time.getEditText().getText().toString();
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
                        open_time.getEditText().setText(time);
                    })
                    .display();
        });

        close_time.getEditText().setOnClickListener(v -> {
            String txtClose = close_time.getEditText().getText().toString();
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
                        close_time.getEditText().setText(time);
                    })
                    .display();
        });
       register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               location.setError(null);
               open_time.setError(null);
               close_time.setError(null);
               password.setError(null);
               confirm_password.setError(null);
               if (location.getEditText().getText().toString().trim().isEmpty()) {
                   location.setError("Please select the location");
               } else if (open_time.getEditText().getText().toString().trim().isEmpty()) {
                   open_time.setError("Please select the open time");
               } else if (close_time.getEditText().getText().toString().trim().isEmpty()) {
                  close_time.setError("please select the close time");
               } else if (password.getEditText().getText().toString().trim().isEmpty()) {
                   password.setError("Please enter password");
               } else if (password.getEditText().getText().toString().trim().length()<8) {
                   password.setError("password too short");
               } else if (confirm_password.getEditText().getText().toString().trim().isEmpty()) {
                 confirm_password.setError("Please confirm the password");
               }  else if (!password.getEditText().getText().toString().trim().equals(confirm_password.getEditText().getText().toString().trim())) {
                   confirm_password.setError("confirmed password was wrong");
               } else {

//                   salon.setName(txtname.getEditText().getText().toString().trim());
//                   salon.setType(dropdownType.getText().toString());
//                   salon.setMobileNo(txtmobile.getEditText().getText().toString());
//                   salon.setAddr1(txtadd1.getEditText().getText().toString());
//                   salon.setAddr2(txtadd2.getEditText().getText().toString());
//                   Intent intent = new Intent(Registration_Part3.this, Registration_Part4.class);
//
//                   intent.putExtra("salon", salon);
//                   startActivity(intent);
                   Toast.makeText(Registration_Part4.this,"This is my Toast message!", Toast.LENGTH_LONG).show();
               }
           }
       });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == OPEN_LOCATION) {
            if(data.hasExtra("location")) {
                String address = data.getStringExtra("location");
                Double longitude = data.getDoubleExtra("longitude", 0);
                Double latitude = data.getDoubleExtra("latitude", 0);
                location.getEditText().setText(address);

                salon.setLongitude(longitude);
                salon.setLatitude(latitude);

            }
        }
    }

    private void getIntentContents() {
        if(getIntent().hasExtra("salon")) {
            salon = getIntent().getParcelableExtra("salon");
            Log.d(TAG, "getIntentContents: Salon: " + salon.toString());
        }
    }
}