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
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateStickyHeadersSimpleData
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.StickySimpleDemoModel
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.StickyRecyclerViewSimple

@Suppress("SameParameterValue")
@FragmentScreen(screenName = "StickyHeaders", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_sticky_headers_simple")
class StickyHeadersSimpleDemoFragment : BaseFragment() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: StickyRecyclerViewSimple

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.sticky_headers_simple_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @Override
    override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.doOnViewCreated(fragmentView, savedInstanceState)
        if (savedInstanceState != null) return
        recyclerView.adapter.collection.set(generateStickyHeadersSimpleData())
    }
}