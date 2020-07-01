package lk.nibm.swiftsalon.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Job;

public class JobAdapter extends ListAdapter<Job, RecyclerView.ViewHolder> {
    private static final String TAG = "JobAdapter";

    private OnItemListener onItemListener;
    private Job job;
    private View view;

    public JobAdapter(OnItemListener onItemListener) {
        super(DIFF_CALLBACK);
        this.onItemListener = onItemListener;
    }

    private static final DiffUtil.ItemCallback<Job> DIFF_CALLBACK = new DiffUtil.ItemCallback<Job>() {
        @Override
        public boolean areItemsTheSame(@NonNull Job oldItem, @NonNull Job newItem) {
            return oldItem.getSalon_id() == newItem.getSalon_id() &&
                    oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Job oldItem, @NonNull Job newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getDuration() == newItem.getDuration() &&
                    oldItem.getPrice() == newItem.getPrice();
        }
    };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_job, parent, false);
        return new JobViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        job = getItem(position);
        ((JobViewHolder) holder).onBind(job);
    }

    public Job getSelectedJob(int position) {
        job = getItem(position);
        if (job != null) {
            return job;
        }
        return null;
    }
}
