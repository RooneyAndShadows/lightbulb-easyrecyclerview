package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
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
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.SimpleRecyclerView

@FragmentScreen(screenName = "Regular", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_regular", hasLeftDrawer = true)
class RegularDemoFragment : BaseFragment() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: SimpleRecyclerView

    @BindView(name = "testFilter")
    lateinit var testField: EditText

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
        testField.doOnTextChanged { text, start, before, count ->
            recyclerView.filter(text.toString())

        }
        recyclerView.apply {
            val itemDecoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(15))
            addItemDecoration(itemDecoration)
            if (savedInstanceState == null) {
                val initialData = generateData(20)
                adapter.setCollection(initialData)
            }
        }
    }
}