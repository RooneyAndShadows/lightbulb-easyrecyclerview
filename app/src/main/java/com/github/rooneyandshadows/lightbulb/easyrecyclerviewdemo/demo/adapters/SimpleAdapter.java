package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.DemoListItemLayoutBinding;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterConfiguration;
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleAdapter extends EasyRecyclerAdapter<DemoModel> {

    public SimpleAdapter() {
        super(new EasyAdapterConfiguration<>());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DemoListItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.demo_list_item_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        DemoModel model = getItem(position);
        vh.getBinding().setTitle(model.getTitle());
        vh.getBinding().setSubtitle(model.getSubtitle());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final DemoListItemLayoutBinding binding;

        public ViewHolder(DemoListItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public DemoListItemLayoutBinding getBinding() {
            return binding;
        }
    }
}
