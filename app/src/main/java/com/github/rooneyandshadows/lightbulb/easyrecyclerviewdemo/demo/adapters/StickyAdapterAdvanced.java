package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.StickyHeaderItemDecoration;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.DemoStickyAdvancedItemLayoutBinding;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.DemoStickySimpleItemLayoutBinding;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.StickyAdvancedDemoModel;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.StickySimpleDemoModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterConfiguration;
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class StickyAdapterAdvanced extends EasyRecyclerAdapter<StickyAdvancedDemoModel> implements StickyHeaderItemDecoration.StickyHeaderInterface {

    public StickyAdapterAdvanced() {
        super(new EasyAdapterConfiguration<>());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DemoStickyAdvancedItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.demo_sticky_advanced_item_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        StickyAdvancedDemoModel model = getItem(position);
        vh.getBinding().setIsHeader(model.isHeader());
        vh.getBinding().setTitle(model.getTitle());
        vh.getBinding().setDayString(model.getDateString());
        vh.getBinding().setSubtitle(model.getSubtitle());
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;
        for (int i = itemPosition; i > 0; i--) {
            if (isHeader(i)) {
                headerPosition = i;
                return headerPosition;
            }
        }
        return headerPosition;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return R.layout.demo_sticky_header_advanced_item;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        TextView tv = header.findViewById(R.id.header_title);
        tv.setText(getItem(headerPosition).getDateString());
    }

    @Override
    public boolean isHeader(int itemPosition) {
        return getItem(itemPosition).isHeader();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final DemoStickyAdvancedItemLayoutBinding binding;

        public ViewHolder(DemoStickyAdvancedItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public DemoStickyAdvancedItemLayoutBinding getBinding() {
            return binding;
        }
    }
}
