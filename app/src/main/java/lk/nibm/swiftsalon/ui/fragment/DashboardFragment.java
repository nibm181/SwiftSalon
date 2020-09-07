package lk.nibm.swiftsalon.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.model.SliderItem;
import lk.nibm.swiftsalon.ui.activity.EarningsActivity;
import lk.nibm.swiftsalon.ui.activity.JobsActivity;
import lk.nibm.swiftsalon.ui.activity.ProfileActivity;
import lk.nibm.swiftsalon.ui.activity.PromotionsActivity;
import lk.nibm.swiftsalon.ui.activity.StylistsActivity;
import lk.nibm.swiftsalon.ui.adapter.SliderAdapter;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.viewmodel.DashboardViewModel;

public class DashboardFragment extends Fragment {

    private SliderView imageSlider;
    private CardView btnProfile, btnJobs, btnStylists, btnPromotions, btnEarnings;
    private TextView txtSalon;
    private ImageView imgSalon;

    private SliderAdapter sliderAdapter;
    private View view;
    private DashboardViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        btnProfile = view.findViewById(R.id.btn_profile);
        btnJobs = view.findViewById(R.id.btn_jobs);
        btnStylists = view.findViewById(R.id.btn_stylists);
        btnPromotions = view.findViewById(R.id.btn_promotions);
        btnEarnings = view.findViewById(R.id.btn_earnings);
        txtSalon = view.findViewById(R.id.txt_salon);
        imgSalon = view.findViewById(R.id.img_salon);
        imageSlider = view.findViewById(R.id.image_slider);

        btnProfile.setOnClickListener(v -> {
            Intent profile = new Intent(getContext(), ProfileActivity.class);
            startActivity(profile);
        });

        btnJobs.setOnClickListener(v -> {
            Intent jobs = new Intent(getContext(), JobsActivity.class);
            startActivity(jobs);
        });

        btnStylists.setOnClickListener(v -> {
            Intent stylists = new Intent(getContext(), StylistsActivity.class);
            startActivity(stylists);
        });

        btnPromotions.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PromotionsActivity.class);
            startActivity(intent);
        });

        btnEarnings.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EarningsActivity.class);
            startActivity(intent);
        });

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initImageSlider();
        subscribeObservers(initGlide());
        salonApi();
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.sample_salon)
                .error(R.drawable.sample_salon)
                .dontAnimate()
                .optionalCircleCrop();

        return Glide.with(getContext())
                .setDefaultRequestOptions(options);
    }

    private void initImageSlider() {
        sliderAdapter = new SliderAdapter();
        imageSlider.setSliderAdapter(sliderAdapter);
        imageSlider.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        imageSlider.startAutoCycle();

        sliderAdapter.renewItems(addItemToSlider());
    }

    private List<SliderItem> addItemToSlider() {
        List<SliderItem> items = new ArrayList<>();
        SliderItem item = new SliderItem("", "https://www.softwaresuggest.com/blog/wp-content/uploads/2019/10/salon-business-1.png");
        items.add(item);

        item = new SliderItem("", "http://swiftsalon.space/uploads/salon_banner_1.png");
        items.add(item);

        item = new SliderItem("", "http://swiftsalon.space/uploads/salon_banner_2.png");
        items.add(item);

        item = new SliderItem("", "http://swiftsalon.space/uploads/salon_banner_3.png");
        items.add(item);

        return items;
    }

    private void subscribeObservers(RequestManager requestManager) {
        viewModel.getSalon().observe(getViewLifecycleOwner(), new Observer<Resource<Salon>>() {
            @Override
            public void onChanged(Resource<Salon> salonResource) {
                if(salonResource !=null) {
                    if(salonResource.data != null) {

                        switch (salonResource.status) {

                            case LOADING: {
                                txtSalon.setText("");
                                break;
                            }

                            case ERROR:

                            case SUCCESS: {
                                txtSalon.setText(salonResource.data.getName());

                                requestManager
                                        .load(salonResource.data.getImage())
                                        .into(imgSalon);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    private void salonApi() {
        viewModel.getSalonApi();
    }

}
