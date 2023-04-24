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
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.collection.ExtendedCollection
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.collection.ExtendedCollection.SelectableModes.SELECT_NONE
import java.util.*

class FilterableAdapter : EasyRecyclerAdapter<DemoModel>() {
    override val collection: ExtendedCollection<DemoModel>
        get() = super.collection as ExtendedCollection<DemoModel>


    override fun getItemCount(): Int {
        return collection.filteredItems.size
    }

    override fun createCollection(): ExtendedCollection<DemoModel> {
        val adapter = this
        return object : ExtendedCollection<DemoModel>(adapter, SELECT_NONE) {
            override fun filterItem(item: DemoModel, filterQuery: String): Boolean {
                val query = filterQuery.lowercase(Locale.getDefault())
                val title = item.title.lowercase(Locale.getDefault())
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
        val context = vh.binding.demoItemView.context
        vh.binding.title = model.title
        vh.binding.subtitle = model.subtitle
        vh.binding.demoItemView.background = ResourceUtils.getDrawable(context, R.drawable.demo_item_bg)
    }

    @Override
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        val vh = holder as ViewHolder
        vh.binding.demoItemView.background = null
    }

    class ViewHolder(val binding: DemoListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}