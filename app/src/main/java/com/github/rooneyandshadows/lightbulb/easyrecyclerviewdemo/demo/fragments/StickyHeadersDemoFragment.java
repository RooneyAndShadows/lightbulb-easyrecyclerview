package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity;
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.drawable.ShowMenuDrawable;
import com.github.rooneyandshadows.lightbulb.application.fragment.BaseFragment;
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.BaseFragmentConfiguration;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.StickyHeaderItemDecoration;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.VerticalAndHorizontalSpaceItemDecoration;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity.MainActivity;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity.MenuConfigurations;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.SimpleAdapter;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.StickyAdapter;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.StickyDemoModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StickyHeadersDemoFragment extends BaseFragment {
    private EasyRecyclerView<StickyDemoModel, StickyAdapter> recyclerView;

    public static StickyHeadersDemoFragment getNewInstance() {
        return new StickyHeadersDemoFragment();
    }

    @NonNull
    @Override
    protected BaseFragmentConfiguration configureFragment() {
        return new BaseFragmentConfiguration()
                .withLeftDrawer(true)
                .withActionBarConfiguration(new BaseFragmentConfiguration.ActionBarConfiguration(R.id.toolbar)
                        .withActionButtons(true)
                        .attachToDrawer(true)
                        .withSubTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.sticky_headers_demo))
                        .withTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.app_name))
                );
    }

    @Override
    public View createView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demo_sticky_headers, container, false);
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
        setupDrawerButton();
        setupRecycler(savedInstanceState);
    }

    @Override
    protected void selectViews() {
        super.selectViews();
        recyclerView = getView().findViewById(R.id.recycler_view);
    }

    private void setupDrawerButton() {
        ShowMenuDrawable actionBarDrawable = new ShowMenuDrawable(getContextActivity());
        actionBarDrawable.setEnabled(false);
        actionBarDrawable.setBackgroundColor(ResourceUtils.getColorByAttribute(getContextActivity(), R.attr.colorError));
        getActionBarManager().setHomeIcon(actionBarDrawable);
    }

    private void setupRecycler(Bundle savedState) {
        recyclerView.setAdapter(new StickyAdapter());
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

    private List<StickyDemoModel> generateInitialData() {
        List<StickyDemoModel> models = new ArrayList<>();
        for (Integer i = 1; i < 10; i++) {
            models.add(new StickyDemoModel(true, "Header " + i, ""));
            for (Integer j = 1; j < 8; j++)
                models.add(new StickyDemoModel(false, String.format("Demo title %s.%s", i, j), String.format("Demo subtitle %s.%s", i, j)));
        }
        return models;
    }
}
