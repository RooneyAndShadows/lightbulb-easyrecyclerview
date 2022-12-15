package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.BindView
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.SelectableAdapter
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import java.util.ArrayList

@FragmentScreen(screenName = "Selectable", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_selectable")
class SelectionDemoFragment : BaseFragment() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: EasyRecyclerView<DemoModel, SelectableAdapter>

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.selectable_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @Override
    override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        setupRecycler(savedInstanceState)
    }

    @SuppressLint("InflateParams")
    private fun setupRecycler(savedState: Bundle?) {
        recyclerView.adapter = SelectableAdapter()
        recyclerView.addHeaderView(layoutInflater.inflate(R.layout.demo_header_item_selectable_layout, null))
        recyclerView.addItemDecoration(VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(15)))
        if (savedState == null) recyclerView.adapter!!.setCollection(generateInitialData())
    }

    private fun generateInitialData(): List<DemoModel> {
        val models: MutableList<DemoModel> = ArrayList<DemoModel>()
        for (i in 1..20) models.add(DemoModel("Demo title $i", "Demo subtitle $i"))
        return models
    }
}