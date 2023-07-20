package com.github.rooneyandshadows.lightbulb.easyrecyclerview

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.animation.LayoutAnimationController
import android.widget.RelativeLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.github.rooneyandshadows.lightbulb.commons.utils.BundleUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView.LayoutManagerTypes.*
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.R.styleable.EasyRecyclerView_erv_layout_manager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.R.styleable.EasyRecyclerView_erv_supports_overscroll_bounce
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.base.EasyRecyclerItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.HorizontalFlowLayoutManager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.HorizontalLinearLayoutManager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.VerticalFlowLayoutManager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.VerticalLinearLayoutManager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.bounce_overscroll.BounceOverscroll
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.empty_layout.EasyRecyclerEmptyLayoutListener
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.empty_layout.EmptyLayout
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.lazy_loading.LazyLoading
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.lazy_loading.LazyLoadingListener
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.pull_to_refresh.PullToRefresh
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.pull_to_refresh.PullToRefreshListener
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.touch_handler.EasyRecyclerViewTouchHandler
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.touch_handler.EasyRecyclerViewTouchHandler.TouchHelperListeners
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.touch_handler.TouchCallbacks
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.adapters.HeaderViewRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.adapters.HeaderViewRecyclerAdapter.ViewListeners
import com.google.android.material.progressindicator.LinearProgressIndicator

@Suppress("MemberVisibilityCanBePrivate", "unused", "LeakingThis")
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
    private var dataAdapter: EasyRecyclerAdapter<ItemType>? = null
    private var pullToRefresh: PullToRefresh<ItemType>
    private var lazyLoading: LazyLoading<ItemType>
    private var emptyLayout: EmptyLayout<ItemType>
    private var bounceOverscroll: BounceOverscroll<ItemType>
    private var layoutManagerType: LayoutManagerTypes? = null
    private val animationController: LayoutAnimationController? = null
    private var touchHandler: EasyRecyclerViewTouchHandler<ItemType>? = null
    var emptyLayoutView: View? = null
        private set
    val isBounceOverscrollEnabled: Boolean
        get() = bounceOverscroll.enabled
    val isPullToRefreshEnabled: Boolean
        get() = pullToRefresh.hasAttachedListener
    val isAnimating: Boolean
        get() = recyclerView.itemAnimator != null && recyclerView.itemAnimator!!.isRunning
    val isLazyLoadingRunning: Boolean
        get() = lazyLoading.isLoading
    val isShowingLoadingHeader: Boolean
        get() = loadingIndicator.visibility == VISIBLE
    val isShowingEmptyLayout: Boolean
        get() = emptyLayout.isShowing
    val isShowingRefreshLayout: Boolean
        get() = pullToRefresh.refreshing
    val layoutManager: LayoutManager?
        get() = recyclerView.layoutManager
    val itemDecorationCount: Int
        get() = recyclerView.itemDecorationCount
    open val adapter: EasyRecyclerAdapter<ItemType>?
        get() = dataAdapter

    companion object {
        private const val LAYOUT_MANAGER_STATE_KEY = "LAYOUT_MANAGER_STATE_KEY"
        private const val EMPTY_LAYOUT_TAG = "EMPTY_LAYOUT_TAG"
    }

    init {
        inflate(context, R.layout.lv_layout, this)
        emptyLayout = EmptyLayout(this)
        pullToRefresh = PullToRefresh(this)
        lazyLoading = LazyLoading(this)
        bounceOverscroll = BounceOverscroll(this)
        readAttributes(context, attrs)
        initView()
    }

    @Override
    public override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.adapterState = adapter?.saveAdapterState()
        myState.pullToRefreshState = pullToRefresh.saveState()
        myState.lazyLoadingState = lazyLoading.saveState()
        myState.emptyLayoutState = emptyLayout.saveState()
        myState.bounceOverscrollState = bounceOverscroll.saveState()
        myState.showingLoadingIndicator = isShowingLoadingHeader
        myState.layoutManagerType = layoutManagerType!!.value
        if (recyclerView.layoutManager != null) {
            val layoutManagerBundle = Bundle()
            layoutManagerBundle.putParcelable(
                LAYOUT_MANAGER_STATE_KEY,
                recyclerView.layoutManager!!.onSaveInstanceState()
            )
            myState.layoutManagerState = layoutManagerBundle
        }
        return myState
    }

    @Override
    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        adapter?.restoreAdapterState(savedState.adapterState!!)
        pullToRefresh.restoreState(savedState.pullToRefreshState!!)
        lazyLoading.restoreState(savedState.lazyLoadingState!!)
        emptyLayout.restoreState(savedState.emptyLayoutState!!)
        bounceOverscroll.restoreState(savedState.bounceOverscrollState!!)
        layoutManagerType = LayoutManagerTypes.valueOf(savedState.layoutManagerType)
        showLoadingIndicator(savedState.showingLoadingIndicator)
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
    }

    protected open fun getLayoutManagerType(): LayoutManagerTypes {
        return UNDEFINED
    }

    fun setAdapter(adapter: EasyRecyclerAdapter<ItemType>) {
        dataAdapter = adapter
        val wrapperAdapter = HeaderViewRecyclerAdapter(recyclerView)
        wrapperAdapter.setDataAdapter(adapter)
        adapter.wrapperAdapter = wrapperAdapter
        recyclerView.adapter = wrapperAdapter
        emptyLayout.initialize(adapter)
    }

    fun setSwipeCallbacks(swipeCallbacks: TouchCallbacks<ItemType>) {
        touchHandler = EasyRecyclerViewTouchHandler(this, swipeCallbacks)
        touchHandler!!.setTouchHelperListeners(object : TouchHelperListeners() {
            private var hasBeenDisabled = false

            override fun onChildDraw() {
                super.onChildDraw()
                if (!hasBeenDisabled && pullToRefresh.enabled) {
                    hasBeenDisabled = true
                    pullToRefresh.setEnabled(false)
                }
            }

            override fun onClearView() {
                super.onClearView()
                if (hasBeenDisabled && !pullToRefresh.enabled) {
                    hasBeenDisabled = false
                    pullToRefresh.setEnabled(true)
                }
            }
        })
        val touchHelper = ItemTouchHelper(touchHandler!!)
        touchHelper.attachToRecyclerView(recyclerView)
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
     * Sets alternative layout to show in case of empty list.
     *
     * @param emptyLayoutId - Resource identifier for layout to show.
     * @param onLayoutReady - Callback to be executed when layout is ready.
     */
    @JvmOverloads
    fun setEmptyLayout(emptyLayoutId: Int, onLayoutReady: EasyRecyclerEmptyLayoutListener? = null) {
        emptyLayout.setEmptyLayout(emptyLayoutId, onLayoutReady)
    }

    /**
     * Sets alternative layout to show in case of empty list.
     *
     * @param layout    - Layout to show.
     * @param layoutListener - Callbacks to be executed on show/hide.
     */
    @JvmOverloads
    fun setEmptyLayout(layout: View?, layoutListener: EasyRecyclerEmptyLayoutListener? = null) {
        emptyLayout.setEmptyLayout(layout, layoutListener)
    }

    @JvmOverloads
    fun addHeaderView(view: View, viewListeners: ViewListeners? = null) {
        dataAdapter?.wrapperAdapter?.apply {
            if (containsHeaderView(view)) return@apply
            addHeaderView(view, viewListeners)
        }
    }

    fun removeHeaderView(view: View) {
        dataAdapter?.wrapperAdapter?.apply {
            if (!containsHeaderView(view)) return@apply
            removeHeaderView(view)
        }
    }

    @JvmOverloads
    fun addFooterView(view: View, viewListeners: ViewListeners? = null) {
        dataAdapter?.wrapperAdapter?.apply {
            if (containsFooterView(view)) return@apply
            addFooterView(view, viewListeners)
        }
    }

    fun removeFooterView(view: View) {
        dataAdapter?.wrapperAdapter?.apply {
            if (!containsFooterView(view)) return@apply
            removeFooterView(view)
        }
    }

    fun refreshData(showRefreshLayout: Boolean = false) {
        pullToRefresh.refresh(showRefreshLayout)
    }

    fun setPullToRefreshListener(listener: PullToRefreshListener<ItemType>?) {
        pullToRefresh.setOnRefreshListener(listener)
    }

    /**
     * Must be called when refresh listener has finished
     */
    fun onRefreshDataFinished() {
        pullToRefresh.finalizeRefresh()
    }

    fun loadMoreData(showLoadingFooter: Boolean = true) {
        if (isAnimating || isShowingRefreshLayout) return
        lazyLoading.load(showLoadingFooter)
    }

    fun setLazyLoadingListener(listener: LazyLoadingListener<ItemType>?) {
        lazyLoading.setOnLoadingListener(listener)
    }

    /**
     * Must be called when lazy loading listener has finished/
     */
    fun onLazyLoadingFinished(hasMoreData: Boolean) {
        lazyLoading.finalizeLoading(hasMoreData)
    }

    /**
     * Method enables or disables bounce effect on overscroll.
     * @param enabled whether is enabled or not.
     */
    fun setBounceOverscrollEnabled(enabled: Boolean) {
        bounceOverscroll.enabled = enabled
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
     * Notifies adapter for change occurred at position.
     *
     * @param position - position of the changed item.
     */
    fun itemChanged(position: Int?) {
        post {
            dataAdapter?.notifyItemChanged(position!!)
        }
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
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.EasyRecyclerView,
            R.attr.easyRecyclerViewStyle,
            R.style.EasyRecyclerViewDefaultStyle
        )
        try {
            val layoutManagerInt = a.getInt(EasyRecyclerView_erv_layout_manager, 1)
            val emptyLayoutId = a.getResourceId(R.styleable.EasyRecyclerView_erv_empty_layout_id, -1)
            val enableBounceOverscroll = a.getBoolean(EasyRecyclerView_erv_supports_overscroll_bounce, false)
            layoutManagerType = if (getLayoutManagerType() == UNDEFINED) {
                LayoutManagerTypes.valueOf(layoutManagerInt)
            } else {
                getLayoutManagerType()
            }
            emptyLayout.setEmptyLayout(emptyLayoutId)
            bounceOverscroll.enabled = enableBounceOverscroll
        } finally {
            a.recycle()
        }
    }

    private fun initView() {
        initLoadingIndicator()
        configureRecycler()
        configureLayoutManager()
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
        //recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
        //    ViewTreeObserver.OnGlobalLayoutListener {
        //    override fun onGlobalLayout() {
        //        if (renderedCallback != null) renderedCallback!!.execute()
        //        recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        //    }
        //})
    }

    private class SavedState : BaseSavedState {
        var adapterState: Bundle? = null
        var layoutManagerState: Bundle? = null
        var pullToRefreshState: Bundle? = null
        var lazyLoadingState: Bundle? = null
        var emptyLayoutState: Bundle? = null
        var bounceOverscrollState: Bundle? = null
        var showingLoadingIndicator = false
        var layoutManagerType = 0

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            ParcelUtils.apply {
                adapterState = readParcelable(parcel, Bundle::class.java)
                layoutManagerState = readParcelable(parcel, Bundle::class.java)
                pullToRefreshState = readParcelable(parcel, Bundle::class.java)
                lazyLoadingState = readParcelable(parcel, Bundle::class.java)
                emptyLayoutState = readParcelable(parcel, Bundle::class.java)
                bounceOverscrollState = readParcelable(parcel, Bundle::class.java)
                showingLoadingIndicator = readBoolean(parcel)!!
                layoutManagerType = readInt(parcel)!!
            }
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            ParcelUtils.apply {
                writeParcelable(out, adapterState)
                writeParcelable(out, layoutManagerState)
                writeParcelable(out, pullToRefreshState)
                writeParcelable(out, lazyLoadingState)
                writeParcelable(out, emptyLayoutState)
                writeParcelable(out, bounceOverscrollState)
                writeBoolean(out, showingLoadingIndicator)
                writeInt(out, layoutManagerType)
            }
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

    fun interface AdapterCreator<ItemType : EasyAdapterDataModel> {
        fun create(): EasyRecyclerAdapter<ItemType>
    }

    fun interface EasyRecyclerItemsReadyListener {
        fun execute()
    }
}