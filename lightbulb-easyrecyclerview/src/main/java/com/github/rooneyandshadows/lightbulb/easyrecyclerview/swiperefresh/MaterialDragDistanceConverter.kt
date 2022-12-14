package com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh

import com.factor.bouncy.BouncyRecyclerView.setAdapter
import com.factor.bouncy.BouncyRecyclerView.setLayoutManager
import com.factor.bouncy.BouncyRecyclerView.stiffness
import com.factor.bouncy.BouncyRecyclerView.flingAnimationSize
import com.factor.bouncy.BouncyRecyclerView.overscrollAnimationSize
import com.factor.bouncy.BouncyRecyclerView.dampingRatio
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.StickyHeaderItemDecoration.StickyHeaderInterface
import kotlin.jvm.JvmOverloads
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.IRefreshStatus
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RefreshView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RefreshLogger
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.NestedScrollingParentHelper
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RecyclerRefreshLayout
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RecyclerRefreshLayout.RefreshStyle
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.IDragDistanceConverter
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RecyclerRefreshLayout.OnRefreshListener
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.MaterialDragDistanceConverter
import androidx.core.view.ViewCompat
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.JustifyContent
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.EasyRecyclerViewTouchHandler.TouchCallbacks
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.EasyRecyclerViewTouchHandler.SwipeConfiguration
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.EasyRecyclerViewTouchHandler.SwipeToDeleteDrawerHelper
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView.LayoutManagerTypes
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.HeaderViewRecyclerAdapter
import com.factor.bouncy.BouncyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView.LoadMoreCallback
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView.RefreshCallback
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.EasyRecyclerViewTouchHandler
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView.EasyRecyclerItemsReadyListener
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView.EasyRecyclerEmptyLayoutListener
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.callbacks.EasyAdapterCollectionChangedListener
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.HeaderViewRecyclerAdapter.ViewListeners
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.VerticalLinearLayoutManager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.HorizontalLinearLayoutManager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.VerticalFlowLayoutManager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.HorizontalFlowLayoutManager
import androidx.dynamicanimation.animation.SpringForce

class MaterialDragDistanceConverter : IDragDistanceConverter {
    override fun convert(scrollDistance: Float, refreshDistance: Float): Float {
        val originalDragPercent = scrollDistance / refreshDistance
        val dragPercent = Math.min(1.0f, Math.abs(originalDragPercent))
        val extraOS = Math.abs(scrollDistance) - refreshDistance
        val tensionSlingshotPercent =
            Math.max(0f, Math.min(extraOS, refreshDistance * 2.0f) / refreshDistance)
        val tensionPercent = (tensionSlingshotPercent / 4 -
                Math.pow((tensionSlingshotPercent / 4).toDouble(), 2.0)).toFloat() * 2f
        val extraMove = refreshDistance * tensionPercent * 2
        val convertY = (refreshDistance * dragPercent + extraMove).toInt()
        return convertY.toFloat()
    }
}