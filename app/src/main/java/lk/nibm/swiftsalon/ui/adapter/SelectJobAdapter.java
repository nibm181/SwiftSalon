package lk.nibm.swiftsalon.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Job;

public class SelectJobAdapter extends ListAdapter<Job, RecyclerView.ViewHolder> {

    private OnItemListener onItemListener;
    private List<Job> jobs;

    public SelectJobAdapter(OnItemListener onItemListener, List<Job> jobs) {
        super(DIFF_CALLBACK);
        this.onItemListener = onItemListener;
        this.jobs = jobs;
    }

    private static DiffUtil.ItemCallback<Job> DIFF_CALLBACK = new DiffUtil.ItemCallback<Job>() {
        @Override
        public boolean areItemsTheSame(@NonNull Job oldItem, @NonNull Job newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Job oldItem, @NonNull Job newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_job_selection, parent, false);
        return new SelectJobViewHolder(view, onItemListener, jobs);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Job job = getItem(position);
        ((SelectJobViewHolder) holder).onBind(job);
    }

    public Job getSelectedJob(int position) {
        Job job = getItem(position);
        if (job != null) {
            return job;
        }
        return null;
    }
}
