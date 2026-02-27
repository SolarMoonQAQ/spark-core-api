package cn.solarmoon.sparkcore.api.geometry.model

import arrow.core.Either
import arrow.core.Option
import arrow.core.none
import arrow.core.raise.either
import arrow.core.right
import cn.solarmoon.sparkcore.api.math.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class Bone(
    val name: String,
    @Contextual val parent: Option<String> = none(),
    val pivot: Float3,
    val rotation: Float3 = Float3(),
    val cubes: List<Cube> = emptyList(),
    val locators: Map<String, Locator> = emptyMap()
) {
    @Transient
    val rotationQuaternion = Quaternionf().rotateLocalZYX(rotation)

    @Transient
    val transform = Mat4()
        .translate(pivot)
        .rotate(rotationQuaternion)
        .translate(-pivot)
}

fun Bone.getTransform(model: Model): Either<ModelError.ParentBoneNotFound, Mat4> {
    val local = transform
    return parent.fold(
        {
            local.right()
        },
        { parentBoneName ->
            either {
                val parentBone = requireNotNull(model.bones[parentBoneName]) { ModelError.ParentBoneNotFound(name, parentBoneName) }
                parentBone.getTransform(model).bind() * local
            }
        }
    )
}
