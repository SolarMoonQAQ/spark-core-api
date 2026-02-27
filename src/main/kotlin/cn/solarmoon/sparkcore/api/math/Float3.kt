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
import kotlin.math.sqrt

@Serializable(Float3Serializer::class)
data class Float3(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
) {
    constructor(value: Float): this(value, value, value)

    operator fun plus(other: Float3) = Float3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Float3) = Float3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = Float3(x * scalar, y * scalar, z * scalar)
    operator fun div(scalar: Float) = Float3(x / scalar, y / scalar, z / scalar)
    operator fun unaryMinus() = Float3(-x, -y, -z)
    operator fun unaryPlus() = this
}

infix fun Float3.dot(other: Float3) = x * other.x + y * other.y + z * other.z

infix fun Float3.cross(other: Float3) = Float3(
    y * other.z - z * other.y,
    z * other.x - x * other.z,
    x * other.y - y * other.x
)

fun Float3.length(): Float {
    return sqrt(x * x + y * y + z * z)
}

fun Float3.lerp(other: Float3, t: Float): Float3 {
    return Float3(
        Interpolations.linear(t, x, other.x),
        Interpolations.linear(t, y, other.y),
        Interpolations.linear(t, z, other.z)
    )
}

fun Float3.toPosition(): Float4 = Float4(x, y, z, 1f)

fun Float3.toDirection(): Float4 = Float4(x, y, z, 0f)

private object Float3Serializer : KSerializer<Float3> {
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("Float3", StructureKind.LIST)

    override fun serialize(encoder: Encoder, value: Float3) {
        encoder.encodeSerializableValue(ListSerializer(Float.serializer()), listOf(value.x, value.y, value.z))
    }

    override fun deserialize(decoder: Decoder): Float3 {
        val (x, y, z) = decoder.decodeSerializableValue(ListSerializer(Float.serializer()))
        return Float3(x, y, z)
    }
}

