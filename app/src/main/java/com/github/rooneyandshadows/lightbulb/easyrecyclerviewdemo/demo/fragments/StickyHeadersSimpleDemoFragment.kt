package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.BindView
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.StickyHeaderItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity.MainActivity
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity.MenuConfigurations
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.StickyAdapterSimple
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateStickyHeadersSimpleData
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.StickySimpleDemoModel

@Suppress("SameParameterValue")
@FragmentScreen(screenName = "StickyHeaders", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_sticky_headers_simple")
class StickyHeadersSimpleDemoFragment : BaseFragment() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: EasyRecyclerView<StickySimpleDemoModel, StickyAdapterSimple>

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.sticky_headers_simple_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @Override
    override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.doOnViewCreated(fragmentView, savedInstanceState)
        if (getFragmentState() === FragmentStates.CREATED) {
            BaseActivity.updateMenuConfiguration(
                requireContext(),
                MainActivity::class.java,
            ) { activity: BaseActivity -> MenuConfigurations.getConfiguration(activity) }
        }
        setupRecycler(savedInstanceState)
    }

    private fun setupRecycler(savedState: Bundle?) {
        recyclerView.adapter = StickyAdapterSimple()
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        recyclerView.addItemDecoration(object : StickyHeaderItemDecoration(recyclerView.adapter!!) {
            @Override
            override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                val firstVisibleItemPosition =
                    (parent.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()
                if (firstVisibleItemPosition != 0) super.onDrawOver(c, parent, state)
            }
        })
        if (savedState == null) recyclerView.adapter!!.setCollection(generateStickyHeadersSimpleData())
    }
}