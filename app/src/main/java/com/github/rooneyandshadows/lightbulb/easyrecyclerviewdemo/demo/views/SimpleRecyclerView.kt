package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views

import android.content.Context
import android.util.AttributeSet
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters.SimpleAdapter
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel

class SimpleRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : EasyRecyclerView<DemoModel, SimpleAdapter>(context, attrs, defStyleAttr, defStyleRes) {
    override val adapterCreator: AdapterCreator<SimpleAdapter>
        get() = object : AdapterCreator<SimpleAdapter> {
            override fun createAdapter(): SimpleAdapter {
                return SimpleAdapter()
            }
        }
}