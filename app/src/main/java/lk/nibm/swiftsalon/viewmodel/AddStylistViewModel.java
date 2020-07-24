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
import java.util.HashMap;
import java.util.List;

import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.repository.StylistRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Resource;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddStylistViewModel extends AndroidViewModel {
    private static final String TAG = "AddStylistViewModel";

    private Application application;
    private StylistRepository repository;
    private MediatorLiveData<Resource<GenericResponse<Stylist>>> stylist = new MediatorLiveData<>();

    private boolean isFetching;

    public AddStylistViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        repository = StylistRepository.getInstance(application);
    }

    public LiveData<Resource<GenericResponse<Stylist>>> saveStylist() {
        return stylist;
    }

    public void saveApi(Stylist objStylist, List<Job> objJobs, Uri imageUri) {
        if (!isFetching) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("stylist", objStylist);
            hashMap.put("jobs", objJobs);

            executeSave(hashMap, getUploadImagePart(imageUri));
        }
    }

    private void executeSave(HashMap<String, Object> hashMap, MultipartBody.Part image) {
        isFetching = true;

        final LiveData<Resource<GenericResponse<Stylist>>> repositorySource = repository.saveStylistApi(hashMap, image);

        stylist.addSource(repositorySource, resource -> {
            if (resource != null) {
                stylist.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS) {
                    isFetching = false;
                } else if (resource.status == Resource.Status.ERROR) {
                    isFetching = false;
                    stylist.removeSource(repositorySource);
                }
            } else {
                stylist.removeSource(repositorySource);
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

        if(file != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            return MultipartBody.Part.createFormData("userfile", file.getName(), requestBody);
        }
        else {
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
