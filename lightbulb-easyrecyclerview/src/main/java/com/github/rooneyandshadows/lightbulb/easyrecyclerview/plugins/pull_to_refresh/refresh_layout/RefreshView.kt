package com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.pull_to_refresh.refresh_layout

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.R
import kotlin.math.min

/**
 * the default implementation class of the interface IRefreshStatus, and the class should always be rewritten
 */
@Suppress("MemberVisibilityCanBePrivate")
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
        mPaint.color = ResourceUtils.getColorByAttribute(context, R.attr.colorPrimary)
    }

    private fun startAnimator() {
        mRotateAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        mRotateAnimator!!.interpolator = LinearInterpolator()
        mRotateAnimator!!.addUpdateListener { animation ->
            val rotateProgress = animation.animatedValue as Float
            setStartDegrees(DEFAULT_START_DEGREES + rotateProgress * 360)
        }
        mRotateAnimator!!.repeatMode = ValueAnimator.RESTART
        mRotateAnimator!!.repeatCount = ValueAnimator.INFINITE
        mRotateAnimator!!.duration = ANIMATION_DURATION.toLong()
        mRotateAnimator!!.start()
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
        val radius = min(w, h) / 2.0f
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
            val swipeProgress = min(1.0f, pullProgress)
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