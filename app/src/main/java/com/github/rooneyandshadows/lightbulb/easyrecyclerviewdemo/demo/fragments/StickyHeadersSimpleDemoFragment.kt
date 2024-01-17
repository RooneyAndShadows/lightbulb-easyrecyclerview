package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentViewBinding
import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbFragment
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils.Companion.getPhrase
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.FragmentDemoStickyHeadersSimpleBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateStickyHeadersSimpleData

@Suppress("SameParameterValue")
@FragmentScreen(screenName = "StickyHeaders", screenGroup = "Demo")
@LightbulbFragment(layoutName = "fragment_demo_sticky_headers_simple")
class StickyHeadersSimpleDemoFragment : BaseFragment() {
    @FragmentViewBinding
    lateinit var viewBinding: FragmentDemoStickyHeadersSimpleBinding

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(getPhrase(requireContext(), R.string.sticky_headers_simple_demo))
            .withTitle(getPhrase(requireContext(), R.string.app_name))
    }

    @Override
    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(fragmentView, savedInstanceState)
        if (savedInstanceState != null) return
        viewBinding.recyclerView.adapter.collection.set(generateStickyHeadersSimpleData())
    }
}