package lk.nibm.swiftsalon.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import lk.nibm.swiftsalon.model.Promotion;
import lk.nibm.swiftsalon.persistence.SwiftSalonDao;
import lk.nibm.swiftsalon.persistence.SwiftSalonDatabase;
import lk.nibm.swiftsalon.request.ServiceGenerator;
import lk.nibm.swiftsalon.request.response.ApiResponse;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.AppExecutor;
import lk.nibm.swiftsalon.util.NetworkBoundResource;
import lk.nibm.swiftsalon.util.NetworkOnlyBoundResource;
import lk.nibm.swiftsalon.util.Resource;
import okhttp3.MultipartBody;

public class PromotionRepository {

    private static final String TAG = "PromotionRepository";

    private static PromotionRepository instance;
    private SwiftSalonDao swiftSalonDao;

    public static PromotionRepository getInstance(Context context) {
        if (instance == null) {
            instance = new PromotionRepository(context);
        }
        return instance;
    }

    private PromotionRepository(Context context) {
        swiftSalonDao = SwiftSalonDatabase.getInstance(context).getDao();
    }

    public LiveData<Resource<List<Promotion>>> getPromotionsApi(int salonId) {
        return new NetworkBoundResource<List<Promotion>, GenericResponse<List<Promotion>>>(AppExecutor.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GenericResponse<List<Promotion>> item) {

                if (item.getContent() != null) {

                    Promotion[] promotions = new Promotion[item.getContent().size()];

                    //delete items for insert latest data
                    swiftSalonDao.deletePromotions(salonId);

                    swiftSalonDao.insertPromotions(promotions);
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<Promotion> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Promotion>> loadFromDb() {
                return swiftSalonDao.getPromotions(salonId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<List<Promotion>>>> createCall() {
                return ServiceGenerator.getSalonApi().getPromotions(salonId);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericResponse<Promotion>>> savePromotionApi(Promotion promotion, MultipartBody.Part image) {
        return new NetworkOnlyBoundResource<Promotion, GenericResponse<Promotion>>(AppExecutor.getInstance()) {

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericResponse<Promotion>>> createCall() {
                return ServiceGenerator.getSalonApi().savePromotion(promotion, image);
            }

            @Override
            protected void saveCallResult(@NonNull GenericResponse<Promotion> item) {
                if (item.getContent() != null) {
                    swiftSalonDao.insertPromotion(item.getContent());
                }
            }
        }.getAsLiveData();
    }

}
