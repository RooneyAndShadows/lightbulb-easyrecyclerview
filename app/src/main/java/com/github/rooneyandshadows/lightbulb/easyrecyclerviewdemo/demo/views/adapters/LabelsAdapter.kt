package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.collection.ExtendedCollection
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.collection.ExtendedCollection.SelectableModes.SELECT_SINGLE

class LabelsAdapter : EasyRecyclerAdapter<DemoModel>() {
    override val collection: ExtendedCollection<DemoModel>
        get() = super.collection as ExtendedCollection<DemoModel>

    override fun createCollection(): ExtendedCollection<DemoModel> {
        return ExtendedCollection(this, SELECT_SINGLE)
    }

    @Override
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LabelItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.demo_list_item_label_layout, parent, false)
        )
    }

    @Override
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as LabelItemViewHolder
        holder.bindData(collection.getItem(position)!!)
    }

    @Override
    override fun onViewRecycled(viewHolder: RecyclerView.ViewHolder) {
        super.onViewRecycled(viewHolder)
        val holder = viewHolder as LabelItemViewHolder
        holder.recycle()
    }

    class LabelItemViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        private val container: RelativeLayout
        private val textView: TextView

        init {
            container = view as RelativeLayout
            textView = view.findViewById(R.id.labelTextView)
        }

        fun bindData(model: DemoModel) {
            container.background =
                ResourceUtils.getDrawable(container.context, R.drawable.bg_label_item)
            textView.text = model.title
        }

        fun recycle() {
            container.background = null
        }
    }
}