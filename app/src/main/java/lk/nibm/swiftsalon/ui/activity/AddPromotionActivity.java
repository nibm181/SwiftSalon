package lk.nibm.swiftsalon.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import lk.nibm.swiftsalon.R;

public class AddPromotionActivity extends AppCompatActivity {

    private TextInputLayout txtDate;
    private List<Date> selectedDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promotion);

        txtDate = findViewById(R.id.txt_date);
        txtDate.getEditText().setFocusable(false);
        txtDate.getEditText().setOnKeyListener(null);

        txtDate.getEditText().setOnClickListener(v -> {
            //showDatePicker();
        });
    }

    private void showDatePicker() {

        new DoubleDateAndTimePickerDialog.Builder(this)
                //.bottomSheet()
                //.curved()
                //.stepSizeMinutes(15)
                .title("Double")
                .tab0Text("Depart")
                .tab1Text("Return")
                .listener(new DoubleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(List<Date> dates) {

                    }
                }).display();

    }
}