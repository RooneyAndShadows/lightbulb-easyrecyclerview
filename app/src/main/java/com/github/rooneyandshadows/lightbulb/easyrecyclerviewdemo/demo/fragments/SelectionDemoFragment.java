package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments;

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
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.VerticalAndHorizontalSpaceItemDecoration;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity.MainActivity;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity.MenuConfigurations;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.SelectableAdapter;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.SimpleAdapter;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SelectionDemoFragment extends BaseFragment {
    private EasyRecyclerView<DemoModel, SelectableAdapter> recyclerView;

    public static SelectionDemoFragment getNewInstance() {
        return new SelectionDemoFragment();
    }

    @NonNull
    @Override
    protected BaseFragmentConfiguration configureFragment() {
        return new BaseFragmentConfiguration()
                .withLeftDrawer(false)
                .withActionBarConfiguration(new BaseFragmentConfiguration.ActionBarConfiguration(R.id.toolbar)
                        .withActionButtons(true)
                        .attachToDrawer(false)
                        .withSubTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.selectable_demo))
                        .withTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.app_name))
                );
    }

    @Override
    public View createView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demo_selectable, container, false);
    }

    @Override
    protected void viewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {
        super.viewCreated(fragmentView, savedInstanceState);
        setupRecycler(savedInstanceState);
    }

    @Override
    protected void selectViews() {
        super.selectViews();
        recyclerView = getView().findViewById(R.id.recycler_view);
    }

    private void setupRecycler(Bundle savedState) {
        recyclerView.setAdapter(new SelectableAdapter());
        recyclerView.addHeaderView(getLayoutInflater().inflate(R.layout.demo_header_item_selectable_layout, null));
        recyclerView.addItemDecoration(new VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(15)));
        if (savedState == null)
            recyclerView.getAdapter().setCollection(generateInitialData());
    }

    private List<DemoModel> generateInitialData() {
        List<DemoModel> models = new ArrayList<>();
        for (int i = 1; i <= 20; i++)
            models.add(new DemoModel("Demo title " + i, "Demo subtitle " + i));
        return models;
    }
}
