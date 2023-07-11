package com.github.rooneyandshadows.lightbulb.easyrecyclerview.pull_to_refresh

import android.R.attr.colorBackground
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.github.rooneyandshadows.lightbulb.commons.utils.BundleUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils.Companion.getColorByAttribute
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils.Companion.getDimenPxById
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.R.dimen.erv_header_refresh_indicator_size
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.pull_to_refresh.refresh_layout.RecyclerRefreshLayout
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.pull_to_refresh.refresh_layout.RefreshView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel

internal class PullToRefresh<ItemType : EasyAdapterDataModel>(
    private val recyclerView: EasyRecyclerView<ItemType>
) {
    private val refreshLayout: RecyclerRefreshLayout = recyclerView.findViewById(R.id.refreshLayout)
    private val refreshView: RefreshView = RefreshView(context)
    private val context: Context
        get() = recyclerView.context
    private var onRefreshListener: PullToRefreshListener<ItemType>? = null
    var enabled: Boolean = false
        private set
    var refreshing = false
        private set
    val hasAttachedListener: Boolean
        get() = onRefreshListener != null

    companion object {
        private const val ENABLED_KEY = "ENABLED_KEY"
        private const val IS_REFRESHING_KEY = "IS_REFRESHING_KEY"
        private const val IS_SHOWING_REFRESH_LAYOUT = "IS_SHOWING_REFRESH_LAYOUT"
    }

    init {
        configureRefreshLayout()
        setEnabled(false)
    }

    fun setOnRefreshListener(listener: PullToRefreshListener<ItemType>?) {
        onRefreshListener = listener
        setEnabled(listener != null)
    }

    fun setEnabled(newState: Boolean) {
        enabled = newState
        enableRefreshLayout(newState)
    }

    fun refresh(showLoading: Boolean = false) {
        if (!enabled || refreshing || onRefreshListener == null) {
            return
        }
        if (showLoading) {
            showRefreshLayout(true)
        }
        refreshing = true
        onRefreshListener!!.execute(recyclerView)
    }

    fun finalizeRefresh() {
        if (!enabled) return
        refreshing = false
        showRefreshLayout(false)
    }

    fun saveState(): Bundle {
        val out = Bundle()
        BundleUtils.putBoolean(ENABLED_KEY, out, enabled)
        BundleUtils.putBoolean(IS_REFRESHING_KEY, out, refreshing)
        BundleUtils.putBoolean(IS_SHOWING_REFRESH_LAYOUT, out, refreshLayout.isRefreshing)
        return out
    }

    fun restoreState(savedState: Bundle) {
        val enabled = BundleUtils.getBoolean(ENABLED_KEY, savedState)
        val isShowingRefreshLayout = BundleUtils.getBoolean(IS_SHOWING_REFRESH_LAYOUT, savedState)
        setEnabled(enabled)
        refreshing = BundleUtils.getBoolean(IS_REFRESHING_KEY, savedState)
        showRefreshLayout(isShowingRefreshLayout)
    }

    private fun enableRefreshLayout(newState: Boolean) {
        refreshLayout.isEnabled = newState
        if (newState) {
            recyclerView.bounceOverscrollEnabled = false
        }
    }

    private fun configureRefreshLayout() {
        val indicatorSize: Int = getDimenPxById(context, erv_header_refresh_indicator_size)
        val layoutParams = LayoutParams(MATCH_PARENT, indicatorSize)
        val refreshBackgroundColor: Int = getColorByAttribute(context, colorBackground)
        refreshView.setBackgroundColor(refreshBackgroundColor)
        refreshLayout.setRefreshView(refreshView, layoutParams)
        refreshLayout.setRefreshStyle(RecyclerRefreshLayout.RefreshStyle.NORMAL)
        refreshLayout.setOnRefreshListener {
            onRefreshListener?.execute(recyclerView)
        }
    }

    private fun showRefreshLayout(newState: Boolean) {
        if (!enabled || refreshLayout.isRefreshing == newState) return
        refreshLayout.setRefreshing(newState)
    }
}