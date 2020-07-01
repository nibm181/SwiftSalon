package lk.nibm.swiftsalon.util;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;

import lk.nibm.swiftsalon.request.response.ApiResponse;
import lk.nibm.swiftsalon.request.response.GenericResponse;

public abstract class NetworkOnlyBoundResource<CacheObject, RequestObject> {

    private static final String TAG = "NetworkOnlyBoundResourc";
    private AppExecutor appExecutor;
    private MediatorLiveData<Resource<RequestObject>> results = new MediatorLiveData<>();

    public NetworkOnlyBoundResource(AppExecutor appExecutor) {
        this.appExecutor = appExecutor;
        init();
    }

    private void init() {

        //update liveData for loading status
        results.setValue((Resource<RequestObject>) Resource.loading(null));

        final LiveData<ApiResponse<RequestObject>> apiResponse = createCall();

        results.addSource(apiResponse, new Observer<ApiResponse<RequestObject>>() {
            @Override
            public void onChanged(ApiResponse<RequestObject> requestObjectApiResponse) {
                results.removeSource(apiResponse);

                if (requestObjectApiResponse instanceof ApiResponse.ApiSuccessResponse) {
                    Log.d(TAG, "onChanged: ApiSuccessResponse");

                    results.setValue(Resource.success((RequestObject) processResponse((ApiResponse.ApiSuccessResponse) requestObjectApiResponse)));
                    appExecutor.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            //save the response to the local db
                            saveCallResult((RequestObject) processResponse((ApiResponse.ApiSuccessResponse) requestObjectApiResponse));
                        }
                    });
                } else if (requestObjectApiResponse instanceof ApiResponse.ApiEmptyResponse) {
                    Log.d(TAG, "onChanged: ApiEmptyResponse");
                    results.setValue(Resource.success(null));
                } else if (requestObjectApiResponse instanceof ApiResponse.ApiErrorResponse) {
                    Log.d(TAG, "onChanged: ApiErrorResponse");
                    results.setValue(Resource.error(((ApiResponse.ApiErrorResponse) requestObjectApiResponse).getErrorMessage(), processErrorResponse((ApiResponse.ApiErrorResponse) requestObjectApiResponse)));
                }

            }
        });

    }

    private CacheObject processResponse(ApiResponse.ApiSuccessResponse response) {
        return (CacheObject) response.getBody();
    }

    private RequestObject processErrorResponse(ApiResponse.ApiErrorResponse response) {
        Log.d(TAG, "processErrorResponse: RESPONSE: " + response.getErrorMessage());
        Gson gson = new Gson();
        try {
            return (RequestObject) gson.fromJson(response.getErrorMessage(), GenericResponse.class);
        } catch (Exception e) {
            return null;
        }
    }

    // Called to create the API call.
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestObject>> createCall();

    // Called to save the result of the API response into the database.
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestObject item);

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<Resource<RequestObject>> getAsLiveData() {
        return results;
    }

}
