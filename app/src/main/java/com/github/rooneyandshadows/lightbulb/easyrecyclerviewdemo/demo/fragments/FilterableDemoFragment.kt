package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.BindView
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.getHomeIcon
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.FilterableRecyclerView

@FragmentScreen(screenName = "Filterable", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_filterable", hasLeftDrawer = false)
class FilterableDemoFragment : BaseFragment() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: FilterableRecyclerView

    @BindView(name = "search_view")
    lateinit var searchView: SearchView

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.filterable_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @Override
    override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        recyclerView.apply {
            val itemDecoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
            addItemDecoration(itemDecoration)
            if (savedInstanceState == null) {
                val initialData = generateData(20)
                adapter.collection.set(initialData)
            }
        }
    }

    @Override
    override fun doOnViewStateRestored(savedInstanceState: Bundle?) {
        super.doOnViewStateRestored(savedInstanceState)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @Override
            override fun onQueryTextSubmit(query: String?): Boolean {
                recyclerView.adapter.collection.filter.filter(query ?: "")
                return true
            }

            @Override
            override fun onQueryTextChange(newText: String?): Boolean {
                recyclerView.adapter.collection.filter.filter(newText ?: "")
                return true
            }
        })
    }
}