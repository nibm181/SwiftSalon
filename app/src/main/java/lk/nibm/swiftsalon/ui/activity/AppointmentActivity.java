package lk.nibm.swiftsalon.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Appointment;

public class AppointmentActivity extends AppCompatActivity {

    private static final String TAG = "AppointmentActivity";

    private TextView txtTitle, txtDateTime, txtStatus, txtCustomer;
    private ImageView image;
    private RecyclerView recyclerViewJobs;
    private ImageButton back;
    private LinearLayout layoutButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        txtTitle = findViewById(R.id.txt_title);
        txtDateTime = findViewById(R.id.txt_date_time);
        txtStatus = findViewById(R.id.txt_status);
        txtCustomer = findViewById(R.id.txt_customer);
        image = findViewById(R.id.img_customer);
        recyclerViewJobs = findViewById(R.id.rv_jobs);
        back = findViewById(R.id.btn_back);
        layoutButtons = findViewById(R.id.layout_buttons);

        getIncomingIntent();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("appointment")) {

            Appointment appointment = getIntent().getParcelableExtra("appointment");

            String title = appointment.getCustomerFirstName() + " (" + appointment.getId() + ")";
            String dateTime = appointment.getDate() + " " + appointment.getTime();
            String status = appointment.getStatus();
            String customer = appointment.getCustomerFirstName() + " " + appointment.getCustomerLastName();

            if(appointment != null) {
                txtTitle.setText(title);
                txtDateTime.setText(dateTime);
                txtStatus.setText(status);
                txtCustomer.setText(customer);
            }
        }
    }
}