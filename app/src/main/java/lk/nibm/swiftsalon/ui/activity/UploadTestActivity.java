package lk.nibm.swiftsalon.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.request.ServiceGenerator;
import lk.nibm.swiftsalon.util.CustomDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadTestActivity extends AppCompatActivity {
    private static final String TAG = "UploadTestActivity";

    private ImageView image;
    private Button btnUpload;
    private Uri selectedImageUri;

    private CustomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_test);

        image = findViewById(R.id.img);
        btnUpload = findViewById(R.id.btn_upload);

        dialog = new CustomDialog(UploadTestActivity.this);

        image.setOnClickListener(v -> {
            selectImage();
        });

        btnUpload.setOnClickListener(v -> {
            uploadFile();
        });
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadTestActivity.this);
        builder.setTitle("Choose your picture");

        builder.setItems(options, (dialog, item) -> {

            if (options[item].equals("Take Photo")) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);

            } else if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_GET_CONTENT);
                pickPhoto.setType("image/*");
                startActivityForResult(pickPhoto, 1);

            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        selectedImageUri = getImageUri(selectedImage);
                        image.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        selectedImageUri = data.getData();
                        image.setImageURI(selectedImageUri);
                    }
                    break;
            }
        }
    }

    private String getFileName(Uri uri) {

        String name = "";
        if (uri != null) {

            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                cursor.moveToFirst();
                name = cursor.getString(nameIndex);
                cursor.close();
            }

        }
        return name;
    }

    private void uploadFile() {

        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImageUri, "r", null);
            InputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            File file = new File(getApplicationContext().getCacheDir(), getFileName(selectedImageUri));
            OutputStream outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);

            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("userfile", file.getName(), requestBody);

            Stylist stylist = new Stylist();
            stylist.setSalonId(1);
            stylist.setName("Stylist");

            Call<ResponseBody> call = ServiceGenerator.getSalonApi().uploadImage(stylist, part);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        dialog.showAlert(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    dialog.showAlert(t.getMessage());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}