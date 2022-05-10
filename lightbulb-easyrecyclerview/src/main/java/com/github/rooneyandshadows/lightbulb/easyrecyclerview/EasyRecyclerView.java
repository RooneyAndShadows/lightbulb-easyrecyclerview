package com.github.rooneyandshadows.lightbulb.easyrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.factor.bouncy.BouncyRecyclerView;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.EasyRecyclerViewTouchHandler;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.FlowLayoutManager;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.HorizontalLinearLayoutManager;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.VerticalLinearLayoutManager;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RecyclerRefreshLayout;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RefreshView;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterDataModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyRecyclerAdapter;
import com.github.rooneyandshadows.lightbulb.recycleradapters.HeaderViewRecyclerAdapter;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.xiaofeng.flowlayoutmanager.Alignment;

import java.util.List;

import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static com.github.rooneyandshadows.lightbulb.easyrecyclerview.swiperefresh.RecyclerRefreshLayout.RefreshStyle.*;


@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class EasyRecyclerView<IType extends EasyAdapterDataModel, AType extends EasyRecyclerAdapter<IType>> extends RelativeLayout {
    private final int showRefreshManualDelay = 300;
    private final int showLoadingManualDelay = 300;
    private final float bouncyFlingAnimationSize = 0.1f;
    private final float bouncyOverscrollAnimationSize = 0.1f;
    private final String LAYOUT_MANAGER_STATE_TAG = "LAYOUT_MANAGER_STATE_TAG";
    private final String EMPTY_LAYOUT_VIEW_TAG = "EMPTY_LAYOUT_TAG";
    private boolean supportsPullToRefresh = false;
    private boolean supportsLazyLoading = false;
    private boolean hasMoreDataToLoad = true;
    private boolean supportsBounceOverscroll = false;
    private boolean showingEmptyLayout = false;
    private boolean showingLoadingFooter = false;
    private boolean showingLoadingHeader = false;
    private boolean showingRefreshLayout = false;
    private boolean pullToRefreshLayoutEnabled = false;
    private boolean overscrollBounceEnabled = false;
    private boolean pullToRefreshEnabled = false;
    private AType dataAdapter;
    private View emptyLayoutView;
    private View loadingFooterView;
    private Integer emptyLayoutId;
    private LayoutManagerTypes layoutManagerType;
    private LinearProgressIndicator loadingIndicator;
    private HeaderViewRecyclerAdapter wrapperAdapter;
    private BouncyRecyclerView recyclerView;
    private RelativeLayout recyclerEmptyLayoutContainer;
    private RecyclerRefreshLayout refreshLayout;
    private LayoutAnimationController animationController;
    private LoadMoreCallback<IType, AType> loadMoreCallback;
    private RefreshCallback<IType, AType> refreshCallback;
    private EasyRecyclerViewTouchHandler<IType, AType> swipeToDeleteCallbacks;
    private EasyRecyclerItemsReadyListener renderedCallback = null;
    private EasyRecyclerEmptyLayoutListener emptyLayoutListeners = null;
    private final Runnable showRefreshLayoutDelayedRunnable = () -> refreshLayout.setRefreshing(true);
    private final Runnable showLoadingDelayedRunnable = () -> {
        if (supportsPullToRefresh)
            if (showingLoadingHeader) {
                enablePullToRefreshLayout(false);
                loadingIndicator.setVisibility(VISIBLE);
            } else {
                enablePullToRefreshLayout(true);
                loadingIndicator.setVisibility(GONE);
            }
    };

    public EasyRecyclerView(Context context) {
        this(context, null);
    }

    public EasyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttributes(context, attrs);
        initView();
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        super.setNestedScrollingEnabled(enabled);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState myState = new SavedState(superState);
        if (dataAdapter != null)
            myState.adapterState = getAdapter().saveAdapterState();
        myState.hasMoreDataToLoad = hasMoreDataToLoad;
        myState.supportsLoadMore = supportsLazyLoading;
        myState.supportsPullToRefresh = supportsPullToRefresh;
        myState.supportsBounceOverscroll = supportsBounceOverscroll;
        myState.overscrollBounceEnabled = overscrollBounceEnabled;
        myState.pullToRefreshEnabled = pullToRefreshEnabled;
        myState.swipeToRefreshLayoutEnabled = pullToRefreshLayoutEnabled;
        myState.showingRefreshLayout = showingRefreshLayout;
        myState.showingLoadingFooterLayout = showingLoadingFooter;
        myState.showingLoadingIndicator = showingLoadingHeader;
        myState.emptyLayoutShowing = showingEmptyLayout;
        myState.layoutManagerType = layoutManagerType.value;
        if (emptyLayoutId != null)
            myState.emptyLayoutId = emptyLayoutId;
        if (recyclerView != null && recyclerView.getLayoutManager() != null) {
            Bundle layoutManagerBundle = new Bundle();
            layoutManagerBundle.putParcelable(LAYOUT_MANAGER_STATE_TAG, recyclerView.getLayoutManager().onSaveInstanceState());
            myState.layoutManagerState = layoutManagerBundle;
        }
        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (dataAdapter != null)
            getAdapter().restoreAdapterState(savedState.adapterState);
        hasMoreDataToLoad = savedState.hasMoreDataToLoad;
        supportsLazyLoading = savedState.supportsLoadMore;
        supportsPullToRefresh = savedState.supportsPullToRefresh;
        supportsBounceOverscroll = savedState.supportsBounceOverscroll;
        overscrollBounceEnabled = savedState.overscrollBounceEnabled;
        pullToRefreshEnabled = savedState.pullToRefreshEnabled;
        layoutManagerType = LayoutManagerTypes.valueOf(savedState.layoutManagerType);
        pullToRefreshLayoutEnabled = savedState.swipeToRefreshLayoutEnabled;
        emptyLayoutId = savedState.emptyLayoutId;
        enableBounceOverscroll(overscrollBounceEnabled);
        enablePullToRefreshLayout(pullToRefreshLayoutEnabled);
        showLoadingIndicator(savedState.showingLoadingIndicator);
        showRefreshLayout(savedState.showingRefreshLayout);
        showLoadingFooter(savedState.showingLoadingFooterLayout);
        setEmptyLayoutVisibility(savedState.emptyLayoutShowing);
        configureLayoutManager();
        if (savedState.layoutManagerState != null && recyclerView != null && recyclerView.getLayoutManager() != null) {
            Parcelable layState = savedState.layoutManagerState.getParcelable(LAYOUT_MANAGER_STATE_TAG);
            recyclerView.getLayoutManager().onRestoreInstanceState(layState);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (swipeToDeleteCallbacks != null)
            swipeToDeleteCallbacks.cancelPendingAction();
    }

    public void enableBounceOverscroll(boolean enabled) {
        overscrollBounceEnabled = enabled;
        recyclerView.setFlingAnimationSize(enabled ? bouncyFlingAnimationSize : 0f);
        recyclerView.setOverscrollAnimationSize(enabled ? bouncyOverscrollAnimationSize : 0f);
        recyclerView.setDampingRatio(enabled ? SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY : SpringForce.DAMPING_RATIO_NO_BOUNCY);
    }

    public void enablePullToRefreshLayout(boolean enabled) {
        pullToRefreshEnabled = enabled;
        pullToRefreshLayoutEnabled = enabled;
        refreshLayout.setEnabled(enabled);
    }

    protected LayoutManagerTypes getLayoutManagerType() {
        return LayoutManagerTypes.UNDEFINED;
    }

    /**
     * Sets recyclerview data adapter.
     *
     * @param adapter - recyclerview adapter.
     */
    public void setAdapter(AType adapter) {
        if (adapter != null) {
            wrapperAdapter = new HeaderViewRecyclerAdapter();
            dataAdapter = adapter;
            dataAdapter.setWrapperAdapter(wrapperAdapter);
            dataAdapter.addOnCollectionChangedListener(() -> setEmptyLayoutVisibility(!dataAdapter.hasItems()));
            wrapperAdapter.setDataAdapter(dataAdapter);
            recyclerView.setAdapter(wrapperAdapter);
        } else {
            dataAdapter = null;
            wrapperAdapter = null;
            recyclerView.setAdapter(null);
        }
    }

    /**
     * Sets recyclerview data adapter and item swipe callbacks.
     *
     * @param adapter        - recyclerview adapter.
     * @param swipeCallbacks - swipe callbacks.
     */
    public void setAdapter(AType adapter, EasyRecyclerViewTouchHandler.TouchCallbacks<IType> swipeCallbacks) {
        setAdapter(adapter);
        if (swipeCallbacks != null) {
            swipeToDeleteCallbacks = new EasyRecyclerViewTouchHandler<>(this, swipeCallbacks);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallbacks);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }

    /**
     * Indicates whether swipe to refresh is enabled.
     *
     * @param supportsPullToRefresh - Whether refresh is enabled.
     */
    public void setSupportsPullToRefresh(boolean supportsPullToRefresh) {
        this.supportsPullToRefresh = supportsPullToRefresh;
    }

    /**
     * Indicates whether lazy loading is enabled.
     *
     * @param supportsLazyLoading - Whether lazy loading is enabled.
     */
    public void setSupportsLazyLoading(boolean supportsLazyLoading) {
        this.supportsLazyLoading = supportsLazyLoading;
    }

    /**
     * Indicates whether there is more data to load.
     *
     * @param hasMoreDataToLoad - Whether there is more data available
     */
    public void setHasMoreDataToLoad(boolean hasMoreDataToLoad) {
        this.hasMoreDataToLoad = hasMoreDataToLoad;
    }

    /**
     * Sets alternative layout to show in case of empty list.
     *
     * @param emptyLayoutId - Resource identifier for layout to show.
     */
    public void setEmptyLayout(int emptyLayoutId) {
        setEmptyLayout(emptyLayoutId, null);
    }

    /**
     * Sets alternative layout to show in case of empty list.
     *
     * @param emptyLayoutView - Layout to show.
     */
    public void setEmptyLayout(View emptyLayoutView) {
        setEmptyLayout(emptyLayoutView, null);
    }

    /**
     * Sets alternative layout to show in case of empty list.
     *
     * @param emptyLayoutId - Resource identifier for layout to show.
     * @param onLayoutReady - Callback to be executed when layout is ready.
     */
    public void setEmptyLayout(int emptyLayoutId, EasyRecyclerEmptyLayoutListener onLayoutReady) {
        this.emptyLayoutId = emptyLayoutId;
        View layout = LayoutInflater.from(getContext()).inflate(emptyLayoutId, null);
        setEmptyLayout(layout, onLayoutReady);
    }

    /**
     * Sets alternative layout to show in case of empty list.
     *
     * @param emptyLayout    - Layout to show.
     * @param layoutListener - Callbacks to be executed on show/hide.
     */
    public void setEmptyLayout(View emptyLayout, EasyRecyclerEmptyLayoutListener layoutListener) {
        if (emptyLayout == null) {
            emptyLayoutView = null;
            recyclerEmptyLayoutContainer.removeAllViews();
            return;
        }
        recyclerEmptyLayoutContainer.removeAllViews();
        emptyLayoutView = emptyLayout;
        this.emptyLayoutListeners = layoutListener;
        this.emptyLayoutView.setTag(EMPTY_LAYOUT_VIEW_TAG);
        boolean isListEmpty = getAdapter() == null || !getAdapter().hasItems();
        recyclerEmptyLayoutContainer.setVisibility(isListEmpty ? VISIBLE : GONE);
        recyclerView.setVisibility(isListEmpty ? GONE : VISIBLE);
        View layout = findViewWithTag(EMPTY_LAYOUT_VIEW_TAG);
        if (layout != null) removeView(layout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        if (this.emptyLayoutListeners != null)
            this.emptyLayoutListeners.onInflated(emptyLayoutView);
        this.recyclerEmptyLayoutContainer.addView(emptyLayoutView, params);
    }

    public void addHeaderView(View view) {
        if (!wrapperAdapter.containsHeaderView(view))
            wrapperAdapter.addHeaderView(view);
    }

    public void removeHeaderView(View view) {
        if (wrapperAdapter.containsHeaderView(view))
            wrapperAdapter.removeHeaderView(view);
    }

    public void addFooterView(View view) {
        if (!wrapperAdapter.containsFooterView(view))
            wrapperAdapter.addFooterView(view);
    }

    public void removeFooterView(View view) {
        if (wrapperAdapter.containsFooterView(view))
            wrapperAdapter.removeFooterView(view);
    }

    public void refreshData() {
        if (!supportsPullToRefresh || showingRefreshLayout)
            return;
        this.showingRefreshLayout = true;
        postDelayed(showRefreshLayoutDelayedRunnable, showRefreshManualDelay);
        if (refreshCallback != null)
            refreshCallback.refresh(EasyRecyclerView.this);
    }

    public void loadMoreData() {
        if (loadMoreCallback != null || hasMoreDataToLoad) {
            showLoadingFooter(true);
            loadMoreCallback.loadMore(this);
        }
    }

    /**
     * Shows or hides global loading indicator
     *
     * @param state - whether is loading or not
     */
    public void showLoadingIndicator(boolean state) {
        if (state == showingLoadingHeader)
            return;
        showingLoadingHeader = state;
        if (!showingLoadingHeader) {
            removeCallbacks(showLoadingDelayedRunnable);
            loadingIndicator.setVisibility(GONE);
        } else
            postDelayed(showLoadingDelayedRunnable, showLoadingManualDelay);
    }

    /**
     * Indicates whether lazy loading is activated.
     *
     * @param isLoading - Whether is loading.
     * @see EasyRecyclerView#setSupportsLazyLoading(boolean) (int)
     */
    public void showLoadingFooter(boolean isLoading) {
        if (!supportsLazyLoading)
            return;
        this.showingLoadingFooter = isLoading;
        recyclerView.post(() -> {
            if (isLoading) {
                if (!wrapperAdapter.containsFooterView(loadingFooterView))
                    wrapperAdapter.addFooterView(loadingFooterView);
            } else {
                if (wrapperAdapter.containsFooterView(loadingFooterView))
                    wrapperAdapter.removeFooterView(loadingFooterView);
            }
        });
    }

    /**
     * Indicates whether refresh is activated.
     *
     * @param isRefreshing - Whether is loading.
     * @see EasyRecyclerView#setSupportsPullToRefresh(boolean) (int)
     */
    public void showRefreshLayout(boolean isRefreshing) {
        if (!pullToRefreshEnabled || isRefreshing == isShowingRefreshLayout())
            return;
        this.showingRefreshLayout = isRefreshing;
        if (!showingRefreshLayout)
            this.removeCallbacks(showRefreshLayoutDelayedRunnable);
        refreshLayout.setRefreshing(isRefreshing);
    }

    /**
     * Sets the {@link LoadMoreCallback} to be called when lazy loading is triggered.
     * The callback will be called only if {@link EasyRecyclerView#supportsLazyLoading} is true.
     *
     * @param callback - The LoadMoreCallback  to be executed on lazy loading.
     * @see EasyRecyclerView#setSupportsLazyLoading(boolean) (int)
     */
    public void setLoadMoreCallback(LoadMoreCallback<IType, AType> callback) {
        if (!supportsLazyLoading)
            return;
        loadMoreCallback = callback;
    }

    /**
     * Sets the {@link RefreshCallback} to be called on swipe refresh.
     * The callback will be called only if {@link EasyRecyclerView#supportsPullToRefresh} is true.
     *
     * @param callback - The RefreshCallback to be executed on refresh.
     * @see EasyRecyclerView#setSupportsPullToRefresh(boolean) (int)
     */
    public void setRefreshCallback(RefreshCallback<IType, AType> callback) {
        if (!supportsPullToRefresh)
            return;
        this.refreshCallback = callback;
        refreshLayout.setOnRefreshListener(() -> {
            if (showingRefreshLayout)
                return;
            showingRefreshLayout = true;
            if (refreshCallback != null)
                refreshCallback.refresh(EasyRecyclerView.this);
        });
    }

    /**
     * Sets the {@link EasyRecyclerItemsReadyListener} to be executed on view ready.
     *
     * @param renderedCallback - The EasyRecyclerItemsReadyCallback to be executed on view ready.
     */
    public void setRenderedCallback(EasyRecyclerItemsReadyListener renderedCallback) {
        this.renderedCallback = renderedCallback;
    }

    /**
     * Sets the {@link RecyclerView.ItemAnimator} to the recyclerview.
     *
     * @param RecyclerView.ItemAnimator - Animator to set.
     */
    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        recyclerView.setItemAnimator(animator);
    }

    /**
     * @return recyclerview adapter items.
     */
    public List<IType> getItems() {
        return dataAdapter.getItems();
    }

    /**
     * @return recyclerview adapter.
     */
    public AType getAdapter() {
        return dataAdapter;
    }

    /**
     * @return true if adapter contains at least on item.
     */
    public boolean hasItems() {
        if (dataAdapter == null)
            return false;
        return dataAdapter.hasItems();
    }

    /**
     * @return true if there is more data to be loaded trough lazy loading.
     */
    public boolean hasMoreDataToLoad() {
        return hasMoreDataToLoad;
    }

    /**
     * @return true if adapter has at least one item selected.
     */
    public boolean hasSelection() {
        return dataAdapter.hasSelection();
    }

    /**
     * @return alternative layout for empty list or null if not presented.
     */
    public View getEmptyLayoutView() {
        return emptyLayoutView;
    }

    /**
     * @return Layout manager for the view
     */
    public RecyclerView.LayoutManager getLayoutManager() {
        return recyclerView.getLayoutManager();
    }

    /**
     * @return Count of added item decorations to the recycler view.
     */
    public int getItemDecorationCount() {
        return recyclerView.getItemDecorationCount();
    }

    /**
     * @return whether component supports refresh
     */
    public boolean supportsPullToRefresh() {
        return supportsPullToRefresh;
    }

    /**
     * @return whether component supports lazy loading
     */
    public boolean supportsLazyLoading() {
        return supportsLazyLoading;
    }

    /**
     * @return whether component supports overscroll bounce
     */
    public boolean supportsBounceOverscroll() {
        return supportsBounceOverscroll;
    }

    /**
     * @return whether overscroll bounce is enabled
     */
    public boolean isOverscrollBounceEnabled() {
        return overscrollBounceEnabled;
    }

    /**
     * @return whether pull to refresh feature is enabled
     */
    public boolean isPullToRefreshEnabled() {
        return pullToRefreshEnabled;
    }

    /**
     * @return true if loading indicator is visible.
     */
    public boolean isShowingLoadingHeader() {
        return showingLoadingHeader;
    }

    /**
     * @return true if refresh is activated.
     */
    public boolean isShowingRefreshLayout() {
        return showingRefreshLayout;
    }

    /**
     * @return true if lazy loading is activated.
     */
    public boolean isShowingLoadingFooter() {
        return showingLoadingFooter;
    }

    /**
     * Notifies adapter for change occurred at position.
     *
     * @param position - position of the changed item.
     */
    public void itemChanged(Integer position) {
        post(() -> dataAdapter.notifyItemChanged(position));
    }

    /**
     * Clears items from the adapter.
     */
    public void clearItems() {
        dataAdapter.clearCollection();
    }

    /**
     * Executed any pending swipe operation for item.
     *
     * @see EasyRecyclerViewTouchHandler#executePendingAction()
     */
    public void executePendingAction() {
        if (swipeToDeleteCallbacks != null)
            swipeToDeleteCallbacks.executePendingAction();
    }

    /**
     * Cancels any pending swipe operation for item.
     *
     * @see EasyRecyclerViewTouchHandler#executePendingAction()
     */
    public void cancelPendingAction() {
        if (swipeToDeleteCallbacks != null)
            swipeToDeleteCallbacks.cancelPendingAction();
    }

    /**
     * Adds item decoration to the recycler view
     *
     * @param itemDecoration - Item decoration to add.
     */
    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.addItemDecoration(itemDecoration);
    }

    /**
     * Removes item decoration from the recycler view
     *
     * @param itemDecoration - Item decoration to remove.
     */
    public void removeItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.removeItemDecoration(itemDecoration);
    }

    /**
     * Removes item decoration from the recycler view
     *
     * @param itemDecorationIndex - index of the item decoration to remove.
     */
    public void removeItemDecorationAt(int itemDecorationIndex) {
        recyclerView.removeItemDecorationAt(itemDecorationIndex);
    }

    private void readAttributes(Context context, AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EasyRecyclerView, 0, 0);
        try {
            TextView textView = new TextView(getContext());
            int emptyLayoutId = attributes.getResourceId(R.styleable.EasyRecyclerView_ERV_EmptyLayoutId, -1);
            if (emptyLayoutId != -1)
                this.emptyLayoutId = emptyLayoutId;
            supportsPullToRefresh = attributes.getBoolean(R.styleable.EasyRecyclerView_ERV_SupportsPullToRefresh, false);
            supportsBounceOverscroll = attributes.getBoolean(R.styleable.EasyRecyclerView_ERV_SupportsOverscrollBounce, false);
            supportsLazyLoading = attributes.getBoolean(R.styleable.EasyRecyclerView_ERV_SupportsLoadMore, false);
            if (getLayoutManagerType() == null || getLayoutManagerType().equals(LayoutManagerTypes.UNDEFINED))
                layoutManagerType = LayoutManagerTypes.valueOf(attributes.getInt(R.styleable.EasyRecyclerView_ERV_LayoutManager, 1));
            else layoutManagerType = getLayoutManagerType();
        } finally {
            attributes.recycle();
        }
    }

    private void initView() {
        inflate(getContext(), R.layout.lv_layout, this);
        loadingFooterView = View.inflate(getContext(), R.layout.lv_loading_footer, null);
        initLoadingIndicator();
        configureRecycler();
        configureLayoutManager();
        configureRefreshLayout();
        configureEmptyLayout();
        setNestedScrollingEnabled(isNestedScrollingEnabled());
    }

    private void initLoadingIndicator() {
        loadingIndicator = findViewById(R.id.loadingIndicator);
        loadingIndicator.setVisibility(showingLoadingHeader ? VISIBLE : GONE);
    }

    private void configureLayoutManager() {
        RecyclerView.LayoutManager manager = null;
        switch (layoutManagerType) {
            case LAYOUT_LINEAR_VERTICAL:
            case UNDEFINED:
                manager = new VerticalLinearLayoutManager<>(this);
                break;
            case LAYOUT_LINEAR_HORIZONTAL:
                manager = new HorizontalLinearLayoutManager<>(this);
                break;
            case LAYOUT_FLOW:
                manager = new FlowLayoutManager();
                ((FlowLayoutManager) manager).setAlignment(Alignment.LEFT);
                recyclerView.setLayoutManager(manager);
                break;
        }
        recyclerView.setLayoutManager(manager);
    }

    private void configureRecycler() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setStiffness(SpringForce.STIFFNESS_MEDIUM);
        recyclerView.clearOnScrollListeners();
        //animationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_fall_down);
        //recyclerView.setLayoutAnimation(animationController);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (renderedCallback != null) {
                    renderedCallback.execute();
                }
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        enableBounceOverscroll(supportsBounceOverscroll);
    }

    private void configureRefreshLayout() {
        refreshLayout = findViewById(R.id.refreshLayout);
        int indicatorSize = ResourceUtils.getDimenPxById(getContext(), R.dimen.erv_header_refresh_indicator_size);
        int refreshBackgroundColor = ResourceUtils.getColorByAttribute(getContext(), android.R.attr.colorBackground);
        int refreshStrokeColor = ResourceUtils.getColorByAttribute(getContext(), android.R.attr.colorPrimary);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, indicatorSize);
        RefreshView refreshView = new RefreshView(getContext());
        refreshView.setBackgroundColor(refreshBackgroundColor);
        refreshLayout.setRefreshView(refreshView, layoutParams);
        refreshLayout.setRefreshStyle(NORMAL);
        enablePullToRefreshLayout(supportsPullToRefresh);
    }

    private void configureEmptyLayout() {
        recyclerEmptyLayoutContainer = findViewById(R.id.recyclerEmptyLayoutContainer);
        if (emptyLayoutId != null)
            setEmptyLayout(emptyLayoutId);
    }

    private void setEmptyLayoutVisibility(boolean visibility) {
        if (emptyLayoutView == null)
            return;
        showingEmptyLayout = visibility;
        if (visibility) {
            recyclerEmptyLayoutContainer.setVisibility(VISIBLE);
            recyclerView.setVisibility(GONE);
            if (emptyLayoutListeners != null)
                emptyLayoutListeners.onShow(emptyLayoutView);
        } else {
            if (emptyLayoutListeners != null)
                emptyLayoutListeners.onHide(emptyLayoutView);
            recyclerEmptyLayoutContainer.setVisibility(GONE);
            recyclerView.setVisibility(VISIBLE);
        }
    }

    private static class SavedState extends BaseSavedState {
        private Bundle adapterState;
        private boolean hasMoreDataToLoad;
        private boolean overscrollBounceEnabled;
        private boolean supportsBounceOverscroll;
        private boolean swipeToRefreshLayoutEnabled;
        private boolean supportsPullToRefresh;
        private boolean supportsLoadMore;
        private boolean pullToRefreshEnabled;
        private boolean showingRefreshLayout;
        private boolean showingLoadingFooterLayout;
        private boolean showingLoadingIndicator;
        private boolean emptyLayoutShowing;
        private int layoutManagerType;
        private Bundle layoutManagerState;
        private Bundle swipeConfigState;
        private int emptyLayoutId;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            adapterState = in.readBundle(EasyRecyclerView.class.getClassLoader());
            hasMoreDataToLoad = in.readByte() != 0;
            overscrollBounceEnabled = in.readByte() != 0;
            swipeToRefreshLayoutEnabled = in.readByte() != 0;
            supportsPullToRefresh = in.readByte() != 0;
            pullToRefreshEnabled = in.readByte() != 0;
            supportsBounceOverscroll = in.readByte() != 0;
            supportsLoadMore = in.readByte() != 0;
            showingRefreshLayout = in.readByte() != 0;
            showingLoadingFooterLayout = in.readByte() != 0;
            showingLoadingIndicator = in.readByte() != 0;
            emptyLayoutShowing = in.readByte() != 0;
            layoutManagerType = in.readInt();
            layoutManagerState = in.readBundle(getClass().getClassLoader());
            swipeConfigState = in.readBundle(getClass().getClassLoader());
            emptyLayoutId = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBundle(adapterState);
            out.writeByte((byte) (hasMoreDataToLoad ? 1 : 0));
            out.writeByte((byte) (overscrollBounceEnabled ? 1 : 0));
            out.writeByte((byte) (swipeToRefreshLayoutEnabled ? 1 : 0));
            out.writeByte((byte) (supportsPullToRefresh ? 1 : 0));
            out.writeByte((byte) (pullToRefreshEnabled ? 1 : 0));
            out.writeByte((byte) (supportsBounceOverscroll ? 1 : 0));
            out.writeByte((byte) (supportsLoadMore ? 1 : 0));
            out.writeByte((byte) (showingRefreshLayout ? 1 : 0));
            out.writeByte((byte) (showingLoadingFooterLayout ? 1 : 0));
            out.writeByte((byte) (showingLoadingIndicator ? 1 : 0));
            out.writeByte((byte) (emptyLayoutShowing ? 1 : 0));
            out.writeInt(layoutManagerType);
            out.writeBundle(layoutManagerState);
            out.writeBundle(swipeConfigState);
            out.writeInt(emptyLayoutId);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public enum LayoutManagerTypes {
        LAYOUT_LINEAR_VERTICAL(1),
        LAYOUT_LINEAR_HORIZONTAL(2),
        LAYOUT_FLOW(3),
        UNDEFINED(4);

        private final int value;
        private static final SparseArray<LayoutManagerTypes> values = new SparseArray<>();

        LayoutManagerTypes(int value) {
            this.value = value;
        }

        static {
            for (LayoutManagerTypes type : LayoutManagerTypes.values()) {
                values.put(type.value, type);
            }
        }

        public static LayoutManagerTypes valueOf(int type) {
            return values.get(type);
        }

        public int getValue() {
            return value;
        }
    }

    public static class EasyRecyclerEmptyLayoutListener {
        public void onInflated(View view) {
        }

        public void onShow(View view) {
        }

        public void onHide(View view) {
        }
    }


    public interface EasyRecyclerItemsReadyListener {
        void execute();
    }

    public interface EasyRecyclerViewAdapterAction<IType extends EasyAdapterDataModel, AType extends EasyRecyclerAdapter<IType>> {
        void execute(EasyRecyclerAdapter<IType> adapter);
    }

    public interface RefreshCallback<IType extends EasyAdapterDataModel, AType extends EasyRecyclerAdapter<IType>> {
        void refresh(EasyRecyclerView<IType, AType> view);
    }

    public interface LoadMoreCallback<IType extends EasyAdapterDataModel, AType extends EasyRecyclerAdapter<IType>> {
        void loadMore(EasyRecyclerView<IType, AType> view);
    }
}