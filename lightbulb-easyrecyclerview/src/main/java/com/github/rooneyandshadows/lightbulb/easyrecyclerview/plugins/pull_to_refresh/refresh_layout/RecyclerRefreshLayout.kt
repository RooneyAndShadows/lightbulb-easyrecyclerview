package com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.pull_to_refresh.refresh_layout

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.Transformation
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Suppress("unused")
class RecyclerRefreshLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null
) :
    ViewGroup(context, attrs) {
    // NestedScroll
    private var mTotalUnconsumed = 0f
    private val mParentScrollConsumed = IntArray(2)
    private val mParentOffsetInWindow = IntArray(2)
    private val mNestedScrollingChildHelper: NestedScrollingChildHelper
    private val mNestedScrollingParentHelper: NestedScrollingParentHelper

    //whether to remind the callback listener(OnRefreshListener)
    private var mIsAnimatingToStart = false
    private var mIsRefreshing = false
    private var mIsFitRefresh = false
    private var mNotifyListener = false
    private var mRefreshViewIndex = INVALID_INDEX
    private var mAnimateToStartDuration = DEFAULT_ANIMATE_DURATION
    private var mAnimateToRefreshDuration = DEFAULT_ANIMATE_DURATION
    private var mFrom = 0
    private val mRefreshViewSize: Int
    private var mTargetOrRefreshViewOffsetY: Float = 0F
    private var mRefreshInitialOffset: Float = 0F
    private var mRefreshTargetOffset: Float = 0F

    // Whether the client has set a custom refreshing position;
    private var mUsingCustomRefreshTargetOffset = false

    // Whether the client has set a custom starting position;
    private var mUsingCustomRefreshInitialOffset = false

    // Whether or not the RefreshView has been measured.
    private var mRefreshViewMeasured = false
    private var mRefreshStyle = RefreshStyle.NORMAL
    private var mTarget: View? = null
    private lateinit var mRefreshView: View
    private var mDragDistanceConverter: IDragDistanceConverter? = null
    private var mRefreshStatus: IRefreshStatus? = null
    private var mOnRefreshListener: RefreshListener? = null
    private var mAnimateToStartInterpolator: Interpolator =
        DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)
    private var mAnimateToRefreshInterpolator: Interpolator =
        DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)
    private val mAnimateToRefreshingAnimation: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            when (mRefreshStyle) {
                RefreshStyle.FLOAT -> {
                    val refreshTargetOffset = mRefreshTargetOffset + mRefreshInitialOffset
                    animateToTargetOffset(
                        refreshTargetOffset,
                        mRefreshView.top.toFloat(),
                        interpolatedTime
                    )
                }

                else -> animateToTargetOffset(
                    mRefreshTargetOffset,
                    mTarget!!.top.toFloat(),
                    interpolatedTime
                )
            }
        }
    }
    private val mAnimateToStartAnimation: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            when (mRefreshStyle) {
                RefreshStyle.FLOAT -> animateToTargetOffset(
                    mRefreshInitialOffset,
                    mRefreshView.top.toFloat(),
                    interpolatedTime
                )

                else -> animateToTargetOffset(0.0f, mTarget!!.top.toFloat(), interpolatedTime)
            }
        }
    }
    val isRefreshing: Boolean
        get() = mIsRefreshing

    init {
        setWillNotDraw(false)
        val metrics = resources.displayMetrics
        mRefreshViewSize = (DEFAULT_REFRESH_SIZE_DP * metrics.density).toInt()
        mRefreshTargetOffset = DEFAULT_REFRESH_TARGET_OFFSET_DP * metrics.density
        mNestedScrollingParentHelper = NestedScrollingParentHelper(this)
        mNestedScrollingChildHelper = NestedScrollingChildHelper(this)
        initRefreshView()
        initDragDistanceConverter()
    }

    private fun animateToTargetOffset(
        targetEnd: Float,
        currentOffset: Float,
        interpolatedTime: Float
    ) {
        val targetOffset = (mFrom + (targetEnd - mFrom) * interpolatedTime).toInt()
        setTargetOrRefreshViewOffsetY((targetOffset - currentOffset).toInt())
    }

    private val mRefreshingListener: Animation.AnimationListener =
        object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                mIsAnimatingToStart = true
                mRefreshStatus!!.refreshing()
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                if (mNotifyListener) {
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener!!.execute()
                    }
                }
                mIsAnimatingToStart = false
            }
        }
    private val mResetListener: Animation.AnimationListener = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {
            mIsAnimatingToStart = true
            mRefreshStatus!!.refreshComplete()
        }

        override fun onAnimationRepeat(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) {
            reset()
        }
    }

    override fun onDetachedFromWindow() {
        reset()
        clearAnimation()
        super.onDetachedFromWindow()
    }

    private fun reset() {
        setTargetOrRefreshViewToInitial()
        mRefreshStatus!!.reset()
        mRefreshView.visibility = GONE
        mIsRefreshing = false
        mIsAnimatingToStart = false
    }

    private fun setTargetOrRefreshViewToInitial() {
        when (mRefreshStyle) {
            RefreshStyle.FLOAT -> setTargetOrRefreshViewOffsetY((mRefreshInitialOffset - mTargetOrRefreshViewOffsetY).toInt())
            else -> setTargetOrRefreshViewOffsetY((0 - mTargetOrRefreshViewOffsetY).toInt())
        }
    }

    private fun initRefreshView() {
        mRefreshView = RefreshView(context)
        mRefreshView.visibility = GONE
        mRefreshStatus = if (mRefreshView is IRefreshStatus) {
            mRefreshView as IRefreshStatus?
        } else {
            throw ClassCastException("the refreshView must implement the interface IRefreshStatus")
        }
        val layoutParams = LayoutParams(mRefreshViewSize, mRefreshViewSize)
        addView(mRefreshView, layoutParams)
    }

    private fun initDragDistanceConverter() {
        mDragDistanceConverter = MaterialDragDistanceConverter()
    }

    /**
     * @param refreshView  must implements the interface IRefreshStatus
     * @param layoutParams the with is always the match_parent， no matter how you set
     * the height you need to set a specific value
     */
    fun setRefreshView(refreshView: View, layoutParams: ViewGroup.LayoutParams?) {
        if (mRefreshView === refreshView) {
            return
        }
        if (mRefreshView.parent != null) {
            (mRefreshView.parent as ViewGroup).removeView(mRefreshView)
        }
        mRefreshStatus = if (refreshView is IRefreshStatus) {
            refreshView
        } else {
            throw ClassCastException("the refreshView must implement the interface IRefreshStatus")
        }
        refreshView.visibility = GONE
        addView(refreshView, layoutParams)
        mRefreshView = refreshView
    }

    fun setDragDistanceConverter(dragDistanceConverter: IDragDistanceConverter) {
        mDragDistanceConverter = dragDistanceConverter
    }

    /**
     * @param animateToStartInterpolator The interpolator used by the animation that
     * move the refresh view from the refreshing point or
     * (the release point) to the start point.
     */
    fun setAnimateToStartInterpolator(animateToStartInterpolator: Interpolator) {
        mAnimateToStartInterpolator = animateToStartInterpolator
    }

    /**
     * @param animateToRefreshInterpolator The interpolator used by the animation that
     * move the refresh view the release point to the refreshing point.
     */
    fun setAnimateToRefreshInterpolator(animateToRefreshInterpolator: Interpolator) {
        mAnimateToRefreshInterpolator = animateToRefreshInterpolator
    }

    /**
     * @param animateToStartDuration The duration used by the animation that
     * move the refresh view from the refreshing point or
     * (the release point) to the start point.
     */
    fun setAnimateToStartDuration(animateToStartDuration: Int) {
        mAnimateToStartDuration = animateToStartDuration
    }

    /**
     * @param animateToRefreshDuration The duration used by the animation that
     * move the refresh view the release point to the refreshing point.
     */
    fun setAnimateToRefreshDuration(animateToRefreshDuration: Int) {
        mAnimateToRefreshDuration = animateToRefreshDuration
    }

    /**
     * @param refreshTargetOffset The minimum distance that trigger refresh.
     */
    fun setRefreshTargetOffset(refreshTargetOffset: Float) {
        mRefreshTargetOffset = refreshTargetOffset
        mUsingCustomRefreshTargetOffset = true
        requestLayout()
    }

    /**
     * @param refreshInitialOffset the top position of the [.mRefreshView] relative to its parent.
     */
    fun setRefreshInitialOffset(refreshInitialOffset: Float) {
        mRefreshInitialOffset = refreshInitialOffset
        mUsingCustomRefreshInitialOffset = true
        requestLayout()
    }

    // NestedScrollingParent
    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return isEnabled
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
        // Dispatch up to the nested parent
        startNestedScroll(axes and ViewCompat.SCROLL_AXIS_VERTICAL)
        mTotalUnconsumed = 0f
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - mTotalUnconsumed.toInt()
                mTotalUnconsumed = 0f
            } else {
                mTotalUnconsumed -= dy.toFloat()
                consumed[1] = dy
            }
            RefreshLogger.i("pre scroll")
            moveSpinner(mTotalUnconsumed)
        }

        // Now let our nested parent consume the leftovers
        val parentConsumed = mParentScrollConsumed
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0]
            consumed[1] += parentConsumed[1]
        }
    }

    override fun getNestedScrollAxes(): Int {
        return mNestedScrollingParentHelper.nestedScrollAxes
    }

    override fun onStopNestedScroll(target: View) {
        mNestedScrollingParentHelper.onStopNestedScroll(target)
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
            finishSpinner()
            mTotalUnconsumed = 0f
        }
        // Dispatch up our nested parent
        stopNestedScroll()
    }

    override fun onNestedScroll(
        target: View, dxConsumed: Int, dyConsumed: Int,
        dxUnconsumed: Int, dyUnconsumed: Int,
    ) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(
            dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            mParentOffsetInWindow
        )

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        val dy = dyUnconsumed + mParentOffsetInWindow[1]
        if (dy < 0) {
            mTotalUnconsumed += abs(dy).toFloat()
            RefreshLogger.i("nested scroll")
            moveSpinner(mTotalUnconsumed)
        }
    }

    // NestedScrollingChild
    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mNestedScrollingChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return mNestedScrollingChildHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return mNestedScrollingChildHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, offsetInWindow: IntArray?,
    ): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedScroll(
            dxConsumed, dyConsumed,
            dxUnconsumed, dyUnconsumed, offsetInWindow
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun onNestedPreFling(
        target: View, velocityX: Float,
        velocityY: Float,
    ): Boolean {
        return dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun onNestedFling(
        target: View, velocityX: Float, velocityY: Float,
        consumed: Boolean,
    ): Boolean {
        return dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (childCount == 0) {
            return
        }
        ensureTarget()
        if (mTarget == null) {
            return
        }
        val width = measuredWidth
        val height = measuredHeight
        val targetTop = reviseTargetLayoutTop(paddingTop)
        val targetLeft = paddingLeft
        val targetRight = targetLeft + width - paddingLeft - paddingRight
        val targetBottom = targetTop + height - paddingTop - paddingBottom
        //try {
        mTarget!!.layout(targetLeft, targetTop, targetRight, targetBottom)
        //} catch (Exception ignored) {
        //    RefreshLogger.e("error: ignored=" + ignored.toString() + " " + ignored.getStackTrace().toString());
        //}
        val refreshViewLeft = (width - mRefreshView.measuredWidth) / 2
        val refreshViewTop = reviseRefreshViewLayoutTop(mRefreshInitialOffset.toInt())
        val refreshViewRight = (width + mRefreshView.measuredWidth) / 2
        val refreshViewBottom = refreshViewTop + mRefreshView.measuredHeight
        mRefreshView.layout(refreshViewLeft, refreshViewTop, refreshViewRight, refreshViewBottom)
        RefreshLogger.i("onLayout: $left : $top : $right : $bottom")
    }

    private fun reviseTargetLayoutTop(layoutTop: Int): Int {
        return when (mRefreshStyle) {
            RefreshStyle.FLOAT -> layoutTop
            RefreshStyle.PINNED -> layoutTop + mTargetOrRefreshViewOffsetY.toInt()
            else ->                 //not consider mRefreshResistanceRate < 1.0f
                layoutTop + mTargetOrRefreshViewOffsetY.toInt()
        }
    }

    private fun reviseRefreshViewLayoutTop(layoutTop: Int): Int {
        return when (mRefreshStyle) {
            RefreshStyle.FLOAT -> layoutTop + mTargetOrRefreshViewOffsetY.toInt()
            RefreshStyle.PINNED -> layoutTop
            else ->                 //not consider mRefreshResistanceRate < 1.0f
                layoutTop + mTargetOrRefreshViewOffsetY.toInt()
        }
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        ensureTarget()
        if (mTarget == null) {
            return
        }
        measureTarget()
        measureRefreshView(widthMeasureSpec, heightMeasureSpec)
        if (!mRefreshViewMeasured && !mUsingCustomRefreshInitialOffset) {
            when (mRefreshStyle) {
                RefreshStyle.PINNED -> {
                    mRefreshInitialOffset = 0.0f
                    mTargetOrRefreshViewOffsetY = mRefreshInitialOffset
                }

                RefreshStyle.FLOAT -> {
                    mRefreshInitialOffset = -mRefreshView.measuredHeight.toFloat()
                    mTargetOrRefreshViewOffsetY = mRefreshInitialOffset
                }

                else -> {
                    mTargetOrRefreshViewOffsetY = 0.0f
                    mRefreshInitialOffset = -mRefreshView.measuredHeight.toFloat()
                }
            }
        }
        if (!mRefreshViewMeasured && !mUsingCustomRefreshTargetOffset) {
            if (mRefreshTargetOffset < mRefreshView.measuredHeight) {
                mRefreshTargetOffset = mRefreshView.measuredHeight.toFloat()
            }
        }
        mRefreshViewMeasured = true
        mRefreshViewIndex = -1
        for (index in 0 until childCount) {
            if (getChildAt(index) === mRefreshView) {
                mRefreshViewIndex = index
                break
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mIsRefreshing && (mRefreshStyle == RefreshStyle.NORMAL || mRefreshStyle == RefreshStyle.PINNED)) canvas.clipRect(
            left, mRefreshView.top, right, bottom
        )
    }

    private fun measureTarget() {
        mTarget!!.measure(
            MeasureSpec.makeMeasureSpec(
                measuredWidth - paddingLeft - paddingRight,
                MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(
                measuredHeight - paddingTop - paddingBottom,
                MeasureSpec.EXACTLY
            )
        )
    }

    private fun measureRefreshView(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val lp = mRefreshView.layoutParams as MarginLayoutParams
        val childWidthMeasureSpec: Int = if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            val width =
                max(0, measuredWidth - paddingLeft - paddingRight - lp.leftMargin - lp.rightMargin)
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        } else {
            getChildMeasureSpec(
                widthMeasureSpec,
                paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin,
                lp.width
            )
        }
        val childHeightMeasureSpec: Int = if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            val height =
                max(0, measuredHeight - paddingTop - paddingBottom - lp.topMargin - lp.bottomMargin)
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        } else {
            getChildMeasureSpec(
                heightMeasureSpec,
                paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin,
                lp.height
            )
        }
        mRefreshView.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->                 // support compile sdk version < 23
                onStopNestedScroll(this)

            else -> {}
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    fun setRefreshing(refreshing: Boolean) {
        if (refreshing && !mIsRefreshing) {
            mIsRefreshing = true
            mNotifyListener = false
            animateToRefreshingPosition(mTargetOrRefreshViewOffsetY.toInt(), mRefreshingListener)
        } else {
            setRefreshing(refreshing, false)
        }
    }

    private fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (mIsRefreshing != refreshing) {
            mNotifyListener = notify
            mIsRefreshing = refreshing
            if (refreshing) {
                animateToRefreshingPosition(
                    mTargetOrRefreshViewOffsetY.toInt(),
                    mRefreshingListener
                )
            } else {
                animateOffsetToStartPosition(mTargetOrRefreshViewOffsetY.toInt(), mResetListener)
            }
        }
    }

    private fun animateOffsetToStartPosition(from: Int, listener: Animation.AnimationListener?) {
        clearAnimation()
        if (computeAnimateToStartDuration(from.toFloat()) <= 0) {
            listener!!.onAnimationStart(null)
            listener.onAnimationEnd(null)
            return
        }
        mFrom = from
        mAnimateToStartAnimation.reset()
        mAnimateToStartAnimation.duration = computeAnimateToStartDuration(from.toFloat()).toLong()
        mAnimateToStartAnimation.interpolator = mAnimateToStartInterpolator
        if (listener != null) {
            mAnimateToStartAnimation.setAnimationListener(listener)
        }
        startAnimation(mAnimateToStartAnimation)
    }

    private fun animateToRefreshingPosition(from: Int, listener: Animation.AnimationListener?) {
        clearAnimation()
        if (computeAnimateToRefreshingDuration(from.toFloat()) <= 0) {
            listener!!.onAnimationStart(null)
            listener.onAnimationEnd(null)
            return
        }
        mFrom = from
        mAnimateToRefreshingAnimation.reset()
        mAnimateToRefreshingAnimation.duration =
            computeAnimateToRefreshingDuration(from.toFloat()).toLong()
        mAnimateToRefreshingAnimation.interpolator = mAnimateToRefreshInterpolator
        if (listener != null) {
            mAnimateToRefreshingAnimation.setAnimationListener(listener)
        }
        startAnimation(mAnimateToRefreshingAnimation)
    }

    private fun computeAnimateToRefreshingDuration(from: Float): Int {
        RefreshLogger.i("from -- refreshing $from")
        return if (from < mRefreshInitialOffset) {
            0
        } else when (mRefreshStyle) {
            RefreshStyle.FLOAT -> (max(
                0.0f,
                min(
                    1.0f,
                    abs(from - mRefreshInitialOffset - mRefreshTargetOffset) / mRefreshTargetOffset
                )
            ) * mAnimateToRefreshDuration).toInt()

            else -> (max(
                0.0f,
                min(
                    1.0f,
                    abs(from - mRefreshTargetOffset) / mRefreshTargetOffset
                )
            ) * mAnimateToRefreshDuration).toInt()
        }
    }

    private fun computeAnimateToStartDuration(from: Float): Int {
        RefreshLogger.i("from -- start $from")
        return if (from < mRefreshInitialOffset) {
            0
        } else when (mRefreshStyle) {
            RefreshStyle.FLOAT -> (max(
                0.0f,
                min(
                    1.0f,
                    abs(from - mRefreshInitialOffset) / mRefreshTargetOffset
                )
            ) * mAnimateToStartDuration).toInt()

            else -> (max(
                0.0f,
                min(
                    1.0f, abs(from) / mRefreshTargetOffset
                )
            ) * mAnimateToStartDuration).toInt()
        }
    }

    /**
     * @param targetOrRefreshViewOffsetY the top position of the target
     * or the RefreshView relative to its parent.
     */
    private fun moveSpinner(targetOrRefreshViewOffsetY: Float) {
        var convertScrollOffset: Float
        val refreshTargetOffset: Float
        if (!mIsRefreshing) {
            when (mRefreshStyle) {
                RefreshStyle.FLOAT -> {
                    convertScrollOffset = (mRefreshInitialOffset
                            + mDragDistanceConverter!!.convert(
                        targetOrRefreshViewOffsetY,
                        mRefreshTargetOffset
                    ))
                    refreshTargetOffset = mRefreshTargetOffset
                }

                else -> {
                    convertScrollOffset = mDragDistanceConverter!!.convert(
                        targetOrRefreshViewOffsetY,
                        mRefreshTargetOffset
                    )
                    refreshTargetOffset = mRefreshTargetOffset
                }
            }
        } else {
            //The Float style will never come here
            convertScrollOffset = if (targetOrRefreshViewOffsetY > mRefreshTargetOffset) {
                mRefreshTargetOffset
            } else {
                targetOrRefreshViewOffsetY
            }
            if (convertScrollOffset < 0.0f) {
                convertScrollOffset = 0.0f
            }
            refreshTargetOffset = mRefreshTargetOffset
        }
        if (!mIsRefreshing) {
            if (convertScrollOffset > refreshTargetOffset && !mIsFitRefresh) {
                mIsFitRefresh = true
                mRefreshStatus!!.pullToRefresh()
            } else if (convertScrollOffset <= refreshTargetOffset && mIsFitRefresh) {
                mIsFitRefresh = false
                mRefreshStatus!!.releaseToRefresh()
            }
        }
        RefreshLogger.i(
            targetOrRefreshViewOffsetY.toString() + " -- " + refreshTargetOffset + " -- "
                    + convertScrollOffset + " -- " + mTargetOrRefreshViewOffsetY + " -- " + mRefreshTargetOffset
        )
        setTargetOrRefreshViewOffsetY((convertScrollOffset - mTargetOrRefreshViewOffsetY).toInt())
    }

    private fun finishSpinner() {
        if (mIsRefreshing || mIsAnimatingToStart) {
            return
        }
        val scrollY = targetOrRefreshViewOffset.toFloat()
        if (scrollY > mRefreshTargetOffset) {
            setRefreshing(refreshing = true, notify = true)
        } else {
            mIsRefreshing = false
            animateOffsetToStartPosition(mTargetOrRefreshViewOffsetY.toInt(), mResetListener)
        }
    }

    private fun setTargetOrRefreshViewOffsetY(offsetY: Int) {
        if (mTarget == null) {
            return
        }
        mTargetOrRefreshViewOffsetY = when (mRefreshStyle) {
            RefreshStyle.FLOAT -> {
                mRefreshView.offsetTopAndBottom(offsetY)
                mRefreshView.top.toFloat()
            }

            RefreshStyle.PINNED -> {
                mTarget!!.offsetTopAndBottom(offsetY)
                mTarget!!.top.toFloat()
            }

            else -> {
                mTarget!!.offsetTopAndBottom(offsetY)
                mRefreshView.offsetTopAndBottom(offsetY)
                mTarget!!.top.toFloat()
            }
        }
        RefreshLogger.i("current offset$mTargetOrRefreshViewOffsetY")
        when (mRefreshStyle) {
            RefreshStyle.FLOAT -> mRefreshStatus!!.pullProgress(
                mTargetOrRefreshViewOffsetY,
                (mTargetOrRefreshViewOffsetY - mRefreshInitialOffset) / mRefreshTargetOffset
            )

            else -> mRefreshStatus!!.pullProgress(
                mTargetOrRefreshViewOffsetY,
                mTargetOrRefreshViewOffsetY / mRefreshTargetOffset
            )
        }
        if (mRefreshView.visibility != VISIBLE) {
            mRefreshView.visibility = VISIBLE
        }
        invalidate()
    }

    private val targetOrRefreshViewOffset: Int
        get() = when (mRefreshStyle) {
            RefreshStyle.FLOAT -> (mRefreshView.top - mRefreshInitialOffset).toInt()
            else -> mTarget!!.top
        }

    private fun ensureTarget() {
        if (!isTargetValid) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child != mRefreshView) {
                    mTarget = child
                    break
                }
            }
        }
    }

    private val isTargetValid: Boolean
        get() {
            for (i in 0 until childCount) {
                if (mTarget === getChildAt(i)) {
                    return true
                }
            }
            return false
        }

    /**
     * Set the style of the RefreshView.
     *
     * @param refreshStyle One of [RefreshStyle.NORMAL]
     * , [RefreshStyle.PINNED], or [RefreshStyle.FLOAT]
     */
    fun setRefreshStyle(refreshStyle: RefreshStyle) {
        mRefreshStyle = refreshStyle
    }

    enum class RefreshStyle {
        NORMAL, PINNED, FLOAT
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    fun setOnRefreshListener(listener: RefreshListener?) {
        mOnRefreshListener = listener
    }

    fun interface RefreshListener {
        fun execute()
    }

    /**
     * Per-child layout information for layouts that support margins.
     */
    class LayoutParams : MarginLayoutParams {
        constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: MarginLayoutParams?) : super(source)
        constructor(source: ViewGroup.LayoutParams?) : super(source)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
        return LayoutParams(p)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    companion object {
        private const val INVALID_INDEX = -1

        //the default height of the RefreshView
        private const val DEFAULT_REFRESH_SIZE_DP = 30

        //the animation duration of the RefreshView scroll to the refresh point or the start point
        private const val DEFAULT_ANIMATE_DURATION = 300

        // the threshold of the trigger to refresh
        private const val DEFAULT_REFRESH_TARGET_OFFSET_DP = 50
        private const val DECELERATE_INTERPOLATION_FACTOR = 2.0f
    }
}