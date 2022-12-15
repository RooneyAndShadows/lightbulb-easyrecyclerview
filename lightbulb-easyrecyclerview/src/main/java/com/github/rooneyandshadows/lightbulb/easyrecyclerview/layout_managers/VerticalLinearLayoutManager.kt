package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import kotlin.math.abs

class VerticalLinearLayoutManager<IType : EasyAdapterDataModel, AType : EasyRecyclerAdapter<IType>>(
    private val easyRecyclerView: EasyRecyclerView<IType, AType>
) : LinearLayoutManager(easyRecyclerView.context, VERTICAL, false) {
    @Override
    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val scrollRange = super.scrollVerticallyBy(dy, recycler, state)
        //val overScroll = dy - scrollRange
        if (abs(dy) > 20) easyRecyclerView.parent.requestDisallowInterceptTouchEvent(true)
        if (needToLoadMoreData(dy)) handleLoadMore()
        return scrollRange
    }

    private fun needToLoadMoreData(dy: Int): Boolean {
        return easyRecyclerView.hasMoreDataToLoad() &&
                !easyRecyclerView.isShowingLoadingHeader &&
                !easyRecyclerView.isAnimating &&
                !easyRecyclerView.isShowingRefreshLayout &&
                !easyRecyclerView.isShowingLoadingFooter && dy > 0
    }

    private fun handleLoadMore() {
        if (!easyRecyclerView.supportsLazyLoading()) return
        val lastView = getChildAt(childCount - 1) ?: return
        val recyclerAdapter: EasyRecyclerAdapter<IType> = easyRecyclerView.adapter ?: return
        val size = easyRecyclerView.items.size
        val last =
            (lastView.layoutParams as LayoutParams).absoluteAdapterPosition - recyclerAdapter.headersCount
        if (last == size - 1) easyRecyclerView.loadMoreData()
    }
}