package lk.nibm.swiftsalon.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.repository.SalonRepository;
import lk.nibm.swiftsalon.service.SyncWorker;
import lk.nibm.swiftsalon.util.Session;
import lk.nibm.swiftsalon.ui.fragment.HistoryFragment;
import lk.nibm.swiftsalon.ui.fragment.HomeFragment;
import lk.nibm.swiftsalon.ui.fragment.DashboardFragment;
import lk.nibm.swiftsalon.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String CHANNEL_ID = "swift_salon";
    public static final String CHANNEL_NAME = "Swift Salon";

    private Salon salon;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        subscribeObservers();
        salonApi();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFrag = null;

                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            selectedFrag = new HomeFragment();
                            break;

                        case R.id.nav_history:
                            selectedFrag = new HistoryFragment();
                            break;
                        case R.id.nav_setting:
                            selectedFrag = new DashboardFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFrag).commit();

                    return true;
                }
            };

    private void subscribeObservers() {
        viewModel.getSalon().observe(this, new Observer<Salon>() {
            @Override
            public void onChanged(Salon repositorySalon) {
                if (repositorySalon != null) {
                    salon = repositorySalon;
                }
            }
        });
    }

    private void salonApi() {
        viewModel.getSalonApi();
    }

}
