package com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.base.EasyRecyclerItemDecoration

class VerticalAndHorizontalSpaceItemDecoration(
    private val verticalSpacing: Int,
    private val horizontalSpacing: Int,
) : EasyRecyclerItemDecoration() {

    constructor(spacing: Int) : this(spacing, spacing)

    @Override
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = horizontalSpacing
        outRect.right = horizontalSpacing
        outRect.top = verticalSpacing
        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1)
            outRect.bottom = verticalSpacing
    }
}