package cn.solarmoon.sparkcore.api.geometry.molang

import arrow.core.left
import arrow.core.right
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.graalvm.polyglot.PolyglotException

@Serializable(JSMolangValueSerializer::class)
data class JSMolangValue(val script: String) {
    constructor(): this("0")

    @Transient
    internal val expression = ThreadLocal.withInitial {
        getMolangJSContext().eval("js", "function f(q, query, math){ return $script; } f")
    }!!
}

fun JSMolangValue.eval(query: Any, math: Any = MathContext) = try {
    expression.get().execute(query, query, math).right()
} catch (e: Exception) {
    e.left()
}

object JSMolangValueSerializer : KSerializer<JSMolangValue> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("JSMolangValue")

    override fun serialize(
        encoder: Encoder,
        value: JSMolangValue
    ) {
        encoder.encodeString(value.script)
    }

    override fun deserialize(decoder: Decoder): JSMolangValue {
        return JSMolangValue(decoder.decodeString())
    }
}