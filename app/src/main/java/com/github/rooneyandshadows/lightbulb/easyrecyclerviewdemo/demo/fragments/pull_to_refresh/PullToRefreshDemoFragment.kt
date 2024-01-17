package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.pull_to_refresh

import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentViewBinding
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentViewModel
import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbFragment
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.FragmentDemoPullToRefreshBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel

@FragmentScreen(screenName = "PullToRefresh", screenGroup = "Demo")
@LightbulbFragment(layoutName = "fragment_demo_pull_to_refresh")
class PullToRefreshDemoFragment : BaseFragment() {
    @FragmentViewBinding
    lateinit var viewBinding: FragmentDemoPullToRefreshBinding

    @FragmentViewModel
    lateinit var viewModel: PullToRefreshDemoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPullToRefreshAction()
    }

    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.pull_to_refresh_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(fragmentView, savedInstanceState)
        val decoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
        viewBinding.recyclerView.addItemDecoration(decoration)
        viewBinding.recyclerView.setPullToRefreshListener {
            viewModel.refreshData()
        }
        if (savedInstanceState != null) return
        viewBinding.recyclerView.adapter.collection.set(viewModel.listData)
    }

    private fun initPullToRefreshAction() {
        viewModel.setListeners(object : PullToRefreshDemoViewModel.DataListener {
            override fun onSuccess(items: List<DemoModel>) {
                viewBinding.recyclerView.adapter.collection.set(items)
                viewBinding.recyclerView.onRefreshDataFinished()
            }

            override fun onFailure(errorDetails: String?) {
                println(errorDetails)
                viewBinding.recyclerView.onRefreshDataFinished()
            }
        })
    }
}