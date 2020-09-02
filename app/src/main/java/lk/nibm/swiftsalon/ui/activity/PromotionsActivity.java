package lk.nibm.swiftsalon.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Promotion;
import lk.nibm.swiftsalon.ui.adapter.OnItemListener;
import lk.nibm.swiftsalon.ui.adapter.PromotionAdapter;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.VerticalSpacingItemDecorator;
import lk.nibm.swiftsalon.viewmodel.PromotionsViewModel;

public class PromotionsActivity extends AppCompatActivity implements OnItemListener {

    private static final String TAG = "PromotionsActivity";

    private ImageButton btnBack;
    private TextView txtWarning;
    private ShimmerFrameLayout shimmerPromotions;
    private RecyclerView recyclerView;

    private CustomDialog dialog;
    private PromotionAdapter adapter;
    private PromotionsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotions);

        btnBack = findViewById(R.id.btn_back);
        txtWarning = findViewById(R.id.txt_warning);
        shimmerPromotions = findViewById(R.id.shimmer_promotions);
        recyclerView = findViewById(R.id.recycler_view);

        viewModel = new ViewModelProvider(this).get(PromotionsViewModel.class);
        dialog = new CustomDialog(PromotionsActivity.this);

        btnBack.setOnClickListener(v -> finish());

        initRecyclerView();
        subscribeObservers();
        jobsApi();
    }

    private void initRecyclerView() {
        adapter = new PromotionAdapter(this, initGlide());
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);

        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.sample_avatar)
                .error(R.drawable.sample_avatar);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    private void subscribeObservers() {
        viewModel.getPromotions().observe(this, listResource -> {
            if (listResource != null) {
                if (listResource.data != null) {

                    switch (listResource.status) {

                        case LOADING: {
                            Log.d(TAG, "onChanged: LOADING");
                            showRecyclerView(false);
                            break;
                        }

                        case ERROR: {
                            Log.d(TAG, "onChanged: ERROR");

                            showRecyclerView(true);
                            adapter.submitList(listResource.data);
                            dialog.showToast(listResource.message);

                            if (listResource.data.isEmpty()) {
                                showWarning();
                            }
                            break;
                        }

                        case SUCCESS: {
                            Log.d(TAG, "onChanged: SUCCESS");

                            if (listResource.data.isEmpty()) {
                                txtWarning.setText("There are no promotions.\n Go to Jobs > Select a Job > Click promote");
                                showWarning();
                            } else {
                                Log.d(TAG, "subscribeObservers: DATA: " + listResource.data.size());
                                showRecyclerView(true);
                                adapter.submitList(listResource.data);
                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    private void jobsApi() {
        viewModel.promotionsApi();
    }

    private void showRecyclerView(boolean show) {
        if (show) {
            recyclerView.setVisibility(View.VISIBLE);
            shimmerPromotions.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            shimmerPromotions.setVisibility(View.VISIBLE);
        }
    }

    private void showWarning() {
        recyclerView.setVisibility(View.GONE);
        shimmerPromotions.setVisibility(View.GONE);
        txtWarning.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(int position) {
        Promotion promotion = adapter.getSelectedPromotion(position);

        String message = "Hii there, I have created a promotion for " + promotion.getJobName()
                + ". Visit my Salon in 'Swift Salon' app to grab the opportunity. ";

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(message)
                .getIntent();

        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }
    }
}