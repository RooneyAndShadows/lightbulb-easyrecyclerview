package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentViewBinding
import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbFragment
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.FragmentDemoHeaderAndFooterBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData

@FragmentScreen(screenName = "HeaderAndFooter", screenGroup = "Demo")
@LightbulbFragment(layoutName = "fragment_demo_header_and_footer")
class HeaderAndFooterDemoFragment : BaseFragment() {
    @FragmentViewBinding
    lateinit var viewBinding: FragmentDemoHeaderAndFooterBinding

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.header_and_footer_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @SuppressLint("InflateParams")
    @Override
    override fun onViewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        viewBinding.recyclerView.apply {
            val headerView: View = layoutInflater.inflate(R.layout.demo_header_item_layout, null)
            val footerView: View = layoutInflater.inflate(R.layout.demo_footer_item_layout, null)
            val itemDemoRecyclerView = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
            addHeaderView(headerView)
            addFooterView(footerView)
            addItemDecoration(itemDemoRecyclerView)
            if (savedInstanceState != null) return@apply
            adapter.collection.set(generateData(20))
        }
    }
}