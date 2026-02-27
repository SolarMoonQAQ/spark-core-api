package cn.solarmoon.sparkcore.api.math

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Quaternionf(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val w: Float = 1f
) {
    operator fun times(other: Quaternionf) = Quaternionf(
        w*other.x + x*other.w + y*other.z - z*other.y,
        w*other.y - x*other.z + y*other.w + z*other.x,
        w*other.z + x*other.y - y*other.x + z*other.w,
        w*other.w - x*other.x - y*other.y - z*other.z,
    )

    operator fun div(scalar: Float) = Quaternionf(x/scalar, y/scalar, z/scalar, w/scalar)
}

fun Quaternionf.length() = sqrt(x*x + y*y + z*z + w*w)

fun Quaternionf.lengthSquared() = x*x + y*y + z*z + w*w

fun Quaternionf.conjugate() = Quaternionf(-x, -y, -z, w)

fun Quaternionf.inverse() = conjugate() / lengthSquared()

fun Quaternionf.normalize(): Quaternionf {
    val len = length()
    return Quaternionf(x/len, y/len, z/len, w/len)
}

fun Quaternionf.rotateX(angle: Float): Quaternionf {
    val half = angle * 0.5f
    val s = sin(half)
    val c = cos(half)
    val qx = Quaternionf(s, 0f, 0f, c)
    return this * qx
}

fun Quaternionf.rotateY(angle: Float): Quaternionf {
    val half = angle * 0.5f
    val s = sin(half)
    val c = cos(half)
    val qy = Quaternionf(0f, s, 0f, c)
    return this * qy
}

fun Quaternionf.rotateZ(angle: Float): Quaternionf {
    val half = angle * 0.5f
    val s = sin(half)
    val c = cos(half)
    val qz = Quaternionf(0f, 0f, s, c)
    return this * qz
}

fun Quaternionf.rotateXYZ(x: Float, y: Float, z: Float): Quaternionf {
    return this
        .rotateX(x)
        .rotateY(y)
        .rotateZ(z)
}

fun Quaternionf.rotateXYZ(xyz: Float3): Quaternionf {
    return rotateXYZ(xyz.x, xyz.y, xyz.z)
}

fun Quaternionf.rotateZYX(x: Float, y: Float, z: Float): Quaternionf {
    return this
        .rotateZ(z)
        .rotateY(y)
        .rotateX(x)
}

fun Quaternionf.rotateZYX(xyz: Float3): Quaternionf {
    return rotateZYX(xyz.z, xyz.y, xyz.x)
}

fun Quaternionf.rotateLocalX(angle: Float): Quaternionf {
    val half = angle * 0.5f
    val s = sin(half)
    val c = cos(half)
    val qx = Quaternionf(s, 0f, 0f, c)
    return qx * this
}

fun Quaternionf.rotateLocalY(angle: Float): Quaternionf {
    val half = angle * 0.5f
    val s = sin(half)
    val c = cos(half)
    val qy = Quaternionf(0f, s, 0f, c)
    return qy * this
}

fun Quaternionf.rotateLocalZ(angle: Float): Quaternionf {
    val half = angle * 0.5f
    val s = sin(half)
    val c = cos(half)
    val qz = Quaternionf(0f, 0f, s, c)
    return qz * this
}

fun Quaternionf.rotateLocalXYZ(x: Float, y: Float, z: Float): Quaternionf {
    return this
        .rotateLocalX(x)
        .rotateLocalY(y)
        .rotateLocalZ(z)
}

fun Quaternionf.rotateLocalXYZ(xyz: Float3): Quaternionf {
    return rotateLocalXYZ(xyz.x, xyz.y, xyz.z)
}

fun Quaternionf.rotateLocalZYX(x: Float, y: Float, z: Float): Quaternionf {
    return this
        .rotateLocalZ(z)
        .rotateLocalY(y)
        .rotateLocalX(x)
}

fun Quaternionf.rotateLocalZYX(xyz: Float3): Quaternionf {
    return rotateLocalZYX(xyz.z, xyz.y, xyz.x)
}


