package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers

import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlin.math.abs

class HorizontalFlowLayoutManager<ItemType : EasyAdapterDataModel>(
    private val easyRecyclerView: EasyRecyclerView<ItemType>,
) : FlexboxLayoutManager(easyRecyclerView.context, FlexDirection.COLUMN) {
    private var scrollingHorizontally = false
    private var scrollingVertically = false

    init {
        justifyContent = JustifyContent.FLEX_START
    }

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
        if (abs(dx) > 20) {
            easyRecyclerView.parent.requestDisallowInterceptTouchEvent(true)
        }
        if (needToLoadData(dx)) {
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