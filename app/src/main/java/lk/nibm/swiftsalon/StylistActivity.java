package lk.nibm.swiftsalon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StylistActivity extends AppCompatActivity {

    ListView listStylist;
    Button btnAdd;
    String [] img, name, hsNo, gender, age;
    SweetAlertDialog pDialog;
    Session session;
    String salonNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stylist);
        session = new Session(getApplicationContext());
        salonNo = session.getsalonNo();
        listStylist = findViewById(R.id.list_stylist);
        btnAdd = findViewById(R.id.stylist_btn_add);

        getData();

        listStylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stylistIntent = new Intent(StylistActivity.this, StylistAddActivity.class);
                startActivity(stylistIntent);
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getData();
    }

    private void getData() {

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#000000"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonArrayRequest req = new JsonArrayRequest("https://newswiftsalon.000webhostapp.com/hairStylist.php?salonNo="+salonNo, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                pDialog.dismiss();
                img = new String[response.length()];
                name = new String[response.length()];
                hsNo = new String[response.length()];
                gender = new String[response.length()];
                age = new String[response.length()];
                JSONObject jsonObject = null;

                for(int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                        img[i] = jsonObject.getString("image");
                        name[i] = jsonObject.getString("name");
                        hsNo[i] = jsonObject.getString("hsNo");
                        gender[i] = jsonObject.getString("gender");
                        age[i] = jsonObject.getString("age");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    MyStylistAdapter myStylistAdapter = new MyStylistAdapter(StylistActivity.this, img, name);
                    listStylist.setAdapter(myStylistAdapter);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(req);
    }
}
