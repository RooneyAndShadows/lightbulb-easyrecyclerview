package com.github.rooneyandshadows.lightbulb.easyrecyclerview.touch_handler

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.touch_handler.EasyRecyclerViewTouchHandler.Directions.LEFT
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.touch_handler.EasyRecyclerViewTouchHandler.Directions.RIGHT
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

@Suppress("unused")
class EasyRecyclerViewTouchHandler<ItemType : EasyAdapterDataModel>(
    private val easyRecyclerView: EasyRecyclerView<ItemType>,
    private val touchCallbacks: TouchCallbacks<ItemType>,
) : SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private var undoClicked = false
    private var snackbar: Snackbar? = null
    private var pendingAction: Runnable? = null
    private var touchHelperListeners: TouchHelperListeners? = null
    private val configuration: SwipeConfiguration = touchCallbacks.getConfiguration(easyRecyclerView.context)
    private val drawerHelper: SwipeToDeleteDrawerHelper = SwipeToDeleteDrawerHelper()
    private val actionsHandler = Handler(Looper.getMainLooper(), null)
    private val isVerticalLayoutManager = easyRecyclerView.layoutManager!!.canScrollVertically()
    private val adapter: EasyRecyclerAdapter<ItemType>
        get() = easyRecyclerView.adapter!!
    private lateinit var allowedDragDirections: Directions
    private lateinit var allowedSwipeDirections: Directions

    @Override
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val position = viewHolder.absoluteAdapterPosition
        if (adapter.headersCount > 0 && position < adapter.headersCount) return 0
        if (adapter.footersCount > 0 && position >= adapter.itemCount) return 0
        val item = getItem(viewHolder)!!
        allowedSwipeDirections = touchCallbacks.getAllowedSwipeDirections(item)
        allowedDragDirections = touchCallbacks.getAllowedDragDirections(item)
        return makeMovementFlags(allowedDragDirections.value, allowedSwipeDirections.value)
    }

    @Override
    override fun isLongPressDragEnabled(): Boolean {
        return allowedDragDirections != Directions.NONE
    }

    @Override
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        val fromPosition = viewHolder.absoluteAdapterPosition - adapter.headersCount
        val toPosition = target.absoluteAdapterPosition - adapter.headersCount
        if (toPosition < 0 || toPosition > adapter.itemCount) return false
        adapter.collection.move(fromPosition, toPosition)
        return true
    }

    @Override
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val item = getItem(viewHolder)
        showSwipedItemSnackBar(item!!, Directions.valueOf(direction))
    }

    @Override
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ACTION_STATE_DRAG && viewHolder != null)
            viewHolder.itemView.alpha = 0.6f
    }

    @Override
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        touchHelperListeners?.onClearView()
        viewHolder.itemView.alpha = 1f
        if (isItemVisible(viewHolder)) executePendingAction()
    }

    @Override
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        when (actionState) {
            ACTION_STATE_DRAG -> {}
            ACTION_STATE_SWIPE -> handleSwipeDraw(viewHolder, c, dX, dY)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        touchHelperListeners?.onChildDraw()
    }

    fun setTouchHelperListeners(listeners: TouchHelperListeners) {
        touchHelperListeners = listeners
    }

    private fun handleSwipeDraw(
        viewHolder: RecyclerView.ViewHolder,
        canvas: Canvas,
        dx: Float,
        dy: Float
    ) {
        val itemView = viewHolder.itemView
        val itemPosition = viewHolder.absoluteAdapterPosition - adapter.headersCount
        if (itemPosition != -1) {
            val item: ItemType? = getItem(viewHolder)
            var moved = -1
            if (item != null) {
                var direction: Directions? = null
                if (isVerticalLayoutManager) {
                    if (dx > 0) direction = RIGHT else if (dx < 0) direction = LEFT
                } else {
                    if (dy > 0) direction = Directions.UP else if (dy < 0) direction =
                        Directions.DOWN
                }
                if (direction == null) {
                    drawerHelper.clearBounds()
                    return
                }
                when (direction) {
                    LEFT, RIGHT ->
                        moved = dx.toInt()

                    Directions.UP, Directions.DOWN ->
                        moved = dy.toInt()

                    else -> {}
                }
                val actionText = getActionText(item)
                val backgroundColor = getActionBackgroundColor(direction)
                val icon = getActionIcon(direction)
                drawerHelper.draw(
                    actionText,
                    itemView,
                    moved,
                    canvas,
                    direction,
                    backgroundColor,
                    icon
                )
            }
        }
    }

    fun cancelPendingAction() {
        undoClicked = true
        runPending()
    }

    fun executePendingAction() {
        runPending()
    }

    private fun runPending() {
        if (pendingAction != null) {
            actionsHandler.removeCallbacks(pendingAction!!)
            snackbar!!.dismiss()
            pendingAction!!.run()
        }
    }

    private fun addPendingAction(item: ItemType, direction: Directions) {
        undoClicked = false
        pendingAction = Runnable {
            val position = adapter.collection.getPosition(item)
            if (undoClicked) {
                adapter.notifyItemChanged(position + adapter.headersCount)
                touchCallbacks.onActionCancelled(item, adapter, position)
            } else {
                touchCallbacks.onSwipeActionApplied(item, position, adapter, direction)
            }
            pendingAction = null
        }
        actionsHandler.postDelayed(pendingAction!!, configuration.swipeActionDelay.toLong())
    }

    @SuppressLint("ShowToast")
    @Synchronized
    private fun showSwipedItemSnackBar(item: ItemType, direction: Directions) {
        executePendingAction()
        addPendingAction(item, direction)
        val pendingActionText = touchCallbacks.getPendingActionText(direction) ?: ""
        val undoText = configuration.swipeCancelActionText
        snackbar =
            Snackbar.make(easyRecyclerView, pendingActionText, configuration.swipeActionDelay)
                .setAction(undoText) { cancelPendingAction() }
                .setActionTextColor(
                    ResourceUtils.getColorByAttribute(
                        easyRecyclerView.context,
                        R.attr.colorError
                    )
                )
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
        //val backgroundView = snackbar!!.view
        snackbar!!.show()
    }

    private fun isItemVisible(viewHolder: RecyclerView.ViewHolder): Boolean {
        return if (easyRecyclerView.layoutManager != null) easyRecyclerView.layoutManager!!.isViewPartiallyVisible(
            viewHolder.itemView,
            false,
            true
        ) else false
    }

    private fun getActionText(item: ItemType): String? {
        return touchCallbacks.getActionBackgroundText(item)
    }

    private fun getActionBackgroundColor(directions: Directions): Int {
        return touchCallbacks.getSwipeBackgroundColor(directions)
    }

    private fun getActionIcon(directions: Directions): Drawable? {
        return touchCallbacks.getSwipeIcon(directions)?.apply {
            setTint(configuration.swipeTextAndIconColor)
        }
    }

    private fun getItem(viewHolder: RecyclerView.ViewHolder): ItemType? {
        val position = viewHolder.absoluteAdapterPosition - adapter.headersCount
        return adapter.collection.getItem(position)
    }

    private inner class SwipeToDeleteDrawerHelper {
        private var backgroundDrawable: ColorDrawable? = null
        fun clearBounds() {
            if (backgroundDrawable != null) backgroundDrawable!!.setBounds(0, 0, 0, 0)
        }

        fun draw(
            text: String?,
            itemView: View,
            moved: Int,
            canvas: Canvas,
            direction: Directions?,
            backgroundColor: Int,
            icon: Drawable?,
        ) {
            backgroundDrawable = ColorDrawable(backgroundColor)
            backgroundDrawable!!.setBounds(
                itemView.left,
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            backgroundDrawable!!.draw(canvas)
            canvas.save()
            val backgroundCornerOffset = 20
            val iconMargin = (itemView.height - configuration.swipeIconSize) / 2
            var iconTop = -1
            var iconBottom = -1
            var iconLeft = -1
            var iconRight = -1
            val textPaint = Paint()
            textPaint.color = configuration.swipeTextAndIconColor
            textPaint.isAntiAlias = true
            textPaint.textSize = configuration.swipeTextSize.toFloat()
            textPaint.isElegantTextHeight = true
            val yPos = calculateTextYpos(itemView, textPaint)
            val textWidth = textPaint.measureText(text)
            when (direction) {
                LEFT -> {
                    canvas.clipRect(
                        itemView.right + moved - backgroundCornerOffset,
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    iconTop = itemView.top + (itemView.height - configuration.swipeIconSize) / 2
                    iconBottom = iconTop + configuration.swipeIconSize
                    iconLeft = itemView.right - iconMargin - configuration.swipeIconSize
                    iconRight = itemView.right - iconMargin
                    canvas.drawText(text!!, iconLeft - iconMargin - textWidth, yPos, textPaint)
                }

                RIGHT -> {
                    canvas.clipRect(
                        itemView.left,
                        itemView.top,
                        itemView.left + moved + backgroundCornerOffset,
                        itemView.bottom
                    )
                    iconTop = itemView.top + (itemView.height - configuration.swipeIconSize) / 2
                    iconBottom = iconTop + configuration.swipeIconSize
                    iconLeft = itemView.left + iconMargin
                    iconRight = itemView.left + iconMargin + configuration.swipeIconSize
                    canvas.drawText(text!!, (iconRight + iconMargin).toFloat(), yPos, textPaint)
                }

                Directions.UP -> {
                    canvas.clipRect(
                        itemView.left,
                        itemView.top + moved - backgroundCornerOffset,
                        itemView.right,
                        itemView.bottom
                    )
                    iconTop = itemView.top + (itemView.height - configuration.swipeIconSize) / 2
                    iconBottom = iconTop + configuration.swipeIconSize
                    iconLeft = itemView.left + iconMargin
                    iconRight = itemView.left + iconMargin + configuration.swipeIconSize
                    canvas.drawText(text!!, (iconRight + iconMargin).toFloat(), yPos, textPaint)
                }

                Directions.DOWN -> {
                    canvas.clipRect(
                        itemView.left,
                        itemView.top,
                        itemView.right,
                        itemView.bottom + moved - backgroundCornerOffset
                    )
                    iconTop = itemView.top + (itemView.height - configuration.swipeIconSize) / 2
                    iconBottom = iconTop + configuration.swipeIconSize
                    iconLeft = itemView.right - iconMargin - configuration.swipeIconSize
                    iconRight = itemView.right - iconMargin
                    canvas.drawText(text!!, iconLeft - iconMargin - textWidth, yPos, textPaint)
                }

                else -> {}
            }
            icon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            icon?.draw(canvas)
            canvas.restore()
        }

        private fun calculateTextYpos(itemView: View, paint: Paint): Float {
            val itemHeight = itemView.height
            val itemCenter = itemHeight / 2
            val textHeight = (paint.descent() + paint.ascent()) / 2
            return itemView.top + (itemCenter - textHeight)
        }
    }

    abstract class TouchHelperListeners {
        open fun onChildDraw() {
        }

        open fun onClearView() {
        }
    }


    enum class Directions(val value: Int) {
        UP(ItemTouchHelper.UP),
        DOWN(ItemTouchHelper.DOWN),
        LEFT(ItemTouchHelper.LEFT),
        RIGHT(ItemTouchHelper.RIGHT),
        LEFT_RIGHT(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT),
        UP_DOWN(ItemTouchHelper.UP or ItemTouchHelper.DOWN),
        ALL(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN),
        NONE(0);

        companion object {
            fun valueOf(value: Int) = values().first { it.value == value }
        }
    }

    class SwipeConfiguration(context: Context) {
        var swipeCancelActionText: String =
            ResourceUtils.getPhrase(context, R.string.erv_swipe_undo_default_text)
        var swipeTextAndIconColor: Int = ResourceUtils.getColorById(context, R.color.white)
        var swipeIconSize: Int = ResourceUtils.getDimenPxById(context, R.dimen.erv_swipe_icon_size)
        var swipeTextSize: Int = ResourceUtils.getDimenPxById(context, R.dimen.erv_swipe_text_size)
        var swipeActionDelay: Int = 4000

        fun withSwipeCancelActionText(text: String): SwipeConfiguration {
            swipeCancelActionText = text
            return this
        }

        fun withSwipeTextAndIconColor(color: Int): SwipeConfiguration {
            swipeTextAndIconColor = color
            return this
        }

        fun withSwipeIconSize(color: Int): SwipeConfiguration {
            swipeIconSize = color
            return this
        }

        fun withSwipeTextSize(color: Int): SwipeConfiguration {
            swipeTextSize = color
            return this
        }

        fun withSwipeActionDelay(delay: Int): SwipeConfiguration {
            val actualDelay = if (delay < 2000) 2000 else delay
            swipeActionDelay = actualDelay
            return this
        }
    }
}