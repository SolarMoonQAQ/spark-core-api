package cn.solarmoon.sparkcore.api.math

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(Float2Serializer::class)
data class Float2(
    val x: Float = 0f,
    val y: Float = 0f
) {
    constructor(value: Float): this(value, value)

    operator fun plus(other: Float2): Float2 {
        return Float2(x + other.x, y + other.y)
    }

    operator fun minus(other: Float2): Float2 {
        return Float2(x - other.x, y - other.y)
    }

    operator fun times(scalar: Float): Float2 {
        return Float2(x * scalar, y * scalar)
    }

    operator fun div(scalar: Float): Float2 {
        return Float2(x / scalar, y / scalar)
    }

    operator fun unaryMinus(): Float2 {
        return Float2(-x, -y)
    }

    operator fun unaryPlus() = this
}

infix fun Float2.dot(other: Float2): Float = x * other.x + y * other.y

infix fun Float2.cross(other: Float2): Float = x * other.y - y * other.x

private object Float2Serializer : KSerializer<Float2> {
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("Float2", StructureKind.LIST)

    override fun serialize(encoder: Encoder, value: Float2) {
        encoder.encodeSerializableValue(ListSerializer(Float.serializer()), listOf(value.x, value.y))
    }

    override fun deserialize(decoder: Decoder): Float2 {
        val (x, y) = decoder.decodeSerializableValue(ListSerializer(Float.serializer()))
        return Float2(x, y)
    }
}
