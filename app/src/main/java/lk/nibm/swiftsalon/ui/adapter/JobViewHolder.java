package lk.nibm.swiftsalon.ui.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Job;

public class JobViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "JobViewHolder";

    private TextView txtName, txtId;
    private OnItemListener onItemListener;

    public JobViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
        super(itemView);
        this.onItemListener = onItemListener;

        itemView.setOnClickListener(this);

        txtName = itemView.findViewById(R.id.txt_name);
        txtId = itemView.findViewById(R.id.txt_id);
    }

    public void onBind(Job job) {
        txtId.setText(String.valueOf(job.getId()));
        txtName.setText(job.getName());
    }

    @Override
    public void onClick(View v) {
        onItemListener.onItemClick(getAdapterPosition());
    }
}
