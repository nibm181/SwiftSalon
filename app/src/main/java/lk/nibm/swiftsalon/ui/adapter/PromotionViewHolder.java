package lk.nibm.swiftsalon.ui.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.text.SimpleDateFormat;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Promotion;

public class PromotionViewHolder extends RecyclerView.ViewHolder {

    private TextView txtJob, txtDates, txtDescription, txtOffer, btnShare;
    private ImageView imgPromotion;

    private RequestManager requestManager;

    public PromotionViewHolder(@NonNull View itemView, RequestManager requestManager, OnItemListener onItemListener) {
        super(itemView);

        this.requestManager = requestManager;

        txtJob = itemView.findViewById(R.id.txt_job);
        txtDates = itemView.findViewById(R.id.txt_dates);
        txtDescription = itemView.findViewById(R.id.txt_description);
        txtOffer = itemView.findViewById(R.id.txt_offer);
        imgPromotion = itemView.findViewById(R.id.img_promotion);
        btnShare = itemView.findViewById(R.id.txt_share);

        btnShare.setOnClickListener(v -> onItemListener.onItemClick(getAdapterPosition()));
    }

    public void onBind(Promotion promotion) {

        String offer = "";
        String job = promotion.getJobName() + " (" + promotion.getJobId() + ")";

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String startDate = dateFormat.format(promotion.getStartDate());
        String endDate = dateFormat.format(promotion.getEndDate());

        String dates = "Validity: " + startDate + "  -  " + endDate;
        if(startDate.equals(endDate)) {
            dates = "Validity: Only on " + startDate;
        }

        if (promotion.getOffAmount() == 0) {
            offer = "No offer amount.";
            txtOffer.setTextColor(Color.parseColor("#808080"));
        } else {
            offer = "Offer Amount: Rs. " + promotion.getOffAmount();
        }

        txtJob.setText(job);
        txtDates.setText(dates);
        txtDescription.setText(promotion.getDescription());
        txtOffer.setText(offer);

        requestManager
                .load(promotion.getImage())
                .into(imgPromotion);
    }

}
