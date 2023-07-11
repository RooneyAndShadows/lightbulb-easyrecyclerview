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
    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
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
    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val scrollRange = super.scrollHorizontallyBy(dx, recycler, state)
        scrollingHorizontally = true
        //val overScroll = dx - scrollRange
        if (abs(dx) > 20) easyRecyclerView.parent.requestDisallowInterceptTouchEvent(true)
        if (needToLoadMoreData(dx)) handleLoadMore()
        return scrollRange
    }

    private fun needToLoadMoreData(dx: Int): Boolean {
        return easyRecyclerView.hasMoreDataToLoad() &&
                !easyRecyclerView.isShowingLoadingHeader &&
                !easyRecyclerView.isAnimating &&
                !easyRecyclerView.isShowingRefreshLayout &&
                !easyRecyclerView.isLazyLoadingRunning && dx > 0
    }

    private fun handleLoadMore() {
        if (!easyRecyclerView.lazyLoadingEnabled) return
        val lastView = getChildAt(childCount - 1) ?: return
        val recyclerAdapter: EasyRecyclerAdapter<ItemType> = easyRecyclerView.adapter
        val size = easyRecyclerView.adapter.collection.size()
        val last = (lastView.layoutParams as RecyclerView.LayoutParams).absoluteAdapterPosition - recyclerAdapter.headersCount
        if (last == size - 1) easyRecyclerView.loadMoreData()
    }
}