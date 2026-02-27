package cn.solarmoon.sparkcore.api.math

import kotlin.math.cos
import kotlin.math.sin

/**
 * 不可变的 4x4 矩阵
 * 按行主序存储：
 * ```
 * m00 m01 m02 m03
 * m10 m11 m12 m13
 * m20 m21 m22 m23
 * m30 m31 m32 m33
 * ```
 */
data class Mat4(
    val m00: Float = 1f, val m01: Float = 0f, val m02: Float = 0f, val m03: Float = 0f,
    val m10: Float = 0f, val m11: Float = 1f, val m12: Float = 0f, val m13: Float = 0f,
    val m20: Float = 0f, val m21: Float = 0f, val m22: Float = 1f, val m23: Float = 0f,
    val m30: Float = 0f, val m31: Float = 0f, val m32: Float = 0f, val m33: Float = 1f
) {

    operator fun plus(other: Mat4) = Mat4(
        m00 + other.m00, m01 + other.m01, m02 + other.m02, m03 + other.m03,
        m10 + other.m10, m11 + other.m11, m12 + other.m12, m13 + other.m13,
        m20 + other.m20, m21 + other.m21, m22 + other.m22, m23 + other.m23,
        m30 + other.m30, m31 + other.m31, m32 + other.m32, m33 + other.m33
    )

    operator fun minus(other: Mat4) = Mat4(
        m00 - other.m00, m01 - other.m01, m02 - other.m02, m03 - other.m03,
        m10 - other.m10, m11 - other.m11, m12 - other.m12, m13 - other.m13,
        m20 - other.m20, m21 - other.m21, m22 - other.m22, m23 - other.m23,
        m30 - other.m30, m31 - other.m31, m32 - other.m32, m33 - other.m33
    )

    operator fun times(other: Mat4) = Mat4(
        m00*other.m00 + m01*other.m10 + m02*other.m20 + m03*other.m30,
        m00*other.m01 + m01*other.m11 + m02*other.m21 + m03*other.m31,
        m00*other.m02 + m01*other.m12 + m02*other.m22 + m03*other.m32,
        m00*other.m03 + m01*other.m13 + m02*other.m23 + m03*other.m33,

        m10*other.m00 + m11*other.m10 + m12*other.m20 + m13*other.m30,
        m10*other.m01 + m11*other.m11 + m12*other.m21 + m13*other.m31,
        m10*other.m02 + m11*other.m12 + m12*other.m22 + m13*other.m32,
        m10*other.m03 + m11*other.m13 + m12*other.m23 + m13*other.m33,

        m20*other.m00 + m21*other.m10 + m22*other.m20 + m23*other.m30,
        m20*other.m01 + m21*other.m11 + m22*other.m21 + m23*other.m31,
        m20*other.m02 + m21*other.m12 + m22*other.m22 + m23*other.m32,
        m20*other.m03 + m21*other.m13 + m22*other.m23 + m23*other.m33,

        m30*other.m00 + m31*other.m10 + m32*other.m20 + m33*other.m30,
        m30*other.m01 + m31*other.m11 + m32*other.m21 + m33*other.m31,
        m30*other.m02 + m31*other.m12 + m32*other.m22 + m33*other.m32,
        m30*other.m03 + m31*other.m13 + m32*other.m23 + m33*other.m33
    )

    operator fun times(scalar: Float) = Mat4(
        m00*scalar, m01*scalar, m02*scalar, m03*scalar,
        m10*scalar, m11*scalar, m12*scalar, m13*scalar,
        m20*scalar, m21*scalar, m22*scalar, m23*scalar,
        m30*scalar, m31*scalar, m32*scalar, m33*scalar
    )

    operator fun times(v: Float4) = Float4(
        m00*v.x + m01*v.y + m02*v.z + m03*v.w,
        m10*v.x + m11*v.y + m12*v.z + m13*v.w,
        m20*v.x + m21*v.y + m22*v.z + m23*v.w,
        m30*v.x + m31*v.y + m32*v.z + m33*v.w
    )

    operator fun unaryMinus() = Mat4(
        -m00, -m01, -m02, -m03,
        -m10, -m11, -m12, -m13,
        -m20, -m21, -m22, -m23,
        -m30, -m31, -m32, -m33
    )

    operator fun unaryPlus() = this

    operator fun get(row: Int, col: Int): Float {
        return when (row) {
            0 -> when (col) {
                0 -> m00; 1 -> m01; 2 -> m02; 3 -> m03
                else -> throw IndexOutOfBoundsException()
            }
            1 -> when (col) {
                0 -> m10; 1 -> m11; 2 -> m12; 3 -> m13
                else -> throw IndexOutOfBoundsException()
            }
            2 -> when (col) {
                0 -> m20; 1 -> m21; 2 -> m22; 3 -> m23
                else -> throw IndexOutOfBoundsException()
            }
            3 -> when (col) {
                0 -> m30; 1 -> m31; 2 -> m32; 3 -> m33
                else -> throw IndexOutOfBoundsException()
            }
            else -> throw IndexOutOfBoundsException()
        }
    }

}

fun Mat4.translate(x: Float, y: Float, z: Float): Mat4 {
    val t = Mat4(
        1f, 0f, 0f, x,
        0f, 1f, 0f, y,
        0f, 0f, 1f, z,
        0f, 0f, 0f, 1f
    )
    return this * t
}

fun Mat4.translate(xyz: Float3): Mat4 {
    return this.translate(xyz.x, xyz.y, xyz.z)
}

fun Mat4.translateLocal(x: Float, y: Float, z: Float): Mat4 {
    val t = Mat4(
        1f, 0f, 0f, x,
        0f, 1f, 0f, y,
        0f, 0f, 1f, z,
        0f, 0f, 0f, 1f
    )
    return t * this
}

fun Mat4.translateLocal(xyz: Float3): Mat4 {
    return this.translateLocal(xyz.x, xyz.y, xyz.z)
}

fun Mat4.rotate(q: Quaternionf): Mat4 {
    val xx = q.x * q.x
    val yy = q.y * q.y
    val zz = q.z * q.z
    val xy = q.x * q.y
    val xz = q.x * q.z
    val yz = q.y * q.z
    val wx = q.w * q.x
    val wy = q.w * q.y
    val wz = q.w * q.z

    val r = Mat4(
        1f - 2f * (yy + zz), 2f * (xy - wz),       2f * (xz + wy),       0f,
        2f * (xy + wz),       1f - 2f * (xx + zz), 2f * (yz - wx),       0f,
        2f * (xz - wy),       2f * (yz + wx),      1f - 2f * (xx + yy), 0f,
        0f,                   0f,                  0f,                  1f
    )

    return this * r
}

fun Mat4.rotateLocal(q: Quaternionf): Mat4 {
    val xx = q.x * q.x
    val yy = q.y * q.y
    val zz = q.z * q.z
    val xy = q.x * q.y
    val xz = q.x * q.z
    val yz = q.y * q.z
    val wx = q.w * q.x
    val wy = q.w * q.y
    val wz = q.w * q.z

    val r = Mat4(
        1f - 2f * (yy + zz), 2f * (xy - wz),       2f * (xz + wy),       0f,
        2f * (xy + wz),       1f - 2f * (xx + zz), 2f * (yz - wx),       0f,
        2f * (xz - wy),       2f * (yz + wx),      1f - 2f * (xx + yy), 0f,
        0f,                   0f,                  0f,                  1f
    )

    return r * this
}

fun Mat4.rotateX(angleRad: Float): Mat4 {
    val c = cos(angleRad)
    val s = sin(angleRad)
    val r = Mat4(
        1f, 0f, 0f, 0f,
        0f, c, -s, 0f,
        0f, s, c, 0f,
        0f, 0f, 0f, 1f
    )
    return this * r
}

fun Mat4.rotateY(angleRad: Float): Mat4 {
    val c = cos(angleRad)
    val s = sin(angleRad)
    val r = Mat4(
        c, 0f, s, 0f,
        0f, 1f, 0f, 0f,
        -s, 0f, c, 0f,
        0f, 0f, 0f, 1f
    )
    return this * r
}

fun Mat4.rotateZ(angleRad: Float): Mat4 {
    val c = cos(angleRad)
    val s = sin(angleRad)
    val r = Mat4(
        c, -s, 0f, 0f,
        s, c, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )
    return this * r
}

fun Mat4.rotateXYZ(xRad: Float, yRad: Float, zRad: Float): Mat4 {
    return this
        .rotateX(xRad)
        .rotateY(yRad)
        .rotateZ(zRad)
}

fun Mat4.rotateXYZ(xyzRad: Float3): Mat4 {
    return this.rotateXYZ(xyzRad.x, xyzRad.y, xyzRad.z)
}

fun Mat4.rotateZYX(xRad: Float, yRad: Float, zRad: Float): Mat4 {
    return this.rotateXYZ(zRad, yRad, xRad)
}

fun Mat4.rotateZYX(xyzRad: Float3): Mat4 {
    return this.rotateZYX(xyzRad.x, xyzRad.y, xyzRad.z)
}

fun Mat4.rotateLocalX(angleRad: Float): Mat4 {
    val c = cos(angleRad)
    val s = sin(angleRad)
    val r = Mat4(
        1f, 0f, 0f, 0f,
        0f, c, -s, 0f,
        0f, s, c, 0f,
        0f, 0f, 0f, 1f
    )
    return r * this
}

fun Mat4.rotateLocalY(angleRad: Float): Mat4 {
    val c = cos(angleRad)
    val s = sin(angleRad)
    val r = Mat4(
        c, 0f, s, 0f,
        0f, 1f, 0f, 0f,
        -s, 0f, c, 0f,
        0f, 0f, 0f, 1f
    )
    return r * this
}

fun Mat4.rotateLocalZ(angleRad: Float): Mat4 {
    val c = cos(angleRad)
    val s = sin(angleRad)
    val r = Mat4(
        c, -s, 0f, 0f,
        s, c, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )
    return r * this
}

fun Mat4.rotateLocalXYZ(xRad: Float, yRad: Float, zRad: Float): Mat4 {
    return this
        .rotateLocalX(xRad)
        .rotateLocalY(yRad)
        .rotateLocalZ(zRad)
}

fun Mat4.rotateLocalXYZ(xyzRad: Float3): Mat4 {
    return this.rotateLocalXYZ(xyzRad.x, xyzRad.y, xyzRad.z)
}

fun Mat4.rotateLocalZYX(xRad: Float, yRad: Float, zRad: Float): Mat4 {
    return this
        .rotateLocalZ(zRad)
        .rotateLocalY(yRad)
        .rotateLocalZ(xRad)
}

fun Mat4.rotateLocalZYX(xyzRad: Float3): Mat4 {
    return this.rotateLocalZYX(xyzRad.x, xyzRad.y, xyzRad.z)
}

fun Mat4.scale(sx: Float, sy: Float, sz: Float): Mat4 {
    val s = Mat4(
        sx, 0f, 0f, 0f,
        0f, sy, 0f, 0f,
        0f, 0f, sz, 0f,
        0f, 0f, 0f, 1f
    )
    return this * s
}

fun Mat4.scale(xyz: Float3): Mat4 {
    return this.scale(xyz.x, xyz.y, xyz.z)
}

fun Mat4.transformPosition(xyz: Float3) = this * xyz.toPosition()

fun Mat4.transformDirection(xyz: Float3) = this * xyz.toDirection()
