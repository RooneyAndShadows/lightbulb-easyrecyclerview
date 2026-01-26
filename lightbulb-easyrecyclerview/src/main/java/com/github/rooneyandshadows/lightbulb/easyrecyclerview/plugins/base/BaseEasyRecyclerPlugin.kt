package com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.R
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel

internal abstract class BaseEasyRecyclerPlugin<ItemType : EasyAdapterDataModel>(
    protected val easyRecyclerView: EasyRecyclerView<ItemType>
) {
    protected val recyclerView: RecyclerView by lazy {
        return@lazy easyRecyclerView.findViewById(R.id.recyclerView)
    }
    protected val context: Context
        get() = easyRecyclerView.context
    protected val inflater: LayoutInflater
        get() = LayoutInflater.from(context)

    abstract fun register()

    abstract fun unregister()

    open fun adapterChanged(newAdapter: RecyclerView.Adapter<ViewHolder>) {
    }

    open fun saveState(): Bundle {
        return Bundle()
    }

    open fun restoreState(savedState: Bundle) {
    }
}