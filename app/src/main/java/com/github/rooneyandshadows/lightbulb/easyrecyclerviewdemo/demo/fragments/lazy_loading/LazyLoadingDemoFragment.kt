package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.lazy_loading

import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentViewBinding
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentViewModel
import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbFragment
import com.github.rooneyandshadows.lightbulb.commons.utils.InteractionUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.FragmentDemoLazyLoadingBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel

@FragmentScreen(screenName = "LazyLoading", screenGroup = "Demo")
@LightbulbFragment(layoutName = "fragment_demo_lazy_loading")
class LazyLoadingDemoFragment : BaseFragment() {
    @FragmentViewBinding
    lateinit var viewBinding: FragmentDemoLazyLoadingBinding

    @FragmentViewModel
    lateinit var viewModel: LazyLoadingDemoViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(fragmentView, savedInstanceState)
        val decoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
        viewBinding.recyclerView.addItemDecoration(decoration)
        viewBinding.recyclerView.setLazyLoadingListener {
            viewModel.getNextPage()
        }
        if (savedInstanceState != null) return
        viewBinding.recyclerView.adapter.collection.set(viewModel.listData)
    }

    private fun initLazyLoadingAction() {
        viewModel.setListeners(object : LazyLoadingDemoViewModel.DataListener {
            override fun onSuccess(items: List<DemoModel>) {
                val hasMoreData = viewBinding.recyclerView.adapter.collection.size() + items.size < 40
                viewBinding.recyclerView.adapter.collection.addAll(items)
                viewBinding.recyclerView.onLazyLoadingFinished(hasMoreData)
                if (!hasMoreData) InteractionUtils.showMessage(
                    requireContext(),
                    "All data has been loaded."
                )
            }

            override fun onFailure(errorDetails: String?) {
                println(errorDetails)
                viewBinding.recyclerView.onLazyLoadingFinished(true)
            }
        })
    }
}