package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers;

import android.view.View;

import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterDataModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyRecyclerAdapter;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import androidx.recyclerview.widget.RecyclerView;

public final class HorizontalFlowLayoutManager<IType extends EasyAdapterDataModel, AType extends EasyRecyclerAdapter<IType>> extends FlexboxLayoutManager {
    private final EasyRecyclerView<IType, AType> easyRecyclerView;
    private boolean scrollingHorizontally = false;
    private boolean scrollingVertically = false;

    public HorizontalFlowLayoutManager(EasyRecyclerView<IType, AType> easyRecyclerView) {
        super(easyRecyclerView.getContext(), FlexDirection.COLUMN);
        setJustifyContent(JustifyContent.FLEX_START);
        this.easyRecyclerView = easyRecyclerView;
    }

    @Override
    public boolean canScrollVertically() {
        return easyRecyclerView.supportsPullToRefresh() && !scrollingHorizontally;
    }

    @Override
    public boolean canScrollHorizontally() {
        return !scrollingVertically;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        scrollingVertically = true;
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state != 0)
            return;
        if (scrollingHorizontally)
            scrollingHorizontally = false;
        if (scrollingVertically)
            scrollingVertically = false;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrollRange = super.scrollHorizontallyBy(dx, recycler, state);
        scrollingHorizontally = true;
        int overScroll = dx - scrollRange;
        if (Math.abs(dx) > 20)
            easyRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);
        if (needToLoadMoreData(dx))
            handleLoadMore();
        return scrollRange;
    }

    private boolean needToLoadMoreData(int dx) {
        return (easyRecyclerView.hasMoreDataToLoad() &&
                !easyRecyclerView.isShowingLoadingHeader() &&
                !easyRecyclerView.isAnimating() &&
                !easyRecyclerView.isShowingRefreshLayout() &&
                !easyRecyclerView.isShowingLoadingFooter() &&
                dx > 0);
    }

    private void handleLoadMore() {
        if (!easyRecyclerView.supportsLazyLoading())
            return;
        View lastView = getChildAt(getChildCount() - 1);
        if (lastView == null)
            return;
        int size = easyRecyclerView.getItems().size();
        int last = ((RecyclerView.LayoutParams) lastView.getLayoutParams()).getAbsoluteAdapterPosition() - easyRecyclerView.getAdapter().getHeadersCount();
        if (last == size - 1)
            easyRecyclerView.loadMoreData();
    }
}
