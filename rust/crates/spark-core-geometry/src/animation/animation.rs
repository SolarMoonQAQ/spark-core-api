use crate::animation::bone_animation::BoneAnimation;
use crate::animation::loop_mode::Loop;
use indexmap::IndexMap;
#[cfg(feature = "serde")]
use serde::{Deserialize, Serialize};
use smart_default::SmartDefault;

#[cfg_attr(feature = "serde", derive(Deserialize, Serialize))]
#[derive(SmartDefault, Debug, Clone)]
pub struct Animation {
    pub name: String,
    #[cfg_attr(feature = "serde", serde(default), serde(rename = "loop"))]
    pub loop_mode: Loop,
    #[cfg_attr(feature = "serde", serde(default))]
    #[default(f32::MAX)]
    pub animation_length: f32,
    #[cfg_attr(feature = "serde", serde(default))]
    pub bones: IndexMap<String, BoneAnimation>,
}