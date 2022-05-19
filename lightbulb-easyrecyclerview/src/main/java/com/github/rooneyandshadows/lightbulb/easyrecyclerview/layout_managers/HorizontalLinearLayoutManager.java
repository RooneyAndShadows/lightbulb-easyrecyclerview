package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers;

import android.view.View;

import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterDataModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyRecyclerAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public final class HorizontalLinearLayoutManager<IType extends EasyAdapterDataModel, AType extends EasyRecyclerAdapter<IType>> extends LinearLayoutManager {
    private final EasyRecyclerView<IType, AType> easyRecyclerView;
    private boolean scrollingHorizontally = false;
    private boolean scrollingVertically = false;

    public HorizontalLinearLayoutManager(EasyRecyclerView<IType, AType> easyRecyclerView) {
        super(easyRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
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
        if (!easyRecyclerView.isShowingRefreshLayout() && !easyRecyclerView.isShowingLoadingHeader() && dx > 0)
            handleLoadMore();
        return scrollRange;
    }

    private void handleLoadMore() {
        if (!easyRecyclerView.supportsLazyLoading())
            return;
        View lastView = getChildAt(getChildCount() - 1);
        if (lastView == null)
            return;
        int size = easyRecyclerView.getItems().size();
        int last = ((RecyclerView.LayoutParams) lastView.getLayoutParams()).getAbsoluteAdapterPosition() - easyRecyclerView.getAdapter().getHeadersCount();
        boolean needToLoadMoreData = !easyRecyclerView.isShowingLoadingFooter() && easyRecyclerView.hasMoreDataToLoad() && (last == size - 1);
        if (needToLoadMoreData)
            easyRecyclerView.loadMoreData();
    }
}