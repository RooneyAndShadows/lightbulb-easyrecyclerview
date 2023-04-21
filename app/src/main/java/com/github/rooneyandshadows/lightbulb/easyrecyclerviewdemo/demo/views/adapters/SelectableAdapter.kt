package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.databinding.DemoSelectableItemLayoutBinding
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.DemoItemView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.collection.ExtendedCollection
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.collection.ExtendedCollection.SelectableModes.SELECT_MULTIPLE

class SelectableAdapter : EasyRecyclerAdapter<DemoModel>() {
    override val collection: ExtendedCollection<DemoModel>
        get() = super.collection as ExtendedCollection<DemoModel>

    override fun createCollection(): ExtendedCollection<DemoModel> {
        return ExtendedCollection(this, SELECT_MULTIPLE)
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
        collection.getItem(position)?.apply {
            vh.binding.item = this
            vh.bind()
        }
    }

    @Override
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        val vh = holder as ViewHolder
        vh.recycle()
    }

    class ViewHolder(
        val binding: DemoSelectableItemLayoutBinding,
        private val adapter: SelectableAdapter,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val demoItemView: DemoItemView = binding.itemView
        private val collection: ExtendedCollection<DemoModel>
            get() = adapter.collection
        private val positionInAdapter: Int
            get() = absoluteAdapterPosition - adapter.headersCount

        init {
            demoItemView.setOnClickListener {
                val demoItem = it as DemoItemView
                val pos = positionInAdapter
                val isSelected = collection.isItemSelected(positionInAdapter)
                collection.selectItemAt(pos, !isSelected, false)
                demoItem.isChecked = !isSelected
            }
        }

        fun bind() {
            demoItemView.apply {
                background = ResourceUtils.getDrawable(context, R.drawable.bg_demo_item)
                isChecked = collection.isItemSelected(positionInAdapter)
            }
        }

        fun recycle() {
            demoItemView.background = null
        }
    }
}