package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.DemoItemLayoutBinding;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.DemoSelectableItemLayoutBinding;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterConfiguration;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterSelectableModes;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyRecyclerAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class SelectableAdapter extends EasyRecyclerAdapter<DemoModel> {

    public SelectableAdapter() {
        super(new EasyAdapterConfiguration<DemoModel>()
                .withSelectMode(EasyAdapterSelectableModes.SELECT_MULTIPLE));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DemoSelectableItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.demo_selectable_item_layout, parent, false);
        return new ViewHolder(binding, this);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        DemoModel model = getItem(position);
        vh.getBinding().setTitle(model.getTitle());
        vh.getBinding().setSubtitle(model.getSubtitle());
        vh.initialize();
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        ViewHolder vh = (ViewHolder) holder;
        vh.recycle();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final DemoSelectableItemLayoutBinding binding;
        private final SelectableAdapter adapter;

        public ViewHolder(DemoSelectableItemLayoutBinding binding, SelectableAdapter adapter) {
            super(binding.getRoot());
            this.adapter = adapter;
            this.binding = binding;
        }

        public DemoSelectableItemLayoutBinding getBinding() {
            return binding;
        }

        public void initialize() {
            int itemPos = getAbsoluteAdapterPosition() - adapter.getHeadersCount();
            boolean isSelectedInAdapter = adapter.isItemSelected(itemPos);
            binding.cardContainer.setBackgroundDrawable(getBackgroundDrawable(isSelectedInAdapter));
            binding.cardContainer.setOnClickListener(v -> adapter.selectItemAt(itemPos, !adapter.isItemSelected(itemPos)));
        }


        private Drawable getBackgroundDrawable(boolean selected) {
            return selected ? ResourceUtils.getDrawable(itemView.getContext(), R.drawable.bg_card_selected) : ResourceUtils.getDrawable(itemView.getContext(), R.drawable.bg_card);
        }

        public void recycle() {
            binding.cardContainer.setBackgroundDrawable(null);
        }
    }
}
