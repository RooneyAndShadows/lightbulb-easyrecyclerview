package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.StickyHeaderItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters.StickyAdapterAdvanced
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.StickyAdvancedDemoModel
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.routing.screens.Screens.Demo.StickyHeadersAdvanced

class StickyRecyclerViewAdvanced @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : EasyRecyclerView<StickyAdvancedDemoModel>(context, attrs) {
    override val adapter: StickyAdapterAdvanced
        get() = super.adapter as StickyAdapterAdvanced

    init {
        setAdapter(StickyAdapterAdvanced())
        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        addItemDecoration(object : StickyHeaderItemDecoration(adapter!!) {
            override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                val firstVisibleItemPosition =
                    (parent.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()
                if (firstVisibleItemPosition != 0) super.onDrawOver(c, parent, state)
            }
        })
    }
}