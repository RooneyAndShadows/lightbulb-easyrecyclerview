package com.github.rooneyandshadows.lightbulb.easyrecyclerview

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.LayoutAnimationController
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.factor.bouncy.BouncyRecyclerView
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.*
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.EasyRecyclerViewTouchHandler.TouchCallbacks
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RecyclerRefreshLayout
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RecyclerRefreshLayout.RefreshStyle
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RefreshView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.HeaderViewRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.HeaderViewRecyclerAdapter.ViewListeners
import com.google.android.material.progressindicator.LinearProgressIndicator

class EasyRecyclerView<IType : EasyAdapterDataModel?, AType : EasyRecyclerAdapter<IType>?> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {
    private val showRefreshManualDelay = 300
    private val showLoadingManualDelay = 300
    private val bouncyFlingAnimationSize = 0.1f
    private val bouncyOverscrollAnimationSize = 0.1f
    private val LAYOUT_MANAGER_STATE_TAG = "LAYOUT_MANAGER_STATE_TAG"
    private val EMPTY_LAYOUT_VIEW_TAG = "EMPTY_LAYOUT_TAG"
    private var supportsPullToRefresh = false
    private var supportsLazyLoading = false
    private var hasMoreDataToLoad = true
    private var supportsBounceOverscroll = false
    private var showingEmptyLayout = false
    var isShowingLoadingFooter = false
        private set
    var isShowingLoadingHeader = false
        private set
    var isShowingRefreshLayout = false
        private set
    val isAnimating: Boolean
        get() = recyclerView.itemAnimator != null && recyclerView.itemAnimator!!.isRunning
    var emptyLayoutView: View? = null
        private set
    private var overscrollBounceEnabled = false
    private var pullToRefreshEnabled = false
    private var dataAdapter: AType? = null
    private var loadingFooterView: View? = null
    private var emptyLayoutId: Int? = null
    private var layoutManagerType: LayoutManagerTypes? = null
    private lateinit var loadingIndicator: LinearProgressIndicator
    private var wrapperAdapter: HeaderViewRecyclerAdapter? = null
    private lateinit var recyclerView: BouncyRecyclerView
    private var recyclerEmptyLayoutContainer: RelativeLayout? = null
    private var refreshLayout: RecyclerRefreshLayout? = null
    private val animationController: LayoutAnimationController? = null
    private var loadMoreCallback: LoadMoreCallback<IType, AType>? = null
    private var refreshCallback: RefreshCallback<IType, AType>? = null
    private var swipeToDeleteCallbacks: EasyRecyclerViewTouchHandler<IType, AType>? = null
    private var renderedCallback: EasyRecyclerItemsReadyListener? = null
    private var emptyLayoutListeners: EasyRecyclerEmptyLayoutListener? = null
    private val showRefreshLayoutDelayedRunnable = Runnable { refreshLayout!!.setRefreshing(true) }
    private val showLoadingDelayedRunnable = Runnable {
        if (supportsPullToRefresh)
            if (isShowingLoadingHeader) {
                enablePullToRefreshLayout(false)
                loadingIndicator.visibility = VISIBLE
            } else {
                enablePullToRefreshLayout(true)
                loadingIndicator.visibility = GONE
            }
    }

    init {
        readAttributes(context, attrs)
        initView()
    }

    @Override
    public override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        if (dataAdapter != null) myState.adapterState = adapter!!.saveAdapterState()
        myState.hasMoreDataToLoad = hasMoreDataToLoad
        myState.supportsLoadMore = supportsLazyLoading
        myState.supportsPullToRefresh = supportsPullToRefresh
        myState.supportsBounceOverscroll = supportsBounceOverscroll
        myState.overscrollBounceEnabled = overscrollBounceEnabled
        myState.pullToRefreshEnabled = pullToRefreshEnabled
        myState.showingRefreshLayout = isShowingRefreshLayout
        myState.showingLoadingFooterLayout = isShowingLoadingFooter
        myState.showingLoadingIndicator = isShowingLoadingHeader
        myState.emptyLayoutShowing = showingEmptyLayout
        myState.layoutManagerType = layoutManagerType!!.value
        if (emptyLayoutId != null) myState.emptyLayoutId = emptyLayoutId
        if (recyclerView.layoutManager != null) {
            val layoutManagerBundle = Bundle()
            layoutManagerBundle.putParcelable(LAYOUT_MANAGER_STATE_TAG, recyclerView.layoutManager!!.onSaveInstanceState())
            myState.layoutManagerState = layoutManagerBundle
        }
        return myState
    }

    @Override
    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        if (dataAdapter != null) adapter!!.restoreAdapterState(savedState.adapterState)
        hasMoreDataToLoad = savedState.hasMoreDataToLoad
        supportsLazyLoading = savedState.supportsLoadMore
        supportsPullToRefresh = savedState.supportsPullToRefresh
        supportsBounceOverscroll = savedState.supportsBounceOverscroll
        overscrollBounceEnabled = savedState.overscrollBounceEnabled
        pullToRefreshEnabled = savedState.pullToRefreshEnabled
        layoutManagerType = LayoutManagerTypes.valueOf(savedState.layoutManagerType)
        emptyLayoutId = savedState.emptyLayoutId
        enableBounceOverscroll(overscrollBounceEnabled)
        enablePullToRefreshLayout(pullToRefreshEnabled)
        showLoadingIndicator(savedState.showingLoadingIndicator)
        showRefreshLayout(savedState.showingRefreshLayout)
        showLoadingFooter(savedState.showingLoadingFooterLayout)
        setEmptyLayoutVisibility(savedState.emptyLayoutShowing)
        configureLayoutManager()
        if (savedState.layoutManagerState != null && recyclerView.layoutManager != null) {
            val layState = savedState.layoutManagerState!!.getParcelable<Parcelable>(LAYOUT_MANAGER_STATE_TAG)
            recyclerView.layoutManager!!.onRestoreInstanceState(layState)
        }
    }

    @Override
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (swipeToDeleteCallbacks != null) swipeToDeleteCallbacks!!.cancelPendingAction()
    }

    protected fun getLayoutManagerType(): LayoutManagerTypes {
        return LayoutManagerTypes.UNDEFINED
    }

    /**
     * Sets recyclerview data adapter and item swipe callbacks.
     *
     * @param adapter        - recyclerview adapter.
     * @param swipeCallbacks - swipe callbacks.
     */
    fun setAdapter(adapter: AType, swipeCallbacks: TouchCallbacks<IType>?) {
        this.adapter = adapter
        if (swipeCallbacks != null) {
            swipeToDeleteCallbacks = EasyRecyclerViewTouchHandler(this, swipeCallbacks)
            val itemTouchHelper = ItemTouchHelper(
                swipeToDeleteCallbacks!!
            )
            itemTouchHelper.attachToRecyclerView(recyclerView)
        }
    }

    /**
     * @param supportsPullToRefresh - Whether pull to refresh is supported.
     */
    fun setSupportsPullToRefresh(supportsPullToRefresh: Boolean) {
        this.supportsPullToRefresh = supportsPullToRefresh
    }

    /**
     * @param supportsBounceOverscroll - Whether overscroll bounce effect is supported.
     */
    fun setSupportsBounceOverscroll(supportsBounceOverscroll: Boolean) {
        this.supportsBounceOverscroll = supportsBounceOverscroll
    }

    /**
     * @param enabled - Whether overscroll bounce is enabled.
     */
    fun enableBounceOverscroll(enabled: Boolean) {
        if (!supportsBounceOverscroll) return
        enableBounceOverscrollInternally(enabled)
    }

    /**
     * @param enabled - Whether pull to refresh is enabled.
     */
    fun enablePullToRefreshLayout(enabled: Boolean) {
        if (!supportsPullToRefresh) return
        enablePullToRefreshLayoutInternally(enabled)
    }

    /**
     * @param supportsLazyLoading - Whether lazy loading is supported.
     */
    fun setSupportsLazyLoading(supportsLazyLoading: Boolean) {
        this.supportsLazyLoading = supportsLazyLoading
    }

    /**
     * @param hasMoreDataToLoad - Whether there is more data available
     */
    fun setHasMoreDataToLoad(hasMoreDataToLoad: Boolean) {
        this.hasMoreDataToLoad = hasMoreDataToLoad
    }

    /**
     * Sets alternative layout to show in case of empty list.
     *
     * @param emptyLayoutId - Resource identifier for layout to show.
     */
    fun setEmptyLayout(emptyLayoutId: Int) {
        setEmptyLayout(emptyLayoutId, null)
    }

    /**
     * Sets alternative layout to show in case of empty list.
     *
     * @param emptyLayoutView - Layout to show.
     */
    fun setEmptyLayout(emptyLayoutView: View?) {
        setEmptyLayout(emptyLayoutView, null)
    }

    /**
     * Sets alternative layout to show in case of empty list.
     *
     * @param emptyLayoutId - Resource identifier for layout to show.
     * @param onLayoutReady - Callback to be executed when layout is ready.
     */
    fun setEmptyLayout(emptyLayoutId: Int, onLayoutReady: EasyRecyclerEmptyLayoutListener?) {
        this.emptyLayoutId = emptyLayoutId
        val layout = LayoutInflater.from(context).inflate(emptyLayoutId, null)
        setEmptyLayout(layout, onLayoutReady)
    }

    /**
     * Sets alternative layout to show in case of empty list.
     *
     * @param emptyLayout    - Layout to show.
     * @param layoutListener - Callbacks to be executed on show/hide.
     */
    fun setEmptyLayout(emptyLayout: View?, layoutListener: EasyRecyclerEmptyLayoutListener?) {
        if (emptyLayout == null) {
            emptyLayoutView = null
            recyclerEmptyLayoutContainer!!.removeAllViews()
            return
        }
        recyclerEmptyLayoutContainer!!.removeAllViews()
        emptyLayoutView = emptyLayout
        emptyLayoutListeners = layoutListener
        emptyLayoutView!!.tag = EMPTY_LAYOUT_VIEW_TAG
        val isListEmpty = adapter == null || !adapter!!.hasItems()
        recyclerEmptyLayoutContainer!!.visibility = if (isListEmpty) VISIBLE else GONE
        recyclerView.visibility = if (isListEmpty) GONE else VISIBLE
        val layout = findViewWithTag<View>(EMPTY_LAYOUT_VIEW_TAG)
        layout?.let { removeView(it) }
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        params.addRule(ALIGN_PARENT_LEFT, TRUE)
        params.addRule(ALIGN_PARENT_TOP, TRUE)
        params.addRule(ALIGN_PARENT_BOTTOM, TRUE)
        params.addRule(ALIGN_PARENT_RIGHT, TRUE)
        if (emptyLayoutListeners != null) emptyLayoutListeners!!.onInflated(emptyLayoutView)
        recyclerEmptyLayoutContainer!!.addView(emptyLayoutView, params)
    }

    @JvmOverloads
    fun addHeaderView(view: View?, viewListeners: ViewListeners? = null) {
        if (!wrapperAdapter!!.containsHeaderView(view)) wrapperAdapter!!.addHeaderView(view, viewListeners)
    }

    fun removeHeaderView(view: View?) {
        if (wrapperAdapter!!.containsHeaderView(view)) wrapperAdapter!!.removeHeaderView(view)
    }

    fun addFooterView(view: View?) {
        if (!wrapperAdapter!!.containsFooterView(view)) wrapperAdapter!!.addFooterView(view)
    }

    fun addFooterView(view: View?, viewListeners: ViewListeners?) {
        if (!wrapperAdapter!!.containsFooterView(view)) wrapperAdapter!!.addFooterView(view, viewListeners)
    }

    fun removeFooterView(view: View?) {
        if (wrapperAdapter!!.containsFooterView(view)) wrapperAdapter!!.removeFooterView(view)
    }

    fun refreshData() {
        if (!supportsPullToRefresh || isShowingRefreshLayout) return
        isShowingRefreshLayout = true
        postDelayed(showRefreshLayoutDelayedRunnable, showRefreshManualDelay.toLong())
        if (refreshCallback != null) refreshCallback!!.refresh(this@EasyRecyclerView)
    }

    fun loadMoreData() {
        if (loadMoreCallback != null || hasMoreDataToLoad) {
            showLoadingFooter(true)
            if (loadMoreCallback != null) loadMoreCallback!!.loadMore(this)
        }
    }

    /**
     * Shows or hides global loading indicator
     *
     * @param state - whether is loading or not
     */
    fun showLoadingIndicator(state: Boolean) {
        if (state == isShowingLoadingHeader) return
        isShowingLoadingHeader = state
        if (!isShowingLoadingHeader) {
            removeCallbacks(showLoadingDelayedRunnable)
            loadingIndicator.visibility = GONE
        } else postDelayed(showLoadingDelayedRunnable, showLoadingManualDelay.toLong())
    }

    /**
     * Indicates whether lazy loading is activated.
     *
     * @param isLoading - Whether is loading.
     * @see EasyRecyclerView.setSupportsLazyLoading
     */
    fun showLoadingFooter(isLoading: Boolean) {
        if (!supportsLazyLoading) return
        isShowingLoadingFooter = isLoading
        recyclerView.post {
            if (isLoading) {
                if (!wrapperAdapter!!.containsFooterView(loadingFooterView)) wrapperAdapter!!.addFooterView(loadingFooterView)
            } else {
                if (wrapperAdapter!!.containsFooterView(loadingFooterView)) wrapperAdapter!!.removeFooterView(
                    loadingFooterView
                )
            }
        }
    }

    /**
     * Indicates whether refresh is activated.
     *
     * @param isRefreshing - Whether is loading.
     * @see EasyRecyclerView.setSupportsPullToRefresh
     */
    fun showRefreshLayout(isRefreshing: Boolean) {
        if (!pullToRefreshEnabled || isRefreshing == isShowingRefreshLayout) return
        isShowingRefreshLayout = isRefreshing
        recyclerView.post {
            if (!isShowingRefreshLayout) removeCallbacks(showRefreshLayoutDelayedRunnable)
            refreshLayout!!.setRefreshing(isRefreshing)
        }
    }

    /**
     * Sets the [LoadMoreCallback] to be called when lazy loading is triggered.
     * The callback will be called only if [EasyRecyclerView.supportsLazyLoading] is true.
     *
     * @param callback - The LoadMoreCallback  to be executed on lazy loading.
     * @see EasyRecyclerView.setSupportsLazyLoading
     */
    fun setLoadMoreCallback(callback: LoadMoreCallback<IType, AType>?) {
        if (!supportsLazyLoading) return
        loadMoreCallback = callback
    }

    /**
     * Sets the [RefreshCallback] to be called on swipe refresh.
     * The callback will be called only if [EasyRecyclerView.supportsPullToRefresh] is true.
     *
     * @param callback - The RefreshCallback to be executed on refresh.
     * @see EasyRecyclerView.setSupportsPullToRefresh
     */
    fun setRefreshCallback(callback: RefreshCallback<IType, AType>?) {
        if (!supportsPullToRefresh) return
        refreshCallback = callback
        refreshLayout!!.setOnRefreshListener(object : RecyclerRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                if (isShowingRefreshLayout)
                    return
                isShowingRefreshLayout = true
                if (refreshCallback != null)
                    refreshCallback!!.refresh(this@EasyRecyclerView)
            }
        })
    }

    /**
     * Sets the [EasyRecyclerItemsReadyListener] to be executed on view ready.
     *
     * @param renderedCallback - The EasyRecyclerItemsReadyCallback to be executed on view ready.
     */
    fun setRenderedCallback(renderedCallback: EasyRecyclerItemsReadyListener?) {
        this.renderedCallback = renderedCallback
    }

    /**
     * Sets the [androidx.recyclerview.widget.RecyclerView.ItemAnimator] to the recyclerview.
     *
     * @param animator - Animator to set.
     */
    fun setItemAnimator(animator: RecyclerView.ItemAnimator?) {
        recyclerView.itemAnimator = animator
    }

    /**
     * @return recyclerview adapter items.
     */
    val items: List<IType>
        get() = dataAdapter!!.items
    /**
     * @return recyclerview adapter.
     */
    /**
     * Sets recyclerview data adapter.
     *
     * @param adapter - recyclerview adapter.
     */
    var adapter: AType?
        get() = dataAdapter
        set(adapter) {
            if (adapter != null) {
                wrapperAdapter = HeaderViewRecyclerAdapter(recyclerView)
                dataAdapter = adapter
                dataAdapter!!.setWrapperAdapter(wrapperAdapter)
                dataAdapter!!.addOnCollectionChangedListener { setEmptyLayoutVisibility(!dataAdapter!!.hasItems()) }
                wrapperAdapter!!.setDataAdapter(dataAdapter)
                recyclerView.adapter = wrapperAdapter
            } else {
                dataAdapter = null
                wrapperAdapter = null
                recyclerView.adapter = null
            }
        }

    /**
     * @return true if adapter contains at least on item.
     */
    fun hasItems(): Boolean {
        return if (dataAdapter == null) false else dataAdapter!!.hasItems()
    }

    /**
     * @return true if there is more data to be loaded trough lazy loading.
     */
    fun hasMoreDataToLoad(): Boolean {
        return hasMoreDataToLoad
    }

    /**
     * @return true if adapter has at least one item selected.
     */
    fun hasSelection(): Boolean {
        return dataAdapter!!.hasSelection()
    }

    /**
     * @return Layout manager for the view
     */
    val layoutManager: RecyclerView.LayoutManager?
        get() = recyclerView.layoutManager

    /**
     * @return Count of added item decorations to the recycler view.
     */
    val itemDecorationCount: Int
        get() = recyclerView.itemDecorationCount

    /**
     * @return whether component supports refresh
     */
    fun supportsPullToRefresh(): Boolean {
        return supportsPullToRefresh
    }

    /**
     * @return whether component supports lazy loading
     */
    fun supportsLazyLoading(): Boolean {
        return supportsLazyLoading
    }

    /**
     * @return whether component supports overscroll bounce
     */
    fun supportsBounceOverscroll(): Boolean {
        return supportsBounceOverscroll
    }

    /**
     * Notifies adapter for change occurred at position.
     *
     * @param position - position of the changed item.
     */
    fun itemChanged(position: Int?) {
        post { dataAdapter!!.notifyItemChanged(position!!) }
    }

    /**
     * Clears items from the adapter.
     */
    fun clearItems() {
        dataAdapter!!.clearCollection()
    }

    /**
     * Executed any pending swipe operation for item.
     *
     * @see EasyRecyclerViewTouchHandler.executePendingAction
     */
    fun executePendingAction() {
        if (swipeToDeleteCallbacks != null) swipeToDeleteCallbacks!!.executePendingAction()
    }

    /**
     * Cancels any pending swipe operation for item.
     *
     * @see EasyRecyclerViewTouchHandler.executePendingAction
     */
    fun cancelPendingAction() {
        if (swipeToDeleteCallbacks != null) swipeToDeleteCallbacks!!.cancelPendingAction()
    }

    /**
     * Adds item decoration to the recycler view
     *
     * @param itemDecoration - Item decoration to add.
     */
    fun addItemDecoration(itemDecoration: RecyclerView.ItemDecoration?) {
        recyclerView.addItemDecoration(itemDecoration!!)
    }

    /**
     * Removes item decoration from the recycler view
     *
     * @param itemDecoration - Item decoration to remove.
     */
    fun removeItemDecoration(itemDecoration: RecyclerView.ItemDecoration?) {
        recyclerView.removeItemDecoration(itemDecoration!!)
    }

    /**
     * Removes item decoration from the recycler view
     *
     * @param itemDecorationIndex - index of the item decoration to remove.
     */
    fun removeItemDecorationAt(itemDecorationIndex: Int) {
        recyclerView.removeItemDecorationAt(itemDecorationIndex)
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.EasyRecyclerView, 0, 0)
        try {
            val textView = TextView(getContext())
            val emptyLayoutId = attributes.getResourceId(R.styleable.EasyRecyclerView_ERV_EmptyLayoutId, -1)
            if (emptyLayoutId != -1) this.emptyLayoutId = emptyLayoutId
            supportsPullToRefresh = attributes.getBoolean(R.styleable.EasyRecyclerView_ERV_SupportsPullToRefresh, false)
            supportsBounceOverscroll =
                attributes.getBoolean(R.styleable.EasyRecyclerView_ERV_SupportsOverscrollBounce, false)
            supportsLazyLoading = attributes.getBoolean(R.styleable.EasyRecyclerView_ERV_SupportsLoadMore, false)
            layoutManagerType =
                if (getLayoutManagerType() == null || getLayoutManagerType() == LayoutManagerTypes.UNDEFINED) LayoutManagerTypes.valueOf(
                    attributes.getInt(
                        R.styleable.EasyRecyclerView_ERV_LayoutManager,
                        1
                    )
                ) else getLayoutManagerType()
        } finally {
            attributes.recycle()
        }
    }

    private fun initView() {
        inflate(context, R.layout.lv_layout, this)
        loadingFooterView = inflate(context, R.layout.lv_loading_footer, null)
        initLoadingIndicator()
        configureRecycler()
        configureLayoutManager()
        configureRefreshLayout()
        configureEmptyLayout()
        isNestedScrollingEnabled = isNestedScrollingEnabled
        enableBounceOverscrollInternally(supportsBounceOverscroll)
        enablePullToRefreshLayoutInternally(supportsPullToRefresh)
    }

    private fun initLoadingIndicator() {
        loadingIndicator = findViewById(R.id.loadingIndicator)
        loadingIndicator.visibility = if (isShowingLoadingHeader) VISIBLE else GONE
    }

    private fun configureLayoutManager() {
        when (layoutManagerType) {
            LayoutManagerTypes.UNDEFINED, LayoutManagerTypes.LAYOUT_LINEAR_VERTICAL ->
                recyclerView.layoutManager = VerticalLinearLayoutManager(this)
            LayoutManagerTypes.LAYOUT_LINEAR_HORIZONTAL ->
                recyclerView.layoutManager = HorizontalLinearLayoutManager(this)
            LayoutManagerTypes.LAYOUT_FLOW_VERTICAL ->
                recyclerView.layoutManager = VerticalFlowLayoutManager(this)
            LayoutManagerTypes.LAYOUT_FLOW_HORIZONTAL ->
                recyclerView.layoutManager = HorizontalFlowLayoutManager(this)
            else -> {}
        }
    }

    private fun configureRecycler() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.stiffness = SpringForce.STIFFNESS_MEDIUM
        recyclerView.clearOnScrollListeners()
        //animationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_fall_down);
        //recyclerView.setLayoutAnimation(animationController);
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (renderedCallback != null) renderedCallback!!.execute()
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun configureRefreshLayout() {
        refreshLayout = findViewById(R.id.refreshLayout)
        val indicatorSize: Int = ResourceUtils.getDimenPxById(context, R.dimen.erv_header_refresh_indicator_size)
        val refreshBackgroundColor: Int = ResourceUtils.getColorByAttribute(context, android.R.attr.colorBackground)
        val refreshStrokeColor: Int = ResourceUtils.getColorByAttribute(context, android.R.attr.colorPrimary)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, indicatorSize)
        val refreshView = RefreshView(context)
        refreshView.setBackgroundColor(refreshBackgroundColor)
        refreshLayout!!.setRefreshView(refreshView, layoutParams)
        refreshLayout!!.setRefreshStyle(RefreshStyle.NORMAL)
    }

    private fun configureEmptyLayout() {
        recyclerEmptyLayoutContainer = findViewById(R.id.recyclerEmptyLayoutContainer)
        if (emptyLayoutId != null) setEmptyLayout(emptyLayoutId!!)
    }

    private fun setEmptyLayoutVisibility(visibility: Boolean) {
        if (emptyLayoutView == null) return
        showingEmptyLayout = visibility
        if (visibility) {
            recyclerEmptyLayoutContainer!!.visibility = VISIBLE
            recyclerView.visibility = INVISIBLE
            if (emptyLayoutListeners != null) emptyLayoutListeners!!.onShow(emptyLayoutView)
        } else {
            if (emptyLayoutListeners != null) emptyLayoutListeners!!.onHide(emptyLayoutView)
            recyclerEmptyLayoutContainer!!.visibility = INVISIBLE
            recyclerView.visibility = VISIBLE
        }
    }

    private fun enableBounceOverscrollInternally(enabled: Boolean) {
        overscrollBounceEnabled = enabled
        recyclerView.flingAnimationSize = if (enabled) bouncyFlingAnimationSize else 0f
        recyclerView.overscrollAnimationSize = if (enabled) bouncyOverscrollAnimationSize else 0f
        recyclerView.dampingRatio =
            if (enabled) SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY else SpringForce.DAMPING_RATIO_NO_BOUNCY
    }

    private fun enablePullToRefreshLayoutInternally(enabled: Boolean) {
        pullToRefreshEnabled = enabled
        if (enabled) enableBounceOverscrollInternally(false)
        refreshLayout!!.isEnabled = enabled
    }

    private class SavedState : BaseSavedState {
        var adapterState: Bundle? = null
        var hasMoreDataToLoad = false
        var overscrollBounceEnabled = false
        var supportsBounceOverscroll = false
        var supportsPullToRefresh = false
        var supportsLoadMore = false
        var pullToRefreshEnabled = false
        var showingRefreshLayout = false
        var showingLoadingFooterLayout = false
        var showingLoadingIndicator = false
        var emptyLayoutShowing = false
        var layoutManagerType = 0
        var layoutManagerState: Bundle? = null
        private var swipeConfigState: Bundle? = null
        var emptyLayoutId = 0

        internal constructor(superState: Parcelable?) : super(superState) {}

        private constructor(parcel: Parcel) : super(parcel) {
            adapterState = parcel.readBundle(EasyRecyclerView::class.java.classLoader)
            hasMoreDataToLoad = parcel.readByte().toInt() != 0
            overscrollBounceEnabled = parcel.readByte().toInt() != 0
            supportsPullToRefresh = parcel.readByte().toInt() != 0
            pullToRefreshEnabled = parcel.readByte().toInt() != 0
            supportsBounceOverscroll = parcel.readByte().toInt() != 0
            supportsLoadMore = parcel.readByte().toInt() != 0
            showingRefreshLayout = parcel.readByte().toInt() != 0
            showingLoadingFooterLayout = parcel.readByte().toInt() != 0
            showingLoadingIndicator = parcel.readByte().toInt() != 0
            emptyLayoutShowing = parcel.readByte().toInt() != 0
            layoutManagerType = parcel.readInt()
            layoutManagerState = parcel.readBundle(javaClass.classLoader)
            swipeConfigState = parcel.readBundle(javaClass.classLoader)
            emptyLayoutId = parcel.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeBundle(adapterState)
            out.writeByte((if (hasMoreDataToLoad) 1 else 0).toByte())
            out.writeByte((if (overscrollBounceEnabled) 1 else 0).toByte())
            out.writeByte((if (supportsPullToRefresh) 1 else 0).toByte())
            out.writeByte((if (pullToRefreshEnabled) 1 else 0).toByte())
            out.writeByte((if (supportsBounceOverscroll) 1 else 0).toByte())
            out.writeByte((if (supportsLoadMore) 1 else 0).toByte())
            out.writeByte((if (showingRefreshLayout) 1 else 0).toByte())
            out.writeByte((if (showingLoadingFooterLayout) 1 else 0).toByte())
            out.writeByte((if (showingLoadingIndicator) 1 else 0).toByte())
            out.writeByte((if (emptyLayoutShowing) 1 else 0).toByte())
            out.writeInt(layoutManagerType)
            out.writeBundle(layoutManagerState)
            out.writeBundle(swipeConfigState)
            out.writeInt(emptyLayoutId)
        }
    }

    enum class LayoutManagerTypes(val value: Int) {
        LAYOUT_LINEAR_VERTICAL(1),
        LAYOUT_LINEAR_HORIZONTAL(2),
        LAYOUT_FLOW_VERTICAL(3),
        LAYOUT_FLOW_HORIZONTAL(4),
        UNDEFINED(5);

        companion object {
            fun valueOf(value: Int) = values().first { it.value == value }
        }
    }

    class EasyRecyclerEmptyLayoutListener {
        fun onInflated(view: View?) {}
        fun onShow(view: View?) {}
        fun onHide(view: View?) {}
    }

    interface EasyRecyclerItemsReadyListener {
        fun execute()
    }

    interface EasyRecyclerViewAdapterAction<IType : EasyAdapterDataModel?, AType : EasyRecyclerAdapter<IType>?> {
        fun execute(adapter: EasyRecyclerAdapter<IType>?)
    }

    interface RefreshCallback<IType : EasyAdapterDataModel?, AType : EasyRecyclerAdapter<IType>?> {
        fun refresh(view: EasyRecyclerView<IType, AType>?)
    }

    interface LoadMoreCallback<IType : EasyAdapterDataModel?, AType : EasyRecyclerAdapter<IType>?> {
        fun loadMore(view: EasyRecyclerView<IType, AType>?)
    }
}