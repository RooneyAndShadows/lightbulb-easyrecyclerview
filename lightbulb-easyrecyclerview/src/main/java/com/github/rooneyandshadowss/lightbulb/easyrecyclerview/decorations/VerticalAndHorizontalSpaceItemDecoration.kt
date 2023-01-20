package com.github.rooneyandshadowss.lightbulb.easyrecyclerview.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalAndHorizontalSpaceItemDecoration : RecyclerView.ItemDecoration {
    private val verticalSpacing: Int
    private val horizontalSpacing: Int

    constructor(spacing: Int) {
        verticalSpacing = spacing
        horizontalSpacing = spacing
    }

    constructor(verticalSpacing: Int, horizontalSpacing: Int) {
        this.verticalSpacing = verticalSpacing
        this.horizontalSpacing = horizontalSpacing
    }

    @Override
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = horizontalSpacing
        outRect.right = horizontalSpacing
        outRect.top = verticalSpacing
        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) outRect.bottom = verticalSpacing
    }
}