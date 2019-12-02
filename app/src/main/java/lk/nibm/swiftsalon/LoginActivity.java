package lk.nibm.swiftsalon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    EditText txtUname, txtPwd;
    Button btnSign;
    String salonNo;
    SweetAlertDialog pDialog;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new Session(getApplicationContext());
        txtUname = findViewById(R.id.txt_uname);
        txtPwd = findViewById(R.id.txt_pwd);
        btnSign = findViewById(R.id.btn_sign);

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtUname.getText().toString().trim().isEmpty()) {
                    txtUname.setError("Enter User Name");
                }
                else if(txtPwd.getText().toString().trim().isEmpty()){
                    txtPwd.setError("Enter Password");
                }
                else{
                    pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#000000"));
                    pDialog.setTitleText("Loading");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    checkPwd();

                }
            }
        });
    }

    private void checkPwd() {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://newswiftsalon.000webhostapp.com/signInSalon.php?userName="+txtUname.getText().toString()+"&pwd="+txtPwd.getText().toString();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();
                try {
                    salonNo = response.getString("salonNo");
                    //Toast.makeText(getApplicationContext(), response.getString("salonNo"), Toast.LENGTH_SHORT).show();
                    if(response.getString("salonNo").matches("0")){
                        pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#000000"));
                        pDialog.setTitleText("Incorrect Username or Password.");
                        pDialog.show();

                        Button btnC = pDialog.findViewById(R.id.confirm_button);
                        btnC.setBackgroundColor(ContextCompat.getColor(LoginActivity.this,R.color.colorPrimary));
                    }
                    else {
                        session.setsalonNo(response.getString("salonNo"));
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(req);
    }
}
