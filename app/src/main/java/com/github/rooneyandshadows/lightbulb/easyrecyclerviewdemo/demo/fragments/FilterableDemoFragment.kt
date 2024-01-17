package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentViewBinding
import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbFragment
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.FragmentDemoFilterableBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData

@FragmentScreen(screenName = "Filterable", screenGroup = "Demo")
@LightbulbFragment(layoutName = "fragment_demo_filterable")
class FilterableDemoFragment : BaseFragment() {
    @FragmentViewBinding
    lateinit var viewBinding: FragmentDemoFilterableBinding

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.filterable_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @Override
    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(fragmentView, savedInstanceState)
        val itemDecoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
        viewBinding.recyclerView.addItemDecoration(itemDecoration)
        if (savedInstanceState == null) {
            val dataToSet = generateData(20)
            viewBinding.recyclerView.adapter.collection.set(dataToSet)
        }
    }

    @Override
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @Override
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewBinding.recyclerView.adapter.collection.filter.filter(query ?: "")
                return true
            }

            @Override
            override fun onQueryTextChange(newText: String?): Boolean {
                viewBinding.recyclerView.adapter.collection.filter.filter(newText ?: "")
                return true
            }
        })
    }
}