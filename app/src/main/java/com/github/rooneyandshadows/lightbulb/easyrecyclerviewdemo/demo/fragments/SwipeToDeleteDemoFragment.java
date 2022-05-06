package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.rooneyandshadows.lightbulb.application.fragment.BaseFragment;
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.BaseFragmentConfiguration;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.VerticalAndHorizontalSpaceItemDecoration;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers.EasyRecyclerViewSwipeHandler;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters.SimpleAdapter;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;

public class SwipeToDeleteDemoFragment extends BaseFragment {
    private EasyRecyclerView<DemoModel, SimpleAdapter> recyclerView;

    public static SwipeToDeleteDemoFragment getNewInstance() {
        return new SwipeToDeleteDemoFragment();
    }

    @NonNull
    @Override
    protected BaseFragmentConfiguration configureFragment() {
        return new BaseFragmentConfiguration()
                .withLeftDrawer(false)
                .withActionBarConfiguration(new BaseFragmentConfiguration.ActionBarConfiguration(R.id.toolbar)
                        .withActionButtons(true)
                        .attachToDrawer(false)
                        .withSubTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.swipe_to_delete_demo))
                        .withTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.app_name))
                );
    }

    @Override
    public View createView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demo_swipe_to_delete, container, false);
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
        recyclerView.setAdapter(new SimpleAdapter(), configureSwipeHandler(recyclerView));
        recyclerView.setEmptyLayout(generateEmptyLayout());
        recyclerView.getRecyclerView().addItemDecoration(new VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(15)));
        if (savedState == null)
            recyclerView.getAdapter().setCollection(generateData(10));
    }

    private EasyRecyclerViewSwipeHandler.SwipeCallbacks<DemoModel> configureSwipeHandler(EasyRecyclerView<DemoModel, SimpleAdapter> recyclerView) {
        return new EasyRecyclerViewSwipeHandler.SwipeCallbacks<DemoModel>() {
            @Override
            public EasyRecyclerViewSwipeHandler.Directions setAllowedSwipeDirections(DemoModel item) {
                return EasyRecyclerViewSwipeHandler.Directions.LEFT;
            }

            @Override
            public String getActionBackgroundText(DemoModel item) {
                return item.getItemName();
            }

            @Override
            public void swipedLeftAction(DemoModel item, Integer position) {
                //   int actualPosition = recyclerView.getAdapter().getPosition(item);
                //   recyclerView.getAdapter().removeItem(actualPosition);
            }

            @Override
            public void swipedRightAction(DemoModel item, Integer position) {
            }

            @Override
            public void cancelAction(DemoModel item, Integer position) {
                recyclerView.itemChanged(position);
            }

            @Override
            public String getPendingActionText(Integer direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    return "Delete";
                } else
                    return "";
            }

            @Override
            public String getCancelActionText() {
                return "Cancel";
            }
        };
    }

    private View generateEmptyLayout() {
        View emptyLayout = getLayoutInflater().inflate(R.layout.demo_empty_layout, null);
        emptyLayout.findViewById(R.id.emptyLayoutRefreshButton).setOnClickListener(v -> {
            ImageView emptyLayoutImage = emptyLayout.findViewById(R.id.emptyImage);
            ProgressBar progressBar = emptyLayout.findViewById(R.id.progressBar);
            emptyLayoutImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            emptyLayout.postDelayed(() -> recyclerView.getAdapter().appendCollection(generateData(10)), 2000);
        });
        return emptyLayout;
    }

    private List<DemoModel> generateData(int count) {
        List<DemoModel> models = new ArrayList<>();
        for (int i = 1; i <= count; i++)
            models.add(new DemoModel("Demo title " + i, "Demo subtitle " + i));
        return models;
    }
}
