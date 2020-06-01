package lk.nibm.swiftsalon.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.service.config.Session;
import lk.nibm.swiftsalon.ui.fragment.HistoryFragment;
import lk.nibm.swiftsalon.ui.fragment.HomeFragment;
import lk.nibm.swiftsalon.ui.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    String salonNo;
    public String url, txtSalon, txtImg;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new Session(getApplication());
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        salonNo=session.getSalonNo();
        getSalon();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getSalon();
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
                            selectedFrag = new ProfileFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFrag).commit();

                    return true;
                }
            };

    void getSalon() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        url = "https://newswiftsalon.000webhostapp.com/salonInfo.php?no="+salonNo;
        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    txtSalon = obj.getString("salonName");
                    txtImg = obj.getString("image");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(req);
    }
}
