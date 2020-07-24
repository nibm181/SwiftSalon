package lk.nibm.swiftsalon.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Stylist;
import lk.nibm.swiftsalon.ui.adapter.OnItemListener;
import lk.nibm.swiftsalon.ui.adapter.StylistAdapter;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.VerticalSpacingItemDecorator;
import lk.nibm.swiftsalon.viewmodel.StylistsViewModel;

public class StylistsActivity extends AppCompatActivity implements OnItemListener {
    private static final String TAG = "StylistsActivity";

    private ImageButton btnBack;
    private FloatingActionButton btnAdd;
    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerStylists;
    private TextView txtEmpty;

    private StylistsViewModel viewModel;
    private StylistAdapter adapter;
    private CustomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stylists);

        btnBack = findViewById(R.id.btn_back);
        btnAdd = findViewById(R.id.btn_add);
        recyclerView = findViewById(R.id.rv_stylists);
        shimmerStylists = findViewById(R.id.shimmer_stylists);
        txtEmpty = findViewById(R.id.txt_empty);

        viewModel = new ViewModelProvider(this).get(StylistsViewModel.class);
        dialog = new CustomDialog(StylistsActivity.this);

        btnBack.setOnClickListener(v -> finish());

        btnAdd.setOnClickListener(v -> {
            Intent addStylist = new Intent(StylistsActivity.this, AddStylistActivity.class);
            startActivity(addStylist);
        });

        initRecyclerView();
        subscribeObservers();
        stylistsApi();
    }

    private void subscribeObservers() {
        viewModel.getStylists().observe(this, listResource -> {
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
                                showEmpty();
                            }
                            break;
                        }

                        case SUCCESS: {
                            Log.d(TAG, "onChanged: SUCCESS");

                            if (listResource.data.isEmpty()) {
                                txtEmpty.setText("There are no stylists. Please add stylists to view your business to clients.");
                                showEmpty();
                            } else {
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

    private void initRecyclerView() {
        adapter = new StylistAdapter(this, initGlide());
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(15);

        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void stylistsApi() {
        viewModel.stylistsApi();
    }

    private void showRecyclerView(boolean show) {
        if (show) {
            recyclerView.setVisibility(View.VISIBLE);
            shimmerStylists.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            shimmerStylists.setVisibility(View.VISIBLE);
        }
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        shimmerStylists.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.VISIBLE);
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.sample_avatar)
                .centerCrop()
                .circleCrop()
                .error(R.drawable.sample_avatar);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    @Override
    public void onItemClick(int position) {
        Stylist stylist = adapter.getSelectedStylist(position);

        Intent intent = new Intent(StylistsActivity.this, StylistActivity.class);
        intent.putExtra("stylist", stylist);
        startActivity(intent);
    }
}