package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.views

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R

@Suppress("unused")
class DemoItemView(
    context: Context,
    attrs: AttributeSet? = null,
) : RelativeLayout(context, attrs) {
    private val stateChecked = intArrayOf(R.attr.state_checked)
    private val stateNotChecked = intArrayOf(-R.attr.state_checked)
    private val stateCheckable = intArrayOf(R.attr.state_enabled)
    private val stateNotCheckable = intArrayOf(-R.attr.state_enabled)
    private val titleTextView: TextView by lazy {
        return@lazy findViewById<TextView>(R.id.title)!!
    }
    private val subtitleTextView: TextView by lazy {
        return@lazy findViewById<TextView>(R.id.subtitle)!!
    }
    var isChecked = false
        set(value) {
            if (field == value) return
            field = value
            refreshDrawableState()
        }
    var title: String = ""
        set(value) {
            field = value
            titleTextView.text = value
        }
    var subtitle: String = ""
        set(value) {
            field = value
            subtitleTextView.text = value
        }

    init {
        inflate(context, R.layout.demo_item_view, this)
        readAttributes(context, attrs)
        setupView()
    }

    fun setupView() {
        clipToPadding = false
        clipChildren = false
        initBackground()
        setupTextViews()
    }

    private fun setupTextViews() {
        titleTextView.apply {
            isDuplicateParentStateEnabled = true
            setTextColor(ContextCompat.getColorStateList(context, R.color.demo_item_text_color_primary))
        }
        subtitleTextView.apply {
            isDuplicateParentStateEnabled = true
            setTextColor(ContextCompat.getColorStateList(context, R.color.demo_item_text_color_secondary))
        }
    }

    fun initBackground() {
        //foreground = ResourceUtils.getDrawable(context, R.drawable.bg_demo_item_ripple)
        //background = ResourceUtils.getDrawable(context, R.drawable.bg_demo_item)
        background = ResourceUtils.getDrawable(context, R.drawable.demo_item_bg_combined)
        elevation = ResourceUtils.getDimenById(context, R.dimen.demo_item_elevation)
    }

    fun removeBackground() {
        background = null
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.DemoItemView, 0, 0)
        try {
            title = attributes.getString(R.styleable.DemoItemView_div_title) ?: ""
            subtitle = attributes.getString(R.styleable.DemoItemView_div_subtitle) ?: ""
        } finally {
            attributes.recycle()
        }
    }

    @Override
    override fun onCreateDrawableState(extraSpace: Int): IntArray? {
        val drawableState = super.onCreateDrawableState(extraSpace + 2)
        if (isChecked) {
            mergeDrawableStates(drawableState, stateChecked);
        }
        if (isEnabled) {
            mergeDrawableStates(drawableState, stateCheckable);
        }
        return drawableState
    }

    companion object DemoItemViewBindings {
        @BindingAdapter(value = ["DemoItemViewTitleTextChanged"], requireAll = false)
        @JvmStatic
        fun bindTitleTextChangedEvent(view: DemoItemView, bindingListener: InverseBindingListener) {
            bindingListener.onChange()
            view.titleTextView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    bindingListener.onChange()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }

        @InverseBindingAdapter(attribute = "div_title", event = "DemoItemViewTitleTextChanged")
        @JvmStatic
        fun getTitleText(view: DemoItemView): String {
            return view.title
        }

        @BindingAdapter("div_title")
        @JvmStatic
        fun setTitleText(view: DemoItemView, newTitle: String?) {
            if (view.title == newTitle) return
            view.title = newTitle ?: ""
        }


        @BindingAdapter(value = ["DemoItemViewSubtitleTextChanged"], requireAll = false)
        @JvmStatic
        fun bindSubtitleTextChangedEvent(view: DemoItemView, bindingListener: InverseBindingListener) {
            bindingListener.onChange()
            view.subtitleTextView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    bindingListener.onChange()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }

        @InverseBindingAdapter(attribute = "div_subtitle", event = "DemoItemViewSubtitleTextChanged")
        @JvmStatic
        fun getSubtitleText(view: DemoItemView): String {
            return view.subtitle
        }

        @BindingAdapter("div_subtitle")
        @JvmStatic
        fun setSubtitleText(view: DemoItemView, newSubtitle: String?) {
            if (view.subtitle == newSubtitle) return
            view.subtitle = newSubtitle ?: ""
        }
    }
}