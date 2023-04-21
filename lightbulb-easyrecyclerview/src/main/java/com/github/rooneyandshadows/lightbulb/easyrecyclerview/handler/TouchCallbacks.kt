package com.github.rooneyandshadows.lightbulb.easyrecyclerview.handler

import android.content.Context
import android.graphics.drawable.Drawable
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.handler.EasyRecyclerViewTouchHandler.*
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel

abstract class TouchCallbacks<ItemType : EasyAdapterDataModel>(private val context: Context) {

    abstract fun onSwipeActionApplied(
        item: ItemType,
        position: Int,
        adapter: EasyRecyclerAdapter<ItemType>,
        direction: Directions,
    )

    abstract fun getAllowedSwipeDirections(item: ItemType): Directions
    abstract fun getAllowedDragDirections(item: ItemType): Directions
    abstract fun onActionCancelled(item: ItemType, adapter: EasyRecyclerAdapter<ItemType>, position: Int)
    abstract fun getActionBackgroundText(item: ItemType): String?
    abstract fun getConfiguration(context: Context): SwipeConfiguration

    open fun getSwipeBackgroundColor(direction: Directions): Int {
        return ResourceUtils.getColorByAttribute(context, R.attr.colorError)
    }

    open fun getSwipeIcon(direction: Directions): Drawable? {
        return ResourceUtils.getDrawable(context, R.drawable.icon_delete)
    }

    open fun getPendingActionText(direction: Directions): String? {
        return ResourceUtils.getPhrase(context, R.string.erv_swipe_pending_action_default_text)
    }
}