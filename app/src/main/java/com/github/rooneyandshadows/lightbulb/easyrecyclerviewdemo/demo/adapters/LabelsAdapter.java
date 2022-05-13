package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterConfiguration;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyRecyclerAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LabelsAdapter extends EasyRecyclerAdapter<DemoModel> {

    public LabelsAdapter() {
        super(new EasyAdapterConfiguration<>());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LabelItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.demo_list_item_label_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        LabelItemViewHolder holder = (LabelItemViewHolder) viewHolder;
        holder.bindData(getItem(position));
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder viewHolder) {
        super.onViewRecycled(viewHolder);
        LabelItemViewHolder holder = (LabelItemViewHolder) viewHolder;
        holder.recycle();
    }

    public static class LabelItemViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout container;
        private final TextView textView;

        LabelItemViewHolder(View view) {
            super(view);
            container = (RelativeLayout) view;
            textView = view.findViewById(R.id.labelTextView);
        }

        private void bindData(DemoModel model) {
            container.setBackground(ResourceUtils.getDrawable(container.getContext(), R.drawable.bg_label_item));
            textView.setText(model.getTitle());
        }

        private void recycle() {
            container.setBackground(null);
        }
    }
}