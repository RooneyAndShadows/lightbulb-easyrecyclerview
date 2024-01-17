package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.annotation.SuppressLint
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
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.FragmentDemoSelectableBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.collection.ExtendedCollection.SelectionChangeListener

@FragmentScreen(screenName = "Selectable", screenGroup = "Demo")
@LightbulbFragment(layoutName = "fragment_demo_selectable")
class SelectionDemoFragment : BaseFragment() {
    @FragmentViewBinding
    lateinit var viewBinding: FragmentDemoSelectableBinding

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.selectable_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @SuppressLint("InflateParams")
    @Override
    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(fragmentView, savedInstanceState)
        viewBinding.recyclerView.apply {
            val headerView =
                layoutInflater.inflate(R.layout.demo_header_item_selectable_layout, null)
            val itemDecoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
            addHeaderView(headerView)
            addItemDecoration(itemDecoration)
            if (savedInstanceState != null) return@apply
            adapter.collection.set(generateData(20))
            adapter.collection.addOnSelectionChangeListener(object : SelectionChangeListener {
                override fun onChanged(newSelection: IntArray?) {
                    val result = Bundle().apply {
                        putIntArray("DATA", newSelection)
                    }
                    parentFragmentManager.setFragmentResult("TEST", result)
                }
            })
        }
    }
}