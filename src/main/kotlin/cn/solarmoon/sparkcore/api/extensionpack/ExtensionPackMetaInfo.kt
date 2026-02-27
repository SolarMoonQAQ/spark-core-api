package cn.solarmoon.sparkcore.api.extensionpack

import kotlinx.serialization.Serializable

@Serializable
data class ExtensionPackMetaInfo(
    val dependencies: List<String> = emptyList(),
    val version: String = "",
    val description: String = "",
    val author: String = "",
)
