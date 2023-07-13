package com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.bounce_overscroll.edge_factory

import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView

class DefaultEdge : RecyclerView.EdgeEffectFactory() {
    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
        return EdgeEffect(view.context)
    }
}