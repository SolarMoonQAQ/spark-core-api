package cn.solarmoon.sparkcore.api.geometry.animation

import kotlinx.serialization.Serializable

@Serializable
data class AnimationSet(
    val animations: Map<String, Animation>
)