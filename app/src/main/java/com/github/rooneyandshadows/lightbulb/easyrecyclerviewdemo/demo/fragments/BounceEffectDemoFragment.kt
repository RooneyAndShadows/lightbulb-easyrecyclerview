package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentViewBinding
import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbFragment
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.FragmentDemoBouncyBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData

@FragmentScreen(screenName = "Bouncy", screenGroup = "Demo")
@LightbulbFragment(layoutName = "fragment_demo_bouncy")
class BounceEffectDemoFragment : BaseFragment() {
    @FragmentViewBinding
    lateinit var viewBinding: FragmentDemoBouncyBinding

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.bounce_effect_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @Override
    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(fragmentView, savedInstanceState)
        val itemDecoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
        val dataToSet = generateData(20)
        viewBinding.recyclerView.addItemDecoration(itemDecoration)
        if (savedInstanceState != null) return
        viewBinding.recyclerView.adapter.collection.set(dataToSet)
    }
}