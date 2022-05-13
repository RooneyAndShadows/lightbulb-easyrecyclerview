package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate;
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity;
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.drawable.ShowMenuDrawable;
import com.github.rooneyandshadows.lightbulb.application.fragment.BaseFragment;
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.BaseFragmentConfiguration;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.StickyHeaderItemDecoration;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity.MainActivity;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity.MenuConfigurations;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.StickyAdapterAdvanced;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.StickyAdvancedDemoModel;

import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StickyHeadersAdvancedDemoFragment extends BaseFragment {
    private EasyRecyclerView<StickyAdvancedDemoModel, StickyAdapterAdvanced> recyclerView;

    public static StickyHeadersAdvancedDemoFragment getNewInstance() {
        return new StickyHeadersAdvancedDemoFragment();
    }

    @NonNull
    @Override
    protected BaseFragmentConfiguration configureFragment() {
        return new BaseFragmentConfiguration()
                .withLeftDrawer(false)
                .withActionBarConfiguration(new BaseFragmentConfiguration.ActionBarConfiguration(R.id.toolbar)
                        .withActionButtons(true)
                        .attachToDrawer(false)
                        .withSubTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.sticky_headers_advanced_demo))
                        .withTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.app_name))
                );
    }

    @Override
    public View createView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demo_sticky_headers_advanced, container, false);
    }

    @Override
    protected void viewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {
        super.viewCreated(fragmentView, savedInstanceState);
        if (getFragmentState() == FragmentStates.CREATED) {
            BaseActivity.updateMenuConfiguration(
                    getContextActivity(),
                    MainActivity.class,
                    MenuConfigurations::getConfiguration
            );
        }
        setupRecycler(savedInstanceState);
    }

    @Override
    protected void selectViews() {
        super.selectViews();
        recyclerView = getView().findViewById(R.id.recycler_view);
    }

    private void setupRecycler(Bundle savedState) {
        recyclerView.setAdapter(new StickyAdapterAdvanced());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContextActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new StickyHeaderItemDecoration(recyclerView.getAdapter()) {
            @Override
            public void onDrawOver(@NotNull Canvas c, @NotNull RecyclerView parent, RecyclerView.@NotNull State state) {
                int firstVisibleItemPosition = ((LinearLayoutManager) parent.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                if (firstVisibleItemPosition != 0)
                    super.onDrawOver(c, parent, state);
            }
        });
        if (savedState == null)
            recyclerView.getAdapter().setCollection(generateInitialData());
    }

    private List<StickyAdvancedDemoModel> generateInitialData() {
        List<StickyAdvancedDemoModel> models = new ArrayList<>();
        OffsetDateTime date = DateUtilsOffsetDate.nowLocal();
        for (int position = 1; position <= 60; position++) {
            boolean isHeader = isPositionHeader(position);
            models.add(new StickyAdvancedDemoModel(date, isHeader, String.format("Demo title %s", position), String.format("Demo subtitle %s", position)));
            if (isHeader)
                date = DateUtilsOffsetDate.addHours(date, 24);
        }

        return models;
    }

    private boolean isPositionHeader(int position) {
        int[] headerPositions = new int[]{1, 7, 12, 20, 25, 34, 40};
        return Arrays.stream(headerPositions).anyMatch(value -> position == value);
    }
}
