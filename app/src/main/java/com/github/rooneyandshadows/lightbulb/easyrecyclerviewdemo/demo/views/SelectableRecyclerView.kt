package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views

import android.content.Context
import android.util.AttributeSet
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters.SelectableAdapter
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel

class SelectableRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : EasyRecyclerView<DemoModel>(context, attrs) {
    override val adapterCreator: AdapterCreator<DemoModel>
        get() = object : AdapterCreator<DemoModel> {
            override fun createAdapter(): SelectableAdapter {
                return SelectableAdapter()
            }
        }
}