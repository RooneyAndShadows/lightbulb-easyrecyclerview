package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity;
import com.github.rooneyandshadows.lightbulb.application.fragment.BaseFragment;
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.BaseFragmentConfiguration;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.VerticalAndHorizontalSpaceItemDecoration;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity.MainActivity;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity.MenuConfigurations;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.SimpleAdapter;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EmptylayoutDemoFragment extends BaseFragment {
    private EasyRecyclerView<DemoModel, SimpleAdapter> recyclerView;

    public static EmptylayoutDemoFragment getNewInstance() {
        return new EmptylayoutDemoFragment();
    }

    @NonNull
    @Override
    protected BaseFragmentConfiguration configureFragment() {
        return new BaseFragmentConfiguration()
                .withLeftDrawer(false)
                .withActionBarConfiguration(new BaseFragmentConfiguration.ActionBarConfiguration(R.id.toolbar)
                        .withActionButtons(true)
                        .attachToDrawer(false)
                        .withSubTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.empty_layout_demo))
                        .withTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.app_name))
                );
    }

    @Override
    public View createView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demo_empty_layout, container, false);
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
        setupRecycler();
    }

    @Override
    protected void viewStateRestored(@Nullable Bundle savedInstanceState) {
        super.viewStateRestored(savedInstanceState);
        View emptyLayout = generateEmptyLayout();
        recyclerView.setEmptyLayout(emptyLayout);
    }

    @Override
    protected void selectViews() {
        super.selectViews();
        recyclerView = getView().findViewById(R.id.recycler_view);
    }

    private void setupRecycler() {
        recyclerView.setAdapter(new SimpleAdapter());
        recyclerView.addItemDecoration(new VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(15)));
    }

    private View generateEmptyLayout() {
        View emptyLayout = getLayoutInflater().inflate(R.layout.demo_empty_layout, null);
        emptyLayout.findViewById(R.id.emptyLayoutRefreshButton).setOnClickListener(v -> {
            ImageView emptyLayoutImage = emptyLayout.findViewById(R.id.emptyImage);
            ProgressBar progressBar = emptyLayout.findViewById(R.id.progressBar);
            emptyLayoutImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            emptyLayout.postDelayed(() -> recyclerView.getAdapter().appendCollection(generateData()), 2000);
        });
        return emptyLayout;
    }

    private List<DemoModel> generateData() {
        List<DemoModel> models = new ArrayList<>();
        for (int i = 1; i <= 20; i++)
            models.add(new DemoModel("Demo title " + i, "Demo subtitle " + i));
        return models;
    }
}
