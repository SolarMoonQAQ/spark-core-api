package cn.solarmoon.sparkcore.api.geometry.model

import kotlinx.serialization.Serializable

@Serializable
data class ModelDescription(
    val identifier: String,
    val textureWidth: Int,
    val textureHeight: Int,
)
