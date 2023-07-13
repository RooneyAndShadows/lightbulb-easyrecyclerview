package com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.empty_layout

import android.view.View

abstract class EasyRecyclerEmptyLayoutListener {
    open fun onInflated(view: View) {
    }

    open fun onShow(view: View) {
    }

    open fun onHide(view: View) {
    }
}