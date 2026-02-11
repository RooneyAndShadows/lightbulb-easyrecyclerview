package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers

import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlin.math.abs

class VerticalFlowLayoutManager<ItemType : EasyAdapterDataModel>(
    private val easyRecyclerView: EasyRecyclerView<ItemType>,
) : FlexboxLayoutManager(easyRecyclerView.context, FlexDirection.ROW) {

    init {
        justifyContent = JustifyContent.FLEX_START
    }

    @Override
    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        //val overScroll = dy - scrollRange
        val scrollRange = super.scrollVerticallyBy(dy, recycler, state)
        if (abs(dy) > 20) {
            easyRecyclerView.parent.requestDisallowInterceptTouchEvent(true)
        }
        if (needToLoadData(dy)) {
            easyRecyclerView.loadMoreData()
        }
        return scrollRange
    }

    private fun needToLoadData(dy: Int): Boolean {
        val adapter = easyRecyclerView.adapter
        if (adapter == null || dy <= 0) return false
        val lastView = getChildAt(childCount - 1) ?: return false
        val lastViewAdapterPos =
            (lastView.layoutParams as RecyclerView.LayoutParams).absoluteAdapterPosition
        val headersCount = adapter.headersCount
        val visibleLastPosition = lastViewAdapterPos - headersCount
        val totalSize = adapter.collection.size()
        return visibleLastPosition == totalSize - 1
    }
}