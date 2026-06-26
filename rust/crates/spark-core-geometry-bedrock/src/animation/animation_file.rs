use crate::animation::animation::AnimationDto;
use serde::Deserialize;
use std::collections::HashMap;

#[derive(Deserialize)]
pub struct AnimationFileDto {
    pub format_version: String,
    #[serde(default)]
    pub animations: HashMap<String, AnimationDto>,
}