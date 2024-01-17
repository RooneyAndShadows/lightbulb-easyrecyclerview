package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentViewBinding
import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbFragment
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.touch_handler.EasyRecyclerViewTouchHandler.Directions
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.touch_handler.EasyRecyclerViewTouchHandler.SwipeConfiguration
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.touch_handler.TouchCallbacks
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.FragmentDemoDragToReorderBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter

@Suppress("SameParameterValue")
@FragmentScreen(screenName = "Drag", screenGroup = "Demo")
@LightbulbFragment(layoutName = "fragment_demo_drag_to_reorder")
class DragToReorderDemoFragment : BaseFragment() {
    @FragmentViewBinding
    lateinit var viewBinding: FragmentDemoDragToReorderBinding

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.drag_to_reorder_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @SuppressLint("InflateParams")
    @Override
    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(fragmentView, savedInstanceState)
        viewBinding.recyclerView.apply {
            val swipeHandler = configureSwipeHandler()
            val emptyLayout = generateEmptyLayout()
            val itemDecoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
            val headerView = layoutInflater.inflate(R.layout.demo_header_item_drag_to_reorder, null)
            setSwipeCallbacks(swipeHandler)
            addHeaderView(headerView)
            setEmptyLayout(emptyLayout)
            addItemDecoration(itemDecoration)
            if (savedInstanceState == null) {
                adapter.collection.set(generateData(20))
            }
        }
    }

    private fun configureSwipeHandler(): TouchCallbacks<DemoModel> {
        return object : TouchCallbacks<DemoModel>(requireContext()) {
            @Override
            override fun getAllowedSwipeDirections(item: DemoModel): Directions {
                return Directions.NONE
            }

            @Override
            override fun getAllowedDragDirections(item: DemoModel): Directions {
                return Directions.UP_DOWN
            }

            @Override
            override fun getActionBackgroundText(item: DemoModel): String {
                return item.title
            }

            @Override
            override fun onSwipeActionApplied(
                item: DemoModel,
                position: Int,
                adapter: EasyRecyclerAdapter<DemoModel>,
                direction: Directions,
            ) {
            }

            @Override
            override fun onActionCancelled(
                item: DemoModel,
                adapter: EasyRecyclerAdapter<DemoModel>,
                position: Int
            ) {
            }

            @Override
            override fun getSwipeBackgroundColor(direction: Directions): Int {
                return ResourceUtils.getColorByAttribute(requireContext(), R.attr.colorError)
            }

            @Override
            override fun getSwipeIcon(direction: Directions): Drawable? {
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
            val dataToSet = generateData(20)
            emptyLayoutImage.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            emptyLayout.postDelayed({
                viewBinding.recyclerView.adapter.collection.addAll(dataToSet)
            }, 2000)
        }
        return emptyLayout
    }
}