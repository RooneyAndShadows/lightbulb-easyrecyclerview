package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity

import android.annotation.SuppressLint
import android.view.View
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.SliderMenu
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.config.SliderMenuConfiguration
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.items.PrimaryMenuItem
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity.MainActivityNavigator.*

object MenuConfigurations {
    @SuppressLint("InflateParams")
    fun getConfiguration(activity: BaseActivity): SliderMenuConfiguration {
        val headingView: View = activity.layoutInflater.inflate(R.layout.demo_drawer_header_view, null)
        val configuration = SliderMenuConfiguration()
        configuration.withHeaderView(headingView)
        configuration.addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.regular_demo),
                null,
                null,
                1
            ) { slider: SliderMenu ->
                route().back()
                slider.closeSlider()
            }
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.filterable_demo),
                null,
                null,
                1
            ) { slider: SliderMenu ->
                route().toDemoFilterable()
                    .forward()
                slider.closeSlider()
            }
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.selectable_demo),
                null,
                null,
                1
            ) { slider: SliderMenu ->
                route().toDemoSelectable()
                    .forward()
                slider.closeSlider()
            }
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.flow_layout_manager_demo),
                null,
                null,
                1
            ) { slider: SliderMenu ->
                route().toDemoFlowLayout()
                    .forward()
                slider.closeSlider()
            }
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.header_and_footer_demo),
                null,
                null,
                1
            ) { slider: SliderMenu ->
                route().toDemoHeaderAndFooter()
                    .forward()
                slider.closeSlider()
            }
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.empty_layout_demo),
                null,
                null,
                1
            ) { slider: SliderMenu ->
                route().toDemoEmptyLayout()
                    .forward()
                slider.closeSlider()
            }
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.bounce_effect_demo),
                null,
                null,
                1
            ) { slider: SliderMenu ->
                route().toDemoBouncy()
                    .forward()
                slider.closeSlider()
            }
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.pull_to_refresh_demo),
                null,
                null,
                1
            ) { slider: SliderMenu ->
                route().toDemoPullToRefresh()
                    .forward()
                slider.closeSlider()
            }
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.lazy_loading_demo),
                null,
                null,
                1
            ) { slider: SliderMenu ->
                route().toDemoLazyLoading()
                    .forward()
                slider.closeSlider()
            }
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.swipe_to_delete_demo),
                null,
                null,
                1
            ) { slider: SliderMenu ->
                route().toDemoSwipeToDelete()
                    .forward()
                slider.closeSlider()
            }
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.drag_to_reorder_demo),
                null,
                null,
                1
            ) { slider: SliderMenu ->
                route().toDemoDrag()
                    .forward()
                slider.closeSlider()
            }
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.sticky_headers_simple_demo),
                null,
                null,
                1
            ) { slider: SliderMenu ->
                route().toDemoStickyHeaders()
                    .forward()
                slider.closeSlider()
            }
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.sticky_headers_advanced_demo),
                null,
                null,
                1
            ) { slider: SliderMenu ->
                route().toDemoStickyHeadersAdvanced()
                    .forward()
                slider.closeSlider()
            }
        )
        return configuration
    }
}