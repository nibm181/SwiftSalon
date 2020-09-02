package lk.nibm.swiftsalon.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Earnings;
import lk.nibm.swiftsalon.model.StylistEarning;
import lk.nibm.swiftsalon.util.ChartValueFormatter;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.viewmodel.EarningsViewModel;

public class EarningsActivity extends AppCompatActivity {
    private static final String TAG = "EarningsActivity";

    private PieChart chart;
    private CardView cardDay, cardMonth, cardStylist;
    private TextView txtDay, txtDayEarning, txtDayAppointments, txtMonth, txtMonthEarning, txtMonthAppointments, txtStylist, txtDayMsg, txtMonthMsg, txtStylistMsg;
    private ImageButton btnBack, btnDayPrevious, btnDayNext, btnMonthPrevious, btnMonthNext, btnStylistPrevious, btnStylistNext;
    private ProgressBar prgDay, prgMonth, prgStylist;

    private EarningsViewModel viewModel;
    private CustomDialog dialog;
    private Calendar dayDate, monthDate, stylistDate, currentDate;
    private ArrayList<PieEntry> pieEntries;
    private PieDataSet dataSet;

    private MaterialDatePicker.Builder<Long> datePickerBuilder;
    private CalendarConstraints.Builder constraintsBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings);

        chart = findViewById(R.id.pie_chart);
        txtDay = findViewById(R.id.txt_day);
        txtDayEarning = findViewById(R.id.txt_day_earning);
        txtDayAppointments = findViewById(R.id.txt_day_appointments);
        txtMonth = findViewById(R.id.txt_month);
        txtMonthEarning = findViewById(R.id.txt_month_earning);
        txtMonthAppointments = findViewById(R.id.txt_month_appointments);
        cardDay = findViewById(R.id.card_day);
        cardMonth = findViewById(R.id.card_month);
        cardStylist = findViewById(R.id.card_stylist);
        btnBack = findViewById(R.id.btn_back);
        btnDayPrevious = findViewById(R.id.btn_day_previous);
        btnDayNext = findViewById(R.id.btn_day_next);
        btnMonthPrevious = findViewById(R.id.btn_month_previous);
        btnMonthNext = findViewById(R.id.btn_month_next);
        btnStylistPrevious = findViewById(R.id.btn_stylist_previous);
        btnStylistNext = findViewById(R.id.btn_stylist_next);
        txtStylist = findViewById(R.id.txt_stylist);
        txtMonthMsg = findViewById(R.id.txt_month_msg);
        txtDayMsg = findViewById(R.id.txt_day_msg);
        txtStylistMsg = findViewById(R.id.txt_stylist_msg);
        prgDay = findViewById(R.id.prg_day);
        prgMonth = findViewById(R.id.prg_month);
        prgStylist = findViewById(R.id.prg_stylist);

        viewModel = new ViewModelProvider(this).get(EarningsViewModel.class);
        dialog = new CustomDialog(EarningsActivity.this);

        btnBack.setOnClickListener(v -> finish());

        txtDay.setOnClickListener(v -> {
            showDatePicker();
        });

        initDates();
        initChart();
        subscribeObservers();
        initDatePicker();
    }

    private void initDates() {

        currentDate = Calendar.getInstance();
        dayDate = Calendar.getInstance();
        monthDate = Calendar.getInstance();
        stylistDate = Calendar.getInstance();

        btnDayNext.setVisibility(View.GONE);
        btnMonthNext.setVisibility(View.GONE);
        btnStylistNext.setVisibility(View.GONE);

        viewModel.MonthlyEarningsApi(dayDate.getTime());
        viewModel.DailyEarningsApi(dayDate.getTime());
        viewModel.StylistEarningsApi(stylistDate.getTime());

        //Daily earnings
        btnDayPrevious.setOnClickListener(v -> {
            dayDate.add(Calendar.DAY_OF_MONTH, -1);
            btnDayNext.setVisibility(View.VISIBLE);
            viewModel.DailyEarningsApi(dayDate.getTime());
        });

        btnDayNext.setOnClickListener(v -> {
            dayDate.add(Calendar.DAY_OF_MONTH, 1);
            if (isSameDay(currentDate, dayDate)) {
                btnDayNext.setVisibility(View.GONE);
            }
            viewModel.DailyEarningsApi(dayDate.getTime());
        });

        //Monthly earnings
        btnMonthPrevious.setOnClickListener(v -> {
            monthDate.add(Calendar.MONTH, -1);
            btnMonthNext.setVisibility(View.VISIBLE);
            viewModel.MonthlyEarningsApi(monthDate.getTime());
        });

        btnMonthNext.setOnClickListener(v -> {
            monthDate.add(Calendar.MONTH, 1);
            if (isSameMonth(currentDate, monthDate)) {
                btnMonthNext.setVisibility(View.GONE);
            }
            viewModel.MonthlyEarningsApi(monthDate.getTime());
        });

        //Earnings by stylist
        btnStylistPrevious.setOnClickListener(v -> {
            stylistDate.add(Calendar.MONTH, -1);
            btnStylistNext.setVisibility(View.VISIBLE);
            viewModel.StylistEarningsApi(stylistDate.getTime());
        });

        btnStylistNext.setOnClickListener(v -> {
            stylistDate.add(Calendar.MONTH, 1);
            if (isSameMonth(currentDate, stylistDate)) {
                btnStylistNext.setVisibility(View.GONE);
            }
            viewModel.StylistEarningsApi(stylistDate.getTime());
        });
    }

    private void subscribeObservers() {
        viewModel.getMonthlyEarning().observe(this, resource -> {
            Log.d(TAG, "onChanged: monthly resource: " + resource.status);
            switch (resource.status) {

                case LOADING: {
                    Log.d(TAG, "onChanged: LOADING");
                    showMonthlyProgressBar(true);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
                    String monthYear = dateFormat.format(monthDate.getTime());
                    txtMonth.setText(monthYear);
                    break;
                }

                case ERROR: {
                    Log.d(TAG, "onChanged: ERROR");
                    Log.d(TAG, "onChanged: ERROR MSG: " + resource.data);
                    showMonthlyProgressBar(false);

                    if (resource.data != null) {
                        dialog.showAlert(resource.data.getMessage());
                    }
                    showMonthMsg("We could not connect you with us.");
                    break;
                }

                case SUCCESS: {
                    Log.d(TAG, "onChanged: SUCCESS");
                    showMonthlyProgressBar(false);

                    if (resource.data.getStatus() == 1) {

                        if (resource.data.getContent() != null) {
                            Earnings earnings = resource.data.getContent();
                            String earning = "Rs. " + earnings.getEarning();
                            String count = earnings.getCount() + " Appointments";

                            txtMonthEarning.setText(earning);
                            txtMonthAppointments.setText(count);
                        } else {
                            showMonthMsg("Oops! Something went wrong.");
                        }

                    } else {
                        showMonthMsg("Not enough data found");
                    }
                    break;
                }

            }
        });

        viewModel.getDailyEarning().observe(this, resource -> {
            Log.d(TAG, "onChanged: daily resource: " + resource.status);
            switch (resource.status) {

                case LOADING: {
                    Log.d(TAG, "onChanged: LOADING");
                    showDailyProgressBar(true);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String date = dateFormat.format(dayDate.getTime());
                    txtDay.setText(date);
                    break;
                }

                case ERROR: {
                    Log.d(TAG, "onChanged: ERROR");
                    Log.d(TAG, "onChanged: ERROR MSG: " + resource.data);
                    showDailyProgressBar(false);

                    if (resource.data != null) {
                        dialog.showAlert(resource.data.getMessage());
                    }
                    showDayMsg("We could not connect you with us.");
                    break;
                }

                case SUCCESS: {
                    Log.d(TAG, "onChanged: SUCCESS");
                    showDailyProgressBar(false);

                    if (resource.data.getStatus() == 1) {

                        if (resource.data.getContent() != null) {
                            Earnings earnings = resource.data.getContent();
                            String earning = "Rs. " + earnings.getEarning();
                            String count = earnings.getCount() + " Appointments";

                            txtDayEarning.setText(earning);
                            txtDayAppointments.setText(count);
                        } else {
                            showDayMsg("Oops! Something went wrong.");
                        }

                    } else {
                        showDayMsg("Not enough data found");
                    }
                    break;
                }

            }
        });

        viewModel.getStylistEarning().observe(this, resource -> {
            Log.d(TAG, "onChanged: stylist resource: " + resource.status);
            switch (resource.status) {

                case LOADING: {
                    Log.d(TAG, "onChanged: LOADING");
                    showStylistProgressBar(true);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
                    String date = dateFormat.format(stylistDate.getTime());
                    txtStylist.setText(date);
                    break;
                }

                case ERROR: {
                    Log.d(TAG, "onChanged: ERROR");
                    Log.d(TAG, "onChanged: ERROR MSG: " + resource.data);
                    showStylistProgressBar(false);

                    if (resource.data != null) {
                        dialog.showAlert(resource.data.getMessage());
                    }
                    showStylistMsg("We could not connect you with us.");
                    break;
                }

                case SUCCESS: {
                    Log.d(TAG, "onChanged: SUCCESS");
                    showStylistProgressBar(false);

                    if (resource.data.getStatus() == 1) {

                        if (resource.data.getContent() != null) {
                            setDataValue(resource.data.getContent());
                        } else {
                            showStylistMsg("Oops! Something went wrong.");
                        }

                    } else {
                        showStylistMsg("Not enough data found");
                    }
                    break;
                }

            }
        });
    }

    private void initChart() {

        int[] colors = new int[15];
        int counter = 0;

        for (int color : ColorTemplate.JOYFUL_COLORS) {
            colors[counter] = color;
            counter++;
        }
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors[counter] = color;
            counter++;
        }
        for (int color : ColorTemplate.PASTEL_COLORS) {
            colors[counter] = color;
            counter++;
        }

        if(pieEntries == null) {
            pieEntries = new ArrayList<>();
        }

        dataSet = new PieDataSet(pieEntries, "");

        dataSet.setColors(colors);
        dataSet.setValueTextSize(13);
        dataSet.setValueTextColor(getResources().getColor(R.color.dark));

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new ChartValueFormatter(chart));

        chart.setData(pieData);
        chart.setUsePercentValues(true);
        chart.setDrawEntryLabels(false);
        chart.setHoleRadius(30);
        chart.setCenterText("stylists");
        chart.setCenterTextRadiusPercent(80);
        chart.setCenterTextSize(14);
        chart.setTransparentCircleRadius(45);
        chart.setDescription(null);
        chart.setEntryLabelColor(getResources().getColor(R.color.white));

        Legend legend = chart.getLegend();
        legend.setTextColor(getResources().getColor(R.color.white));
        legend.setWordWrapEnabled(true);

        chart.animateX(1000, Easing.EaseOutSine);
        chart.invalidate();

    }

    private void setDataValue(List<StylistEarning> stylistEarnings) {

        pieEntries = new ArrayList<>();
        for(StylistEarning stylistEarning : stylistEarnings) {
            if(stylistEarning.getStylistEarning() > 0)
                pieEntries.add(new PieEntry(stylistEarning.getStylistEarning(), stylistEarning.getStylistName()));
        }

        dataSet.setValues(pieEntries);
        chart.notifyDataSetChanged();
    }

    private void initDatePicker() {
        datePickerBuilder = MaterialDatePicker.Builder.datePicker();
        datePickerBuilder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar);
        constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointBackward.before(currentDate.getTimeInMillis()));
    }

    private void showDatePicker() {
        datePickerBuilder.setSelection(dayDate.getTimeInMillis());
        datePickerBuilder.setCalendarConstraints(constraintsBuilder.setOpenAt(dayDate.getTimeInMillis()).build());
        MaterialDatePicker<Long> datePicker = datePickerBuilder.build();
        datePicker.show(getSupportFragmentManager(), datePicker.toString());

        datePicker.addOnPositiveButtonClickListener(selection -> {
            dayDate.setTimeInMillis(selection);
            if (isSameDay(currentDate, dayDate)) {
                btnDayNext.setVisibility(View.GONE);
            }
            else {
                btnDayNext.setVisibility(View.VISIBLE);
            }
            viewModel.DailyEarningsApi(dayDate.getTime());
        });
    }

    private void showMonthlyProgressBar(boolean show) {
        btnMonthNext.setEnabled(!show);
        btnMonthPrevious.setEnabled(!show);

        txtMonthMsg.setVisibility(View.GONE);
        if (show) {
            prgMonth.setVisibility(View.VISIBLE);
            txtMonthEarning.setVisibility(View.GONE);
            txtMonthAppointments.setVisibility(View.GONE);
        } else {
            prgMonth.setVisibility(View.GONE);
            txtMonthEarning.setVisibility(View.VISIBLE);
            txtMonthAppointments.setVisibility(View.VISIBLE);
        }
    }

    private void showDailyProgressBar(boolean show) {
        btnDayNext.setEnabled(!show);
        btnDayPrevious.setEnabled(!show);

        txtDayMsg.setVisibility(View.GONE);
        if (show) {
            prgDay.setVisibility(View.VISIBLE);
            txtDayEarning.setVisibility(View.GONE);
            txtDayAppointments.setVisibility(View.GONE);
        } else {
            prgDay.setVisibility(View.GONE);
            txtDayEarning.setVisibility(View.VISIBLE);
            txtDayAppointments.setVisibility(View.VISIBLE);
        }
    }

    private void showStylistProgressBar(boolean show) {
        btnStylistNext.setEnabled(!show);
        btnStylistPrevious.setEnabled(!show);

        txtStylistMsg.setVisibility(View.GONE);
        if (show) {
            prgStylist.setVisibility(View.VISIBLE);
            chart.setVisibility(View.GONE);
        } else {
            prgStylist.setVisibility(View.GONE);
            chart.setVisibility(View.VISIBLE);
        }
    }

    private void showMonthMsg(String msg) {
        prgMonth.setVisibility(View.GONE);
        txtMonthEarning.setVisibility(View.GONE);
        txtMonthAppointments.setVisibility(View.GONE);
        txtMonthMsg.setVisibility(View.VISIBLE);
        txtMonthMsg.setText(msg);
    }

    private void showDayMsg(String msg) {
        prgDay.setVisibility(View.GONE);
        txtDayEarning.setVisibility(View.GONE);
        txtDayAppointments.setVisibility(View.GONE);
        txtDayMsg.setVisibility(View.VISIBLE);
        txtDayMsg.setText(msg);
    }

    private void showStylistMsg(String msg) {
        chart.setVisibility(View.GONE);
        txtStylistMsg.setVisibility(View.VISIBLE);
        txtStylistMsg.setText(msg);
    }

    public boolean isSameMonth(Calendar cal1, Calendar cal2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
        return dateFormat.format(cal1.getTime()).equals(dateFormat.format(cal2.getTime()));
    }

    public boolean isSameDay(Calendar cal1, Calendar cal2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(cal1.getTime()).equals(dateFormat.format(cal2.getTime()));
    }
}