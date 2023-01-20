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
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.handler.EasyRecyclerViewTouchHandler
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.handler.EasyRecyclerViewTouchHandler.SwipeConfiguration
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.handler.TouchCallbacks
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.*
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter

@Suppress("SameParameterValue")
@FragmentScreen(screenName = "SwipeToDelete", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_swipe_to_delete")
class SwipeToDeleteDemoFragment : BaseFragment() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: EasyRecyclerView<DemoModel, SimpleAdapter>

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(contextActivity, R.string.swipe_to_delete_demo))
            .withTitle(ResourceUtils.getPhrase(contextActivity, R.string.app_name))
    }

    @Override
    override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        setupRecycler(savedInstanceState)
    }

    @SuppressLint("InflateParams")
    private fun setupRecycler(savedState: Bundle?) {
        recyclerView.setAdapter(SimpleAdapter(), configureSwipeHandler(recyclerView))
        recyclerView.addHeaderView(layoutInflater.inflate(R.layout.demo_header_item_swipe_to_delete, null))
        recyclerView.setEmptyLayout(generateEmptyLayout())
        recyclerView.addItemDecoration(VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(15)))
        if (savedState == null) recyclerView.adapter!!.setCollection(generateData(20))
    }

    private fun configureSwipeHandler(recyclerView: EasyRecyclerView<DemoModel, SimpleAdapter>?): TouchCallbacks<DemoModel> {
        return object : TouchCallbacks<DemoModel>(requireContext()) {
            @Override
            override fun getAllowedSwipeDirections(item: DemoModel): EasyRecyclerViewTouchHandler.Directions {
                return EasyRecyclerViewTouchHandler.Directions.LEFT_RIGHT
            }

            @Override
            override fun getAllowedDragDirections(item: DemoModel): EasyRecyclerViewTouchHandler.Directions {
                return EasyRecyclerViewTouchHandler.Directions.NONE
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
                direction: EasyRecyclerViewTouchHandler.Directions
            ) {
                recyclerView!!.post {
                    val actualPosition = recyclerView.adapter!!.getPosition(item)
                    adapter.removeItem(actualPosition)
                }
            }

            @Override
            override fun onActionCancelled(item: DemoModel, adapter: EasyRecyclerAdapter<DemoModel>, position: Int) {
            }

            @Override
            override fun getSwipeBackgroundColor(direction: EasyRecyclerViewTouchHandler.Directions): Int {
                return ResourceUtils.getColorByAttribute(contextActivity, R.attr.colorError)
            }

            @Override
            override fun getSwipeIcon(direction: EasyRecyclerViewTouchHandler.Directions): Drawable {
                return ResourceUtils.getDrawable(recyclerView!!.context, R.drawable.icon_delete)!!
            }

            @Override
            override fun getPendingActionText(direction: EasyRecyclerViewTouchHandler.Directions): String {
                return "Delete"
            }

            @Override
            override fun getConfiguration(context: Context): SwipeConfiguration {
                return SwipeConfiguration(context)
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun generateEmptyLayout(): View {
        val emptyLayout = layoutInflater.inflate(R.layout.demo_empty_layout, null)
        emptyLayout.findViewById<View>(R.id.emptyLayoutRefreshButton).setOnClickListener {
            val emptyLayoutImage = emptyLayout.findViewById<ImageView>(R.id.emptyImage)
            val progressBar = emptyLayout.findViewById<ProgressBar>(R.id.progressBar)
            emptyLayoutImage.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            emptyLayout.postDelayed({ recyclerView.adapter!!.appendCollection(generateData(20)) }, 2000)
        }
        return emptyLayout
    }

    private fun generateData(count: Int): List<DemoModel> {
        val models: MutableList<DemoModel> = ArrayList()
        for (i in 1..count) models.add(DemoModel("Demo title $i", "Demo subtitle $i"))
        return models
    }
}