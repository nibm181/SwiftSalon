package lk.nibm.swiftsalon.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.viewmodel.ImageViewModel;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";

    public static final int ADD = 2;
    public static final int REQUEST_GALLERY = 0;
    public static final int REQUEST_CAMERA = 1;
    private boolean IS_PERMISSION_GRANTED;

    private ImageView image;
    private ImageButton btnBack, btnEdit, btnSave, btnCloseEdit, btnGallery, btnCamera, btnRemove;
    private LinearLayout layoutEdit;
    private ProgressBar prgSave;

    private int openMode;
    private Salon salon;
    private Stylist stylist;
    private String type;
    private boolean isRemoved, isImageExist, initImageExist;

    private CustomDialog dialog;
    private RequestManager requestManager;
    private ImageViewModel viewModel;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        image = findViewById(R.id.image);
        btnBack = findViewById(R.id.btn_back);
        btnEdit = findViewById(R.id.btn_edit);
        btnCloseEdit = findViewById(R.id.btn_close_edit);
        btnSave = findViewById(R.id.btn_save);
        btnGallery = findViewById(R.id.btn_gallery);
        btnCamera = findViewById(R.id.btn_camera);
        btnRemove = findViewById(R.id.btn_remove);
        layoutEdit = findViewById(R.id.layout_edit);
        prgSave = findViewById(R.id.progress_bar);

        viewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        dialog = new CustomDialog(ImageActivity.this);
        getIncomingContents();
        subscribeObservers();

        btnBack.setOnClickListener(v -> {
            supportFinishAfterTransition();
        });

        btnEdit.setOnClickListener(v -> {
            showEdit(true);
        });

        btnCloseEdit.setOnClickListener(v -> {
            if (type.equals("salon")) {
                requestManager.load(salon.getImage())
                        .into(image);
            } else if (type.equals("stylist")) {
                requestManager.load(stylist.getImage())
                        .into(image);
            }
            else if(type.equals("add")) {
                supportFinishAfterTransition();
            }

            isImageExist = initImageExist;
            showRemove();
            showEdit(false);
        });

        btnCamera.setOnClickListener(v -> {
            openMode = REQUEST_CAMERA;
            readFile();
        });

        btnGallery.setOnClickListener(v -> {
            openMode = REQUEST_GALLERY;
            readFile();
        });

        btnSave.setOnClickListener(v -> {
            if(type.equals("add")) {
                sendImageUri();
            }
            else {
                saveImage();
            }
        });

        btnRemove.setOnClickListener(v -> {
            isRemoved = true;
            isImageExist = false;
            selectedImageUri = null;

            requestManager.load((Uri) null)
                    .into(image);

            showRemove();
        });

    }

    private void requestPermission() {
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Grant permission to upload images at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            IS_PERMISSION_GRANTED = true;
            readFile();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
        }
    };

    public void readFile(){
        if(openMode == REQUEST_CAMERA){
            if(IS_PERMISSION_GRANTED){
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, REQUEST_CAMERA);
            }else{
                requestPermission();
            }
        }else if(openMode == REQUEST_GALLERY){
            if(IS_PERMISSION_GRANTED){
                Intent pickPhoto = new Intent(Intent.ACTION_GET_CONTENT);
                pickPhoto.setType("image/*");
                startActivityForResult(pickPhoto, REQUEST_GALLERY);
            }else{
                requestPermission();
            }
        }
    }

    private void getIncomingContents() {

        if (getIntent().hasExtra("salon")) {
            type = "salon";

            salon = getIntent().getParcelableExtra("salon");
            if (salon != null) {
                requestManager = initGlide(R.drawable.sample_salon);
                requestManager.load(salon.getImage())
                        .into(image);

                if(salon.getImage() != null && !salon.getImage().equals("")) {
                    initImageExist = true;
                }
            }

        } else if (getIntent().hasExtra("stylist")) {
            type = "stylist";

            stylist = getIntent().getParcelableExtra("stylist");
            if (stylist != null) {
                requestManager = initGlide(R.drawable.sample_avatar);
                requestManager.load(stylist.getImage())
                        .into(image);

                if(stylist.getImage() != null && !stylist.getImage().equals("")) {
                    initImageExist = true;
                }
            }
        }
        else if(getIntent().hasExtra("image")) {
            type = "add";
            showAdd();

            selectedImageUri = Uri.parse(getIntent().getStringExtra("image"));
            if(selectedImageUri != null) {

                if(getIntent().hasExtra("promo")) {
                    requestManager = initGlide(R.drawable.sample_promo);
                } else {
                    requestManager = initGlide(R.drawable.sample_avatar);
                }

                requestManager.load(selectedImageUri)
                        .into(image);

                if(!selectedImageUri.toString().equals("")) {
                    initImageExist = true;
                }
            }

        }

        isImageExist = initImageExist;
        showRemove();
    }

    private void subscribeObservers() {
        viewModel.saveSalonImage().observe(this, new Observer<Resource<GenericResponse<Salon>>>() {
            @Override
            public void onChanged(Resource<GenericResponse<Salon>> resource) {
                if (resource != null) {

                    switch (resource.status) {

                        case LOADING: {
                            Log.d(TAG, "onChanged: LOADING");
                            showProgressBar(true);
                            break;
                        }

                        case ERROR: {
                            Log.d(TAG, "onChanged: ERROR");
                            showProgressBar(false);

                            if(resource.data != null) {
                                dialog.showAlert(resource.data.getMessage());
                            }
                            else {
                                dialog.showAlert(resource.message);
                            }
                            break;
                        }

                        case SUCCESS: {
                            Log.d(TAG, "onChanged: SUCCESS");

                            if (resource.data.getStatus() == 1) {

                                if (resource.data.getContent() != null) {
                                    dialog.showToast("Successfully updated");
                                    supportFinishAfterTransition();
                                } else {
                                    showProgressBar(false);
                                    dialog.showAlert("Oops! Something went wrong. Try again later.");
                                }

                            } else {
                                Log.d(TAG, "subscribeObservers: MESSAGE: " + resource.data.getMessage());
                                showProgressBar(false);
                                dialog.showAlert(resource.data.getMessage());
                            }
                            break;
                        }

                    }

                }
            }
        });

        viewModel.saveStylistImage().observe(this, resource -> {
            if (resource != null) {

                switch (resource.status) {

                    case LOADING: {
                        Log.d(TAG, "onChanged: LOADING");
                        showProgressBar(true);
                        break;
                    }

                    case ERROR: {
                        Log.d(TAG, "onChanged: ERROR");
                        showProgressBar(false);

                        if(resource.data != null) {
                            dialog.showAlert(resource.data.getMessage());
                        }
                        else {
                            dialog.showAlert(resource.message);
                        }

                        break;
                    }

                    case SUCCESS: {
                        Log.d(TAG, "onChanged: SUCCESS");

                        if (resource.data.getStatus() == 1) {

                            if (resource.data.getContent() != null) {
                                dialog.showToast("Successfully updated");

                                Intent data = new Intent();

                                stylist.setImage(resource.data.getContent().getImage());
                                data.putExtra("stylist", stylist);

                                setResult(RESULT_OK, data);
                                supportFinishAfterTransition();
                            } else {
                                showProgressBar(false);
                                dialog.showAlert("Oops! Something went wrong. Try again later.");
                            }

                        } else {
                            Log.d(TAG, "subscribeObservers: MESSAGE: " + resource.data.getMessage());
                            showProgressBar(false);
                            dialog.showAlert(resource.data.getMessage());
                        }
                        break;
                    }

                }

            }
        });
    }

    private RequestManager initGlide(int errorImageId) {
        RequestOptions options = new RequestOptions()
                .placeholder(errorImageId)
                .error(errorImageId);

        return Glide.with(getApplicationContext())
                .setDefaultRequestOptions(options);
    }

    private void showEdit(boolean show) {
        if (show) {
            layoutEdit.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
            btnCloseEdit.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.GONE);
        } else {
            layoutEdit.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
            btnCloseEdit.setVisibility(View.GONE);
            btnBack.setVisibility(View.VISIBLE);

            selectedImageUri = null;
        }
    }

    private void showAdd() {
        layoutEdit.setVisibility(View.VISIBLE);
        btnEdit.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
        btnCloseEdit.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.GONE);
    }

    private void showRemove() {
        if(!isImageExist) {
            btnRemove.setEnabled(false);
            btnRemove.setAlpha((float) 0.1);
        }
        else {
            btnRemove.setEnabled(true);
            btnRemove.setAlpha((float) 1);
        }
    }

    private void showProgressBar(boolean show) {
        if (show) {
            btnSave.setEnabled(false);
            prgSave.setVisibility(View.VISIBLE);
        } else {
            btnSave.setEnabled(true);
            prgSave.setVisibility(View.GONE);
        }
    }

    private void sendImageUri() {

        String stringUri = "";
        if(selectedImageUri != null) {
            stringUri = selectedImageUri.toString();
        }

        Log.d(TAG, "sendImageUri: URI: " + stringUri);
        Intent data = new Intent();
        data.putExtra("image", stringUri);

        setResult(RESULT_OK, data);
        supportFinishAfterTransition();
    }

    private void saveImage() {
        if (selectedImageUri != null || isRemoved) {

            if (isOnline()) {

                if (type.equals("salon")) {
                    viewModel.saveSalonImageApi(salon.getId(), selectedImageUri);
                } else if (type.equals("stylist")) {
                    viewModel.saveStylistImageApi(stylist.getId(), selectedImageUri);
                }
            } else {
                dialog.showToast("Check your connection and try again.");
            }

        } else {
            showEdit(false);
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private long calculateImageSize(Uri imageUri) {
        try {
            Cursor returnCursor = getContentResolver().
                    query(imageUri, null, null, null, null);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();

            return returnCursor.getLong(sizeIndex);
        }
        catch (Exception e) {
            return 2050;
        }
    }

    public Uri getImageUri(Bitmap inImage, int mode) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        if(mode == REQUEST_CAMERA) {
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        }
        else if(mode == REQUEST_GALLERY) {
            inImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        }

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime().getTime(), null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            
            switch (requestCode) {
                case REQUEST_CAMERA: {

                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        Uri imageUri = getImageUri(selectedImage, REQUEST_CAMERA);

                        if ((selectedImage.getByteCount() / 1024) <= 2048) {

                            selectedImageUri = imageUri;
                            requestManager
                                    .load(selectedImage).into(image);
                            isImageExist = true;

                        } else {
                            dialog.showAlert("Image size is too large. Size must be less than 2MB");
                        }
                        showRemove();
                    }
                    break;

                }

                case REQUEST_GALLERY: {

                    if (resultCode == RESULT_OK && data != null) {
                        Uri imageUri = data.getData();

                        if(type.equals("add") || Objects.equals(getContentResolver().getType(imageUri), "image/webp")) {
                            Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            imageUri = getImageUri(bitmap, REQUEST_GALLERY);
                        }

                        if ((calculateImageSize(imageUri) / 1024) <= 2048) {

                            selectedImageUri = imageUri;
                            requestManager
                                    .load(selectedImageUri).into(image);
                            isImageExist = true;

                        } else {
                            dialog.showAlert("Image size is too large. Size must be less than 2MB");
                        }
                        showRemove();
                    }
                    break;

                }
                
            }
        }
    }
}