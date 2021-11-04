package com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalAndHorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int verticalSpacing;
    private final int horizontalSpacing;

    public VerticalAndHorizontalSpaceItemDecoration(int spacing) {
        this.verticalSpacing = spacing;
        this.horizontalSpacing = spacing;
    }

    public VerticalAndHorizontalSpaceItemDecoration(int verticalSpacing, int horizontalSpacing) {
        this.verticalSpacing = verticalSpacing;
        this.horizontalSpacing = horizontalSpacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = horizontalSpacing;
        outRect.right = horizontalSpacing;
        outRect.top = verticalSpacing;
        if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1)
            outRect.bottom = verticalSpacing;
    }
}