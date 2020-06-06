package lk.nibm.swiftsalon.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.util.Session;

public class FirstActivity extends AppCompatActivity {


    Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        session = new Session(getApplicationContext());

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(session.getSalonId() == 0)
                {
                    Intent stylistIntent = new Intent(FirstActivity.this, LoginActivity.class);
                    startActivity(stylistIntent);
                    finish();
                }
                else
                {
                    Intent stylistIntent = new Intent(FirstActivity.this, MainActivity.class);
                    startActivity(stylistIntent);
                    finish();
                }
            }
        }, 2000);


    }

}
