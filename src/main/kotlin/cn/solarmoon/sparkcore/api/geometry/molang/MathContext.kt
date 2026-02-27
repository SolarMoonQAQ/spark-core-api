package cn.solarmoon.sparkcore.api.geometry.molang

import org.graalvm.polyglot.HostAccess
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

object MathContext {
    @HostAccess.Export
    val pi = Math.PI

    @HostAccess.Export
    fun sin(value: Double) = kotlin.math.sin(Math.toDegrees(value))

    @HostAccess.Export
    fun cos(value: Double) = kotlin.math.cos(Math.toDegrees(value))

    @HostAccess.Export
    fun tan(value: Double) = kotlin.math.tan(Math.toDegrees(value))

    @HostAccess.Export
    fun asin(value: Double) = Math.toDegrees(kotlin.math.asin(value))

    @HostAccess.Export
    fun acos(value: Double) = Math.toDegrees(kotlin.math.acos(value))

    @HostAccess.Export
    fun atan(value: Double) = Math.toDegrees(kotlin.math.atan(value))

    @HostAccess.Export
    fun atan2(y: Double, x: Double) = Math.toDegrees(kotlin.math.atan2(y, x))

    @HostAccess.Export
    fun exp(value: Double) = kotlin.math.exp(value)

    @HostAccess.Export
    fun log(value: Double) = ln(value)

    @HostAccess.Export
    fun pow(base: Double, exponent: Double) = base.pow(exponent)

    @HostAccess.Export
    fun sqrt(value: Double) = kotlin.math.sqrt(value)

    @HostAccess.Export
    fun abs(value: Double) = kotlin.math.abs(value)

    @HostAccess.Export
    fun floor(value: Double) = kotlin.math.floor(value)

    @HostAccess.Export
    fun ceil(value: Double) = kotlin.math.ceil(value)

    @HostAccess.Export
    fun round(value: Double) = value.roundToInt()

    @HostAccess.Export
    fun min(value1: Double, value2: Double) = value1.coerceAtMost(value2)

    @HostAccess.Export
    fun max(value1: Double, value2: Double) = value1.coerceAtLeast(value2)

    @HostAccess.Export
    fun clamp(value: Double, min: Double, max: Double) = value.coerceIn(min, max)

    @HostAccess.Export
    fun lerp(start: Double, end: Double, alpha: Double) = start + alpha * (end - start)
}