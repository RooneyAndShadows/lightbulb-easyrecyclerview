package com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh

import android.R
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.factor.bouncy.BouncyRecyclerView.setAdapter
import com.factor.bouncy.BouncyRecyclerView.setLayoutManager
import com.factor.bouncy.BouncyRecyclerView.stiffness
import com.factor.bouncy.BouncyRecyclerView.flingAnimationSize
import com.factor.bouncy.BouncyRecyclerView.overscrollAnimationSize
import com.factor.bouncy.BouncyRecyclerView.dampingRatio
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.StickyHeaderItemDecoration.StickyHeaderInterface
import kotlin.jvm.JvmOverloads
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.IRefreshStatus
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RefreshView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RefreshLogger
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.NestedScrollingParentHelper
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RecyclerRefreshLayout
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RecyclerRefreshLayout.RefreshStyle
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.IDragDistanceConverter
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RecyclerRefreshLayout.OnRefreshListener
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.MaterialDragDistanceConverter
import androidx.core.view.ViewCompat
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.JustifyContent
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.EasyRecyclerViewTouchHandler.TouchCallbacks
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.EasyRecyclerViewTouchHandler.SwipeConfiguration
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.EasyRecyclerViewTouchHandler.SwipeToDeleteDrawerHelper
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView.LayoutManagerTypes
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.HeaderViewRecyclerAdapter
import com.factor.bouncy.BouncyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView.LoadMoreCallback
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView.RefreshCallback
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.EasyRecyclerViewTouchHandler
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView.EasyRecyclerItemsReadyListener
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView.EasyRecyclerEmptyLayoutListener
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.callbacks.EasyAdapterCollectionChangedListener
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.HeaderViewRecyclerAdapter.ViewListeners
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.VerticalLinearLayoutManager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.HorizontalLinearLayoutManager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.VerticalFlowLayoutManager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.HorizontalFlowLayoutManager
import androidx.dynamicanimation.animation.SpringForce

/**
 * the default implementation class of the interface IRefreshStatus, and the class should always be rewritten
 */
class RefreshView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr), IRefreshStatus {
    private val mArcBounds = RectF()
    private val mPaint = Paint()
    private var mStartDegrees = 0f
    private var mSwipeDegrees = 0f
    private var mStrokeWidth = 0f
    private var mHasTriggeredRefresh = false
    private var mRotateAnimator: ValueAnimator? = null
    private fun initData() {
        val density = resources.displayMetrics.density
        mStrokeWidth = density * DEFAULT_STROKE_WIDTH
        mStartDegrees = DEFAULT_START_DEGREES.toFloat()
        mSwipeDegrees = 0.0f
    }

    private fun initPaint() {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mStrokeWidth
        mPaint.color = getColorByAttribute.getColorByAttribute(context, R.attr.colorPrimary)
    }

    private fun startAnimator() {
        mRotateAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        mRotateAnimator.setInterpolator(LinearInterpolator())
        mRotateAnimator.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation ->
            val rotateProgress = animation.animatedValue as Float
            setStartDegrees(DEFAULT_START_DEGREES + rotateProgress * 360)
        })
        mRotateAnimator.setRepeatMode(ValueAnimator.RESTART)
        mRotateAnimator.setRepeatCount(ValueAnimator.INFINITE)
        mRotateAnimator.setDuration(ANIMATION_DURATION.toLong())
        mRotateAnimator.start()
    }

    private fun resetAnimator() {
        if (mRotateAnimator != null) {
            mRotateAnimator!!.cancel()
            mRotateAnimator!!.removeAllUpdateListeners()
            mRotateAnimator = null
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawArc(canvas)
    }

    override fun onDetachedFromWindow() {
        resetAnimator()
        super.onDetachedFromWindow()
    }

    private fun drawArc(canvas: Canvas) {
        canvas.drawArc(mArcBounds, mStartDegrees, mSwipeDegrees, false, mPaint)
    }

    private fun setStartDegrees(startDegrees: Float) {
        mStartDegrees = startDegrees
        postInvalidate()
    }

    fun setSwipeDegrees(swipeDegrees: Float) {
        mSwipeDegrees = swipeDegrees
        postInvalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val radius = Math.min(w, h) / 2.0f
        val centerX = w / 2.0f
        val centerY = h / 2.0f
        mArcBounds[centerX - radius, centerY - radius, centerX + radius] = centerY + radius
        mArcBounds.inset(mStrokeWidth / 2.0f, mStrokeWidth / 2.0f)
    }

    override fun reset() {
        resetAnimator()
        mHasTriggeredRefresh = false
        mStartDegrees = DEFAULT_START_DEGREES.toFloat()
        mSwipeDegrees = 0.0f
    }

    override fun refreshing() {
        mHasTriggeredRefresh = true
        mSwipeDegrees = MAX_ARC_DEGREE.toFloat()
        startAnimator()
    }

    override fun refreshComplete() {}
    override fun pullToRefresh() {}
    override fun releaseToRefresh() {}
    override fun pullProgress(pullDistance: Float, pullProgress: Float) {
        if (!mHasTriggeredRefresh) {
            val swipeProgress = Math.min(1.0f, pullProgress)
            setSwipeDegrees(swipeProgress * MAX_ARC_DEGREE)
        }
    }

    companion object {
        private const val MAX_ARC_DEGREE = 330
        private const val ANIMATION_DURATION = 888
        private const val DEFAULT_START_DEGREES = 285
        private const val DEFAULT_STROKE_WIDTH = 2
    }

    init {
        initData()
        initPaint()
    }
}