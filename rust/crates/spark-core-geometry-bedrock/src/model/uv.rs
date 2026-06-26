use glam::Vec2;
use serde::Deserialize;
use std::collections::HashMap;

#[derive(Deserialize)]
#[serde(untagged)]
pub enum UVUnionDto {
    Face(HashMap<DirectionDto, FaceUVDto>),
    Box(Vec2)
}

#[derive(Deserialize)]
pub struct FaceUVDto {
    #[serde(default)]
    pub uv: Vec2,
    #[serde(default)]
    pub uv_size: Vec2,
}

#[derive(Deserialize, Eq, Hash, PartialEq)]
pub enum DirectionDto {
    North,
    South,
    East,
    West,
    Up,
    Down,
}