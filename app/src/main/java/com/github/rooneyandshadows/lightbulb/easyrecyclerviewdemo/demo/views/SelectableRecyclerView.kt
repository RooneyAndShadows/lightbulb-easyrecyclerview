package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views

import android.content.Context
import android.util.AttributeSet
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters.SelectableAdapter
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel

class SelectableRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : EasyRecyclerView<DemoModel, SelectableAdapter>(context, attrs, defStyleAttr, defStyleRes) {
    override val adapterCreator: AdapterCreator<SelectableAdapter>
        get() = object : AdapterCreator<SelectableAdapter> {
            override fun createAdapter(): SelectableAdapter {
                return SelectableAdapter()
            }
        }
}