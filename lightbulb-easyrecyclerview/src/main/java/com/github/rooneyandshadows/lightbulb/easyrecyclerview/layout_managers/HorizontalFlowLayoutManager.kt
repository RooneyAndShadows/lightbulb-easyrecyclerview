package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers

import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class HorizontalFlowLayoutManager<IType : EasyAdapterDataModel?, AType : EasyRecyclerAdapter<IType>?>(easyRecyclerView: EasyRecyclerView<IType, AType>) :
    FlexboxLayoutManager(easyRecyclerView.context, FlexDirection.COLUMN) {
    private val easyRecyclerView: EasyRecyclerView<IType, AType>
    private var scrollingHorizontally = false
    private var scrollingVertically = false
    override fun canScrollVertically(): Boolean {
        return easyRecyclerView.supportsPullToRefresh() && !scrollingHorizontally
    }

    override fun canScrollHorizontally(): Boolean {
        return !scrollingVertically
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        scrollingVertically = true
        return super.scrollVerticallyBy(dy, recycler, state)
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state != 0) return
        if (scrollingHorizontally) scrollingHorizontally = false
        if (scrollingVertically) scrollingVertically = false
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val scrollRange = super.scrollHorizontallyBy(dx, recycler, state)
        scrollingHorizontally = true
        val overScroll = dx - scrollRange
        if (Math.abs(dx) > 20) easyRecyclerView.parent.requestDisallowInterceptTouchEvent(true)
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
        val size = easyRecyclerView.items.size
        val last =
            (lastView.layoutParams as RecyclerView.LayoutParams).absoluteAdapterPosition - easyRecyclerView.adapter.getHeadersCount()
        if (last == size - 1) easyRecyclerView.loadMoreData()
    }

    init {
        justifyContent = JustifyContent.FLEX_START
        this.easyRecyclerView = easyRecyclerView
    }
}