package cn.solarmoon.sparkcore.api.math

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable(Int3Serializer::class)
data class Int3(
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0
) {
    constructor(value: Int): this(value, value, value)

    operator fun plus(other: Int3): Int3 {
        return Int3(x + other.x, y + other.y, z + other.z)
    }

    operator fun minus(other: Int3): Int3 {
        return Int3(x - other.x, y - other.y, z - other.z)
    }

    operator fun times(scalar: Int): Int3 {
        return Int3(x * scalar, y * scalar, z * scalar)
    }

    operator fun div(scalar: Int): Int3 {
        return Int3(x / scalar, y / scalar, z / scalar)
    }

    operator fun unaryMinus(): Int3 {
        return Int3(-x, -y, -z)
    }

    operator fun unaryPlus() = this
}

fun Int3.toFloat(): Float3 {
    return Float3(x.toFloat(), y.toFloat(), z.toFloat())
}

private object Int3Serializer : KSerializer<Int3> {
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("Int3", StructureKind.LIST)

    override fun serialize(encoder: Encoder, value: Int3) {
        encoder.encodeSerializableValue(ListSerializer(Int.serializer()), listOf(value.x, value.y, value.z))
    }

    override fun deserialize(decoder: Decoder): Int3 {
        val (x, y, z) = decoder.decodeSerializableValue(ListSerializer(Int.serializer()))
        return Int3(x, y, z)
    }
}
