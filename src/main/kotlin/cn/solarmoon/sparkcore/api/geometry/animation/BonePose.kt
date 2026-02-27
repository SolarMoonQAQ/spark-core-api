package cn.solarmoon.sparkcore.api.geometry.animation

import arrow.core.raise.either
import cn.solarmoon.sparkcore.api.geometry.molang.MathContext
import cn.solarmoon.sparkcore.api.math.*
import kotlinx.serialization.Serializable

@Serializable
data class BonePose(
    val position: KeyFrameTrack = emptyMap(),
    val rotation: KeyFrameTrack = emptyMap(),
    val scale: KeyFrameTrack = emptyMap()
)

/**
 * 计算骨骼动画姿态局部变换
 */
fun BonePose.getLocalTransform(time: Float, query: Any, math: Any = MathContext) =
    either {
        val pos = position.sample(time, query, math).bind()
        val rot = rotation.sample(time, query, math).bind()
        val scale = scale.sample(time, query, math).bind()

        val rotQ = Quaternionf().rotateLocalZYX(rot)

        Mat4()
            .translateLocal(pos)
            .rotateLocal(rotQ)
            .scale(scale)
    }