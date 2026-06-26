use crate::model::cube::CubeDto;
use crate::model::locator::LocatorDto;
use glam::Vec3;
use serde::Deserialize;
use std::collections::HashMap;

#[derive(Deserialize)]
pub struct BoneDto {
    pub name: String,
    #[serde(default)]
    pub parent: Option<String>,
    #[serde(default)]
    pub pivot: Vec3,
    #[serde(default)]
    pub rotation: Vec3,
    #[serde(default)]
    pub cubes: Vec<CubeDto>,
    #[serde(default)]
    pub locators: HashMap<String, LocatorDto>,
}