package com.github.rooneyandshadows.lightbulb.easyrecyclerview.actions

import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel

class LoadMoreDataAction<ItemType : EasyAdapterDataModel> @JvmOverloads constructor(
    task: Action<ItemType>,
    onSuccess: OnComplete<ItemType>,
    onError: OnError<ItemType>? = null
) : AsyncAction<ItemType>(task, onSuccess, onError) {

    @Override
    override fun beforeExecute(easyRecyclerView: EasyRecyclerView<ItemType>) {
        super.beforeExecute(easyRecyclerView)
        easyRecyclerView.showLoadingFooter(true)
    }

    @Override
    override fun onComplete(easyRecyclerView: EasyRecyclerView<ItemType>) {
        super.onComplete(easyRecyclerView)
        easyRecyclerView.showLoadingFooter(false)
    }

    @Override
    override fun onError(easyRecyclerView: EasyRecyclerView<ItemType>, exception: Exception) {
        super.onError(easyRecyclerView, exception)
        easyRecyclerView.showLoadingFooter(false)
    }
}