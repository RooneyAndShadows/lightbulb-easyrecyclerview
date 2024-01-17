package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.apt.annotations.FragmentViewBinding
import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbFragment
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.FragmentDemoEmptyLayoutBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData

@FragmentScreen(screenName = "EmptyLayout", screenGroup = "Demo")
@LightbulbFragment(layoutName = "fragment_demo_empty_layout")
class EmptyLayoutDemoFragment : BaseFragment() {
    @FragmentViewBinding
    lateinit var viewBinding: FragmentDemoEmptyLayoutBinding

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.empty_layout_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @Override
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewBinding.recyclerView.apply {
            val emptyLayout = generateEmptyLayout()
            val itemDecoration = VerticalAndHorizontalSpaceItemDecoration(ResourceUtils.dpToPx(12))
            addItemDecoration(itemDecoration)
            setEmptyLayout(emptyLayout)
        }
    }

    @SuppressLint("InflateParams")
    private fun generateEmptyLayout(): View {
        val emptyLayout: View = layoutInflater.inflate(R.layout.demo_empty_layout, null)
        emptyLayout.findViewById<View>(R.id.emptyLayoutRefreshButton).setOnClickListener {
            val emptyLayoutImage = emptyLayout.findViewById<ImageView>(R.id.emptyImage)
            val progressBar: ProgressBar = emptyLayout.findViewById(R.id.progressBar)
            emptyLayoutImage.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            emptyLayout.postDelayed({
                viewBinding.recyclerView.adapter.collection.addAll(generateData(20))
            }, 2000)
        }
        return emptyLayout
    }
}