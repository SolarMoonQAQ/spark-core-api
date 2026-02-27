package cn.solarmoon.sparkcore.api.geometry.animation

import kotlinx.serialization.Serializable

@Serializable
data class Animation(
    val loop: Loop = Loop.ONCE,
    val animationLength: Float = Float.MAX_VALUE,
    val bones: Map<String, BonePose>
)
