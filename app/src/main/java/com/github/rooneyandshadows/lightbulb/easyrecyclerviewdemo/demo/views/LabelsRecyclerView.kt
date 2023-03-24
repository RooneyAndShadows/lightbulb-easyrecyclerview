package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views

import android.content.Context
import android.util.AttributeSet
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters.LabelsAdapter
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel

class LabelsRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : EasyRecyclerView<DemoModel, LabelsAdapter>(context, attrs) {
    override val adapterCreator: AdapterCreator<LabelsAdapter>
        get() = object : AdapterCreator<LabelsAdapter> {
            override fun createAdapter(): LabelsAdapter {
                return LabelsAdapter()
            }
        }

    @Override
    override fun getLayoutManagerType(): LayoutManagerTypes {
        return LayoutManagerTypes.LAYOUT_FLOW_VERTICAL
    }
}