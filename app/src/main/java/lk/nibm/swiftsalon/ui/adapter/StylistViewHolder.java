package lk.nibm.swiftsalon.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Stylist;

public class StylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "StylistViewHolder";

    private TextView txtName, txtId;
    private ImageView imgStylist;

    private OnItemListener onItemListener;
    private RequestManager requestManager;

    public StylistViewHolder(@NonNull View itemView, OnItemListener onItemListener, RequestManager requestManager) {
        super(itemView);
        this.onItemListener = onItemListener;
        this.requestManager = requestManager;

        txtName = itemView.findViewById(R.id.txt_name);
        txtId = itemView.findViewById(R.id.txt_id);
        imgStylist = itemView.findViewById(R.id.img_stylist);

        itemView.setOnClickListener(this);
    }

    public void onBind(Stylist stylist) {
        txtId.setText(String.valueOf(stylist.getId()));
        txtName.setText(stylist.getName());

        requestManager
                .load(stylist.getImage())
                .apply(RequestOptions.circleCropTransform())
                .into(imgStylist);
    }

    @Override
    public void onClick(View v) {
        onItemListener.onItemClick(getAdapterPosition());
    }
}
