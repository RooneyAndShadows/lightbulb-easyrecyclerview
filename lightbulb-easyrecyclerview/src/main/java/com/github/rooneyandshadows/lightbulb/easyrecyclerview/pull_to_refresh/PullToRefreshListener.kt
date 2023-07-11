package com.github.rooneyandshadows.lightbulb.easyrecyclerview.pull_to_refresh

import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel

fun interface PullToRefreshListener<ItemType : EasyAdapterDataModel> {
    fun execute(view: EasyRecyclerView<ItemType>)
}