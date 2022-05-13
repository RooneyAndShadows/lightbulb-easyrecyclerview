package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity;
import com.github.rooneyandshadows.lightbulb.application.fragment.BaseFragment;
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.BaseFragmentConfiguration;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.FlexboxSpaceItemDecoration;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity.MainActivity;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity.MenuConfigurations;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.LabelsAdapter;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FlowLayoutManagerDemoFragment extends BaseFragment {
    private EasyRecyclerView<DemoModel, LabelsAdapter> recyclerView;

    public static FlowLayoutManagerDemoFragment getNewInstance() {
        return new FlowLayoutManagerDemoFragment();
    }

    @NonNull
    @Override
    protected BaseFragmentConfiguration configureFragment() {
        return new BaseFragmentConfiguration()
                .withLeftDrawer(false)
                .withActionBarConfiguration(new BaseFragmentConfiguration.ActionBarConfiguration(R.id.toolbar)
                        .withActionButtons(true)
                        .attachToDrawer(false)
                        .withSubTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.flow_layout_manager_demo))
                        .withTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.app_name))
                );
    }

    @Override
    public View createView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demo_flow_layout_manager, container, false);
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
        recyclerView.setAdapter(new LabelsAdapter());
        recyclerView.addItemDecoration(new FlexboxSpaceItemDecoration(ResourceUtils.dpToPx(10), recyclerView));
        if (savedState == null)
            recyclerView.getAdapter().setCollection(generateInitialData());
    }

    private List<DemoModel> generateInitialData() {
        List<DemoModel> models = new ArrayList<>();
        models.add(new DemoModel("Star", ""));
        models.add(new DemoModel("Tag", ""));
        models.add(new DemoModel("Search", ""));
        models.add(new DemoModel("Block", ""));
        models.add(new DemoModel("Center", ""));
        models.add(new DemoModel("Right", ""));
        models.add(new DemoModel("Cat", ""));
        models.add(new DemoModel("Tree", ""));
        models.add(new DemoModel("Person", ""));
        models.add(new DemoModel("Generation", ""));
        models.add(new DemoModel("Utility", ""));
        models.add(new DemoModel("Category", ""));
        models.add(new DemoModel("Label", ""));
        models.add(new DemoModel("Side", ""));
        models.add(new DemoModel("Section", ""));
        models.add(new DemoModel("Page", ""));
        models.add(new DemoModel("Class", ""));
        models.add(new DemoModel("Type", ""));
        models.add(new DemoModel("Performance", ""));
        models.add(new DemoModel("Object", ""));
        models.add(new DemoModel("Count", ""));
        models.add(new DemoModel("Letter", ""));
        models.add(new DemoModel("Subtitle", ""));
        models.add(new DemoModel("Height", ""));
        models.add(new DemoModel("Strenght", ""));
        models.add(new DemoModel("Star", ""));
        models.add(new DemoModel("Tag", ""));
        models.add(new DemoModel("Search", ""));
        models.add(new DemoModel("Block", ""));
        models.add(new DemoModel("Center", ""));
        models.add(new DemoModel("Right", ""));
        models.add(new DemoModel("Cat", ""));
        models.add(new DemoModel("Tree", ""));
        models.add(new DemoModel("Person", ""));
        models.add(new DemoModel("Generation", ""));
        models.add(new DemoModel("Utility", ""));
        models.add(new DemoModel("Category", ""));
        models.add(new DemoModel("Label", ""));
        models.add(new DemoModel("Side", ""));
        models.add(new DemoModel("Section", ""));
        models.add(new DemoModel("Page", ""));
        models.add(new DemoModel("Class", ""));
        models.add(new DemoModel("Type", ""));
        models.add(new DemoModel("Performance", ""));
        models.add(new DemoModel("Object", ""));
        models.add(new DemoModel("Count", ""));
        models.add(new DemoModel("Letter", ""));
        models.add(new DemoModel("Subtitle", ""));
        models.add(new DemoModel("Height", ""));
        models.add(new DemoModel("Strenght", ""));
        models.add(new DemoModel("Star", ""));
        models.add(new DemoModel("Tag", ""));
        models.add(new DemoModel("Search", ""));
        models.add(new DemoModel("Block", ""));
        models.add(new DemoModel("Center", ""));
        models.add(new DemoModel("Right", ""));
        models.add(new DemoModel("Cat", ""));
        models.add(new DemoModel("Tree", ""));
        models.add(new DemoModel("Person", ""));
        models.add(new DemoModel("Generation", ""));
        models.add(new DemoModel("Utility", ""));
        models.add(new DemoModel("Category", ""));
        models.add(new DemoModel("Label", ""));
        models.add(new DemoModel("Side", ""));
        models.add(new DemoModel("Section", ""));
        models.add(new DemoModel("Page", ""));
        models.add(new DemoModel("Class", ""));
        models.add(new DemoModel("Type", ""));
        models.add(new DemoModel("Performance", ""));
        models.add(new DemoModel("Object", ""));
        models.add(new DemoModel("Count", ""));
        models.add(new DemoModel("Letter", ""));
        models.add(new DemoModel("Subtitle", ""));
        models.add(new DemoModel("Height", ""));
        models.add(new DemoModel("Strenght", ""));
        models.add(new DemoModel("Star", ""));
        models.add(new DemoModel("Tag", ""));
        models.add(new DemoModel("Search", ""));
        models.add(new DemoModel("Block", ""));
        models.add(new DemoModel("Center", ""));
        models.add(new DemoModel("Right", ""));
        models.add(new DemoModel("Cat", ""));
        models.add(new DemoModel("Tree", ""));
        models.add(new DemoModel("Person", ""));
        models.add(new DemoModel("Generation", ""));
        models.add(new DemoModel("Utility", ""));
        models.add(new DemoModel("Category", ""));
        models.add(new DemoModel("Label", ""));
        models.add(new DemoModel("Side", ""));
        models.add(new DemoModel("Section", ""));
        models.add(new DemoModel("Page", ""));
        models.add(new DemoModel("Class", ""));
        models.add(new DemoModel("Type", ""));
        models.add(new DemoModel("Performance", ""));
        models.add(new DemoModel("Object", ""));
        models.add(new DemoModel("Count", ""));
        models.add(new DemoModel("Letter", ""));
        models.add(new DemoModel("Subtitle", ""));
        models.add(new DemoModel("Height", ""));
        models.add(new DemoModel("Strenght", ""));
        models.add(new DemoModel("Star", ""));
        models.add(new DemoModel("Tag", ""));
        models.add(new DemoModel("Search", ""));
        models.add(new DemoModel("Block", ""));
        models.add(new DemoModel("Center", ""));
        models.add(new DemoModel("Right", ""));
        models.add(new DemoModel("Cat", ""));
        models.add(new DemoModel("Tree", ""));
        models.add(new DemoModel("Person", ""));
        models.add(new DemoModel("Generation", ""));
        models.add(new DemoModel("Utility", ""));
        models.add(new DemoModel("Category", ""));
        models.add(new DemoModel("Label", ""));
        models.add(new DemoModel("Side", ""));
        models.add(new DemoModel("Section", ""));
        models.add(new DemoModel("Page", ""));
        models.add(new DemoModel("Class", ""));
        models.add(new DemoModel("Type", ""));
        models.add(new DemoModel("Performance", ""));
        models.add(new DemoModel("Object", ""));
        models.add(new DemoModel("Count", ""));
        models.add(new DemoModel("Letter", ""));
        models.add(new DemoModel("Subtitle", ""));
        models.add(new DemoModel("Height", ""));
        models.add(new DemoModel("Strenght", ""));
        return models;
    }
}
