package cn.solarmoon.sparkcore.api.geometry.molang

import arrow.core.raise.either
import cn.solarmoon.sparkcore.api.js.safeAsDouble
import cn.solarmoon.sparkcore.api.math.Float3
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(Vec3MolangValueSerializer::class)
data class Vec3MolangValue(
    val x: JSMolangValue,
    val y: JSMolangValue,
    val z: JSMolangValue
)

fun Vec3MolangValue.evalAsFloat3(query: Any, math: Any) = either {
    val x = x.eval(query, math).bind().safeAsDouble().bind().toFloat()
    val y = y.eval(query, math).bind().safeAsDouble().bind().toFloat()
    val z = z.eval(query, math).bind().safeAsDouble().bind().toFloat()
    Float3(x, y, z)
}

private object Vec3MolangValueSerializer: KSerializer<Vec3MolangValue> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Vec3MolangValue")

    override fun serialize(
        encoder: Encoder,
        value: Vec3MolangValue
    ) {
        encoder.encodeSerializableValue(ListSerializer(JSMolangValue.serializer()), listOf(value.x, value.y, value.z))
    }

    override fun deserialize(decoder: Decoder): Vec3MolangValue {
        val (x, y, z) = decoder.decodeSerializableValue(ListSerializer(JSMolangValue.serializer()))
        return Vec3MolangValue(x, y, z)
    }
}