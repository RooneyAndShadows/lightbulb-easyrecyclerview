package com.github.rooneyandshadows.lightbulb.easyrecyclerview.adapter

import androidx.recyclerview.widget.ConcatAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.adapters.StaticViewsAdapter

internal class AdapterManager<ItemType : EasyAdapterDataModel>() {
    val rootAdapter: ConcatAdapter = ConcatAdapter()
    val headersAdapter: StaticViewsAdapter = StaticViewsAdapter()
    val footersAdapter: StaticViewsAdapter = StaticViewsAdapter()
    var dataAdapter: EasyRecyclerAdapter<ItemType>? = null
        private set

    fun setDataAdapter(adapter: EasyRecyclerAdapter<ItemType>?) {
        dataAdapter?.let { currentAdapter ->
            rootAdapter.removeAdapter(currentAdapter)
        }
        dataAdapter = adapter

        adapter?.let { newAdapter ->
            rootAdapter.addAdapter(1, newAdapter)
        }
    }

    init {
        rootAdapter.addAdapter(headersAdapter)
        rootAdapter.addAdapter(footersAdapter)
    }
}