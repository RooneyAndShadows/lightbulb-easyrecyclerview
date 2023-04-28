package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.BindView
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.PermissionUtils.Companion.launch
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.getHomeIcon
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.SimpleRecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@FragmentScreen(screenName = "Regular", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_regular", hasLeftDrawer = true)
class RegularDemoFragment : BaseFragment() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: SimpleRecyclerView

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .withHomeIcon(getHomeIcon(requireContext()))
            .attachToDrawer(true)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.regular_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @Override
    override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        recyclerView.apply {
            val itemDecoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
            addItemDecoration(itemDecoration)
            if (savedInstanceState != null) return@apply
            val initialData = generateData(20)
            adapter.collection.set(initialData)
        }
    }
}