package lk.nibm.swiftsalon.viewmodel;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import lk.nibm.swiftsalon.model.Promotion;
import lk.nibm.swiftsalon.repository.PromotionRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Resource;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddPromotionViewModel extends AndroidViewModel {
    private static final String TAG = "AddPromotionViewModel";

    private Application application;
    private PromotionRepository repository;
    private MediatorLiveData<Resource<GenericResponse<Promotion>>> promotion = new MediatorLiveData<>();

    private boolean isFetching;

    public AddPromotionViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        repository = PromotionRepository.getInstance(application);
    }

    public LiveData<Resource<GenericResponse<Promotion>>> savePromotion() {
        return promotion;
    }

    public void saveApi(Promotion objPromotion, Uri imageUri) {
        if (!isFetching) {
            executeSave(objPromotion, getUploadImagePart(imageUri));
        }
    }

    private void executeSave(Promotion objPromotion, MultipartBody.Part image) {
        isFetching = true;

        final LiveData<Resource<GenericResponse<Promotion>>> repositorySource = repository.savePromotionApi(objPromotion, image);

        promotion.addSource(repositorySource, resource -> {
            if (resource != null) {
                promotion.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS) {
                    isFetching = false;
                } else if (resource.status == Resource.Status.ERROR) {
                    isFetching = false;
                    promotion.removeSource(repositorySource);
                }
            } else {
                promotion.removeSource(repositorySource);
            }
        });
    }

    private MultipartBody.Part getUploadImagePart(Uri imageUri) {
        File file = null;
        try {
            ParcelFileDescriptor parcelFileDescriptor = application.getApplicationContext()
                    .getContentResolver()
                    .openFileDescriptor(imageUri, "r", null);

            InputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            file = new File(getApplication().getCacheDir(), getFileName(imageUri));
            OutputStream outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (file != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            return MultipartBody.Part.createFormData("userfile", file.getName(), requestBody);
        } else {
            return null;
        }
    }

    private String getFileName(Uri uri) {

        String name = "";
        if (uri != null) {

            Cursor cursor = application.getApplicationContext()
                    .getContentResolver()
                    .query(uri, null, null, null, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                cursor.moveToFirst();
                name = cursor.getString(nameIndex);
                cursor.close();
            }

        }
        return name;
    }
}
