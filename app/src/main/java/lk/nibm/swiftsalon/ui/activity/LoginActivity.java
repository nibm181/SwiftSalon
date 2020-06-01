package lk.nibm.swiftsalon.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.common.CustomDialog;
import lk.nibm.swiftsalon.service.config.Session;

public class LoginActivity extends AppCompatActivity {

    EditText txtEmail, txtPwd;
    RelativeLayout btnLogin;
    TextView btnRegister, txtLogin;
    ProgressBar prgLogin;
    String salonNo;
    CustomDialog dialog;
    Session session;

    ConstraintLayout constraintLayout;
    ConstraintSet currentConstraintSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new Session(getApplicationContext());
        txtEmail = findViewById(R.id.txt_uname);
        txtPwd = findViewById(R.id.txt_pwd);
        btnLogin = findViewById(R.id.btn_login);
        txtLogin = findViewById(R.id.btn_login_text);
        prgLogin = findViewById(R.id.btn_login_progress);
        btnRegister = findViewById(R.id.btn_register);

        constraintLayout = findViewById(R.id.login_parent_layout);
        currentConstraintSet = new ConstraintSet();
        currentConstraintSet.clone(constraintLayout);

        btnLogin.setEnabled(false);

        //enable or disable login button
        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtEmail.getText().toString().trim().isEmpty() || txtPwd.getText().toString().trim().isEmpty()) {
                    btnLogin.setEnabled(false);
                }
                else {
                    btnLogin.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //enable or disable login button
        txtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtEmail.getText().toString().trim().isEmpty() || txtPwd.getText().toString().trim().isEmpty()) {
                    btnLogin.setEnabled(false);
                }
                else {
                    btnLogin.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                txtLogin.setVisibility(View.GONE);
                prgLogin.setVisibility(View.VISIBLE);

                isLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

        //move the layout when keyboard appears
        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if(isOpen) {
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.txt_uname, ConstraintSet.TOP, R.id.textView2, ConstraintSet.BOTTOM,380);
                    constraintSet.applyTo(constraintLayout);
                }
                else {
                    currentConstraintSet.applyTo(constraintLayout);
                }
            }
        });

        txtPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.txt_uname, ConstraintSet.TOP, R.id.textView2, ConstraintSet.BOTTOM,380);
                constraintSet.applyTo(constraintLayout);
            }
        });
    }

    private void isLogin() {

        dialog = new CustomDialog(LoginActivity.this);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://newswiftsalon.000webhostapp.com/signInSalon.php?userName="+txtEmail.getText().toString()+"&pwd="+txtPwd.getText().toString();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    salonNo = response.getString("salonNo");
                    if(response.getString("salonNo").matches("0")){
                        dialog.showAlert("Incorrect email or password.");

                        txtLogin.setVisibility(View.VISIBLE);
                        prgLogin.setVisibility(View.GONE);
                    }
                    else {
                        session.setSalonNo(salonNo);
                        session.setSignedIn(true);

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
                dialog.showAlert("Oops! We are unable to connect you with us.");

                txtLogin.setVisibility(View.VISIBLE);
                prgLogin.setVisibility(View.GONE);
            }
        });

        if(isOnline()) {
            requestQueue.add(req);
        }
        else {
            dialog.showToast("Check your connection and try again.");

            txtLogin.setVisibility(View.VISIBLE);
            prgLogin.setVisibility(View.GONE);
        }

    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
