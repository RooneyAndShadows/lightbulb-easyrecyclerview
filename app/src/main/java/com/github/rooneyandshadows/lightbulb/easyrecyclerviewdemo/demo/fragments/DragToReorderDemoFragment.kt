package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.BindView
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.handler.EasyRecyclerViewTouchHandler.*
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.handler.TouchCallbacks
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.SimpleAdapter
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import java.util.ArrayList

@Suppress("SameParameterValue")
@FragmentScreen(screenName = "Drag", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_drag_to_reorder")
class DragToReorderDemoFragment : BaseFragment() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: EasyRecyclerView<DemoModel, SimpleAdapter>

    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.drag_to_reorder_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @Override
    override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.doOnViewCreated(fragmentView, savedInstanceState)
        setupRecycler(savedInstanceState)

    }

    @SuppressLint("InflateParams")
    @Override
    private fun setupRecycler(savedState: Bundle?) {
        recyclerView.setAdapter(SimpleAdapter(), configureSwipeHandler())
        recyclerView.addHeaderView(layoutInflater.inflate(R.layout.demo_header_item_drag_to_reorder, null))
        recyclerView.setEmptyLayout(generateEmptyLayout())
        recyclerView.addItemDecoration(VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(15)))
        if (savedState == null) recyclerView.adapter!!.setCollection(generateData(20))
    }

    private fun configureSwipeHandler(): TouchCallbacks<DemoModel> {
        return object : TouchCallbacks<DemoModel>(requireContext()) {
            override fun getAllowedSwipeDirections(item: DemoModel): Directions {
                return Directions.NONE
            }

            @Override
            override fun getAllowedDragDirections(item: DemoModel): Directions {
                return Directions.UP_DOWN
            }

            @Override
            override fun getActionBackgroundText(item: DemoModel): String {
                return item.itemName
            }

            @Override
            override fun onSwipeActionApplied(
                item: DemoModel,
                position: Int,
                adapter: EasyRecyclerAdapter<DemoModel>,
                direction: Directions
            ) {
            }

            @Override
            override fun onActionCancelled(item: DemoModel, adapter: EasyRecyclerAdapter<DemoModel>, position: Int) {
            }

            @Override
            override fun getSwipeBackgroundColor(direction: Directions): Int {
                return ResourceUtils.getColorByAttribute(requireContext(), R.attr.colorError)
            }

            @Override
            override fun getSwipeIcon(direction: Directions): Drawable {
                return ResourceUtils.getDrawable(requireContext(), R.drawable.icon_delete)!!
            }

            @Override
            override fun getPendingActionText(direction: Directions): String {
                return "Delete"
            }

            @Override
            override fun getConfiguration(context: Context): SwipeConfiguration {
                return SwipeConfiguration(requireContext())
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun generateEmptyLayout(): View {
        val emptyLayout: View = layoutInflater.inflate(R.layout.demo_empty_layout, null)
        emptyLayout.findViewById<View>(R.id.emptyLayoutRefreshButton).setOnClickListener {
            val emptyLayoutImage = emptyLayout.findViewById<ImageView>(R.id.emptyImage)
            val progressBar: ProgressBar = emptyLayout.findViewById(R.id.progressBar)
            emptyLayoutImage.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            emptyLayout.postDelayed({ recyclerView.adapter!!.appendCollection(generateData(20)) }, 2000)
        }
        return emptyLayout
    }

    private fun generateData(count: Int): List<DemoModel> {
        val models: MutableList<DemoModel> = ArrayList<DemoModel>()
        for (i in 1..count) models.add(DemoModel("Demo title $i", "Demo subtitle $i"))
        return models
    }
}