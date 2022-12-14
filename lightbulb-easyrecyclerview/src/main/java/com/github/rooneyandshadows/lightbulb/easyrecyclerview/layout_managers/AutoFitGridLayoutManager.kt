package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AutoFitGridLayoutManager(context: Context?, columnWidth: Int) : GridLayoutManager(context, 1) {
    private var columnWidth = 0
    private var columnWidthChanged = true
    fun setColumnWidth(newColumnWidth: Int) {
        if (newColumnWidth > 0 && newColumnWidth != columnWidth) {
            columnWidth = newColumnWidth
            columnWidthChanged = true
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (columnWidthChanged && columnWidth > 0) {
            val totalSpace: Int = if (orientation == VERTICAL)
                width - paddingRight - paddingLeft
            else
                height - paddingTop - paddingBottom
            val spanCount = Math.max(1, totalSpace / columnWidth)
            setSpanCount(spanCount)
            columnWidthChanged = false
        }
        super.onLayoutChildren(recycler, state)
    }

    init {
        setColumnWidth(columnWidth)
    }
}