package cn.solarmoon.sparkcore.api.geometry.model

import cn.solarmoon.sparkcore.api.math.Float3
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(LocatorSerializer::class)
data class Locator(
    val offset: Float3,
    val rotation: Float3
)

// locator有两种json形式，由dto接管
@Serializable
private sealed interface LocatorDto {
    @Serializable
    data class Complex(
        val offset: Float3,
        val rotation: Float3
    ): LocatorDto

    @Serializable(SimpleLocatorSerializer::class)
    data class Simple(
        val offset: Float3
    ): LocatorDto

    object SimpleLocatorSerializer : KSerializer<Simple> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("Locator.Simple")

        override fun serialize(
            encoder: Encoder,
            value: Simple
        ) {
            encoder.encodeSerializableValue(Float3.serializer(), value.offset)
        }

        override fun deserialize(decoder: Decoder): Simple {
            return Simple(decoder.decodeSerializableValue(Float3.serializer()))
        }
    }
}

private object LocatorSerializer : KSerializer<Locator> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Locator")

    override fun serialize(encoder: Encoder, value: Locator) {
        if (value.rotation == Float3()) {
            encoder.encodeSerializableValue(LocatorDto.Simple.serializer(), LocatorDto.Simple(value.offset))
        } else {
            encoder.encodeSerializableValue(LocatorDto.Complex.serializer(), LocatorDto.Complex(value.offset, value.rotation))
        }
    }

    override fun deserialize(decoder: Decoder): Locator {
        val dto = try {
            decoder.decodeSerializableValue(LocatorDto.Simple.serializer())
        } catch (e: SerializationException) {
            decoder.decodeSerializableValue(LocatorDto.Complex.serializer())
        }
        return when (dto) {
            is LocatorDto.Simple -> Locator(dto.offset, Float3())
            is LocatorDto.Complex -> Locator(dto.offset, dto.rotation)
        }
    }
}


