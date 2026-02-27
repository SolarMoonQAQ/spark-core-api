package cn.solarmoon.sparkcore.api.geometry.model

sealed interface ModelError {
    data class BoneNotFound(val boneName: String) : ModelError
    data class ParentBoneNotFound(val selfBoneName: String, val parentBoneName: String) : ModelError
}