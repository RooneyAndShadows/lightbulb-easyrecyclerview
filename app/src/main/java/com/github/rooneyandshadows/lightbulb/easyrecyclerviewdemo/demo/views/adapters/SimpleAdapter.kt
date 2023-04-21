package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.DemoListItemLayoutBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.collection.BasicCollection

class SimpleAdapter : EasyRecyclerAdapter<DemoModel>() {
    override val collection: BasicCollection<DemoModel>
        get() = super.collection as BasicCollection<DemoModel>

    override fun createCollection(): BasicCollection<DemoModel> {
        return BasicCollection(this)
    }

    @Override
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: DemoListItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.demo_list_item_layout,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @Override
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as ViewHolder
        val model: DemoModel = collection.getItem(position) ?: return
        val context = vh.binding.demoItemView.context
        vh.binding.title = model.title
        vh.binding.subtitle = model.subtitle
        vh.binding.demoItemView.background = ResourceUtils.getDrawable(context, R.drawable.bg_demo_item)
    }

    @Override
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        val vh = holder as ViewHolder
        vh.binding.demoItemView.background = null
    }

    class ViewHolder(val binding: DemoListItemLayoutBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}