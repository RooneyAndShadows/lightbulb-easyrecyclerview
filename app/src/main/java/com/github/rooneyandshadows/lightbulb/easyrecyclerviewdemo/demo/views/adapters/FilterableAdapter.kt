package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.DemoListItemLayoutBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterSelectableModes.SELECT_NONE
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.collection.ExtendedCollection
import java.util.*

class FilterableAdapter : EasyRecyclerAdapter<ExtendedCollection<DemoModel>>() {

    override fun createCollection(): ExtendedCollection<DemoModel> {
        val adapter = this
        return object : ExtendedCollection<DemoModel>(adapter, SELECT_NONE) {
            override fun filterItem(item: DemoModel, filterQuery: String): Boolean {
                val query = filterQuery.lowercase(Locale.getDefault())
                val title = item.itemName.lowercase(Locale.getDefault())
                val subtitle = item.subtitle.lowercase(Locale.getDefault())
                return title.contains(query) || subtitle.contains(query)
            }
        }
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
        val model: DemoModel = collection.getFilteredItem(position) ?: return
        vh.binding.title = model.itemName
        vh.binding.subtitle = model.subtitle
    }

    class ViewHolder(val binding: DemoListItemLayoutBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}