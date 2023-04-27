package com.github.rooneyandshadows.lightbulb.easyrecyclerview.actions

import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel

class RefreshDataAction<ItemType : EasyAdapterDataModel>(
    task: Action<ItemType>,
    onSuccess: OnComplete<ItemType>,
    onError: OnError<ItemType>? = null,
) : AsyncAction<ItemType>(task, onSuccess, onError) {
    @Override
    override fun beforeExecute(easyRecyclerView: EasyRecyclerView<ItemType>) {
        super.beforeExecute(easyRecyclerView)
        easyRecyclerView.showRefreshLayout(true)
    }

    @Override
    override fun onComplete(easyRecyclerView: EasyRecyclerView<ItemType>) {
        super.onComplete(easyRecyclerView)
        easyRecyclerView.showRefreshLayout(false)
    }

    @Override
    override fun onError(easyRecyclerView: EasyRecyclerView<ItemType>, exception: Exception) {
        super.onError(easyRecyclerView, exception)
        easyRecyclerView.showRefreshLayout(false)
    }
}