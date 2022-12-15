package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity

import android.view.View
import com.github.rooneyandshadows.lightbulb.application.activity.routing.BaseApplicationRouter
import java.util.function.Consumer
import kotlin.jvm.functions.Function1

object MenuConfigurations {
    fun getConfiguration(activity: BaseActivity): SliderMenuConfiguration {
        val headingView: View = activity.getLayoutInflater().inflate(R.layout.demo_drawer_header_view, null)
        val configuration = SliderMenuConfiguration()
        configuration.withHeaderView(headingView)
        configuration.addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.regular_demo),
                null,
                null,
                1,
                action<SliderMenu>(Consumer<SliderMenu> { slider: SliderMenu ->
                    Router.Companion.getInstance().getRouter()
                        .toDemoRegular(BaseApplicationRouter.NavigationCommands.BACK_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.selectable_demo),
                null,
                null,
                1,
                action<SliderMenu>(Consumer<SliderMenu> { slider: SliderMenu ->
                    Router.Companion.getInstance().getRouter()
                        .toDemoSelectable(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.flow_layout_manager_demo),
                null,
                null,
                1,
                action<SliderMenu>(Consumer<SliderMenu> { slider: SliderMenu ->
                    Router.Companion.getInstance().getRouter()
                        .toFlowLayoutManager(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.header_and_footer_demo),
                null,
                null,
                1,
                action<SliderMenu>(Consumer<SliderMenu> { slider: SliderMenu ->
                    Router.Companion.getInstance().getRouter()
                        .toDemoHeaderAndFooter(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.empty_layout_demo),
                null,
                null,
                1,
                action<SliderMenu>(Consumer<SliderMenu> { slider: SliderMenu ->
                    Router.Companion.getInstance().getRouter()
                        .toEmptyLayout(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.bounce_effect_demo),
                null,
                null,
                1,
                action<SliderMenu>(Consumer<SliderMenu> { slider: SliderMenu ->
                    Router.Companion.getInstance().getRouter()
                        .toBounceEffect(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.pull_to_refresh_demo),
                null,
                null,
                1,
                action<SliderMenu>(Consumer<SliderMenu> { slider: SliderMenu ->
                    Router.Companion.getInstance().getRouter()
                        .toPullToRefresh(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.lazy_loading_demo),
                null,
                null,
                1,
                action<SliderMenu>(Consumer<SliderMenu> { slider: SliderMenu ->
                    Router.Companion.getInstance().getRouter()
                        .toLazyLoading(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.swipe_to_delete_demo),
                null,
                null,
                1,
                action<SliderMenu>(Consumer<SliderMenu> { slider: SliderMenu ->
                    Router.Companion.getInstance().getRouter()
                        .toSwipeToDelete(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.drag_to_reorder_demo),
                null,
                null,
                1,
                action<SliderMenu>(Consumer<SliderMenu> { slider: SliderMenu ->
                    Router.Companion.getInstance().getRouter()
                        .toDragToReorder(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.sticky_headers_simple_demo),
                null,
                null,
                1,
                action<SliderMenu>(Consumer<SliderMenu> { slider: SliderMenu ->
                    Router.Companion.getInstance().getRouter()
                        .toStickyHeadersSimple(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.sticky_headers_advanced_demo),
                null,
                null,
                1,
                action<SliderMenu>(Consumer<SliderMenu> { slider: SliderMenu ->
                    Router.Companion.getInstance().getRouter()
                        .toStickyHeadersAdvanced(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        )
        return configuration
    }

    fun <T> action(callable: Consumer<T>): Function1<T, Unit> {
        return { t: T ->
            callable.accept(t)
            Unit
        }
    }
}