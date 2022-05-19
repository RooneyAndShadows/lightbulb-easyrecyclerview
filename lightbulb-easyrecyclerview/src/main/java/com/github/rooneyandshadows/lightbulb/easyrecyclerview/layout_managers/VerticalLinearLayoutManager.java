package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers;

import android.view.View;

import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterDataModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyRecyclerAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public final class VerticalLinearLayoutManager<IType extends EasyAdapterDataModel, AType extends EasyRecyclerAdapter<IType>> extends LinearLayoutManager {
    private final EasyRecyclerView<IType, AType> easyRecyclerView;

    public VerticalLinearLayoutManager(EasyRecyclerView<IType, AType> easyRecyclerView) {
        super(easyRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
        this.easyRecyclerView = easyRecyclerView;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrollRange = super.scrollVerticallyBy(dy, recycler, state);
        int overScroll = dy - scrollRange;
        if (easyRecyclerView.isShowingLoadingHeader() || easyRecyclerView.isAnimating())
            return scrollRange;
        if (Math.abs(dy) > 20)
            easyRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);
        if (!easyRecyclerView.isShowingRefreshLayout() && !easyRecyclerView.isShowingLoadingFooter() && dy > 0) {
            handleLoadMore();
        }
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