package lk.nibm.swiftsalon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StylistAddActivity extends AppCompatActivity {

    EditText txtName, txtAge;
    RadioButton rdoMale, rdoFemale;
    RadioGroup rdoGroup;
    Button btnAdd;
    ImageButton btnImgAdd;
    ImageView imgPhoto;
    String salonNo;
    SweetAlertDialog alertDialog, pDialog;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stylist_add);
        session = new Session(getApplication());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(new String [] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        txtName = findViewById(R.id.txt_stylist_add);
        txtAge = findViewById(R.id.txt_age_add);
        rdoMale = findViewById(R.id.rdo_male);
        rdoFemale = findViewById(R.id.rdo_female);
        rdoGroup = findViewById(R.id.rdo_grp_gender);
        btnAdd = findViewById(R.id.btn_add_stylist);
        btnImgAdd = findViewById(R.id.btn_img_add);
        imgPhoto = findViewById(R.id.img_stylist_add);

        salonNo = session.getsalonNo();

        rdoMale.setChecked(true);

        btnImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog = new SweetAlertDialog(StylistAddActivity.this, SweetAlertDialog.NORMAL_TYPE);
                alertDialog.setTitleText("Choose");
                alertDialog.setConfirmText("Camera");
                alertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, 0);
                            }
                        });
                alertDialog.setCancelButton("Gallery", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto , 1);
                            }
                        });
                alertDialog.show();

                Button btnC = alertDialog.findViewById(R.id.confirm_button);
                btnC.setBackgroundColor(ContextCompat.getColor(StylistAddActivity.this,R.color.colorPrimary));

                Button btnW = alertDialog.findViewById(R.id.cancel_button);
                btnW.setBackgroundColor(ContextCompat.getColor(StylistAddActivity.this,R.color.colorPrimary));
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtName.getText().toString().trim().isEmpty()) {
                    txtName.setError("Name required.");
                }
                else if(txtAge.getText().toString().trim().isEmpty()) {
                    txtAge.setError("Age required.");
                }
                else {
                    saveStylist();
                }

            }
        });
    }

    private void saveStylist() {

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#000000"));
        pDialog.setCancelable(false);
        pDialog.show();

        int selectedId = rdoGroup.getCheckedRadioButtonId();
        final RadioButton selectedRdo = findViewById(selectedId);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest req = new StringRequest(Request.Method.POST, "https://newswiftsalon.000webhostapp.com/hairStylist.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                txtAge.setText("");
                txtName.setText("");
                rdoMale.setChecked(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("name", txtName.getText().toString().trim());
                map.put("age", txtAge.getText().toString().trim());
                map.put("gender", selectedRdo.getText().toString().trim());
                map.put("salonNo", salonNo);

                return map;
            }
        };

        requestQueue.add(req);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Bitmap srcBmp = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    Bitmap dstBmp;
                    if (srcBmp.getWidth() >= srcBmp.getHeight()){

                        dstBmp = Bitmap.createBitmap(
                                srcBmp,
                                srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                                0,
                                srcBmp.getHeight(),
                                srcBmp.getHeight()
                        );

                    }else{

                        dstBmp = Bitmap.createBitmap(
                                srcBmp,
                                0,
                                srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                                srcBmp.getWidth(),
                                srcBmp.getWidth()
                        );
                    }
                    imgPhoto.setImageBitmap(dstBmp);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    imgPhoto.setImageURI(selectedImage);
                }
                break;
        }
    }
}
