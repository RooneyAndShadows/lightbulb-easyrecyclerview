package com.github.rooneyandshadows.lightbulb.easyrecyclerview.empty_layout

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater.from
import android.view.View
import android.widget.RelativeLayout
import android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM
import android.widget.RelativeLayout.ALIGN_PARENT_LEFT
import android.widget.RelativeLayout.ALIGN_PARENT_RIGHT
import android.widget.RelativeLayout.ALIGN_PARENT_TOP
import android.widget.RelativeLayout.GONE
import android.widget.RelativeLayout.INVISIBLE
import android.widget.RelativeLayout.LayoutParams
import android.widget.RelativeLayout.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout.TRUE
import android.widget.RelativeLayout.VISIBLE
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.commons.utils.BundleUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.R
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.collection.EasyRecyclerAdapterCollection.CollectionChangeListener
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel

internal class EmptyLayout<ItemType : EasyAdapterDataModel>(
    private val easyRecyclerView: EasyRecyclerView<ItemType>
) {
    private val emptyLayoutContainer: RelativeLayout by lazy {
        return@lazy easyRecyclerView.findViewById(R.id.recyclerEmptyLayoutContainer)
    }
    private val recyclerView: RecyclerView by lazy {
        return@lazy easyRecyclerView.findViewById(R.id.recyclerView)
    }
    private val adapter: EasyRecyclerAdapter<ItemType>
        get() = easyRecyclerView.adapter
    private val context: Context
        get() = easyRecyclerView.context
    private var layoutId: Int? = null
    private var layout: View? = null
    private var emptyLayoutListeners: EasyRecyclerEmptyLayoutListener? = null
    var isShowing: Boolean = false
        private set

    companion object {
        private const val LAYOUT_ID_KEY = "LAYOUT_ID_KEY"
        private const val IS_SHOWING_KEY = "IS_SHOWING_KEY"
    }

    init {
        adapter.collection.addOnCollectionChangedListener(object : CollectionChangeListener {
            override fun onChanged() {
                showInternally(adapter.collection.isEmpty())
            }
        })
    }

    fun saveState(): Bundle {
        val out = Bundle()
        BundleUtils.putInt(LAYOUT_ID_KEY, out, layoutId ?: -1)
        BundleUtils.putBoolean(IS_SHOWING_KEY, out, isShowing)
        return out
    }

    fun restoreState(savedState: Bundle) {
        val layoutId = BundleUtils.getInt(LAYOUT_ID_KEY, savedState)
        val isShowing = BundleUtils.getBoolean(IS_SHOWING_KEY, savedState)
        this.layoutId = if (layoutId == -1) null else layoutId
        showInternally(isShowing)
    }

    fun setEmptyLayout(@LayoutRes layoutId: Int, listeners: EasyRecyclerEmptyLayoutListener? = null) {
        this.layoutId = if (layoutId == -1) null else layoutId
        this.layoutId ?: return
        val layout = from(context).inflate(this.layoutId!!, null)
        setEmptyLayout(layout, listeners)
    }

    fun setEmptyLayout(emptyLayout: View?, layoutListener: EasyRecyclerEmptyLayoutListener? = null) {
        emptyLayoutListeners = layoutListener
        emptyLayoutContainer.removeAllViews()
        if (emptyLayout == null) {
            layout = null
            layoutId = null
            return
        }
        layout = emptyLayout
        emptyLayoutListeners?.onInflated(layout!!)
        emptyLayoutContainer.addView(layout, generateLayoutParams())
        showInternally(adapter.collection.isEmpty())
    }

    private fun generateLayoutParams(): LayoutParams {
        val params = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        params.addRule(ALIGN_PARENT_LEFT, TRUE)
        params.addRule(ALIGN_PARENT_TOP, TRUE)
        params.addRule(ALIGN_PARENT_BOTTOM, TRUE)
        params.addRule(ALIGN_PARENT_RIGHT, TRUE)
        return params
    }

    private fun showInternally(newState: Boolean) {
        easyRecyclerView.post {
            emptyLayoutContainer.visibility = if (newState) VISIBLE else GONE
            recyclerView.visibility = if (newState) GONE else VISIBLE
        }
        if (layout == null || isShowing == newState) return
        isShowing = newState
        if (newState) {
            emptyLayoutListeners?.onShow(layout!!)
        } else {
            emptyLayoutListeners?.onHide(layout!!)
        }
    }
}