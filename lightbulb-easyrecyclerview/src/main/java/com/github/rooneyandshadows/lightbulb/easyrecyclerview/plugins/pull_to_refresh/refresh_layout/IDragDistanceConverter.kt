package com.github.rooneyandshadows.lightbulb.easyrecyclerview.plugins.pull_to_refresh.refresh_layout

interface IDragDistanceConverter {
    /**
     * @param scrollDistance the distance between the ACTION_DOWN point and the ACTION_MOVE point
     * @param refreshDistance the distance between the refresh point and the start point
     * @return the real distance of the refresh view moved
     */
    fun convert(scrollDistance: Float, refreshDistance: Float): Float
}