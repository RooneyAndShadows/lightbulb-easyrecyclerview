package com.github.rooneyandshadows.lightbulb.easyrecyclerview

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.ClassLoaderCreator
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.LayoutAnimationController
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.github.rooneyandshadows.lightbulb.commons.utils.BundleUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.R.styleable.EasyRecyclerView_erv_supports_overscroll_bounce
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.adapter.AdapterManager
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.base.EasyRecyclerItemDecoration
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
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.adapters.StaticViewsAdapter.ViewListeners
import com.google.android.material.progressindicator.LinearProgressIndicator


@Suppress("MemberVisibilityCanBePrivate", "unused", "LeakingThis", "UNCHECKED_CAST")
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
    private var adapterManager: AdapterManager<ItemType> = AdapterManager()
    private var pullToRefresh: PullToRefresh<ItemType>
    private var lazyLoading: LazyLoading<ItemType>
    private var emptyLayout: EmptyLayout<ItemType>
    private var bounceOverscroll: BounceOverscroll<ItemType>
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
        get() = loadingIndicator.isVisible
    val isShowingEmptyLayout: Boolean
        get() = emptyLayout.isShowing
    val isShowingRefreshLayout: Boolean
        get() = pullToRefresh.refreshing
    val layoutManager: LayoutManager?
        get() = recyclerView.layoutManager
    val itemDecorationCount: Int
        get() = recyclerView.itemDecorationCount
    open val adapter: EasyRecyclerAdapter<ItemType>?
        get() = adapterManager.dataAdapter

    companion object {
        private const val LAYOUT_MANAGER_STATE_KEY = "LAYOUT_MANAGER_STATE_KEY"
        private const val EMPTY_LAYOUT_TAG = "EMPTY_LAYOUT_TAG"
    }

    init {
        inflate(context, R.layout.lv_layout, this)
        emptyLayout = EmptyLayout(this).apply {
            register()
        }
        pullToRefresh = PullToRefresh(this).apply {
            register()
        }
        lazyLoading = LazyLoading(this).apply {
            register()
        }
        bounceOverscroll = BounceOverscroll(this).apply {
            register()
        }
        readAttributes(context, attrs)
        initView()
    }

    @Override
    public override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.adapterState = adapter?.onSaveInstanceState()
        myState.layoutManagerState = recyclerView.layoutManager?.onSaveInstanceState()
        myState.pullToRefreshState = pullToRefresh.saveState()
        myState.lazyLoadingState = lazyLoading.saveState()
        myState.emptyLayoutState = emptyLayout.saveState()
        myState.bounceOverscrollState = bounceOverscroll.saveState()
        myState.showingLoadingIndicator = isShowingLoadingHeader
        return myState
    }

    @Override
    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        adapter?.onRestoreInstanceState(savedState.adapterState!!)
        recyclerView.layoutManager?.onRestoreInstanceState(savedState.layoutManagerState)
        pullToRefresh.restoreState(savedState.pullToRefreshState!!)
        lazyLoading.restoreState(savedState.lazyLoadingState!!)
        emptyLayout.restoreState(savedState.emptyLayoutState!!)
        bounceOverscroll.restoreState(savedState.bounceOverscrollState!!)
        showLoadingIndicator(savedState.showingLoadingIndicator)
    }

    @Override
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        touchHandler?.apply { cancelPendingAction() }
        emptyLayout.unregister()
        pullToRefresh.unregister()
        lazyLoading.unregister()
        bounceOverscroll.unregister()
    }

    fun setAdapter(adapter: EasyRecyclerAdapter<ItemType>) {
        adapterManager.setDataAdapter(adapter)
        recyclerView.adapter = adapterManager.rootAdapter
        emptyLayout.adapterChanged(adapterManager.rootAdapter)
    }

    fun setLayoutManager(layoutManager: LayoutManager) {
        recyclerView.layoutManager = layoutManager
    }

    fun setRecycledViewPool(pool: RecycledViewPool) {
        recyclerView.setRecycledViewPool(pool)
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
    fun setEmptyLayout(layout: View?, layoutListener: EasyRecyclerEmptyLayoutListener? = null) {
        emptyLayout.setEmptyLayout(layout, layoutListener)
    }

    fun addHeaderView(
        id: String,
        viewFactory: (ViewGroup) -> View,
        viewListeners: ViewListeners? = null
    ) {
        val adapter = adapterManager.headersAdapter
        if (adapter.containsView(id)) return
        adapter.addView(
            id = id,
            viewFactory = viewFactory,
            listeners = viewListeners
        )
    }

    fun removeHeaderView(id: String) {
        val adapter = adapterManager.headersAdapter
        if (!adapter.containsView(id)) return
        adapter.removeViewById(id)
    }

    fun addFooterView(
        id: String,
        viewFactory: (ViewGroup) -> View,
        viewBinder: ((View, Int) -> Unit)? = null,
        viewListeners: ViewListeners? = null
    ) {
        val adapter = adapterManager.footersAdapter
        if (adapter.containsView(id)) return
        adapter.addView(
            id = id,
            viewFactory = viewFactory,
            viewBinder = viewBinder,
            listeners = viewListeners
        )
    }

    fun removeFooterView(id: String) {
        val adapter = adapterManager.footersAdapter
        if (!adapter.containsView(id)) return
        adapter.removeViewById(id)
    }

    fun containsHeaderView(id: String): Boolean {
        return adapterManager.headersAdapter.containsView(id)
    }

    fun containsFooterView(id: String): Boolean {
        return adapterManager.footersAdapter.containsView(id)
    }

    fun updateFooterViewById(id: String) {
        val adapter = adapterManager.footersAdapter
        if (adapter.containsView(id)) {
            adapter.updateViewById(id)
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
            val emptyLayoutId =
                a.getResourceId(R.styleable.EasyRecyclerView_erv_empty_layout_id, -1)
            val enableBounceOverscroll =
                a.getBoolean(EasyRecyclerView_erv_supports_overscroll_bounce, false)
            emptyLayout.setEmptyLayout(emptyLayoutId)
            bounceOverscroll.enabled = enableBounceOverscroll
        } finally {
            a.recycle()
        }
    }

    private fun initView() {
        initLoadingIndicator()
        configureRecycler()
        isNestedScrollingEnabled = isNestedScrollingEnabled
    }

    private fun initLoadingIndicator() {
        loadingIndicator.visibility = if (isShowingLoadingHeader) VISIBLE else GONE
    }

    private fun configureRecycler() {
        recyclerView.clearOnScrollListeners()
    }

    private class SavedState : BaseSavedState {
        var adapterState: Bundle? = null
        var layoutManagerState: Parcelable? = null
        var pullToRefreshState: Bundle? = null
        var lazyLoadingState: Bundle? = null
        var emptyLayoutState: Bundle? = null
        var bounceOverscrollState: Bundle? = null
        var showingLoadingIndicator = false

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : this(parcel, null)

        constructor(source: Parcel, loader: ClassLoader?) : super(source, loader) {
            ParcelUtils.apply {
                adapterState = readParcelable(source, Bundle::class.java)
                @Suppress("DEPRECATION")
                layoutManagerState = readParcelable(source, loader)
                pullToRefreshState = readParcelable(source, Bundle::class.java)
                lazyLoadingState = readParcelable(source, Bundle::class.java)
                emptyLayoutState = readParcelable(source, Bundle::class.java)
                bounceOverscrollState = readParcelable(source, Bundle::class.java)
                showingLoadingIndicator = readBoolean(source)!!
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
            }
        }

        companion object CREATOR : ClassLoaderCreator<SavedState> {
            override fun createFromParcel(
                parcel: Parcel,
                loader: ClassLoader?
            ): SavedState {
                return SavedState(parcel, loader)
            }

            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    fun interface AdapterCreator<ItemType : EasyAdapterDataModel> {
        fun create(): EasyRecyclerAdapter<ItemType>
    }

    fun interface EasyRecyclerItemsReadyListener {
        fun execute()
    }
}