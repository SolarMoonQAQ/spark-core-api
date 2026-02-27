package cn.solarmoon.sparkcore.api.geometry.animation

import kotlinx.serialization.Serializable

@Serializable
enum class LerpMode {
    LINEAR, CATMULLROM
}