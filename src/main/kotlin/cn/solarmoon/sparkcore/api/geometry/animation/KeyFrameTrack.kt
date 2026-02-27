package cn.solarmoon.sparkcore.api.geometry.animation

import arrow.core.Either
import arrow.core.firstOrNone
import arrow.core.getOrElse
import arrow.core.lastOrNone
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import cn.solarmoon.sparkcore.api.geometry.molang.MathContext
import cn.solarmoon.sparkcore.api.geometry.molang.Vec3MolangValue
import cn.solarmoon.sparkcore.api.geometry.molang.evalAsFloat3
import cn.solarmoon.sparkcore.api.math.Float3
import cn.solarmoon.sparkcore.api.math.Interpolations
import cn.solarmoon.sparkcore.api.math.lerp

typealias KeyFrameTrack = Map<Float, KeyFrame>

fun KeyFrameTrack.sample(time: Float, query: Any, math: Any = MathContext): Either<Exception, Float3> {
    if (isEmpty()) return Float3().right()

    // 找到前后关键帧
    val keys = keys.sorted()
    val t0 = keys
        .lastOrNone { it <= time }
        .getOrElse { keys.first() }
    val t1 = keys
        .firstOrNone { it >= time }
        .getOrElse { keys.last() }
    val kf0 = this[t0]!!
    val kf1 = this[t1]!!

    // 前后关键帧相同时不参与插值
    if (t0 == t1) return kf0.post.evalAsFloat3(query, math)

    // 结算插值（将time看作连续的）
    val alpha = (time - t0) / (t1 - t0)

    return either {
        when (kf0.lerpMode) {
            LerpMode.LINEAR -> {
                val p1 = kf0.post.evalAsFloat3(query, math).bind()
                val p2 = kf1.pre.evalAsFloat3(query, math).bind()
                p1.lerp(p2, alpha)
            }
            LerpMode.CATMULLROM -> {
                val p0 = kf0.post.evalAsFloat3(query, math).bind()
                val p1 = kf0.pre.evalAsFloat3(query, math).bind()
                val p2 = kf1.pre.evalAsFloat3(query, math).bind()
                val p3 = kf1.post.evalAsFloat3(query, math).bind()

                Float3(
                    Interpolations.catmullRom(alpha, p0.x, p1.x, p2.x, p3.x),
                    Interpolations.catmullRom(alpha, p0.y, p1.y, p2.y, p3.y),
                    Interpolations.catmullRom(alpha, p0.z, p1.z, p2.z, p3.z)
                )
            }
        }
    }
}