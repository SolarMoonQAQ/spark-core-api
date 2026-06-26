use crate::animation::animation::Animation;
#[cfg(feature = "serde")]
use serde::{Deserialize, Serialize};
use std::collections::HashMap;
#[cfg(feature = "serde-bedrock")]
use spark_core_geometry_bedrock::animation::animation_file::AnimationFileDto;

#[cfg_attr(feature = "serde", derive(Serialize, Deserialize))]
#[cfg_attr(feature = "serde-bedrock", serde(from = "AnimationFileDto"))]
#[derive(Debug, Clone)]
pub struct AnimationFile {
    pub format_version: String,
    #[cfg_attr(feature = "serde", serde(default))]
    pub animations: HashMap<String, Animation>,
}