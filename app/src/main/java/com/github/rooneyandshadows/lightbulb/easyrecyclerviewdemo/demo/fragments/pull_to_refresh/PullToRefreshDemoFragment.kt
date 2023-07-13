package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.pull_to_refresh

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.BindView
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragmentWithViewModel
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.SimpleRecyclerView

@FragmentScreen(screenName = "PullToRefresh", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_pull_to_refresh")
class PullToRefreshDemoFragment : BaseFragmentWithViewModel<PullToRefreshDemoViewModel>() {
    override val viewModelClass: Class<PullToRefreshDemoViewModel>
        get() = PullToRefreshDemoViewModel::class.java

    @BindView(name = "recycler_view")
    lateinit var recyclerView: SimpleRecyclerView

    @Override
    override fun doOnCreate(savedInstanceState: Bundle?, viewModel: PullToRefreshDemoViewModel) {
        initPullToRefreshAction()
    }

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.pull_to_refresh_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @SuppressLint("InflateParams")
    @Override
    override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        val decoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
        recyclerView.addItemDecoration(decoration)
        recyclerView.setPullToRefreshListener {
            viewModel.refreshData()
        }
        if (savedInstanceState != null) return
        recyclerView.adapter!!.collection.set(viewModel.listData)
    }

    private fun initPullToRefreshAction() {
        viewModel.setListeners(object : PullToRefreshDemoViewModel.DataListener {
            override fun onSuccess(items: List<DemoModel>) {
                recyclerView.adapter!!.collection.set(items)
                recyclerView.onRefreshDataFinished()
            }

            override fun onFailure(errorDetails: String?) {
                println(errorDetails)
                recyclerView.onRefreshDataFinished()
            }
        })
    }
}