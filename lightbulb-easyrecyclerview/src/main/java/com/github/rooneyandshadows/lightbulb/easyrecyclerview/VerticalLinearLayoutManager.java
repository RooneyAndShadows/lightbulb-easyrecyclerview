package com.github.rooneyandshadows.lightbulb.easyrecyclerview;

import android.view.View;

import com.github.rooneyandshadows.lightbulb.recycleradapters.LightBulbAdapter;
import com.github.rooneyandshadows.lightbulb.recycleradapters.LightBulbAdapterDataModel;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

final class VerticalLinearLayoutManager<IType extends LightBulbAdapterDataModel, AType extends LightBulbAdapter<IType>> extends LinearLayoutManager {
    private final EasyRecyclerView<IType, AType> easyRecyclerView;

    public VerticalLinearLayoutManager(EasyRecyclerView<IType, AType> easyRecyclerView) {
        super(easyRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
        this.easyRecyclerView = easyRecyclerView;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrollRange = super.scrollVerticallyBy(dy, recycler, state);
        int overScroll = dy - scrollRange;
        if (easyRecyclerView.isShowingLoadingHeader())
            return scrollRange;
        if (overScroll > 0) {
            //Bottom overscroll disable refreshLayout and enable bounce overscroll.
            if (easyRecyclerView.isSupportsRefresh() && !easyRecyclerView.isShowingLoadingHeader()) {
                easyRecyclerView.enableBounceOverscroll(true);
                easyRecyclerView.enableSwipeToRefreshLayout(false);
            }
        } else if (overScroll < 0) {
            //top overscroll enable refreshLayout and disable bounce overscroll
            if (easyRecyclerView.isSupportsRefresh() && !easyRecyclerView.isShowingLoadingHeader()) {
                easyRecyclerView.enableBounceOverscroll(false);
                easyRecyclerView.enableSwipeToRefreshLayout(true);
            }
        }
        if (Math.abs(dy) > 20)
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
