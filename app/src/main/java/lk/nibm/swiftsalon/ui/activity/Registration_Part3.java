package lk.nibm.swiftsalon.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputLayout;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Salon;

public class Registration_Part3 extends AppCompatActivity {

    private static final String TAG = "Registration_Part3";
    private TextInputLayout txtName, txtMobile, txtAdd1, txtAdd2, Type;
    private AutoCompleteTextView dropdownType;
    private RelativeLayout btnImage;
    private ImageView imgSalon;
    private String[] arrayType = new String[]{
            "Gents", "Ladies", "unisex"};
    private Button btnNext;
    private Uri imageUri;
    private String stringUri = "";
    Salon salon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration__part3);
        imgSalon = findViewById(R.id.img_salon);
        btnImage = findViewById(R.id.layout_img);
        txtName = findViewById(R.id.txt_name);
        dropdownType = findViewById(R.id.dropdown_type);
        Type = findViewById(R.id.txt_type);
        txtMobile = findViewById(R.id.txt_mobileNo);
        txtAdd1 = findViewById(R.id.txt_addr1);
        txtAdd2 = findViewById(R.id.txt_addr2);
        btnNext = findViewById(R.id.btn_nxt);

        salon = new Salon();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        Registration_Part3.this,
                        R.layout.dropdown_menu_popup_item,
                        arrayType);

        dropdownType.setAdapter(adapter);

        btnImage.setOnClickListener(v -> {
            if (imageUri != null) {
                stringUri = imageUri.toString();
            }

            Intent editImage = new Intent(Registration_Part3.this, ImageActivity.class);
            editImage.putExtra("image", stringUri);

            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this, imgSalon, "image");

            startActivityForResult(editImage, ImageActivity.ADD, options.toBundle());

        });

        btnNext.setOnClickListener(v -> {
            openNextActivity();
        });
        getIncomingContent();
    }

    private void getIncomingContent() {
        if (getIntent().hasExtra("email")) {
            salon.setEmail(getIntent().getStringExtra("email"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImageActivity.ADD && resultCode == RESULT_OK) {

            if (data != null) {

                if (data.hasExtra("image")) {

                    imageUri = Uri.parse(data.getStringExtra("image"));

                    Glide.with(Registration_Part3.this)
                            .load(imageUri)
                            .apply(RequestOptions
                                    .circleCropTransform()
                                    .placeholder(R.drawable.sample_avatar)
                                    .error(R.drawable.sample_avatar)
                            )
                            .into(imgSalon);

                }
            }

        }
    }

    private void openNextActivity() {
        txtName.setError(null);
        Type.setError(null);
        txtMobile.setError(null);
        txtAdd1.setError(null);
        txtAdd2.setError(null);

        if (txtName.getEditText().getText().toString().trim().isEmpty()) {
            txtName.setError("Please enter name.");
        } else if (dropdownType.getText().toString().isEmpty()) {
            Type.setError("Please select salon type");
        } else if (txtMobile.getEditText().getText().toString().trim().isEmpty()) {
            txtMobile.setError("Please enter mobile no.");
        } else if (txtMobile.getEditText().getText().toString().trim().length() != 10) {
            txtMobile.setError("invalid mobile number");
        } else if (txtAdd1.getEditText().getText().toString().trim().isEmpty()) {
            txtAdd1.setError("Please enter address line1.");
        } else if (txtAdd2.getEditText().getText().toString().trim().isEmpty()) {
            txtAdd2.setError("Please enter address line2.");
        } else {
            if (imageUri == null) {
                salon.setImage("");
            } else {
                salon.setImage(imageUri.toString());
            }
            salon.setName(txtName.getEditText().getText().toString().trim());
            salon.setType(dropdownType.getText().toString());
            salon.setMobileNo(txtMobile.getEditText().getText().toString());
            salon.setAddr1(txtAdd1.getEditText().getText().toString());
            salon.setAddr2(txtAdd2.getEditText().getText().toString());

            Intent intent = new Intent(Registration_Part3.this, Registration_Part4.class);

            intent.putExtra("salon", salon);
            startActivity(intent);
        }
    }
}