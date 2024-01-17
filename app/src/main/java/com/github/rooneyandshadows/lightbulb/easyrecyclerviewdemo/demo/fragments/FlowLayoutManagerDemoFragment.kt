package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentViewBinding
import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbFragment
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.FlexboxSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.FragmentDemoFlowLayoutManagerBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateLabelsData

@FragmentScreen(screenName = "FlowLayout", screenGroup = "Demo")
@LightbulbFragment(layoutName = "fragment_demo_flow_layout_manager")
class FlowLayoutManagerDemoFragment : BaseFragment() {
    @FragmentViewBinding
    lateinit var viewBinding: FragmentDemoFlowLayoutManagerBinding

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(
                ResourceUtils.getPhrase(
                    requireContext(),
                    R.string.flow_layout_manager_demo
                )
            )
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @Override
    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(fragmentView, savedInstanceState)
        viewBinding.recyclerView.apply {
            val itemDecoration = FlexboxSpaceItemDecoration(ResourceUtils.dpToPx(10), this)
            addItemDecoration(itemDecoration)
            if (savedInstanceState != null) return@apply
            adapter.collection.set(generateLabelsData())
        }
    }
}