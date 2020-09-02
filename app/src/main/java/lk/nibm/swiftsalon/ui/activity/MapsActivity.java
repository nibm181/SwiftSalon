package lk.nibm.swiftsalon.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Salon;
import lk.nibm.swiftsalon.request.response.GenericResponse;
import lk.nibm.swiftsalon.util.CustomDialog;
import lk.nibm.swiftsalon.util.Resource;
import lk.nibm.swiftsalon.viewmodel.EditProfileViewModel;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private Marker marker;
    private LatLng latLng;
    private Geocoder geocoder;
    private List<Address> addresses;
    String address = "Unnamed Location";

    private CustomDialog dialog;
    private EditProfileViewModel viewModel;
    private Salon salon;

    private ImageButton btnSave, btnBack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_ok);
        progressBar = findViewById(R.id.progress_bar);

        btnSave.setEnabled(false);

        geocoder = new Geocoder(this, Locale.getDefault());
        dialog = new CustomDialog(MapsActivity.this);
        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);

        getIncomingContent();
        subscribeObservers();

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            completeLocation();
        });
    }

    private void getIncomingContent() {
        if (getIntent().hasExtra("salon")) {
            salon = getIntent().getParcelableExtra("salon");
            if (salon != null) {
                double longitude = salon.getLongitude();
                double latitude = salon.getLatitude();

                if (longitude != 0.0d && latitude != 0.0d) {
                    latLng = new LatLng(latitude, longitude);
                }
            }

        }
    }

    private void subscribeObservers() {
        viewModel.updateSalon().observe(this, new Observer<Resource<GenericResponse<Salon>>>() {
            @Override
            public void onChanged(Resource<GenericResponse<Salon>> resource) {

                if (resource != null) {

                    switch (resource.status) {

                        case LOADING: {
                            Log.d(TAG, "onChanged: LOADING");
                            showProgressBar(true);
                            break;
                        }

                        case ERROR: {
                            Log.d(TAG, "onChanged: ERROR");
                            showProgressBar(false);
                            dialog.showToast(resource.message);
                            break;
                        }

                        case SUCCESS: {
                            Log.d(TAG, "onChanged: SUCCESS");

                            if (resource.data.getStatus() == 1) {

                                if (resource.data.getContent() != null) {
                                    dialog.showToast("Successfully updated");
                                    finish();
                                } else {
                                    showProgressBar(false);
                                    dialog.showAlert("Oops! Something went wrong. Please try again.");
                                }

                            } else {
                                showProgressBar(false);
                                dialog.showAlert(resource.data.getMessage());
                            }
                            break;
                        }

                    }

                }
            }
        });
    }

    private void showProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
        }
    }

    private void completeLocation() {
        address = "Unnamed Location";
        try {
            addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(salon != null) {
            updateApi();
        }
        else {
            Intent data = new Intent();
            data.putExtra("location", address);
            data.putExtra("latitude", marker.getPosition().latitude);
            data.putExtra("longitude", marker.getPosition().longitude);
            setResult(RESULT_OK, data);

            finish();
        }
    }

    private void updateApi() {
        if (isOnline()) {
            salon.setLatitude(marker.getPosition().latitude);
            salon.setLongitude(marker.getPosition().longitude);
            salon.setLocation(address);

            viewModel.updateApi(salon);
        } else {
            dialog.showToast("Check your connection and try again.");
        }
    }


    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (latLng == null) {
            latLng = new LatLng(6.927079, 79.861244);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        marker = mMap.addMarker(new MarkerOptions().title("I am here").position(latLng));
        btnSave.setEnabled(true);

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if (marker != null) {
                    marker.remove();
                }

                latLng = mMap.getCameraPosition().target;
                marker = mMap.addMarker(new MarkerOptions().title("I am here").position(latLng));
            }
        });
    }
}