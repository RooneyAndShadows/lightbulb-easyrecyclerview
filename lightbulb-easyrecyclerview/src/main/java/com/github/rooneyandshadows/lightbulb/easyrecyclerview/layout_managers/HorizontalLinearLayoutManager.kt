package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel
import kotlin.math.abs

class HorizontalLinearLayoutManager<ItemType : EasyAdapterDataModel>(
    private val easyRecyclerView: EasyRecyclerView<ItemType>,
) : LinearLayoutManager(easyRecyclerView.context, HORIZONTAL, false) {
    private var scrollingHorizontally = false
    private var scrollingVertically = false

    @Override
    override fun canScrollVertically(): Boolean {
        return easyRecyclerView.isPullToRefreshEnabled && !scrollingHorizontally
    }

    @Override
    override fun canScrollHorizontally(): Boolean {
        return !scrollingVertically
    }

    @Override
    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        scrollingVertically = true
        return super.scrollVerticallyBy(dy, recycler, state)
    }

    @Override
    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state != 0) return
        if (scrollingHorizontally) scrollingHorizontally = false
        if (scrollingVertically) scrollingVertically = false
    }

    @Override
    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        //val overScroll = dx - scrollRange
        val scrollRange = super.scrollHorizontallyBy(dx, recycler, state)
        scrollingHorizontally = true
        if (abs(dx) > 20) easyRecyclerView.parent.requestDisallowInterceptTouchEvent(true)
        if (needToLoadData(dx)) easyRecyclerView.loadMoreData()
        return scrollRange
    }

    private fun needToLoadData(dx: Int): Boolean {
        if (dx <= 0) return false
        val lastView = getChildAt(childCount - 1) ?: return false
        val recyclerAdapter: EasyRecyclerAdapter<ItemType> = easyRecyclerView.adapter
        val lastViewAdapterPos = (lastView.layoutParams as LayoutParams).absoluteAdapterPosition
        val headersCount = recyclerAdapter.headersCount
        val visibleLastPosition = lastViewAdapterPos - headersCount
        val totalSize = easyRecyclerView.adapter.collection.size()
        return visibleLastPosition == totalSize - 1
    }
}