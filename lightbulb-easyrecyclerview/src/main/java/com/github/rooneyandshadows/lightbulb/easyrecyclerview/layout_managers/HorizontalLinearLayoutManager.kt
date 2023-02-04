package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import kotlin.math.abs

class HorizontalLinearLayoutManager<IType : EasyAdapterDataModel, AType : EasyRecyclerAdapter<IType>>(
    private val easyRecyclerView: EasyRecyclerView<IType, AType>
) : LinearLayoutManager(easyRecyclerView.context, HORIZONTAL, false) {
    private var scrollingHorizontally = false
    private var scrollingVertically = false

    @Override
    override fun canScrollVertically(): Boolean {
        return easyRecyclerView.supportsPullToRefresh() && !scrollingHorizontally
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
                !easyRecyclerView.isShowingLoadingFooter && dx > 0
    }

    private fun handleLoadMore() {
        if (!easyRecyclerView.supportsLazyLoading()) return
        val lastView = getChildAt(childCount - 1) ?: return
        val recyclerAdapter: EasyRecyclerAdapter<IType> = easyRecyclerView.adapter
        val size = easyRecyclerView.items.size
        val last = (lastView.layoutParams as LayoutParams).absoluteAdapterPosition - recyclerAdapter.headersCount
        if (last == size - 1) easyRecyclerView.loadMoreData()
    }
}