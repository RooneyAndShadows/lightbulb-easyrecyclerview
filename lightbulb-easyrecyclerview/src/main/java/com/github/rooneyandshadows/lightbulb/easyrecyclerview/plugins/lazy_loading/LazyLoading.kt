package com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.lazy_loading

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import com.github.rooneyandshadows.lightbulb.commons.utils.BundleUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.R.layout.lv_loading_footer
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.base.BaseEasyRecyclerPlugin
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel

@SuppressLint("InflateParams")
internal class LazyLoading<ItemType : EasyAdapterDataModel>(
    easyRecyclerView: EasyRecyclerView<ItemType>
) : BaseEasyRecyclerPlugin<ItemType>(easyRecyclerView) {
    private var loadingListener: LazyLoadingListener<ItemType>? = null
    var hasMoreData = true
        private set
    var isLoading = false
        private set

    private var footerUpdateRunnable: Runnable? = null

    companion object {
        private const val HAS_MORE_DATA_TO_LOAD_KEY = "HAS_MORE_DATA_TO_LOAD_KEY"
        private const val IS_LOADING_KEY = "IS_LOADING_KEY"
        private const val FOOTER_ID = "footer_loading"
    }

    override fun register() {}
    override fun unregister() {}

    override fun saveState(): Bundle {
        val out = Bundle()
        BundleUtils.putBoolean(HAS_MORE_DATA_TO_LOAD_KEY, out, hasMoreData)
        BundleUtils.putBoolean(IS_LOADING_KEY, out, isLoading)
        return out
    }

    override fun restoreState(savedState: Bundle) {
        hasMoreData = BundleUtils.getBoolean(HAS_MORE_DATA_TO_LOAD_KEY, savedState)
        isLoading = BundleUtils.getBoolean(IS_LOADING_KEY, savedState)
        if (isLoading) {
            toggleLoadingFooter(true)
        }
    }

    fun setOnLoadingListener(listener: LazyLoadingListener<ItemType>?) {
        loadingListener = listener
    }

    fun load(showFooterLayout: Boolean = true) {
        if (loadingListener == null || !hasMoreData || isLoading) return

        isLoading = true
        toggleLoadingFooter(showFooterLayout)

        loadingListener!!.execute(easyRecyclerView)
    }

    fun finalizeLoading(hasMoreDataToLoad: Boolean) {
        isLoading = false
        toggleLoadingFooter(false)
        hasMoreData = hasMoreDataToLoad
    }

    private fun toggleLoadingFooter(show: Boolean) {
        footerUpdateRunnable?.let { easyRecyclerView.removeCallbacks(it) }
        footerUpdateRunnable = Runnable {
            if (show) {
                if (!easyRecyclerView.containsFooterView(FOOTER_ID)) {
                    easyRecyclerView.addFooterView(
                        id = FOOTER_ID,
                        viewFactory = { parent: ViewGroup ->
                            LayoutInflater.from(parent.context)
                                .inflate(lv_loading_footer, parent, false)
                        }
                    )
                }
            } else {
                if (easyRecyclerView.containsFooterView(FOOTER_ID)) {
                    easyRecyclerView.removeFooterView(FOOTER_ID)
                }
            }
            footerUpdateRunnable = null
        }

        easyRecyclerView.post(footerUpdateRunnable!!)
    }
}