use crate::animation::animation::Animation;
use crate::animation::animation_file::AnimationFile;
use crate::animation::bone_animation::BoneAnimation;
use crate::animation::loop_mode::Loop;
use spark_core_geometry_bedrock::animation::animation::AnimationDto;
use spark_core_geometry_bedrock::animation::animation_file::AnimationFileDto;
use spark_core_geometry_bedrock::animation::bone_animation::BoneAnimationDto;
use spark_core_geometry_bedrock::animation::loop_mode::{LoopDto, LoopTypedDto};

impl From<LoopDto> for Loop {
    fn from(dto: LoopDto) -> Self {
        match dto {
            LoopDto::Typed(typ) => match typ {
                LoopTypedDto::Once => Loop::Once,
                LoopTypedDto::HoldOnLastFrame => Loop::HoldOnLastFrame,
            }
            LoopDto::True(_) => Loop::True,
        }
    }
}

trait BoneAnimationDtoExt {
    fn into_bone_animation(self, name: String) -> BoneAnimation;
}

impl BoneAnimationDtoExt for BoneAnimationDto {
    fn into_bone_animation(self, name: String) -> BoneAnimation {
        BoneAnimation {
            name,
            position: self.position.into(),
            rotation: self.rotation.into(),
            scale: self.scale.into(),
        }
    }
}

trait AnimationDtoExt {
    fn into_animation(self, name: String) -> Animation;
}

impl AnimationDtoExt for AnimationDto {
    fn into_animation(self, name: String) -> Animation {
        Animation {
            name,
            animation_length: self.animation_length,
            loop_mode: self.loop_mode.into(),
            bones: self.bones.into_iter().map(|(n, b)| (n.to_string(), b.into_bone_animation(n))).collect(),
        }
    }
}

impl From<AnimationFileDto> for AnimationFile {
    fn from(dto: AnimationFileDto) -> Self {
        Self {
            format_version: dto.format_version,
            animations: dto.animations.into_iter().map(|(n, a)| (n.to_string(), a.into_animation(n))).collect(),
        }
    }
}