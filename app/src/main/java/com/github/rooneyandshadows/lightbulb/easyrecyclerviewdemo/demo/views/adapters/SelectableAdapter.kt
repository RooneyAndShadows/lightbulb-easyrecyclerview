package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.DemoSelectableItemLayoutBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterSelectableModes
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.collection.ExtendedCollection

class SelectableAdapter : EasyRecyclerAdapter<DemoModel>() {
    override val collection: ExtendedCollection<DemoModel>
        get() = super.collection as ExtendedCollection<DemoModel>

    override fun createCollection(): ExtendedCollection<DemoModel> {
        return ExtendedCollection(this, EasyAdapterSelectableModes.SELECT_MULTIPLE)
    }

    @Override
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: DemoSelectableItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.demo_selectable_item_layout,
            parent,
            false
        )
        return ViewHolder(binding, this)
    }

    @Override
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as ViewHolder
        val model: DemoModel = collection.getItem(position) ?: return
        vh.binding.title = model.itemName
        vh.binding.subtitle = model.subtitle
        vh.initialize()
    }

    @Override
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        val vh = holder as ViewHolder
        vh.recycle()
    }

    class ViewHolder(val binding: DemoSelectableItemLayoutBinding, private val adapter: SelectableAdapter) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun initialize() {
            val collection = adapter.collection
            val itemPos = absoluteAdapterPosition - adapter.headersCount
            val isSelectedInAdapter: Boolean = collection.isItemSelected(itemPos)
            binding.cardContainer.setBackgroundDrawable(getBackgroundDrawable(isSelectedInAdapter))
            binding.cardContainer.setOnClickListener {
                collection.selectItemAt(itemPos, !collection.isItemSelected(itemPos))
            }
        }

        private fun getBackgroundDrawable(selected: Boolean): Drawable {
            return if (selected) ResourceUtils.getDrawable(itemView.context, R.drawable.bg_card_selected)!!
            else ResourceUtils.getDrawable(itemView.context, R.drawable.bg_card)!!
        }

        fun recycle() {
            binding.cardContainer.setBackgroundDrawable(null)
        }
    }
}