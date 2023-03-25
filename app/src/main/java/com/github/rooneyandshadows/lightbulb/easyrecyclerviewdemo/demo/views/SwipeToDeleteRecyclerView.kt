package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.handler.EasyRecyclerViewTouchHandler
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.handler.TouchCallbacks
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters.SimpleAdapter
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter

@SuppressLint("InflateParams")
class SwipeToDeleteRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : EasyRecyclerView<DemoModel, SimpleAdapter>(context, attrs) {
    override val adapterCreator: AdapterCreator<SimpleAdapter>
        get() = object : AdapterCreator<SimpleAdapter> {
            override fun createAdapter(): SimpleAdapter {
                return SimpleAdapter()
            }
        }

    init {
        val inflater = LayoutInflater.from(context)
        val header = inflater.inflate(R.layout.demo_header_item_swipe_to_delete, null)
        val emptyLayout = generateEmptyLayout()
        val itemDecoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
        val swipeCallbacks = getSwipeHandler()
        setSwipeCallbacks(swipeCallbacks)
        addHeaderView(header)
        setEmptyLayout(emptyLayout)
        addItemDecoration(itemDecoration)
    }

    @SuppressLint("InflateParams")
    private fun generateEmptyLayout(): View {
        val inflater = LayoutInflater.from(context)
        val emptyLayout = inflater.inflate(R.layout.demo_empty_layout, null)
        emptyLayout.findViewById<View>(R.id.emptyLayoutRefreshButton).setOnClickListener {
            val emptyLayoutImage = emptyLayout.findViewById<ImageView>(R.id.emptyImage)
            val progressBar = emptyLayout.findViewById<ProgressBar>(R.id.progressBar)
            emptyLayoutImage.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            emptyLayout.postDelayed({ adapter.appendCollection(generateData(20)) }, 2000)
        }
        return emptyLayout
    }

    private fun getSwipeHandler(): TouchCallbacks<DemoModel> {
        return object : TouchCallbacks<DemoModel>(context) {
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
                direction: EasyRecyclerViewTouchHandler.Directions,
            ) {
                post {
                    val actualPosition = adapter.getPosition(item)
                    adapter.removeItem(actualPosition)
                }
            }

            @Override
            override fun onActionCancelled(item: DemoModel, adapter: EasyRecyclerAdapter<DemoModel>, position: Int) {
            }

            @Override
            override fun getSwipeBackgroundColor(direction: EasyRecyclerViewTouchHandler.Directions): Int {
                return ResourceUtils.getColorByAttribute(context, R.attr.colorError)
            }

            @Override
            override fun getSwipeIcon(direction: EasyRecyclerViewTouchHandler.Directions): Drawable {
                return ResourceUtils.getDrawable(context, R.drawable.icon_delete)!!
            }

            @Override
            override fun getPendingActionText(direction: EasyRecyclerViewTouchHandler.Directions): String {
                return "Delete"
            }

            @Override
            override fun getConfiguration(context: Context): EasyRecyclerViewTouchHandler.SwipeConfiguration {
                return EasyRecyclerViewTouchHandler.SwipeConfiguration(context)
            }
        }
    }
}