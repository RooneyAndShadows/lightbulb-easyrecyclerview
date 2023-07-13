package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views

import android.content.Context
import android.util.AttributeSet
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters.SelectableAdapter
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter

class SelectableRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : EasyRecyclerView<DemoModel>(context, attrs) {
    override val adapter: SelectableAdapter
        get() = super.adapter as SelectableAdapter

    init {
        setAdapter(SelectableAdapter())
    }
}