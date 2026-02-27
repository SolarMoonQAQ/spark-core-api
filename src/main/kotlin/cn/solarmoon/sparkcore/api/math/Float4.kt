package cn.solarmoon.sparkcore.api.math

data class Float4(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val w: Float = 0f
) {
    constructor(v: Float3, w: Float) : this(v.x, v.y, v.z, w)

    operator fun plus(other: Float4) = Float4(x + other.x, y + other.y, z + other.z, w + other.w)
    operator fun minus(other: Float4) = Float4(x - other.x, y - other.y, z - other.z, w - other.w)
    operator fun times(scalar: Float) = Float4(x * scalar, y * scalar, z * scalar, w * scalar)
    operator fun div(scalar: Float) = Float4(x / scalar, y / scalar, z / scalar, w / scalar)
    operator fun unaryMinus() = Float4(-x, -y, -z, -w)
    operator fun unaryPlus() = this
}

