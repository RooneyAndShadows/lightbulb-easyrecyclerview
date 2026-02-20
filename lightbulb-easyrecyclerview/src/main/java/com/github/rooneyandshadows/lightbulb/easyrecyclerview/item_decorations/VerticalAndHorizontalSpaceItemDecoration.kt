package com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.base.EasyRecyclerItemDecoration

class VerticalAndHorizontalSpaceItemDecoration(
    private val verticalSpacing: Int,
    private val horizontalSpacing: Int
) : EasyRecyclerItemDecoration() {

    constructor(spacing: Int) : this(spacing, spacing)

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        val layoutManager = parent.layoutManager
        val isVertical = layoutManager?.canScrollVertically() == true
        val isHorizontal = layoutManager?.canScrollHorizontally() == true

        if (isVertical) {
            outRect.top = if (position == 0) verticalSpacing else verticalSpacing / 2
            outRect.bottom = if (position == itemCount - 1) verticalSpacing else verticalSpacing / 2
            outRect.left = horizontalSpacing
            outRect.right = horizontalSpacing
        } else if (isHorizontal) {
            outRect.left = if (position == 0) horizontalSpacing else horizontalSpacing / 2
            outRect.right = if (position == itemCount - 1) horizontalSpacing else horizontalSpacing / 2
            outRect.top = verticalSpacing
            outRect.bottom = verticalSpacing
        } else {
            outRect.set(horizontalSpacing, verticalSpacing, horizontalSpacing, verticalSpacing)
        }
    }
}