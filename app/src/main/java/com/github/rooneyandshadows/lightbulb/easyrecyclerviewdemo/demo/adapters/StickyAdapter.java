package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.StickyHeaderItemDecoration;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.DemoItemLayoutBinding;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.DemoStickyItemLayoutBinding;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.StickyDemoModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterConfiguration;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyRecyclerAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class StickyAdapter extends EasyRecyclerAdapter<StickyDemoModel> implements StickyHeaderItemDecoration.StickyHeaderInterface {

    public StickyAdapter() {
        super(new EasyAdapterConfiguration<>());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.demo_sticky_header_item, parent, false);
            return new HeaderViewHolder(itemView);
        } else {
            DemoStickyItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.demo_sticky_item_layout, parent, false);
            return new ViewHolder(binding);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isHeader())
            return 0;
        else return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vh = (ViewHolder) holder;
            StickyDemoModel model = getItem(position);
            vh.getBinding().setIsHeader(model.isHeader());
            vh.getBinding().setTitle(model.getTitle());
            vh.getBinding().setSubtitle(model.getSubtitle());
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder vh = (HeaderViewHolder) holder;
            vh.bind(getItem(position));
        }
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
        return R.layout.demo_sticky_header_item;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        TextView tv = header.findViewById(R.id.header_title);
        tv.setText(getItem(headerPosition).getTitle());
    }

    @Override
    public boolean isHeader(int itemPosition) {
        return getItem(itemPosition).isHeader();
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.header_title);
        }

        public void bind(StickyDemoModel model) {
            title.setText(model.getTitle());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final DemoStickyItemLayoutBinding binding;

        public ViewHolder(DemoStickyItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public DemoStickyItemLayoutBinding getBinding() {
            return binding;
        }
    }
}
