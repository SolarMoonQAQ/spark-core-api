package cn.solarmoon.sparkcore.api.geometry.model

import cn.solarmoon.sparkcore.api.util.Direction
import cn.solarmoon.sparkcore.api.math.Float2
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject

@Serializable(UVUnionSerializer::class)
sealed interface UVUnion {
    @Serializable(FaceUVUnionSerializer::class)
    data class Face(val uvMap: Map<Direction, FaceUV>): UVUnion
    @Serializable(BoxUVUnionSerializer::class)
    data class Box(val uv: Float2): UVUnion
}

@Serializable
data class FaceUV(
    val uv: Float2,
    val uvSize: Float2
)

private object FaceUVUnionSerializer : KSerializer<UVUnion.Face> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("UVUnion.Face")

    override fun serialize(
        encoder: Encoder,
        value: UVUnion.Face
    ) {
        encoder.encodeSerializableValue(MapSerializer(Direction.serializer(), FaceUV.serializer()), value.uvMap)
    }

    override fun deserialize(decoder: Decoder): UVUnion.Face {
        return UVUnion.Face(decoder.decodeSerializableValue(MapSerializer(Direction.serializer(), FaceUV.serializer())))
    }
}

private object BoxUVUnionSerializer : KSerializer<UVUnion.Box> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("UVUnion.Box")

    override fun serialize(encoder: Encoder, value: UVUnion.Box) {
        encoder.encodeSerializableValue(Float2.serializer(), value.uv)
    }

    override fun deserialize(decoder: Decoder): UVUnion.Box {
        return UVUnion.Box(decoder.decodeSerializableValue(Float2.serializer()))
    }
}

private object UVUnionSerializer: KSerializer<UVUnion> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("UVUnion")

    override fun serialize(encoder: Encoder, value: UVUnion) {
        when(value) {
            is UVUnion.Face -> encoder.encodeSerializableValue(FaceUVUnionSerializer, value)
            is UVUnion.Box -> encoder.encodeSerializableValue(BoxUVUnionSerializer, value)
        }
    }

    override fun deserialize(decoder: Decoder): UVUnion {
        return if (decoder is JsonDecoder) {
            when (val element = decoder.decodeJsonElement()) {
                is JsonObject -> decoder.json.decodeFromJsonElement(FaceUVUnionSerializer, element)
                is JsonArray -> decoder.json.decodeFromJsonElement(BoxUVUnionSerializer, element)
                else -> throw SerializationException("无法解析的 UVUnion 类型: $element")
            }
        } else {
            try {
                decoder.decodeSerializableValue(FaceUVUnionSerializer)
            } catch (_: SerializationException) {
                decoder.decodeSerializableValue(BoxUVUnionSerializer)
            }
        }
    }

}