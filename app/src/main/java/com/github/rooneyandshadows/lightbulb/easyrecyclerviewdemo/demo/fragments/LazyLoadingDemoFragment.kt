package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.BindView
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.actions.AsyncAction
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.actions.AsyncAction.*
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.actions.LoadMoreDataAction
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.SimpleRecyclerView
import java.lang.Exception

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE", "SameParameterValue")
@FragmentScreen(screenName = "LazyLoading", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_lazy_loading")
class LazyLoadingDemoFragment : BaseFragment() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: SimpleRecyclerView

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.lazy_loading_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @Override
    override fun doOnViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        recyclerView.apply {
            addItemDecoration(VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12)))
            setLazyLoadingAction(LoadMoreDataAction(object : Action<DemoModel> {
                override fun execute(easyRecyclerView: EasyRecyclerView<DemoModel>): List<DemoModel> {
                    Thread.sleep(1500)
                    val adapter = adapter
                    val offset = adapter.collection.size()
                    return generateData(10, offset)
                }
            }, object : OnComplete<DemoModel> {
                override fun execute(result: List<DemoModel>, easyRecyclerView: EasyRecyclerView<DemoModel>) {
                    easyRecyclerView.adapter.apply {
                        collection.addAll(result)
                    }
                }
            }, object : OnError<DemoModel> {
                override fun execute(error: Exception, easyRecyclerView: EasyRecyclerView<DemoModel>) {
                    error.printStackTrace()
                }
            }))
            if (savedInstanceState != null) return@apply
            adapter.collection.set(generateData(10))
        }
    }
}