package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.BindView
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.SimpleAdapter
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel

@FragmentScreen(screenName = "PullToRefresh", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_pull_to_refresh")
class PullToRefreshDemoFragment : BaseFragment() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: EasyRecyclerView<DemoModel, SimpleAdapter>

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.lazy_loading_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @Override
    override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        setupRecycler(savedInstanceState)
    }

    @SuppressLint("InflateParams")
    private fun setupRecycler(savedState: Bundle?) {
        recyclerView.adapter = SimpleAdapter()
        recyclerView.addItemDecoration(VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(15)))
        recyclerView.addHeaderView(layoutInflater.inflate(R.layout.demo_header_item_pull_to_refresh_layout, null))
        recyclerView.setRefreshCallback(object : EasyRecyclerView.RefreshCallback<DemoModel, SimpleAdapter> {
            override fun refresh(view: EasyRecyclerView<DemoModel, SimpleAdapter>) {
                recyclerView.postDelayed(
                    {
                        recyclerView.adapter!!.setCollection(generateData(10))
                        recyclerView.showRefreshLayout(false)
                    }, 2000
                )
            }

        })
        if (savedState == null) recyclerView.adapter!!.setCollection(generateData(4))
    }
}