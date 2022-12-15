package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.BindView
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.FlexboxSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity.MainActivity
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity.MenuConfigurations
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.LabelsAdapter
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import java.util.ArrayList

@FragmentScreen(screenName = "FlowLayout", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_flow_layout_manager")
class FlowLayoutManagerDemoFragment : BaseFragment() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: EasyRecyclerView<DemoModel, LabelsAdapter>

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
        if (getFragmentState() === FragmentStates.CREATED) {
            BaseActivity.updateMenuConfiguration(
                requireContext(),
                MainActivity::class.java
            ) { activity: BaseActivity -> MenuConfigurations.getConfiguration(activity) }
        }
        setupRecycler(savedInstanceState)
    }

    private fun setupRecycler(savedState: Bundle?) {
        recyclerView.adapter = LabelsAdapter()
        recyclerView.addItemDecoration(FlexboxSpaceItemDecoration(ResourceUtils.dpToPx(10), recyclerView))
        if (savedState == null) recyclerView.adapter!!.setCollection(generateInitialData())
    }

    private fun generateInitialData(): List<DemoModel> {
        val models: MutableList<DemoModel> = ArrayList<DemoModel>()
        models.add(DemoModel("Star", ""))
        models.add(DemoModel("Tag", ""))
        models.add(DemoModel("Search", ""))
        models.add(DemoModel("Block", ""))
        models.add(DemoModel("Center", ""))
        models.add(DemoModel("Right", ""))
        models.add(DemoModel("Cat", ""))
        models.add(DemoModel("Tree", ""))
        models.add(DemoModel("Person", ""))
        models.add(DemoModel("Generation", ""))
        models.add(DemoModel("Utility", ""))
        models.add(DemoModel("Category", ""))
        models.add(DemoModel("Label", ""))
        models.add(DemoModel("Side", ""))
        models.add(DemoModel("Section", ""))
        models.add(DemoModel("Page", ""))
        models.add(DemoModel("Class", ""))
        models.add(DemoModel("Type", ""))
        models.add(DemoModel("Performance", ""))
        models.add(DemoModel("Object", ""))
        models.add(DemoModel("Count", ""))
        models.add(DemoModel("Letter", ""))
        models.add(DemoModel("Subtitle", ""))
        models.add(DemoModel("Height", ""))
        models.add(DemoModel("Strenght", ""))
        models.add(DemoModel("Star", ""))
        models.add(DemoModel("Tag", ""))
        models.add(DemoModel("Search", ""))
        models.add(DemoModel("Block", ""))
        models.add(DemoModel("Center", ""))
        models.add(DemoModel("Right", ""))
        models.add(DemoModel("Cat", ""))
        models.add(DemoModel("Tree", ""))
        models.add(DemoModel("Person", ""))
        models.add(DemoModel("Generation", ""))
        models.add(DemoModel("Utility", ""))
        models.add(DemoModel("Category", ""))
        models.add(DemoModel("Label", ""))
        models.add(DemoModel("Side", ""))
        models.add(DemoModel("Section", ""))
        models.add(DemoModel("Page", ""))
        models.add(DemoModel("Class", ""))
        models.add(DemoModel("Type", ""))
        models.add(DemoModel("Performance", ""))
        models.add(DemoModel("Object", ""))
        models.add(DemoModel("Count", ""))
        models.add(DemoModel("Letter", ""))
        models.add(DemoModel("Subtitle", ""))
        models.add(DemoModel("Height", ""))
        models.add(DemoModel("Strenght", ""))
        models.add(DemoModel("Star", ""))
        models.add(DemoModel("Tag", ""))
        models.add(DemoModel("Search", ""))
        models.add(DemoModel("Block", ""))
        models.add(DemoModel("Center", ""))
        models.add(DemoModel("Right", ""))
        models.add(DemoModel("Cat", ""))
        models.add(DemoModel("Tree", ""))
        models.add(DemoModel("Person", ""))
        models.add(DemoModel("Generation", ""))
        models.add(DemoModel("Utility", ""))
        models.add(DemoModel("Category", ""))
        models.add(DemoModel("Label", ""))
        models.add(DemoModel("Side", ""))
        models.add(DemoModel("Section", ""))
        models.add(DemoModel("Page", ""))
        models.add(DemoModel("Class", ""))
        models.add(DemoModel("Type", ""))
        models.add(DemoModel("Performance", ""))
        models.add(DemoModel("Object", ""))
        models.add(DemoModel("Count", ""))
        models.add(DemoModel("Letter", ""))
        models.add(DemoModel("Subtitle", ""))
        models.add(DemoModel("Height", ""))
        models.add(DemoModel("Strenght", ""))
        models.add(DemoModel("Star", ""))
        models.add(DemoModel("Tag", ""))
        models.add(DemoModel("Search", ""))
        models.add(DemoModel("Block", ""))
        models.add(DemoModel("Center", ""))
        models.add(DemoModel("Right", ""))
        models.add(DemoModel("Cat", ""))
        models.add(DemoModel("Tree", ""))
        models.add(DemoModel("Person", ""))
        models.add(DemoModel("Generation", ""))
        models.add(DemoModel("Utility", ""))
        models.add(DemoModel("Category", ""))
        models.add(DemoModel("Label", ""))
        models.add(DemoModel("Side", ""))
        models.add(DemoModel("Section", ""))
        models.add(DemoModel("Page", ""))
        models.add(DemoModel("Class", ""))
        models.add(DemoModel("Type", ""))
        models.add(DemoModel("Performance", ""))
        models.add(DemoModel("Object", ""))
        models.add(DemoModel("Count", ""))
        models.add(DemoModel("Letter", ""))
        models.add(DemoModel("Subtitle", ""))
        models.add(DemoModel("Height", ""))
        models.add(DemoModel("Strenght", ""))
        models.add(DemoModel("Star", ""))
        models.add(DemoModel("Tag", ""))
        models.add(DemoModel("Search", ""))
        models.add(DemoModel("Block", ""))
        models.add(DemoModel("Center", ""))
        models.add(DemoModel("Right", ""))
        models.add(DemoModel("Cat", ""))
        models.add(DemoModel("Tree", ""))
        models.add(DemoModel("Person", ""))
        models.add(DemoModel("Generation", ""))
        models.add(DemoModel("Utility", ""))
        models.add(DemoModel("Category", ""))
        models.add(DemoModel("Label", ""))
        models.add(DemoModel("Side", ""))
        models.add(DemoModel("Section", ""))
        models.add(DemoModel("Page", ""))
        models.add(DemoModel("Class", ""))
        models.add(DemoModel("Type", ""))
        models.add(DemoModel("Performance", ""))
        models.add(DemoModel("Object", ""))
        models.add(DemoModel("Count", ""))
        models.add(DemoModel("Letter", ""))
        models.add(DemoModel("Subtitle", ""))
        models.add(DemoModel("Height", ""))
        models.add(DemoModel("Strenght", ""))
        return models
    }
}