package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel
import kotlin.math.abs

class VerticalLinearLayoutManager<ItemType : EasyAdapterDataModel>(
    private val easyRecyclerView: EasyRecyclerView<ItemType>,
) : LinearLayoutManager(easyRecyclerView.context, VERTICAL, false) {
    @Override
    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        val scrollRange = super.scrollVerticallyBy(dy, recycler, state)
        //val overScroll = dy - scrollRange
        if (abs(dy) > 20) {
            easyRecyclerView.parent.requestDisallowInterceptTouchEvent(true)
        }
        if (needToLoadData(dy)) {
            easyRecyclerView.loadMoreData()
        }
        return scrollRange
    }

    private fun needToLoadData(dx: Int): Boolean {
        val adapter = easyRecyclerView.adapter
        if (adapter == null || dx <= 0) return false
        val lastView = getChildAt(childCount - 1) ?: return false
        val lastViewAdapterPos =
            (lastView.layoutParams as RecyclerView.LayoutParams).absoluteAdapterPosition
        val headersCount = adapter.headersCount
        val visibleLastPosition = lastViewAdapterPos - headersCount
        val totalSize = adapter.collection.size()
        return visibleLastPosition == totalSize - 1
    }
}