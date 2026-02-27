package cn.solarmoon.sparkcore.api.geometry.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeometryFile(
    val formatVersion: String,
    @SerialName("minecraft:geometry") val models: List<Model>
)