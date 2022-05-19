package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers;


import android.view.View;

import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterDataModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyRecyclerAdapter;
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
        if (easyRecyclerView.isShowingLoadingHeader())
            return scrollRange;
        if (Math.abs(dy) > 20)
            easyRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);
        if (!easyRecyclerView.isShowingRefreshLayout() && !easyRecyclerView.isShowingLoadingHeader() && dy > 0)
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
