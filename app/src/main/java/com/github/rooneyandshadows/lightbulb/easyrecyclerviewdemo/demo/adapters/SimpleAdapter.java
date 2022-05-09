package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.DemoItemLayoutBinding;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterConfiguration;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyRecyclerAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleAdapter extends EasyRecyclerAdapter<DemoModel> {

    public SimpleAdapter() {
        super(new EasyAdapterConfiguration<DemoModel>().withStableIds(true));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DemoItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.demo_item_layout, parent, false);
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
        private final DemoItemLayoutBinding binding;

        public ViewHolder(DemoItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public DemoItemLayoutBinding getBinding() {
            return binding;
        }
    }
}
