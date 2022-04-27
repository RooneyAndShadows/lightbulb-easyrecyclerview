package com.github.rooneyandshadows.lightbulb.easyrecyclerview;

import android.view.View;

import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterDataModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyRecyclerAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

final class HorizontalLinearLayoutManager<IType extends EasyAdapterDataModel, AType extends EasyRecyclerAdapter<IType>> extends LinearLayoutManager {
    private final EasyRecyclerView<IType, AType> easyRecyclerView;

    public HorizontalLinearLayoutManager(EasyRecyclerView<IType, AType> easyRecyclerView) {
        super(easyRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        this.easyRecyclerView = easyRecyclerView;
    }

    @Override
    public int scrollVerticallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrollRange = super.scrollVerticallyBy(dx, recycler, state);
        int overScroll = dx - scrollRange;
        if (!easyRecyclerView.supportsBounceOverscroll() || !easyRecyclerView.supportsPullToRefresh())
            return scrollRange;
        if (overScroll > 0) {
            //Bottom overscroll disable refreshLayout and enable bounce overscroll.
            if (!easyRecyclerView.isShowingLoadingHeader()) {
                easyRecyclerView.enableBounceOverscroll(true);
                easyRecyclerView.enablePullToRefreshLayout(false);
            }
        } else if (overScroll < 0) {
            //top overscroll enable refreshLayout and disable bounce overscroll
            if (!easyRecyclerView.isShowingLoadingHeader()) {
                easyRecyclerView.enableBounceOverscroll(false);
                easyRecyclerView.enablePullToRefreshLayout(true);
            }
        }
        if (Math.abs(dx) > 20)
            easyRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);
        if (!easyRecyclerView.isShowingLoadingHeader())
            handleLoadMore();
        return scrollRange;
    }

    private void handleLoadMore() {
        View lastView = getChildAt(getChildCount() - 1);
        if (lastView == null)
            return;
        int size = easyRecyclerView.getItems().size();
        int last = ((RecyclerView.LayoutParams) lastView.getLayoutParams()).getAbsoluteAdapterPosition() - easyRecyclerView.getAdapter().getHeadersCount();
        if (last == size - 1 && !easyRecyclerView.isShowingLoadingFooter()) {
            easyRecyclerView.loadMoreData();
        }
    }
}
