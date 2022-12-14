package com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh

import android.util.Log
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

object RefreshLogger {
    private const val TAG = "RefreshLayout"
    private var mEnableDebug = false
    fun setEnableDebug(enableDebug: Boolean) {
        mEnableDebug = enableDebug
    }

    fun i(msg: String?) {
        if (mEnableDebug) {
            Log.i(TAG, msg!!)
        }
    }

    fun v(msg: String?) {
        if (mEnableDebug) {
            Log.v(TAG, msg!!)
        }
    }

    fun d(msg: String?) {
        if (mEnableDebug) {
            Log.d(TAG, msg!!)
        }
    }

    fun w(msg: String?) {
        if (mEnableDebug) {
            Log.w(TAG, msg!!)
        }
    }

    fun e(msg: String?) {
        if (mEnableDebug) {
            Log.e(TAG, msg!!)
        }
    }
}