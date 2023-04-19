package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.StickyHeaderItemDecoration.StickyHeaderInterface
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.DemoStickyAdvancedItemLayoutBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.StickyAdvancedDemoModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.collection.BasicCollection
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.collection.EasyRecyclerAdapterCollection

class StickyAdapterAdvanced : EasyRecyclerAdapter<StickyAdvancedDemoModel>(), StickyHeaderInterface {
    override val collection: EasyRecyclerAdapterCollection<StickyAdvancedDemoModel>
        get() = super.collection as BasicCollection<StickyAdvancedDemoModel>

    @Override
    override fun createCollection(): BasicCollection<StickyAdvancedDemoModel> {
        return BasicCollection(this)
    }

    @Override
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: DemoStickyAdvancedItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.demo_sticky_advanced_item_layout,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @Override
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as ViewHolder
        val model: StickyAdvancedDemoModel = collection.getItem(position) ?: return
        vh.binding.isHeader = model.isHeader
        vh.binding.title = model.itemName
        vh.binding.dayString = model.dateString
        vh.binding.subtitle = model.subtitle
    }

    @Override
    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var headerPosition = 0
        for (i in itemPosition downTo 1) {
            if (isHeader(i)) {
                headerPosition = i
                return headerPosition
            }
        }
        return headerPosition
    }

    @Override
    override fun getHeaderLayout(headerPosition: Int): Int {
        return R.layout.demo_sticky_header_advanced_item
    }

    @Override
    override fun bindHeaderData(header: View?, headerPosition: Int) {
        val tv: TextView = header!!.findViewById(R.id.header_title)
        tv.text = collection.getItem(headerPosition)!!.dateString
    }

    @Override
    override fun isHeader(itemPosition: Int): Boolean {
        return collection.getItem(itemPosition)!!.isHeader
    }

    class ViewHolder(val binding: DemoStickyAdvancedItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}