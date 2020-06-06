package lk.nibm.swiftsalon.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Appointment;

public class AppointmentActivity extends AppCompatActivity {

    private static final String TAG = "AppointmentActivity";

    private TextView title, dateTime, status, customer;
    private ImageView image;
    private RecyclerView recyclerViewJobs;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        title = findViewById(R.id.txt_title);
        dateTime = findViewById(R.id.txt_date_time);
        status = findViewById(R.id.txt_status);
        customer = findViewById(R.id.txt_customer);
        image = findViewById(R.id.img_customer);
        recyclerViewJobs = findViewById(R.id.rv_jobs);
        back = findViewById(R.id.btn_back);

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
            if(appointment != null) {
                title.setText(appointment.getCustomerFirstName() + "(" + appointment.getId() + ")");
                dateTime.setText(appointment.getDate());
                status.setText(appointment.getStatus());
                customer.setText(appointment.getCustomerFirstName());
            }
        }
    }
}