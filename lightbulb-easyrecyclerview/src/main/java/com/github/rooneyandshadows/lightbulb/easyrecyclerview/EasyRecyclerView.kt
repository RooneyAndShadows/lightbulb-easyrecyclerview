package com.github.rooneyandshadows.lightbulb.easyrecyclerview

import android.content.Context
import android.os.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.LayoutAnimationController
import android.widget.EdgeEffect
import android.widget.RelativeLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.github.rooneyandshadows.lightbulb.commons.utils.BundleUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView.LayoutManagerTypes.*
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.actions.LoadMoreDataAction
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.actions.RefreshDataAction
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.base.EasyRecyclerItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.edge.BounceEdge
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.handler.EasyRecyclerViewTouchHandler
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.handler.TouchCallbacks
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.HorizontalFlowLayoutManager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.HorizontalLinearLayoutManager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.VerticalFlowLayoutManager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.VerticalLinearLayoutManager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RecyclerRefreshLayout
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RecyclerRefreshLayout.RefreshStyle
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RefreshView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.collection.EasyRecyclerAdapterCollection.CollectionChangeListener
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.adapters.HeaderViewRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.adapters.HeaderViewRecyclerAdapter.ViewListeners
import com.google.android.material.progressindicator.LinearProgressIndicator

@Suppress("MemberVisibilityCanBePrivate", "unused", "UNUSED_PARAMETER")
abstract class EasyRecyclerView<ItemType : EasyAdapterDataModel>
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.easyRecyclerViewStyle,
    defStyleRes: Int = R.style.EasyRecyclerViewDefaultStyle,
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val loadingIndicator: LinearProgressIndicator by lazy {
        return@lazy findViewById(R.id.loadingIndicator)!!
    }
    private val recyclerView: RecyclerView by lazy {
        return@lazy findViewById(R.id.recyclerView)!!
    }
    private val refreshLayout: RecyclerRefreshLayout by lazy {
        return@lazy findViewById<RecyclerRefreshLayout?>(R.id.refreshLayout).apply {
            isEnabled = false
        }
    }
    private val recyclerEmptyLayoutContainer: RelativeLayout by lazy {
        return@lazy findViewById(R.id.recyclerEmptyLayoutContainer)!!
    }
    private val loadingFooterView: View by lazy {
        return@lazy inflate(context, R.layout.lv_loading_footer, null)
    }
    private val defaultEdgeFactory = object : EdgeEffectFactory() {
        override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
            return EdgeEffect(view.context)
        }
    }
    private var hasMoreDataToLoad = true
    private var emptyLayoutId: Int? = null
    private var layoutManagerType: LayoutManagerTypes? = null
    private val animationController: LayoutAnimationController? = null
    private var touchHandler: EasyRecyclerViewTouchHandler<ItemType>? = null
    private var renderedCallback: EasyRecyclerItemsReadyListener? = null
    private var emptyLayoutListeners: EasyRecyclerEmptyLayoutListener? = null
    private var loadMoreDataAction: LoadMoreDataAction<ItemType>? = null
    private var refreshDataAction: RefreshDataAction<ItemType>? = null
    private val dataAdapter: EasyRecyclerAdapter<ItemType> by lazy {
        return@lazy adapterCreator.createAdapter().apply dataAdapter@{
            collection.addOnCollectionChangedListener(object : CollectionChangeListener {
                override fun onChanged() {
                    setEmptyLayoutVisibility(collection.isEmpty())
                }
            })
            val recyclerView = this@EasyRecyclerView.recyclerView
            wrapperAdapter = HeaderViewRecyclerAdapter(recyclerView).apply {
                setDataAdapter(this@dataAdapter)
                recyclerView.adapter = this
            }
        }
    }
    var bounceOverscrollEnabled: Boolean
        set(value) {
            if (value && refreshLayout.isEnabled) {
                recyclerView.edgeEffectFactory = defaultEdgeFactory
                return
            }
            recyclerView.edgeEffectFactory = if (value) BounceEdge() else defaultEdgeFactory
        }
        get() = recyclerView.edgeEffectFactory is BounceEdge
    var pullToRefreshEnabled: Boolean
        set(value) {
            refreshLayout.apply {
                if (value == isEnabled) return@apply
                if (value) bounceOverscrollEnabled = false
                isEnabled = value
            }
        }
        get() = refreshLayout.isEnabled
    var emptyLayoutView: View? = null
        private set
    val isAnimating: Boolean
        get() = recyclerView.itemAnimator != null && recyclerView.itemAnimator!!.isRunning
    val isShowingLoadingFooter: Boolean
        get() = adapter.wrapperAdapter!!.containsFooterView(loadingFooterView)
    val isShowingLoadingHeader: Boolean
        get() = loadingIndicator.visibility == VISIBLE
    val isShowingEmptyLayout: Boolean
        get() = recyclerEmptyLayoutContainer.visibility == VISIBLE
    val isShowingRefreshLayout: Boolean
        get() = refreshLayout.isRefreshing
    val supportsLazyLoading: Boolean
        get() = loadMoreDataAction != null
    val layoutManager: LayoutManager?
        get() = recyclerView.layoutManager
    val itemDecorationCount: Int
        get() = recyclerView.itemDecorationCount
    private val isRunningAction: Boolean
        get() = loadMoreDataAction?.isRunning ?: false || refreshDataAction?.isRunning ?: false
    open val adapter: EasyRecyclerAdapter<ItemType>
        get() = dataAdapter
    protected abstract val adapterCreator: AdapterCreator<ItemType>

    companion object {
        private const val LAYOUT_MANAGER_STATE_KEY = "LAYOUT_MANAGER_STATE_KEY"
        private const val EMPTY_LAYOUT_TAG = "EMPTY_LAYOUT_TAG"
    }

    init {
        inflate(context, R.layout.lv_layout, this)
        readAttributes(context, attrs)
        initView()
    }

    @Override
    public override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.adapterState = adapter.saveAdapterState()
        myState.hasMoreDataToLoad = hasMoreDataToLoad
        myState.overscrollBounceEnabled = bounceOverscrollEnabled
        myState.pullToRefreshEnabled = pullToRefreshEnabled
        myState.showingRefreshLayout = refreshLayout.isRefreshing
        myState.showingLoadingFooterLayout = isShowingLoadingFooter
        myState.showingLoadingIndicator = isShowingLoadingHeader
        myState.emptyLayoutShowing = isShowingEmptyLayout
        myState.layoutManagerType = layoutManagerType!!.value
        if (emptyLayoutId != null)
            myState.emptyLayoutId = emptyLayoutId!!
        if (recyclerView.layoutManager != null) {
            val layoutManagerBundle = Bundle()
            layoutManagerBundle.putParcelable(LAYOUT_MANAGER_STATE_KEY, recyclerView.layoutManager!!.onSaveInstanceState())
            myState.layoutManagerState = layoutManagerBundle
        }
        return myState
    }

    @Override
    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        adapter.restoreAdapterState(savedState.adapterState!!)
        hasMoreDataToLoad = savedState.hasMoreDataToLoad
        bounceOverscrollEnabled = savedState.overscrollBounceEnabled
        pullToRefreshEnabled = savedState.pullToRefreshEnabled
        layoutManagerType = LayoutManagerTypes.valueOf(savedState.layoutManagerType)
        emptyLayoutId = savedState.emptyLayoutId
        bounceOverscrollEnabled = savedState.overscrollBounceEnabled
        pullToRefreshEnabled = savedState.pullToRefreshEnabled
        showLoadingIndicator(savedState.showingLoadingIndicator)
        showRefreshLayout(savedState.showingRefreshLayout)
        showLoadingFooter(savedState.showingLoadingFooterLayout)
        setEmptyLayoutVisibility(savedState.emptyLayoutShowing)
        configureLayoutManager()
        if (savedState.layoutManagerState != null && recyclerView.layoutManager != null) {
            val layoutManagerState = BundleUtils.getParcelable(
                LAYOUT_MANAGER_STATE_KEY,
                savedState.layoutManagerState!!,
                Parcelable::class.java
            )
            recyclerView.layoutManager!!.onRestoreInstanceState(layoutManagerState)
        }
    }

    @Override
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        touchHandler?.apply { cancelPendingAction() }
        loadMoreDataAction?.apply { onDetached() }
        refreshDataAction?.apply { onDetached() }
    }

    protected open fun getLayoutManagerType(): LayoutManagerTypes {
        return UNDEFINED
    }

    fun setSwipeCallbacks(swipeCallbacks: TouchCallbacks<ItemType>) {
        touchHandler = EasyRecyclerViewTouchHandler(this, swipeCallbacks).apply {
            ItemTouchHelper(this).apply {
                attachToRecyclerView(recyclerView)
            }
        }
    }

    /**
     * Sets the [androidx.recyclerview.widget.RecyclerView.ItemAnimator] to the recyclerview.
     *
     * @param animator - Animator to set.
     */
    fun setItemAnimator(animator: ItemAnimator?) {
        recyclerView.itemAnimator = animator
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
        post {
            if (emptyLayout == null) {
                emptyLayoutView = null
                recyclerEmptyLayoutContainer.removeAllViews()
                return@post
            }
            recyclerEmptyLayoutContainer.removeAllViews()
            emptyLayoutView = emptyLayout
            emptyLayoutListeners = layoutListener
            emptyLayoutView!!.tag = EMPTY_LAYOUT_TAG
            val isListEmpty = adapter.collection.isEmpty()
            recyclerEmptyLayoutContainer.visibility = if (isListEmpty) VISIBLE else GONE
            recyclerView.visibility = if (isListEmpty) GONE else VISIBLE
            val layout = findViewWithTag<View>(EMPTY_LAYOUT_TAG)
            layout?.let { removeView(it) }
            val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            params.addRule(ALIGN_PARENT_LEFT, TRUE)
            params.addRule(ALIGN_PARENT_TOP, TRUE)
            params.addRule(ALIGN_PARENT_BOTTOM, TRUE)
            params.addRule(ALIGN_PARENT_RIGHT, TRUE)
            if (emptyLayoutListeners != null) emptyLayoutListeners!!.onInflated(emptyLayoutView)
            recyclerEmptyLayoutContainer.addView(emptyLayoutView, params)
        }
    }

    @JvmOverloads
    fun addHeaderView(view: View, viewListeners: ViewListeners? = null) {
        dataAdapter.wrapperAdapter?.apply {
            if (containsHeaderView(view)) return@apply
            addHeaderView(view, viewListeners)
        }
    }

    fun removeHeaderView(view: View) {
        dataAdapter.wrapperAdapter?.apply {
            if (!containsHeaderView(view)) return@apply
            removeHeaderView(view)
        }
    }

    @JvmOverloads
    fun addFooterView(view: View, viewListeners: ViewListeners? = null) {
        dataAdapter.wrapperAdapter?.apply {
            if (containsFooterView(view)) return@apply
            addFooterView(view, viewListeners)
        }
    }

    fun removeFooterView(view: View) {
        dataAdapter.wrapperAdapter?.apply {
            if (!containsFooterView(view)) return@apply
            removeFooterView(view)
        }
    }

    fun refreshData() {
        if (isRunningAction) return
        refreshDataAction?.apply {
            executeAsync()
        }
    }

    fun loadMoreData() {
        if (isRunningAction || !hasMoreDataToLoad) return
        loadMoreDataAction?.apply {
            executeAsync()
        }
    }

    /**
     * Shows or hides global loading indicator
     *
     * @param state - whether is loading or not
     */
    fun showLoadingIndicator(state: Boolean) {
        if (state == isShowingLoadingHeader) return
        if (state) loadingIndicator.visibility = VISIBLE
        else loadingIndicator.visibility = GONE
    }

    /**
     * Shows loading footer view.
     *
     * @param isLoading - Whether is loading.
     */
    fun showLoadingFooter(isLoading: Boolean) {
        if (isLoading) addFooterView(loadingFooterView)
        else removeFooterView(loadingFooterView)
    }

    /**
     * Shows refresh layout visibillity
     *
     * @param newState - Whether is loading.
     */
    fun showRefreshLayout(newState: Boolean) {
        if (!pullToRefreshEnabled || refreshLayout.isRefreshing == newState) return
        refreshLayout.setRefreshing(newState)
    }

    /**
     * Sets the [RefreshDataAction] to be called on swipe refresh.
     *
     * @param refreshAction action to be executed on refresh.
     */
    fun setRefreshAction(refreshAction: RefreshDataAction<ItemType>?) {
        if (refreshAction == null) refreshDataAction?.dispose()
        this.refreshDataAction = refreshAction?.apply {
            onAttached(this@EasyRecyclerView)
        }
    }

    /**
     * Sets the [LoadMoreDataAction] called on lazy loading.
     *
     * @param lazyLoadingAction action to be executed.
     */
    fun setLazyLoadingAction(lazyLoadingAction: LoadMoreDataAction<ItemType>?) {
        if (lazyLoadingAction == null) loadMoreDataAction?.dispose()
        loadMoreDataAction = lazyLoadingAction?.apply {
            onAttached(this@EasyRecyclerView)
        }
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
     * @return true if there is more data to be loaded trough lazy loading.
     */
    fun hasMoreDataToLoad(): Boolean {
        return hasMoreDataToLoad
    }

    /**
     * Notifies adapter for change occurred at position.
     *
     * @param position - position of the changed item.
     */
    fun itemChanged(position: Int?) {
        post { dataAdapter.notifyItemChanged(position!!) }
    }

    /**
     * Executed any pending swipe operation for item.
     *
     * @see EasyRecyclerViewTouchHandler.executePendingAction
     */
    fun executePendingAction() {
        if (touchHandler != null) touchHandler!!.executePendingAction()
    }

    /**
     * Cancels any pending swipe operation for item.
     *
     * @see EasyRecyclerViewTouchHandler.executePendingAction
     */
    fun cancelPendingAction() {
        if (touchHandler != null) touchHandler!!.cancelPendingAction()
    }

    /**
     * Adds item decoration to the recycler view
     *
     * @param itemDecoration - Item decoration to add.
     */
    fun addItemDecoration(itemDecoration: EasyRecyclerItemDecoration) {
        recyclerView.addItemDecoration(itemDecoration)
    }

    /**
     * Adds item decoration to the recycler view
     *
     * @param itemDecoration - Item decoration to add.
     */
    fun addItemDecoration(itemDecoration: ItemDecoration) {
        recyclerView.addItemDecoration(itemDecoration)
    }

    /**
     * Removes item decoration from the recycler view
     *
     * @param itemDecoration - Item decoration to remove.
     */
    fun removeItemDecoration(itemDecoration: EasyRecyclerItemDecoration) {
        recyclerView.removeItemDecoration(itemDecoration)
    }

    /**
     * Removes item decoration from the recycler view
     *
     * @param itemDecorationIndex - index of the item decoration to remove.
     */
    fun removeItemDecorationAt(itemDecorationIndex: Int) {
        recyclerView.removeItemDecorationAt(itemDecorationIndex)
    }

    fun invalidateItemDecorations() {
        recyclerView.invalidateItemDecorations()
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attributes = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.EasyRecyclerView,
            R.attr.easyRecyclerViewStyle,
            R.style.EasyRecyclerViewDefaultStyle
        )
        try {
            attributes.apply {
                val emptyLayoutId = getResourceId(R.styleable.EasyRecyclerView_erv_empty_layout_id, -1)
                if (emptyLayoutId != -1) this@EasyRecyclerView.emptyLayoutId = emptyLayoutId
                bounceOverscrollEnabled = getBoolean(R.styleable.EasyRecyclerView_erv_supports_overscroll_bounce, false)
                pullToRefreshEnabled = getBoolean(R.styleable.EasyRecyclerView_erv_supports_pull_to_refresh, false)
                layoutManagerType = if (getLayoutManagerType() == UNDEFINED) LayoutManagerTypes.valueOf(
                    getInt(R.styleable.EasyRecyclerView_erv_layout_manager, 1)
                ) else getLayoutManagerType()
            }
        } finally {
            attributes.recycle()
        }
    }

    private fun initView() {
        initLoadingIndicator()
        configureRecycler()
        configureLayoutManager()
        configureRefreshLayout()
        configureEmptyLayout()
        isNestedScrollingEnabled = isNestedScrollingEnabled
    }

    private fun initLoadingIndicator() {
        loadingIndicator.visibility = if (isShowingLoadingHeader) VISIBLE else GONE
    }

    private fun configureLayoutManager() {
        when (layoutManagerType) {
            UNDEFINED, LAYOUT_LINEAR_VERTICAL ->
                recyclerView.layoutManager = VerticalLinearLayoutManager(this)
            LAYOUT_LINEAR_HORIZONTAL ->
                recyclerView.layoutManager = HorizontalLinearLayoutManager(this)
            LAYOUT_FLOW_VERTICAL ->
                recyclerView.layoutManager = VerticalFlowLayoutManager(this)
            LAYOUT_FLOW_HORIZONTAL ->
                recyclerView.layoutManager = HorizontalFlowLayoutManager(this)
            else -> {}
        }
    }

    private fun configureRecycler() {
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
        val indicatorSize: Int = ResourceUtils.getDimenPxById(context, R.dimen.erv_header_refresh_indicator_size)
        val refreshBackgroundColor: Int = ResourceUtils.getColorByAttribute(context, android.R.attr.colorBackground)
        //val refreshStrokeColor: Int = ResourceUtils.getColorByAttribute(context, android.R.attr.colorPrimary)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, indicatorSize)
        val refreshView = RefreshView(context)
        refreshView.setBackgroundColor(refreshBackgroundColor)
        refreshLayout.setRefreshView(refreshView, layoutParams)
        refreshLayout.setRefreshStyle(RefreshStyle.NORMAL)
        refreshLayout.setOnRefreshListener(object : RecyclerRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                if (isRunningAction) {
                    refreshLayout.setRefreshing(false)
                    return
                }
                refreshData()
            }
        })
    }

    private fun configureEmptyLayout() {
        if (emptyLayoutId != null) setEmptyLayout(emptyLayoutId!!)
    }

    private fun setEmptyLayoutVisibility(visibility: Boolean) {
        if (emptyLayoutView == null || isShowingEmptyLayout == visibility) return
        if (visibility) {
            recyclerEmptyLayoutContainer.visibility = VISIBLE
            recyclerView.visibility = INVISIBLE
            if (emptyLayoutListeners != null) emptyLayoutListeners!!.onShow(emptyLayoutView)
        } else {
            if (emptyLayoutListeners != null) emptyLayoutListeners!!.onHide(emptyLayoutView)
            recyclerEmptyLayoutContainer.visibility = INVISIBLE
            recyclerView.visibility = VISIBLE
        }
    }

    private class SavedState : BaseSavedState {
        var adapterState: Bundle? = null
        var hasMoreDataToLoad = false
        var overscrollBounceEnabled = false
        var pullToRefreshEnabled = false
        var showingRefreshLayout = false
        var showingLoadingFooterLayout = false
        var showingLoadingIndicator = false
        var emptyLayoutShowing = false
        var layoutManagerType = 0
        var layoutManagerState: Bundle? = null
        private var swipeConfigState: Bundle? = null
        var emptyLayoutId = 0

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            adapterState = parcel.readBundle(EasyRecyclerView::class.java.classLoader)
            hasMoreDataToLoad = parcel.readByte().toInt() != 0
            overscrollBounceEnabled = parcel.readByte().toInt() != 0
            pullToRefreshEnabled = parcel.readByte().toInt() != 0
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
            out.writeByte((if (pullToRefreshEnabled) 1 else 0).toByte())
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

    abstract class EasyRecyclerEmptyLayoutListener {
        open fun onInflated(view: View?) {}
        open fun onShow(view: View?) {}
        open fun onHide(view: View?) {}
    }

    interface EasyRecyclerItemsReadyListener {
        fun execute()
    }

    interface AdapterCreator<ItemType : EasyAdapterDataModel> {
        fun createAdapter(): EasyRecyclerAdapter<ItemType>
    }
}