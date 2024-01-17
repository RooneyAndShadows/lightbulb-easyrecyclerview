package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentViewBinding
import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbFragment
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.FragmentDemoSwipeToDeleteBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData

@Suppress("SameParameterValue")
@FragmentScreen(screenName = "SwipeToDelete", screenGroup = "Demo")
@LightbulbFragment(layoutName = "fragment_demo_swipe_to_delete")
class SwipeToDeleteDemoFragment : BaseFragment() {
    @FragmentViewBinding
    lateinit var viewBinding: FragmentDemoSwipeToDeleteBinding

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(contextActivity, R.string.swipe_to_delete_demo))
            .withTitle(ResourceUtils.getPhrase(contextActivity, R.string.app_name))
    }

    @Override
    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(fragmentView, savedInstanceState)
        if (savedInstanceState != null) return
        viewBinding.recyclerView.adapter.collection.set(generateData(20))
    }
}