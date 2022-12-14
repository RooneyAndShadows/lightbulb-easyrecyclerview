package com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalSpaceItemDecoration(private val itemSpacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.top = itemSpacing
        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) outRect.bottom = itemSpacing
    }
}