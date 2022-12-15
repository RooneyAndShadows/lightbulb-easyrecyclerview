package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations.StickyHeaderItemDecoration.StickyHeaderInterface
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.DemoStickySimpleItemLayoutBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.StickySimpleDemoModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterSelectableModes.SELECT_NONE
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter

class StickyAdapterSimple : EasyRecyclerAdapter<StickySimpleDemoModel>(SELECT_NONE), StickyHeaderInterface {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View
        return if (viewType == 0) {
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.demo_sticky_header_simple_item, parent, false)
            HeaderViewHolder(itemView)
        } else {
            val binding: DemoStickySimpleItemLayoutBinding =
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.demo_sticky_simple_item_layout,
                    parent,
                    false
                )
            ViewHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)!!.isHeader) 0 else 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model: StickySimpleDemoModel = getItem(position)!!
            holder.binding.title = model.itemName
            holder.binding.subtitle = model.subtitle
        } else if (holder is HeaderViewHolder) {
            holder.bind(getItem(position))
        }
    }

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

    override fun getHeaderLayout(headerPosition: Int): Int {
        return R.layout.demo_sticky_header_simple_item
    }

    override fun bindHeaderData(header: View?, headerPosition: Int) {
        val tv: TextView = header!!.findViewById(R.id.header_title)
        tv.text = getItem(headerPosition)!!.itemName
    }

    override fun isHeader(itemPosition: Int): Boolean {
        return getItem(itemPosition)!!.isHeader
    }

    internal inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView

        fun bind(model: StickySimpleDemoModel?) {
            title.text = model!!.itemName
        }

        init {
            title = itemView.findViewById<View>(R.id.header_title) as TextView
        }
    }

    class ViewHolder(val binding: DemoStickySimpleItemLayoutBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}