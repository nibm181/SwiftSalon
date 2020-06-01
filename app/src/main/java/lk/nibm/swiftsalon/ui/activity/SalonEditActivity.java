package lk.nibm.swiftsalon.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.service.config.Session;

public class SalonEditActivity extends AppCompatActivity {

    EditText txtName, txtAddr1, txtAddr2, txtCity, txtMobile;
    ImageView imgPic;
    Button btnSave;
    String salonNo;
    String imgUrl;
    SweetAlertDialog pDialog;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_edit);
        session = new Session(getApplicationContext());
        txtName = findViewById(R.id.txt_salon_edit);
        txtAddr1 = findViewById(R.id.txt_add1_edit);
        txtAddr2 = findViewById(R.id.txt_add2_edit);
        txtCity = findViewById(R.id.txt_city_edit);
        txtMobile = findViewById(R.id.txt_mobile_edit);
        imgPic = findViewById(R.id.img_salon_edit);
        btnSave = findViewById(R.id.btn_save_edit);

        salonNo = session.getSalonNo();

        getData();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtName.getText().toString().trim().isEmpty()){
                    txtName.setError("Please enter name.");
                }
                else if(txtAddr1.getText().toString().trim().isEmpty()){
                    txtAddr1.setError("Please enter address 1.");
                }
                else if(txtCity.getText().toString().trim().isEmpty()){
                    txtCity.setError("Please enter city.");
                }
                else if(txtMobile.getText().toString().trim().isEmpty())
                {
                    txtMobile.setError("Please enter mobile.");
                }
                else{
                    update();
                }

            }
        });
    }

    private void getData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        String url = "https://newswiftsalon.000webhostapp.com/Salon.php?single="+salonNo;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    txtName.setText(response.getString("salonName"));
                    txtAddr1.setText(response.getString("addr1"));
                    txtAddr2.setText(response.getString("addr2"));
                    txtCity.setText(response.getString("city"));
                    txtMobile.setText(response.getString("mobileNo"));
                    imgUrl = response.getString("image");

                    Glide.with(getApplicationContext())
                            .load(imgUrl)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                            .into(imgPic);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(req);
    }

    private void update() {

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#000000"));
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        String url = "https://newswiftsalon.000webhostapp.com/editSalon.php?";

        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("salonName", txtName.getText().toString().trim());
                map.put("addr1", txtAddr1.getText().toString().trim());
                map.put("addr2", txtAddr2.getText().toString().trim());
                map.put("city", txtCity.getText().toString().trim());
                map.put("mobileNo", txtMobile.getText().toString().trim());
                map.put("salonNo", salonNo);
                return map;
            }
        };
        requestQueue.add(req);

    }
}
