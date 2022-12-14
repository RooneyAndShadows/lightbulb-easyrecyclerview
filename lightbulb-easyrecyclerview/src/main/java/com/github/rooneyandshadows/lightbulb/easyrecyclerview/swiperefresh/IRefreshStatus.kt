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

/**
 * [RecyclerRefreshLayout.mRefreshView] all the custom drop-down refresh view need to
 * implements the interface
 */
interface IRefreshStatus {
    /**
     * When the content view has reached to the start point and refresh has been completed, view will be reset.
     */
    fun reset()

    /**
     * Refresh View is refreshing
     */
    fun refreshing()

    /**
     * refresh has been completed
     */
    fun refreshComplete()

    /**
     * Refresh View is dropped down to the refresh point
     */
    fun pullToRefresh()

    /**
     * Refresh View is released into the refresh point
     */
    fun releaseToRefresh()

    /**
     * @param pullDistance The drop-down distance of the refresh View
     * @param pullProgress The drop-down progress of the refresh View and the pullProgress may be more than 1.0f
     * pullProgress = pullDistance / refreshTargetOffset
     */
    fun pullProgress(pullDistance: Float, pullProgress: Float)
}