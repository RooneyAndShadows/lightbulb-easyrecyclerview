package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.google.android.material.card.MaterialCardView

class DemoItemView(
    context: Context,
    attrs: AttributeSet? = null,
) : MaterialCardView(context, attrs) {
    private val stateChecked = intArrayOf(R.attr.state_checked)
    private val stateNotChecked = intArrayOf(-R.attr.state_checked)
    private val stateEnabled = intArrayOf(R.attr.state_enabled)
    private val stateNotEnabled = intArrayOf(-R.attr.state_enabled)
    private val titleTextView: TextView
    private val subtitleTextView: TextView
    var isChecked = false
        set(value) {
            if (field == value) return
            field = value
            refreshDrawableState()
        }
    var isEnabled = false
        set(value) {
            if (field == value) return
            field = value
            refreshDrawableState()
        }

    init {
        elevation = ResourceUtils.dpToPx(3).toFloat()
        inflate(context, R.layout.demo_item_view, this)
        titleTextView = findViewById(R.id.title)
        subtitleTextView = findViewById(R.id.subtitle)
    }

    fun setTitle(title: String) {
        titleTextView.text = title
    }

    fun setSubtitle(title: String) {
        titleTextView.text = title
    }

    @Override
    override fun onCreateDrawableState(extraSpace: Int): IntArray? {
        val drawableState = super.onCreateDrawableState(extraSpace + 2)
        if (isChecked) {
            mergeDrawableStates(drawableState, stateChecked);
        }
        if (isEnabled) {
            mergeDrawableStates(drawableState, stateEnabled);
        }
        return drawableState
    }

    @BindingAdapter("title")
    fun setTitle(view: DemoItemView, text: String) {
        view.setTitle(text)
    }

    @BindingAdapter("subtitle")
    fun setSubtitle(view: DemoItemView, text: String) {
        view.setSubtitle(text)
    }
}