package cn.solarmoon.sparkcore.api.geometry.animation

import cn.solarmoon.sparkcore.api.geometry.molang.Vec3MolangValue
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject

@Serializable(KeyFrameSerializer::class)
data class KeyFrame(
    val pre: Vec3MolangValue,
    val post: Vec3MolangValue,
    val lerpMode: LerpMode
)

// KeyFrame有两种json形式，由dto接管
@Serializable
private sealed interface KeyFrameDTO {
    @Serializable
    data class Full(
        val post: Vec3MolangValue,
        val pre: Vec3MolangValue = post,
        val lerpMode: LerpMode = LerpMode.LINEAR
    ) : KeyFrameDTO

    @Serializable(SimpleKeyFrameSerializer::class)
    data class Simple(
        val post: Vec3MolangValue
    ) : KeyFrameDTO
}

private object SimpleKeyFrameSerializer : KSerializer<KeyFrameDTO.Simple> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("KeyFrame.Simple")

    override fun serialize(encoder: Encoder, value: KeyFrameDTO.Simple) {
        encoder.encodeSerializableValue(Vec3MolangValue.serializer(), value.post)
    }

    override fun deserialize(decoder: Decoder): KeyFrameDTO.Simple {
        return KeyFrameDTO.Simple(decoder.decodeSerializableValue(Vec3MolangValue.serializer()))
    }
}

private object KeyFrameSerializer : KSerializer<KeyFrame> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("KeyFrame")

    override fun serialize(encoder: Encoder, value: KeyFrame) {
        if (encoder is JsonDecoder && value.pre == value.post && value.lerpMode == LerpMode.LINEAR) {
            // 简写格式：只输出 post 数组
            encoder.encodeSerializableValue(KeyFrameDTO.Simple.serializer(), KeyFrameDTO.Simple(value.post))
        } else {
            // 完整对象格式
            encoder.encodeSerializableValue(KeyFrameDTO.Full.serializer(), KeyFrameDTO.Full(value.post, value.pre, value.lerpMode))
        }
    }

    override fun deserialize(decoder: Decoder): KeyFrame {
        val dto = if (decoder is JsonDecoder) {
            when(val element = decoder.decodeJsonElement()) {
                is JsonArray -> decoder.json.decodeFromJsonElement(KeyFrameDTO.Simple.serializer(), element)
                is JsonObject -> decoder.json.decodeFromJsonElement(KeyFrameDTO.Full.serializer(), element)
                else -> throw SerializationException("无法找到匹配的 KeyFrame JSON类型")
            }
        } else {
            decoder.decodeSerializableValue(KeyFrameDTO.Full.serializer())
        }
        return when (dto) {
            is KeyFrameDTO.Full -> KeyFrame(dto.pre, dto.post, dto.lerpMode)
            is KeyFrameDTO.Simple -> KeyFrame(dto.post, dto.post, LerpMode.LINEAR)
        }
    }
}

