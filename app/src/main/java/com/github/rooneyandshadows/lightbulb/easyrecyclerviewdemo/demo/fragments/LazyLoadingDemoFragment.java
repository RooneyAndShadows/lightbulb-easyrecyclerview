package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rooneyandshadows.lightbulb.application.fragment.BaseFragment;
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.BaseFragmentConfiguration;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.VerticalAndHorizontalSpaceItemDecoration;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.SimpleAdapter;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LazyLoadingDemoFragment extends BaseFragment {
    private EasyRecyclerView<DemoModel, SimpleAdapter> recyclerView;

    public static LazyLoadingDemoFragment getNewInstance() {
        return new LazyLoadingDemoFragment();
    }

    @NonNull
    @Override
    protected BaseFragmentConfiguration configureFragment() {
        return new BaseFragmentConfiguration()
                .withLeftDrawer(false)
                .withActionBarConfiguration(new BaseFragmentConfiguration.ActionBarConfiguration(R.id.toolbar)
                        .withActionButtons(true)
                        .attachToDrawer(false)
                        .withSubTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.lazy_loading_demo))
                        .withTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.app_name))
                );
    }

    @Override
    public View createView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demo_lazy_loading, container, false);
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
        recyclerView.setAdapter(new SimpleAdapter());
        recyclerView.getRecyclerView().addItemDecoration(new VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(15)));
        recyclerView.setLoadMoreCallback(view -> recyclerView.postDelayed(() -> {
                    recyclerView.getAdapter().appendCollection(generateData(10, recyclerView.getAdapter().getItems().size()));
                    recyclerView.showLoadingFooter(false);
                }, 2000)
        );
        if (savedState == null)
            recyclerView.getAdapter().setCollection(generateData(10, 0));
    }

    private List<DemoModel> generateData(int count, int offset) {
        List<DemoModel> models = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            int number = i + offset;
            models.add(new DemoModel("Demo title " + number, "Demo subtitle " + number));
        }
        return models;
    }
}