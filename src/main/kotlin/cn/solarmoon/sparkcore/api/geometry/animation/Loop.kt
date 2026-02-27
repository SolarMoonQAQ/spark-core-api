package cn.solarmoon.sparkcore.api.geometry.animation

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(LoopSerializer::class)
enum class Loop {
    ONCE,
    HOLD_ON_LAST_FRAME,
    TRUE
}

object LoopSerializer : KSerializer<Loop> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Loop", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Loop) {
        when (value) {
            Loop.TRUE -> encoder.encodeBoolean(true)
            else -> encoder.encodeString(value.name.lowercase())
        }
    }

    override fun deserialize(decoder: Decoder): Loop {
        return try {
            // 尝试解析布尔
            if (decoder.decodeBoolean()) Loop.TRUE else throw SerializationException("只能解析布尔值为 true 的情况")
        } catch (_: Exception) {
            // 否则解析字符串
            val str = decoder.decodeString()
            Loop.valueOf(str.uppercase())
        }
    }
}
