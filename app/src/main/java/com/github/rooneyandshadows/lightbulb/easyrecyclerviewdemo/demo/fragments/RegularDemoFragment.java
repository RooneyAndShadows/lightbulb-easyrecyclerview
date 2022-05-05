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
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.SimpleAdapter;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RegularDemoFragment extends BaseFragment {
    private EasyRecyclerView<DemoModel, SimpleAdapter> recyclerView;

    public static RegularDemoFragment getNewInstance() {
        return new RegularDemoFragment();
    }

    @NonNull
    @Override
    protected BaseFragmentConfiguration configureFragment() {
        return new BaseFragmentConfiguration()
                .withLeftDrawer(true)
                .withActionBarConfiguration(new BaseFragmentConfiguration.ActionBarConfiguration(R.id.toolbar)
                        .withActionButtons(true)
                        .attachToDrawer(true)
                        .withSubTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.regular_demo))
                        .withTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.app_name))
                );
    }

    @Override
    public View createView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demo_regular, container, false);
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
        recyclerView.setAdapter(new SimpleAdapter());
        recyclerView.getRecyclerView().addItemDecoration(new VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(15)));
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
