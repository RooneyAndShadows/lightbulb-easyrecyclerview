package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views

import android.content.Context
import android.util.AttributeSet
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters.LabelsAdapter
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters.FilterableAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter

class LabelsRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : EasyRecyclerView<DemoModel>(context, attrs) {
    override val adapter: EasyRecyclerAdapter<DemoModel>
        get() = super.adapter as LabelsAdapter

    init {
        setAdapter(LabelsAdapter())
    }

    @Override
    override fun getLayoutManagerType(): LayoutManagerTypes {
        return LayoutManagerTypes.LAYOUT_FLOW_VERTICAL
    }
}