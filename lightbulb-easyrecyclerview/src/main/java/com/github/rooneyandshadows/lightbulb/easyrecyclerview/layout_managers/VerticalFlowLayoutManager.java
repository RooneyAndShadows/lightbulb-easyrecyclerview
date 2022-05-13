package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers;

import android.content.Context;

import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterDataModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyRecyclerAdapter;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

public final class VerticalFlowLayoutManager<IType extends EasyAdapterDataModel, AType extends EasyRecyclerAdapter<IType>> extends FlexboxLayoutManager {
    private final EasyRecyclerView<IType, AType> easyRecyclerView;

    public VerticalFlowLayoutManager(EasyRecyclerView<IType, AType> easyRecyclerView) {
        super(easyRecyclerView.getContext(), FlexDirection.ROW);
        setJustifyContent(JustifyContent.FLEX_START);
        this.easyRecyclerView = easyRecyclerView;
    }
}
