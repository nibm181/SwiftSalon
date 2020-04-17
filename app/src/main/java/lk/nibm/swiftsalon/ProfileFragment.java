package lk.nibm.swiftsalon;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfileFragment extends Fragment {

    TextView txtSalonName;
    Button btnStylist, btnPromotion, btnSignout;
    ImageView imgSalon;
    View view;
    MainActivity main;
    String salonImage;
    Session session;
    SweetAlertDialog alertDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtSalonName = view.findViewById(R.id.txt_salonName);
        btnStylist = view.findViewById(R.id.btn_stylists);
        btnPromotion = view.findViewById(R.id.btn_promotion);
        btnSignout = view.findViewById(R.id.btn_signout);
        imgSalon = view.findViewById(R.id.img_salon);
        session = new Session(getContext());
        main = (MainActivity) getActivity();

        txtSalonName.setText(main.txtSalon);

        //For Image View
        salonImage = main.txtImg;
        Glide.with(getContext())
                .load(salonImage)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                .into(imgSalon);

        btnStylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent stylistIntent = new Intent(getActivity(), StylistActivity.class);
                startActivity(stylistIntent);
            }
        });

        btnPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE);
                pDialog.setTitleText("Coming Soon!");
                pDialog.show();

                Button btn = pDialog.findViewById(R.id.confirm_button);
                btn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            }
        });

        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE);
                alertDialog.setTitle("Are you sure you want to logout?");
                alertDialog.setConfirmText("Confirm");
                alertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                        Intent stylistIntent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(stylistIntent);
                        session.clearSession();
                        getActivity().finish();
                        //finish activity
                    }
                });
                alertDialog.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                    }
                });
                alertDialog.show();

                Button btnC = alertDialog.findViewById(R.id.confirm_button);
                btnC.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));

                Button btnW = alertDialog.findViewById(R.id.cancel_button);
                btnW.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));


            }
        });

        imgSalon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent salonEditIntent = new Intent(getActivity(), SalonEditActivity.class);
                startActivity(salonEditIntent);
            }
        });

        return view;
    }


}
