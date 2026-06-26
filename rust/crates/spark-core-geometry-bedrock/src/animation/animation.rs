use crate::animation::bone_animation::BoneAnimationDto;
use crate::animation::loop_mode::LoopDto;
use serde::Deserialize;
use smart_default::SmartDefault;
use std::collections::HashMap;

#[derive(SmartDefault, Deserialize)]
pub struct AnimationDto {
    #[serde(default, rename = "loop")]
    pub loop_mode: LoopDto,
    #[serde(default)]
    #[default(f32::MAX)]
    pub animation_length: f32,
    #[serde(default)]
    pub bones: HashMap<String, BoneAnimationDto>,
}