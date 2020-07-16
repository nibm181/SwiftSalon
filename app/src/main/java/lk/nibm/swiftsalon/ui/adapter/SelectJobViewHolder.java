package lk.nibm.swiftsalon.ui.adapter;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Job;

public class SelectJobViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String TAG = "SelectJobViewHolder";
    private CheckBox cbJob;
    private TextView txtJob;
    private OnItemListener onItemListener;
    private List<Job> jobs;

    public SelectJobViewHolder(@NonNull View itemView, OnItemListener onItemListener, List<Job> jobs) {
        super(itemView);
        this.onItemListener = onItemListener;
        this.jobs = jobs;

        itemView.setOnClickListener(this);

        cbJob = itemView.findViewById(R.id.cb_job);
        txtJob = itemView.findViewById(R.id.txt_job);
    }

    public void onBind(Job job) {
        String jobData = job.getName() + " (" + job.getId() + ")";
        txtJob.setText(jobData);

        for(Job jobHere : jobs) {
            if(job.getId() == jobHere.getId()) {
                cbJob.setChecked(true);
            }
        }

    }


    @Override
    public void onClick(View v) {
        cbJob.setChecked(!cbJob.isChecked());
        onItemListener.onItemClick(getAdapterPosition());
    }
}
