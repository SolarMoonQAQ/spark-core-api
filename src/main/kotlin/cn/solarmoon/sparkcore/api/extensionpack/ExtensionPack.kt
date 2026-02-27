package cn.solarmoon.sparkcore.api.extensionpack

import kotlinx.serialization.Serializable

@Serializable
data class ExtensionPack(
    val entries: Map<String, ByteArray>,
    val metaInfo: ExtensionPackMetaInfo
)
