package com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.base.EasyRecyclerItemDecoration

@Suppress("unused")
class FlexboxSpaceItemDecoration : EasyRecyclerItemDecoration {
    private val verticalSpacing: Int
    private val horizontalSpacing: Int

    constructor(spacing: Int, recyclerView: EasyRecyclerView<*>) {
        verticalSpacing = spacing / 2
        horizontalSpacing = spacing / 2
        recyclerView.findViewById<View>(R.id.recyclerView).setPadding(horizontalSpacing, spacing, horizontalSpacing, 0)
    }

    constructor(verticalSpacing: Int, horizontalSpacing: Int, recyclerView: EasyRecyclerView<*>) {
        this.verticalSpacing = verticalSpacing / 2
        this.horizontalSpacing = horizontalSpacing / 2
        recyclerView.findViewById<View>(R.id.recyclerView)
            .setPadding(this.verticalSpacing, verticalSpacing, this.verticalSpacing, 0)
    }

    @Override
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = horizontalSpacing
        outRect.right = horizontalSpacing
        outRect.bottom = verticalSpacing * 2
    }
}