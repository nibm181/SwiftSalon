package lk.nibm.swiftsalon.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.model.Stylist;

public class StylistAdapter extends ListAdapter<Stylist, RecyclerView.ViewHolder> {
    private static final String TAG = "StylistAdapter";

    private OnItemListener onItemListener;
    private RequestManager requestManager;
    private Stylist stylist;

    public StylistAdapter(OnItemListener onItemListener, RequestManager requestManager) {
        super(DIFF_CALLBACK);
        this.onItemListener = onItemListener;
        this.requestManager = requestManager;
    }

    private static final DiffUtil.ItemCallback<Stylist> DIFF_CALLBACK = new DiffUtil.ItemCallback<Stylist>() {
        @Override
        public boolean areItemsTheSame(@NonNull Stylist oldItem, @NonNull Stylist newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Stylist oldItem, @NonNull Stylist newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getGender().equals(newItem.getGender()) &&
                    oldItem.getImage().equals(newItem.getImage()) &&
                    oldItem.getStatus() == newItem.getStatus();
        }
    };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_stylist, parent, false);
        return new StylistViewHolder(view, onItemListener, requestManager);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        stylist = getItem(position);
        ((StylistViewHolder) holder).onBind(stylist);
    }

    public Stylist getSelectedStylist(int position) {
        stylist = getItem(position);
        if (stylist != null) {
            return stylist;
        }
        return null;
    }
}
