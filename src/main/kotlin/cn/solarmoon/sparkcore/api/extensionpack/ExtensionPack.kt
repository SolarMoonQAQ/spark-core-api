package cn.solarmoon.sparkcore.api.extensionpack

import kotlinx.serialization.Serializable

@Serializable
data class ExtensionPack(
    val metaInfo: ExtensionPackMetaInfo,
    val entries: Map<String, ByteArray>
)
