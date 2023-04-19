package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.BindView
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.VerticalAndHorizontalSpaceItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.SimpleRecyclerView

@FragmentScreen(screenName = "EmptyLayout", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_demo_empty_layout")
class EmptyLayoutDemoFragment : BaseFragment() {
    @BindView(name = "recycler_view")
    lateinit var recyclerView: SimpleRecyclerView

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .attachToDrawer(false)
            .withSubTitle(ResourceUtils.getPhrase(requireContext(), R.string.empty_layout_demo))
            .withTitle(ResourceUtils.getPhrase(requireContext(), R.string.app_name))
    }

    @Override
    override fun doOnViewStateRestored(savedInstanceState: Bundle?) {
        recyclerView.apply {
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
                recyclerView.adapter.collection.addAll(generateData(20))
            }, 2000)
        }
        return emptyLayout
    }
}