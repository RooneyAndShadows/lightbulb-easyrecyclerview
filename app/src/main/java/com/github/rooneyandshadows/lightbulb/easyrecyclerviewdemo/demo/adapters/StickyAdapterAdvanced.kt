package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters

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

class StickyAdapterAdvanced : EasyRecyclerAdapter<StickyAdvancedDemoModel>(), StickyHeaderInterface {
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
        val model: StickyAdvancedDemoModel = getItem(position)!!
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
        tv.text = getItem(headerPosition)!!.dateString
    }

    @Override
    override fun isHeader(itemPosition: Int): Boolean {
        return getItem(itemPosition)!!.isHeader
    }

    internal class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ViewHolder(val binding: DemoStickyAdvancedItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}