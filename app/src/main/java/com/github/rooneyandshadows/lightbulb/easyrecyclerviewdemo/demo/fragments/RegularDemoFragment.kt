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
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.FragmentDemoRegularBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.getHomeIcon

@FragmentScreen(screenName = "Regular", screenGroup = "Demo")
@LightbulbFragment(layoutName = "fragment_demo_regular")
class RegularDemoFragment : BaseFragment() {
    @FragmentViewBinding
    lateinit var viewBinding: FragmentDemoRegularBinding

    var data: IntArray? = null

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .withHomeIcon(getHomeIcon(requireContext()))
            .attachToDrawer(true)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.regular_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener(
            "TEST",
            this
        ) { _, result ->
            data = result.getIntArray("DATA")

        }
    }

    @Override
    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(fragmentView, savedInstanceState)
        println("========================= RESULT")
        println(data?.contentToString())
        viewBinding.recyclerView.apply {
            val itemDecoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
            addItemDecoration(itemDecoration)
            if (savedInstanceState != null) return@apply
            val initialData = generateData(50)
            adapter.collection.set(initialData)
        }
    }
}