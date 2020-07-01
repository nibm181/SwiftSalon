package lk.nibm.swiftsalon.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Salon;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";

    private Salon salon;
    private ImageView image;
    private ImageButton btnBack, btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        image = findViewById(R.id.image);
        btnBack = findViewById(R.id.btn_back);
        btnEdit = findViewById(R.id.btn_edit);

        btnBack.setOnClickListener(v -> {
            supportFinishAfterTransition();
        });

        getIncomingContents();
    }

    private void getIncomingContents() {

        if(getIntent().hasExtra("salon")) {
            salon = getIntent().getParcelableExtra("salon");
            Log.d(TAG, "getIncomingContents: image: " + salon.getImage());
            if(salon != null) {
                RequestManager requestManager = initGlide();
                requestManager.load(salon.getImage())
                        .into(image);
            }
        }
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.sample_salon)
                .error(R.drawable.sample_salon);

        return Glide.with(getApplicationContext())
                .setDefaultRequestOptions(options);
    }
}