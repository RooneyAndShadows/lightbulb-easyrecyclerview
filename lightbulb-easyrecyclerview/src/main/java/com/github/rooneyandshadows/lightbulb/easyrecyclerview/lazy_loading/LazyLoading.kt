package com.github.rooneyandshadows.lightbulb.easyrecyclerview.lazy_loading

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.LayoutInflater.*
import android.view.View
import com.github.rooneyandshadows.lightbulb.commons.utils.BundleUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.R.layout.lv_loading_footer
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel

@Suppress("JoinDeclarationAndAssignment")
@SuppressLint("InflateParams")
internal class LazyLoading<ItemType : EasyAdapterDataModel>(
    private val recyclerView: EasyRecyclerView<ItemType>
) {
    private val context
        get() = recyclerView.context
    private val inflater: LayoutInflater
        get() = from(context)
    private var loadingListener: LazyLoadingListener<ItemType>? = null
    private var loadingFooterView: View
    var hasMoreData = true
        private set
    var isLoading = false
        private set

    init {
        loadingFooterView = inflater.inflate(lv_loading_footer, null)
    }

    companion object {
        private const val HAS_MORE_DATA_TO_LOAD_KEY = "HAS_MORE_DATA_TO_LOAD_KEY"
        private const val IS_LOADING_KEY = "IS_LOADING_KEY"
    }

    fun setOnLoadingListener(listener: LazyLoadingListener<ItemType>?) {
        loadingListener = listener
    }

    fun load(showFooterLayout: Boolean = true) {
        if (loadingListener == null || !hasMoreData || isLoading) {
            return
        }
        isLoading = true
        enableLoadingFooter(showFooterLayout)
        loadingListener!!.execute(recyclerView)
    }


    fun finalizeLoading(hasMoreDataToLoad: Boolean) {
        isLoading = false
        enableLoadingFooter(false)
        hasMoreData = hasMoreDataToLoad
    }

    fun saveState(): Bundle {
        val out = Bundle()
        BundleUtils.putBoolean(HAS_MORE_DATA_TO_LOAD_KEY, out, hasMoreData)
        BundleUtils.putBoolean(IS_LOADING_KEY, out, isLoading)
        return out
    }

    fun restoreState(savedState: Bundle) {
        hasMoreData = BundleUtils.getBoolean(HAS_MORE_DATA_TO_LOAD_KEY, savedState)
        isLoading = BundleUtils.getBoolean(IS_LOADING_KEY, savedState)
        if (isLoading) {
            enableLoadingFooter(true)
        }
    }

    private fun enableLoadingFooter(newState: Boolean) {
        recyclerView.post {
            if (newState) {
                recyclerView.addFooterView(loadingFooterView)
            } else {
                recyclerView.removeFooterView(loadingFooterView)
            }
        }
    }
}