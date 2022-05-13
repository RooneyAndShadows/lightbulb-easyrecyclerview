package com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int itemSpacing;

    public HorizontalSpaceItemDecoration(int horizontalSpace) {
        this.itemSpacing = horizontalSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = itemSpacing;
        if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1)
            outRect.right = itemSpacing;
    }
}