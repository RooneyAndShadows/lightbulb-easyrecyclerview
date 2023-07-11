package com.github.rooneyandshadows.lightbulb.easyrecyclerview.lazy_loading

import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel

fun interface LazyLoadingListener<ItemType : EasyAdapterDataModel> {
    fun execute(view: EasyRecyclerView<ItemType>)
}