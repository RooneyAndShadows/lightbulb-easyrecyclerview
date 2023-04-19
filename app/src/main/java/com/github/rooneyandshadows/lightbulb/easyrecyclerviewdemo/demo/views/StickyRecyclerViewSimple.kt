package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.StickyHeaderItemDecoration
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters.StickyAdapterSimple
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.StickySimpleDemoModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter

class StickyRecyclerViewSimple @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : EasyRecyclerView<StickySimpleDemoModel>(context, attrs) {
    override val adapter: StickyAdapterSimple
        get() = super.adapter as StickyAdapterSimple

    override val adapterCreator: AdapterCreator<StickySimpleDemoModel>
        get() = object : AdapterCreator<StickySimpleDemoModel> {
            override fun createAdapter(): StickyAdapterSimple {
                return StickyAdapterSimple()
            }
        }

    init {
        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        addItemDecoration(object : StickyHeaderItemDecoration(adapter) {
            @Override
            override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                val firstVisibleItemPosition =
                    (parent.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()
                if (firstVisibleItemPosition != 0) super.onDrawOver(c, parent, state)
            }
        })
    }
}