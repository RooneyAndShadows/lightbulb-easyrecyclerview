package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.pull_to_refresh

import androidx.lifecycle.ViewModel
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.actions.LoadMoreDataAction
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.actions.RefreshDataAction
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel


class PullToRefreshDemoViewModel : ViewModel() {
    var pullToRefreshAction: RefreshDataAction<DemoModel>? = null

    @Override
    override fun onCleared() {
        super.onCleared()
        pullToRefreshAction?.dispose()
    }
}