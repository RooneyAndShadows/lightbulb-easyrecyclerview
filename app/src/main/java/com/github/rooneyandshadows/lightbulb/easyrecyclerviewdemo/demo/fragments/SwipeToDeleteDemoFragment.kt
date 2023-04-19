package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.BindView
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters.SimpleAdapter

@Suppress("SameParameterValue")
@FragmentScreen(screenName = "SwipeToDelete", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_swipe_to_delete")
class SwipeToDeleteDemoFragment : BaseFragment() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: EasyRecyclerView<DemoModel>

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(contextActivity, R.string.swipe_to_delete_demo))
            .withTitle(ResourceUtils.getPhrase(contextActivity, R.string.app_name))
    }

    @Override
    override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        if (savedInstanceState != null) return
        recyclerView.adapter.collection.set(generateData(20))
    }
}