package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.BindView
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.FlexboxSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateLabelsData
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.LabelsRecyclerView

@FragmentScreen(screenName = "FlowLayout", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_flow_layout_manager")
class FlowLayoutManagerDemoFragment : BaseFragment() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: LabelsRecyclerView

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.flow_layout_manager_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @Override
    override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        recyclerView.apply {
            val itemDecoration = FlexboxSpaceItemDecoration(ResourceUtils.dpToPx(10), this)
            addItemDecoration(itemDecoration)
            if (savedInstanceState != null) return@apply
            adapter.collection.set(generateLabelsData())
        }
    }
}