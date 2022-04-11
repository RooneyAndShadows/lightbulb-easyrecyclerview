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

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.factor.bouncy.BouncyRecyclerView;
import com.github.rooneyandshadows.java.commons.string.StringUtils;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.refresh_views.RefreshView;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterDataModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyRecyclerAdapter;
import com.github.rooneyandshadows.lightbulb.recycleradapters.HeaderViewRecyclerAdapter;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.xiaofeng.flowlayoutmanager.Alignment;

import java.util.List;

import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static com.dinuscxj.refresh.RecyclerRefreshLayout.*;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class EasyRecyclerView<IType extends EasyAdapterDataModel, AType extends EasyRecyclerAdapter<IType>> extends RelativeLayout {
    private final int showRefreshManualDelay = 300;
    private final int showLoadingManualDelay = 300;
    private final float bouncyFlingAnimationSize = 0.1f;
    private final float bouncyOverscrollAnimationSize = 0.1f;
    private final String LAYOUT_MANAGER_STATE_TAG = "LAYOUT_MANAGER_STATE_TAG";
    private final String EMPTY_LAYOUT_VIEW_TAG = "EMPTY_LAYOUT_TAG";
    private boolean supportsRefresh = false;
    private boolean supportsLazyLoading = false;
    private boolean showingEmptyLayout = false;
    private boolean showingLoadingFooter = false;
    private boolean showingLoadingHeader = false;
    private boolean showingRefreshLayout = false;
    private boolean swipeToRefreshLayoutEnabled = false;
    private boolean overscrollBounceEnabled = false;
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
    private EasyRecyclerViewSwipeHandler.SwipeConfiguration swipeConfiguration;
    private EasyRecyclerViewSwipeHandler<IType, AType> swipeToDeleteCallbacks;
    private EasyRecyclerItemsReadyListener renderedCallback = null;
    private EasyRecyclerEmptyLayoutListener emptyLayoutListeners = null;
    private final Runnable showRefreshLayoutDelayedRunnable = () -> refreshLayout.setRefreshing(true);
    private final Runnable showLoadingDelayedRunnable = () -> {
        if (showingLoadingHeader) {
            enableSwipeToRefreshLayout(false);
            loadingIndicator.setVisibility(VISIBLE);
        } else {
            enableSwipeToRefreshLayout(true);
            loadingIndicator.setVisibility(GONE);
        }
    };

    public EasyRecyclerView(Context context) {
        super(context);
        initView();
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
        myState.supportsLoadMore = supportsLazyLoading;
        myState.supportsRefresh = supportsRefresh;
        myState.swipeToRefreshLayoutEnabled = swipeToRefreshLayoutEnabled;
        myState.showingRefreshLayout = showingRefreshLayout;
        myState.showingLoadingFooterLayout = showingLoadingFooter;
        myState.showingLoadingIndicator = showingLoadingHeader;
        myState.emptyLayoutShowing = showingEmptyLayout;
        myState.layoutManagerType = layoutManagerType.value;
        myState.swipeConfigState = swipeConfiguration.saveConfigurationState();
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
        supportsLazyLoading = savedState.supportsLoadMore;
        supportsRefresh = savedState.supportsRefresh;
        layoutManagerType = LayoutManagerTypes.valueOf(savedState.layoutManagerType);
        swipeToRefreshLayoutEnabled = savedState.swipeToRefreshLayoutEnabled;
        emptyLayoutId = savedState.emptyLayoutId;
        enableBounceOverscroll(overscrollBounceEnabled);
        enableSwipeToRefreshLayout(swipeToRefreshLayoutEnabled);
        showLoadingIndicator(savedState.showingLoadingIndicator);
        showRefreshLayout(savedState.showingRefreshLayout);
        showLoadingFooter(savedState.showingLoadingFooterLayout);
        setEmptyLayoutVisibility(savedState.emptyLayoutShowing);
        configureLayoutManager();
        swipeConfiguration.restoreConfigurationState(savedState.swipeConfigState);
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

    void enableBounceOverscroll(boolean enabled) {
        boolean currentState = recyclerView.getFlingAnimationSize() == bouncyFlingAnimationSize &&
                recyclerView.getOverscrollAnimationSize() == bouncyOverscrollAnimationSize;
        if (currentState == enabled)
            return;
        overscrollBounceEnabled = enabled;
        recyclerView.setFlingAnimationSize(enabled ? bouncyFlingAnimationSize : 0f);
        recyclerView.setOverscrollAnimationSize(enabled ? bouncyOverscrollAnimationSize : 0f);
    }

    void enableSwipeToRefreshLayout(boolean enabled) {
        boolean currentState = refreshLayout.isEnabled();
        boolean newState = supportsRefresh && enabled;
        if (currentState == newState)
            return;
        swipeToRefreshLayoutEnabled = newState;
        refreshLayout.setEnabled(newState);
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
    public void setAdapter(AType adapter, EasyRecyclerViewSwipeHandler.SwipeCallbacks<IType> swipeCallbacks) {
        setAdapter(adapter);
        if (!swipeConfiguration.getEditMode().equals(EasyRecyclerViewSwipeHandler.Modes.NON_EDITABLE)) {
            swipeToDeleteCallbacks = new EasyRecyclerViewSwipeHandler<>(this, adapter, swipeConfiguration);
            swipeToDeleteCallbacks.setSwipeCallbacks(swipeCallbacks);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallbacks);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }

    /**
     * Indicates whether swipe to refresh is enabled.
     *
     * @param supportsRefresh - Whether refresh is enabled.
     */
    public void setSupportsRefresh(boolean supportsRefresh) {
        this.supportsRefresh = supportsRefresh;
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
        if (!supportsRefresh || showingRefreshLayout)
            return;
        this.showingRefreshLayout = true;
        postDelayed(showRefreshLayoutDelayedRunnable, showRefreshManualDelay);
        if (refreshCallback != null)
            refreshCallback.refresh(EasyRecyclerView.this);
    }

    public void loadMoreData() {
        if (loadMoreCallback != null)
            loadMoreCallback.loadMore(this);
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
        recyclerView.post(() -> {
            if (!supportsLazyLoading)
                return;
            this.showingLoadingFooter = isLoading;
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
     * @see EasyRecyclerView#setSupportsRefresh(boolean) (int)
     */
    public void showRefreshLayout(boolean isRefreshing) {
        if (!supportsRefresh || isRefreshing == isShowingRefreshLayout())
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
     * The callback will be called only if {@link EasyRecyclerView#supportsRefresh} is true.
     *
     * @param callback - The RefreshCallback to be executed on refresh.
     * @see EasyRecyclerView#setSupportsRefresh(boolean) (int)
     */
    public void setRefreshCallback(RefreshCallback<IType, AType> callback) {
        if (!supportsRefresh)
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
     * @return true if adapter has at least one item selected.
     */
    public boolean hasSelection() {
        return dataAdapter.hasSelection();
    }

    /**
     * @return original recycler view.
     */
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    /**
     * @return alternative layout for empty list or null if not presented.
     */
    public View getEmptyLayoutView() {
        return emptyLayoutView;
    }

    /**
     * @return whether component supports refresh
     */
    public boolean isSupportsRefresh() {
        return supportsRefresh;
    }

    /**
     * @return whether component supports lazy loading
     */
    public boolean isSupportsLazyLoading() {
        return supportsLazyLoading;
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
     * @see EasyRecyclerViewSwipeHandler#executePendingAction()
     */
    public void executePendingAction() {
        if (swipeToDeleteCallbacks != null)
            swipeToDeleteCallbacks.executePendingAction();
    }

    /**
     * Cancels any pending swipe operation for item.
     *
     * @see EasyRecyclerViewSwipeHandler#executePendingAction()
     */
    public void cancelPendingAction() {
        if (swipeToDeleteCallbacks != null)
            swipeToDeleteCallbacks.cancelPendingAction();
    }

    private void readAttributes(Context context, AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EasyRecyclerView, 0, 0);
        try {
            TextView textView = new TextView(getContext());
            int emptyLayoutId = attributes.getResourceId(R.styleable.EasyRecyclerView_ERV_EmptyLayoutId, -1);
            if (emptyLayoutId != -1)
                this.emptyLayoutId = emptyLayoutId;
            supportsRefresh = attributes.getBoolean(R.styleable.EasyRecyclerView_ERV_SupportsRefresh, false);
            supportsLazyLoading = attributes.getBoolean(R.styleable.EasyRecyclerView_ERV_SupportsLoadMore, false);
            if (getLayoutManagerType() == null || getLayoutManagerType().equals(LayoutManagerTypes.UNDEFINED))
                layoutManagerType = LayoutManagerTypes.valueOf(attributes.getInt(R.styleable.EasyRecyclerView_ERV_LayoutManager, 1));
            else layoutManagerType = getLayoutManagerType();
            //SWIPE ATTRIBUTES
            int editModeValue = attributes.getInteger(R.styleable.EasyRecyclerView_ERV_SwipeMode, 1);
            swipeConfiguration = new EasyRecyclerViewSwipeHandler.SwipeConfiguration();
            swipeConfiguration.setEditMode(EasyRecyclerViewSwipeHandler.Modes.valueOf(editModeValue));
            swipeConfiguration.setSwipeSnackBarUndoTextPhrase(StringUtils.getOrDefault(attributes.getString(R.styleable.EasyRecyclerView_ERV_SwipeUndoTextPhrase), ResourceUtils.getPhrase(context, R.string.lv_swipe_undo_default_text)));
            swipeConfiguration.setSwipeIconSize(attributes.getDimensionPixelSize(R.styleable.EasyRecyclerView_ERV_SwipeIconSize, ResourceUtils.getDimenPxById(context, R.dimen.lv_swipe_icon_size)));
            swipeConfiguration.setSwipeTextSize(attributes.getDimensionPixelSize(R.styleable.EasyRecyclerView_ERV_SwipeTextSize, ResourceUtils.getDimenPxById(context, R.dimen.lv_swipe_text_size)));
            swipeConfiguration.setSwipeAccentColor(attributes.getColor(R.styleable.EasyRecyclerView_ERV_SwipeTextAndIconColor, ResourceUtils.getColorById(context, R.color.view_list_swipe_text_color)));
            swipeConfiguration.setSwipePositiveBackgroundColor(attributes.getColor(R.styleable.EasyRecyclerView_ERV_SwipePositiveBackgroundColor, ResourceUtils.getColorById(context, R.color.view_list_swipe_positive_color)));
            swipeConfiguration.setSwipeNegativeBackgroundColor(attributes.getColor(R.styleable.EasyRecyclerView_ERV_SwipeNegativeBackgroundColor, ResourceUtils.getColorById(context, R.color.view_list_swipe_negative_color)));
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
        recyclerView.setFlingAnimationSize(bouncyFlingAnimationSize);
        recyclerView.setOverscrollAnimationSize(bouncyOverscrollAnimationSize);
        recyclerView.setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
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
    }

    private void configureRefreshLayout() {
        refreshLayout = findViewById(R.id.refreshLayout);
        int indicatorSize = ResourceUtils.getDimenPxById(getContext(), R.dimen.lv_header_refresh_indicator_size);
        int refreshBackgroundColor = ResourceUtils.getColorByAttribute(getContext(), android.R.attr.colorBackground);
        int refreshStrokeColor = ResourceUtils.getColorByAttribute(getContext(), android.R.attr.colorPrimary);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(indicatorSize, indicatorSize);
        RefreshView refreshView = new RefreshView(getContext());
        refreshView.setBackgroundColor(refreshBackgroundColor);
        refreshLayout.setRefreshView(refreshView, layoutParams);
        refreshLayout.setRefreshStyle(RefreshStyle.NORMAL);
        refreshLayout.setEnabled(supportsRefresh);
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
        private boolean overscrollBounceEnabled;
        private boolean swipeToRefreshLayoutEnabled;
        private boolean supportsRefresh;
        private boolean supportsLoadMore;
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
            overscrollBounceEnabled = in.readByte() != 0;
            swipeToRefreshLayoutEnabled = in.readByte() != 0;
            supportsRefresh = in.readByte() != 0;
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
            out.writeByte((byte) (overscrollBounceEnabled ? 1 : 0));
            out.writeByte((byte) (swipeToRefreshLayoutEnabled ? 1 : 0));
            out.writeByte((byte) (supportsRefresh ? 1 : 0));
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