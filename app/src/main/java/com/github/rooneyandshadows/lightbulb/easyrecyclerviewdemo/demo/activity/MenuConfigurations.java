package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity;

import android.view.View;

import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity;
import com.github.rooneyandshadows.lightbulb.application.activity.routing.BaseApplicationRouter;
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.config.SliderMenuConfiguration;
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.items.PrimaryMenuItem;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R;

import java.util.function.Consumer;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MenuConfigurations {

    public static SliderMenuConfiguration getConfiguration(BaseActivity activity) {
        View headingView = activity.getLayoutInflater().inflate(R.layout.demo_drawer_header_view, null);
        SliderMenuConfiguration configuration = new SliderMenuConfiguration();
        configuration.withHeaderView(headingView);
        configuration.addMenuItem(new PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.regular_demo),
                null,
                null,
                1,
                action((slider) -> {
                    Router.getInstance().getRouter().toDemoRegular(BaseApplicationRouter.NavigationCommands.BACK_TO);
                    slider.closeSlider();
                })
        )).addMenuItem(new PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.selectable_demo),
                null,
                null,
                1,
                action((slider) -> {
                    Router.getInstance().getRouter().toDemoSelectable(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO);
                    slider.closeSlider();
                })
        )).addMenuItem(new PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.header_and_footer_demo),
                null,
                null,
                1,
                action((slider) -> {
                    Router.getInstance().getRouter().toDemoHeaderAndFooter(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO);
                    slider.closeSlider();
                })
        )).addMenuItem(new PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.empty_layout_demo),
                null,
                null,
                1,
                action((slider) -> {
                    Router.getInstance().getRouter().toEmptyLayout(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO);
                    slider.closeSlider();
                })
        )).addMenuItem(new PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.bounce_effect_demo),
                null,
                null,
                1,
                action((slider) -> {
                    Router.getInstance().getRouter().toBounceEffect(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO);
                    slider.closeSlider();
                })
        )).addMenuItem(new PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.pull_to_refresh_demo),
                null,
                null,
                1,
                action((slider) -> {
                    Router.getInstance().getRouter().toPullToRefresh(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO);
                    slider.closeSlider();
                })
        )).addMenuItem(new PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.lazy_loading_demo),
                null,
                null,
                1,
                action((slider) -> {
                    Router.getInstance().getRouter().toLazyLoading(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO);
                    slider.closeSlider();
                })
        )).addMenuItem(new PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.swipe_to_delete_demo),
                null,
                null,
                1,
                action((slider) -> {
                    Router.getInstance().getRouter().toSwipeToDelete(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO);
                    slider.closeSlider();
                })
        )).addMenuItem(new PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.drag_to_reorder_demo),
                null,
                null,
                1,
                action((slider) -> {
                    Router.getInstance().getRouter().toDragToReorder(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO);
                    slider.closeSlider();
                })
        )).addMenuItem(new PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.sticky_headers_simple_demo),
                null,
                null,
                1,
                action((slider) -> {
                    Router.getInstance().getRouter().toStickyHeadersSimple(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO);
                    slider.closeSlider();
                })
        )).addMenuItem(new PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.sticky_headers_advanced_demo),
                null,
                null,
                1,
                action((slider) -> {
                    Router.getInstance().getRouter().toStickyHeadersAdvanced(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO);
                    slider.closeSlider();
                })
        ));
        return configuration;
    }

    public static <T> Function1<T, Unit> action(Consumer<T> callable) {
        return t -> {
            callable.accept(t);
            return Unit.INSTANCE;
        };
    }
}