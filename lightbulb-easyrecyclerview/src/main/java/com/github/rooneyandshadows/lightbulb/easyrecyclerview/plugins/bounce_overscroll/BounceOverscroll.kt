package com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.bounce_overscroll

import android.os.Bundle
import com.github.rooneyandshadows.lightbulb.commons.utils.BundleUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.base.BaseEasyRecyclerPlugin
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.bounce_overscroll.edge_factory.BounceEdge
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.bounce_overscroll.edge_factory.DefaultEdge
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel

internal class BounceOverscroll<ItemType : EasyAdapterDataModel>(
    easyRecyclerView: EasyRecyclerView<ItemType>
) : BaseEasyRecyclerPlugin<ItemType>(easyRecyclerView) {
    private val defaultEdge = DefaultEdge()
    private val isPullToRefreshEnabled: Boolean
        get() = easyRecyclerView.isPullToRefreshEnabled
    var enabled: Boolean = false
        set(value) {
            field = value
            syncEdgeFactory()
        }

    companion object {
        private const val ENABLED_KEY = "ENABLED_KEY"
    }

    init {
        syncEdgeFactory()
    }

    override fun saveState(): Bundle {
        val out = Bundle()
        BundleUtils.putBoolean(ENABLED_KEY, out, enabled)
        return out
    }

    override fun restoreState(savedState: Bundle) {
        enabled = BundleUtils.getBoolean(ENABLED_KEY, savedState)
    }

    private fun syncEdgeFactory() {
        if (enabled && isPullToRefreshEnabled) {
            recyclerView.edgeEffectFactory = defaultEdge
        } else {
            recyclerView.edgeEffectFactory = if (enabled) BounceEdge() else defaultEdge
        }
    }
}