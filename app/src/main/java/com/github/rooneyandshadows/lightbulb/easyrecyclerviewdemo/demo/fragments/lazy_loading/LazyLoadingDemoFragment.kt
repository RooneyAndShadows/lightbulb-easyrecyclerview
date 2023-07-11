package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.lazy_loading

import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.BindView
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragmentWithViewModel
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.InteractionUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.SimpleRecyclerView

@FragmentScreen(screenName = "LazyLoading", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_lazy_loading")
class LazyLoadingDemoFragment : BaseFragmentWithViewModel<LazyLoadingDemoViewModel>() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: SimpleRecyclerView
    override val viewModelClass: Class<LazyLoadingDemoViewModel>
        get() = LazyLoadingDemoViewModel::class.java

    @Override
    override fun doOnCreate(savedInstanceState: Bundle?, viewModel: LazyLoadingDemoViewModel) {
        initLazyLoadingAction()
    }

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
        val decoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
        recyclerView.addItemDecoration(decoration)
        recyclerView.setLazyLoadingListener {
            viewModel.getNextPage()
        }
        if (savedInstanceState != null) return
        recyclerView.adapter.collection.set(viewModel.listData)
    }

    private fun initLazyLoadingAction() {
        viewModel.setListeners(object : LazyLoadingDemoViewModel.DataListener {
            override fun onSuccess(items: List<DemoModel>) {
                val hasMoreData = recyclerView.adapter.collection.size() + items.size < 40
                recyclerView.adapter.collection.addAll(items)
                recyclerView.onLazyLoadingFinished(hasMoreData)
                if (!hasMoreData) InteractionUtils.showMessage(
                    requireContext(),
                    "All data has been loaded."
                )
            }

            override fun onFailure(errorDetails: String?) {
                println(errorDetails)
                recyclerView.onLazyLoadingFinished(true)
            }
        })
    }
}