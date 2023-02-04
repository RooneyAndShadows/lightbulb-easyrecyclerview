package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views

import android.content.Context
import android.util.AttributeSet
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters.LabelsAdapter
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel

class LabelsRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : EasyRecyclerView<DemoModel, LabelsAdapter>(context, attrs, defStyleAttr, defStyleRes) {
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