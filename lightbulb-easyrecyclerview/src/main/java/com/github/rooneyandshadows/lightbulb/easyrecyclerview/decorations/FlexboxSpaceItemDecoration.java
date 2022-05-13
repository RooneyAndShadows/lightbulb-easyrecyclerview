package com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations;

import android.graphics.Rect;
import android.view.View;

import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.R;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("rawtypes")
public class FlexboxSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int verticalSpacing;
    private final int horizontalSpacing;

    public FlexboxSpaceItemDecoration(int spacing, EasyRecyclerView recyclerView) {
        this.verticalSpacing = spacing / 2;
        this.horizontalSpacing = spacing / 2;
        recyclerView.findViewById(R.id.recyclerView).setPadding(horizontalSpacing, spacing, horizontalSpacing, verticalSpacing);
    }

    public FlexboxSpaceItemDecoration(int verticalSpacing, int horizontalSpacing, EasyRecyclerView recyclerView) {
        this.verticalSpacing = verticalSpacing / 2;
        this.horizontalSpacing = horizontalSpacing / 2;
        recyclerView.findViewById(R.id.recyclerView).setPadding(this.verticalSpacing, verticalSpacing, this.verticalSpacing, this.horizontalSpacing);
    }

    @Override
    public void getItemOffsets(Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = horizontalSpacing;
        outRect.right = horizontalSpacing;
        outRect.bottom = verticalSpacing * 2;
    }
}
