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

import lk.nibm.swiftsalon.model.Job;
import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.persistence.SwiftSalonDao;
import lk.nibm.swiftsalon.repository.SalonRepository;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.Resource;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RegistrationViewModel extends AndroidViewModel {

    private static final String TAG = "RegistrationViewModel";

    private SalonRepository repository;
    private MediatorLiveData<Resource<GenericResponse>> response = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericResponse<Salon>>> salon = new MediatorLiveData<>();
    private Application application;

    private boolean isFetching;

    public RegistrationViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        repository = SalonRepository.getInstance(application);
    }

    public LiveData<Resource<GenericResponse>> sendEmail() {
        return response;
    }

    public LiveData<Resource<GenericResponse<Salon>>> saveSalon() {
        return salon;
    }

    public void sendEmailApi(String email) {
        if(!isFetching) {
            executeFetch(email);
        }
    }

    public void saveApi(Salon objSalon) {
        if (!isFetching) {
            executeSave(objSalon, getUploadImagePart(Uri.parse(objSalon.getImage())));
        }
    }

    private void executeFetch(String email) {
        isFetching = true;

        final LiveData<Resource<GenericResponse>> repositorySource = repository.sendEmail(email);

        response.addSource(repositorySource, resource -> {
            if (resource != null) {
                response.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS) {
                    isFetching = false;
                } else if (resource.status == Resource.Status.ERROR) {
                    isFetching = false;
                    response.removeSource(repositorySource);
                }
            } else {
                response.removeSource(repositorySource);
            }
        });
    }

    private void executeSave(Salon objSalon, MultipartBody.Part image) {
        isFetching = true;

        final LiveData<Resource<GenericResponse<Salon>>> repositorySource = repository.saveSalonApi(objSalon, image);

        salon.addSource(repositorySource, resource -> {
            if (resource != null) {
                salon.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS) {
                    isFetching = false;
                } else if (resource.status == Resource.Status.ERROR) {
                    isFetching = false;
                    salon.removeSource(repositorySource);
                }
            } else {
                salon.removeSource(repositorySource);
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
