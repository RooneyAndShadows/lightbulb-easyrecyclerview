package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
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
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.StickyAdapterAdvanced
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.StickyAdvancedDemoModel
import java.time.OffsetDateTime
import java.util.*

@FragmentScreen(screenName = "StickyHeadersAdvanced", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_sticky_headers_advanced")
class StickyHeadersAdvancedDemoFragment : BaseFragment() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: EasyRecyclerView<StickyAdvancedDemoModel, StickyAdapterAdvanced>

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.sticky_headers_advanced_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        if (getFragmentState() === FragmentStates.CREATED) {
            BaseActivity.updateMenuConfiguration(
                requireContext(),
                MainActivity::class.java
            ) { activity: BaseActivity? -> MenuConfigurations.getConfiguration(activity) }
        }
        setupRecycler(savedInstanceState)
    }

    private fun setupRecycler(savedState: Bundle?) {
        recyclerView.adapter = StickyAdapterAdvanced()
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        recyclerView.addItemDecoration(object : StickyHeaderItemDecoration(recyclerView.adapter!!) {
            override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                val firstVisibleItemPosition =
                    (parent.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()
                if (firstVisibleItemPosition != 0) super.onDrawOver(c, parent, state)
            }
        })
        if (savedState == null) recyclerView.adapter!!.setCollection(generateInitialData())
    }

    private fun generateInitialData(): List<StickyAdvancedDemoModel> {
        val models: MutableList<StickyAdvancedDemoModel> = ArrayList<StickyAdvancedDemoModel>()
        var date: OffsetDateTime = DateUtilsOffsetDate.nowLocal()
        for (position in 1..60) {
            val isHeader = isPositionHeader(position)
            models.add(
                StickyAdvancedDemoModel(
                    date,
                    isHeader,
                    String.format("Demo title %s", position),
                    String.format("Demo subtitle %s", position)
                )
            )
            if (isHeader) date = DateUtilsOffsetDate.addHours(date, 24)
        }
        return models
    }

    private fun isPositionHeader(position: Int): Boolean {
        val headerPositions = intArrayOf(1, 7, 12, 20, 25, 34, 40)
        return Arrays.stream(headerPositions).anyMatch { value: Int -> position == value }
    }
}