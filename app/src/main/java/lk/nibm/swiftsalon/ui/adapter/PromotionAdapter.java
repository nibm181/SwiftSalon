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
import lk.nibm.swiftsalon.model.Promotion;

public class PromotionAdapter extends ListAdapter<Promotion, RecyclerView.ViewHolder> {

    private OnItemListener onItemListener;
    private RequestManager requestManager;
    private Promotion promotion;

    public PromotionAdapter(OnItemListener onItemListener, RequestManager requestManager) {
        super(DIFF_CALLBACK);
        this.onItemListener = onItemListener;
        this.requestManager = requestManager;
    }

    private static DiffUtil.ItemCallback<Promotion> DIFF_CALLBACK = new DiffUtil.ItemCallback<Promotion>() {
        @Override
        public boolean areItemsTheSame(@NonNull Promotion oldItem, @NonNull Promotion newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Promotion oldItem, @NonNull Promotion newItem) {
            return oldItem.getSalonId() == newItem.getSalonId()
                    && oldItem.getDescription().equals(newItem.getDescription())
                    && oldItem.getOffAmount() == newItem.getOffAmount()
                    && oldItem.getStartDate().equals(newItem.getStartDate())
                    && oldItem.getEndDate().equals(newItem.getEndDate());
        }
    };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_promotion, parent, false);
        return new PromotionViewHolder(view, requestManager,onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        promotion = getItem(position);
        ((PromotionViewHolder) holder).onBind(promotion);
    }

    public Promotion getSelectedPromotion(int position) {
        return getItem(position);
    }
}
