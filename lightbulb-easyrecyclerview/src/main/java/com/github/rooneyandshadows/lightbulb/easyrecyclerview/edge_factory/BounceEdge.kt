package com.github.rooneyandshadows.lightbulb.easyrecyclerview.edge_factory

import android.graphics.Canvas
import android.widget.EdgeEffect
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView

internal class BounceEdge(val isVertical: Boolean = true) : RecyclerView.EdgeEffectFactory() {
    companion object {
        private const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.08f
        private const val FLING_TRANSLATION_MAGNITUDE = 0.08f
    }

    override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {

        return object : EdgeEffect(recyclerView.context) {
            val translationAnim: SpringAnimation = SpringAnimation(
                recyclerView,
                if (isVertical) SpringAnimation.TRANSLATION_Y else SpringAnimation.TRANSLATION_X
            ).setSpring(
                SpringForce()
                    .setFinalPosition(0f)
                    .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY)
                    .setStiffness(SpringForce.STIFFNESS_MEDIUM)
            )

            override fun finish() {
                super.finish()
                if (isVertical) recyclerView.translationY = 0F
                else recyclerView.translationX = 0F
            }

            override fun onPull(deltaDistance: Float) {
                super.onPull(deltaDistance)
                handlePull(deltaDistance)
            }

            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)
                handlePull(deltaDistance)
            }

            private fun handlePull(deltaDistance: Float) {
                if (isVertical) {
                    val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                    val translationYDelta = sign * recyclerView.width * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
                    recyclerView.translationY += translationYDelta
                } else {
                    val sign = if (direction == DIRECTION_RIGHT) -1 else 1
                    val translationXDelta =
                        sign * recyclerView.height * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
                    recyclerView.translationX += translationXDelta
                }
                translationAnim.cancel()
            }

            override fun onRelease() {
                super.onRelease()
                if ((if (isVertical) recyclerView.translationY else recyclerView.translationX) != 0f) {
                    translationAnim.start()
                }
            }

            override fun onAbsorb(velocity: Int) {
                super.onAbsorb(velocity)
                val sign = if (direction == if (isVertical) DIRECTION_BOTTOM else DIRECTION_RIGHT) -1 else 1
                val translationVelocity = sign * velocity * FLING_TRANSLATION_MAGNITUDE
                translationAnim.setStartVelocity(translationVelocity).start()
            }

            override fun draw(canvas: Canvas?): Boolean {
                return false
            }

            override fun isFinished(): Boolean {
                return translationAnim.isRunning.not()
            }
        }
    }
}