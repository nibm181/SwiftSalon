package lk.nibm.swiftsalon.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.modal.Appointment;

public class MyAppointmentAdapter extends RecyclerView.Adapter<MyAppointmentAdapter.MyViewHolder> {

    Context context;

    public List<Appointment> appData;
    SweetAlertDialog pDialog;
    Dialog dialog;

    public MyAppointmentAdapter(Context context, List<Appointment> appData) {
        this.context = context;
        this.appData = appData;

        //request option for glide
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View view;
        view = LayoutInflater.from(context).inflate(R.layout.appointment_list, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);

        //Dialog box init
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.appointment_dialogview);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //For dialog box
        viewHolder.appListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TextView dialogTxtCus = dialog.findViewById(R.id.dialog_txt_cusName);
                TextView dialogTxtMobile = dialog.findViewById(R.id.dialog_txt_mobileNo);
                TextView dialogTxtDateTime = dialog.findViewById(R.id.dialog_txt_dateTime);
                TextView dialogTxtStylist = dialog.findViewById(R.id.dialog_txt_stylistName);
                TextView dialogTxtAppNo = dialog.findViewById(R.id.dialog_txt_appNo);
                ImageView dialogImgCus = dialog.findViewById(R.id.dialog_img_cus);

                dialogTxtCus.setText(appData.get(viewHolder.getAdapterPosition()).getFirstName());
                dialogTxtMobile.setText(appData.get(viewHolder.getAdapterPosition()).getMobileNo());
                dialogTxtDateTime.setText(appData.get(viewHolder.getAdapterPosition()).getDate()+" "+appData.get(viewHolder.getAdapterPosition()).getTime()+":00");
                dialogTxtStylist.setText("For : " + appData.get(viewHolder.getAdapterPosition()).getStylist());
                dialogTxtAppNo.setText(String.valueOf(appData.get(viewHolder.getAdapterPosition()).getAppNo()));

                //glide
                String url = appData.get(viewHolder.getAdapterPosition()).getImage();
                Glide.with(view).load(url).apply(RequestOptions.circleCropTransform()).into(dialogImgCus);

                dialog.show();

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //for recycler view
        holder.txtCus.setText(appData.get(position).getFirstName());
        holder.txtStylist.setText(appData.get(position).getStylist());
        holder.txtTime.setText(appData.get(position).getDate());
        //holder.imgCus.setImageResource(image[position]);

        //load image with glide
        String url = appData.get(position).getImage();
        Glide.with(holder.itemView.getContext()).load(url).apply(RequestOptions.circleCropTransform()).into(holder.imgCus);
    }

    @Override
    public int getItemCount() {
        return appData.size();
    }



    //for recycler view
    public static  class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout appListLayout;
        TextView txtCus;
        TextView txtStylist;
        TextView txtTime;
        ImageView imgCus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            appListLayout = itemView.findViewById(R.id.appointment_list_id);

            txtCus = itemView.findViewById(R.id.txt_cusName);
            txtStylist = itemView.findViewById(R.id.txt_stylist);
            txtTime = itemView.findViewById(R.id.txt_time);
            imgCus = itemView.findViewById(R.id.img_customer);
        }
    }
}
