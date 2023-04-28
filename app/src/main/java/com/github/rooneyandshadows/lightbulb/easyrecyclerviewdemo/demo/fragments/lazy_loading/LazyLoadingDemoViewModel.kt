package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.lazy_loading

import androidx.lifecycle.ViewModel
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.actions.LoadMoreDataAction
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel


class LazyLoadingDemoViewModel : ViewModel() {
    var lazyLoadingAction: LoadMoreDataAction<DemoModel>? = null

    @Override
    override fun onCleared() {
        super.onCleared()
        lazyLoadingAction?.dispose()
    }
}