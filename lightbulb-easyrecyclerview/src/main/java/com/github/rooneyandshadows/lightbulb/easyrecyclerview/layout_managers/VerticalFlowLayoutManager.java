package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers;


import android.view.View;

import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView;
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import androidx.recyclerview.widget.RecyclerView;

public final class VerticalFlowLayoutManager<IType extends EasyAdapterDataModel, AType extends EasyRecyclerAdapter<IType>> extends FlexboxLayoutManager {
    private final EasyRecyclerView<IType, AType> easyRecyclerView;

    public VerticalFlowLayoutManager(EasyRecyclerView<IType, AType> easyRecyclerView) {
        super(easyRecyclerView.getContext(), FlexDirection.ROW);
        setJustifyContent(JustifyContent.FLEX_START);
        this.easyRecyclerView = easyRecyclerView;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrollRange = super.scrollVerticallyBy(dy, recycler, state);
        int overScroll = dy - scrollRange;
        if (Math.abs(dy) > 20)
            easyRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);
        if (needToLoadMoreData(dy))
            handleLoadMore();
        return scrollRange;
    }

    private boolean needToLoadMoreData(int dy) {
        return (easyRecyclerView.hasMoreDataToLoad() &&
                !easyRecyclerView.isShowingLoadingHeader() &&
                !easyRecyclerView.isAnimating() &&
                !easyRecyclerView.isShowingRefreshLayout() &&
                !easyRecyclerView.isShowingLoadingFooter() &&
                dy > 0);
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
