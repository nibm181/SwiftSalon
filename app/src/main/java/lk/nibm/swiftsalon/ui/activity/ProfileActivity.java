package lk.nibm.swiftsalon.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import cn.pedant.SweetAlert.SweetAlertDialog;
import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.util.CapitalizedTextView;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.Session;
import lk.nibm.swiftsalon.viewmodel.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton btnBack, btnEditImage;
    private LinearLayout btnName, btnType, btnEmail, btnMobile, btnAddress, btnLocation, btnTime, btnPassword, btnLogout;
    private TextView txtName, txtEmail, txtMobile, txtAddress, txtLocation, txtTime;
    private CapitalizedTextView txtType;
    private ImageView imgSalon;


    private CustomDialog dialog;
    private ProfileViewModel viewModel;

    private Salon salon;

    public Salon getSalon() {
        return salon;
    }

    private void setSalon(Salon salon) {
        this.salon = salon;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnBack = findViewById(R.id.btn_back);
        btnEditImage = findViewById(R.id.btn_edit_image);
        btnName = findViewById(R.id.btn_name);
        btnType = findViewById(R.id.btn_type);
        btnEmail = findViewById(R.id.btn_email);
        btnMobile = findViewById(R.id.btn_mobile);
        btnAddress = findViewById(R.id.btn_address);
        btnLocation = findViewById(R.id.btn_location);
        btnTime = findViewById(R.id.btn_time);
        btnPassword = findViewById(R.id.btn_password);
        btnLogout = findViewById(R.id.btn_logout);
        txtName = findViewById(R.id.txt_name);
        txtType = findViewById(R.id.txt_type);
        txtEmail = findViewById(R.id.txt_email);
        txtMobile = findViewById(R.id.txt_mobile);
        txtAddress = findViewById(R.id.txt_address);
        txtLocation = findViewById(R.id.txt_location);
        txtTime = findViewById(R.id.txt_time);
        imgSalon = findViewById(R.id.img_salon);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        dialog = new CustomDialog(ProfileActivity.this);

        btnBack.setOnClickListener(v -> finish());

        btnName.setOnClickListener(v -> {
            Intent editProfile = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editProfile.putExtra("salon", getSalon());
            editProfile.putExtra("edit", EditProfileActivity.INPUT_NAME);

            startActivity(editProfile);
        });

        btnType.setOnClickListener(v -> {
            Intent editProfile = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editProfile.putExtra("salon", getSalon());
            editProfile.putExtra("edit", EditProfileActivity.INPUT_TYPE);

            startActivity(editProfile);
        });

        btnEmail.setOnClickListener(v -> {
            Intent editProfile = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editProfile.putExtra("salon", getSalon());
            editProfile.putExtra("edit", EditProfileActivity.INPUT_EMAIL);

            startActivity(editProfile);
        });

        btnMobile.setOnClickListener(v -> {
            Intent editProfile = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editProfile.putExtra("salon", getSalon());
            editProfile.putExtra("edit", EditProfileActivity.INPUT_MOBILE);

            startActivity(editProfile);
        });

        btnTime.setOnClickListener(v -> {
            Intent editProfile = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editProfile.putExtra("salon", getSalon());
            editProfile.putExtra("edit", EditProfileActivity.INPUT_TIME);

            startActivity(editProfile);
        });

        btnAddress.setOnClickListener(v -> {
            Intent editProfile = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editProfile.putExtra("salon", getSalon());
            editProfile.putExtra("edit", EditProfileActivity.INPUT_ADDRESS);

            startActivity(editProfile);
        });

        btnPassword.setOnClickListener(v -> {
            Intent editProfile = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editProfile.putExtra("salon", getSalon());
            editProfile.putExtra("edit", EditProfileActivity.INPUT_PASSWORD);

            startActivity(editProfile);
        });

        btnEditImage.setOnClickListener(v -> {

        });

        btnLocation.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MapsActivity.class);
            intent.putExtra("salon", getSalon());

            startActivity(intent);
        });

        imgSalon.setOnClickListener(v -> {
            Intent editImage = new Intent(ProfileActivity.this, ImageActivity.class);
            editImage.putExtra("salon", getSalon());

            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this, (View) imgSalon, "image");

            startActivity(editImage, options.toBundle());
        });

        btnLogout.setOnClickListener(v -> {
            logout();
        });


        subscribeObservers(initGlide());
        salonApi();
    }

    private void subscribeObservers(RequestManager requestManager) {
        viewModel.getSalon().observe(this, salonResource -> {
            if (salonResource != null) {
                if (salonResource.data != null) {

                    switch (salonResource.status) {

                        case LOADING: {
                            break;
                        }

                        case ERROR: {
                            dialog.showToast(salonResource.message);
                            setSalon(salonResource.data);
                            setValue(salonResource.data, requestManager);
                            break;
                        }

                        case SUCCESS: {
                            setSalon(salonResource.data);
                            setValue(salonResource.data, requestManager);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void salonApi() {
        viewModel.getSalonApi();
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.sample_salon)
                .error(R.drawable.sample_salon)
                .optionalCircleCrop();

        return Glide.with(getApplicationContext())
                .setDefaultRequestOptions(options);
    }

    private void setValue(Salon salon, RequestManager requestManager) {
        String openTime = salon.getOpenTime().substring(0, salon.getOpenTime().length() - 3);
        String closeTime = salon.getCloseTime().substring(0, salon.getCloseTime().length() - 3);

        txtName.setText(salon.getName());
        txtType.setText(salon.getType());
        txtEmail.setText(salon.getEmail());
        txtMobile.setText(salon.getMobileNo());
        txtAddress.setText((salon.getAddr1() + ", " + salon.getAddr2()));
        txtLocation.setText(salon.getLocation());
        txtTime.setText((openTime + " - " + closeTime));

        requestManager
                .load(salon.getImage())
                .into(imgSalon);
    }

    private void logout() {
        SweetAlertDialog confirmDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        confirmDialog.setTitleText("Are you sure you want to logout?")
                .setContentText("")
                .setConfirmText("Ok")
                .setConfirmClickListener(sDialog -> {
                    viewModel.clearToken();
                    Session session = new Session(ProfileActivity.this);
                    session.clearSession();

                    Intent login = new Intent(ProfileActivity.this, LoginActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(login);
                })
                .setCancelButton("Cancel", SweetAlertDialog::dismissWithAnimation)
                .show();

        Button btnConfirm = confirmDialog.findViewById(R.id.confirm_button);
        Button btnCancel = confirmDialog.findViewById(R.id.cancel_button);
        btnConfirm.setBackgroundResource(R.drawable.button_shape);
        btnCancel.setBackgroundResource(R.drawable.button_shape);
    }
}