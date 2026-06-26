use crate::model::uv::UVUnionDto;
use glam::Vec3;
use serde::Deserialize;

#[derive(Deserialize)]
pub struct CubeDto {
    #[serde(default)]
    pub origin: Vec3,
    #[serde(default)]
    pub pivot: Vec3,
    #[serde(default)]
    pub rotation: Vec3,
    #[serde(default)]
    pub size: Vec3,
    #[serde(default)]
    pub inflate: f32,
    #[serde(default)]
    pub mirror: bool,
    pub uv: UVUnionDto
}