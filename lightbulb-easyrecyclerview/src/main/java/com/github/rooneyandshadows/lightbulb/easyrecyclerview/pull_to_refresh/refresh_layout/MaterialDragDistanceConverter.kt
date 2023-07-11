package com.github.rooneyandshadows.lightbulb.easyrecyclerview.pull_to_refresh.refresh_layout

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class MaterialDragDistanceConverter : IDragDistanceConverter {
    @Override
    override fun convert(scrollDistance: Float, refreshDistance: Float): Float {
        val originalDragPercent = scrollDistance / refreshDistance
        val dragPercent = min(1.0f, abs(originalDragPercent))
        val extraOS = abs(scrollDistance) - refreshDistance
        val tensionSlingshotPercent =
            max(0f, min(extraOS, refreshDistance * 2.0f) / refreshDistance)
        val tensionPercent = (tensionSlingshotPercent / 4 - (tensionSlingshotPercent / 4).toDouble().pow(2.0)).toFloat() * 2f
        val extraMove = refreshDistance * tensionPercent * 2
        val convertY = (refreshDistance * dragPercent + extraMove).toInt()
        return convertY.toFloat()
    }
}