use crate::animation::keyframe::KeyframeDto;
use serde::Deserialize;
use std::collections::HashMap;
use glam::{Quat, Vec3};
use splines::Spline;

#[derive(Deserialize)]
pub struct BoneAnimationDto {
    #[serde(default)]
    pub position: TrackDto,
    #[serde(default)]
    pub rotation: TrackDto,
    #[serde(default)]
    pub scale: TrackDto,
}

#[derive(Deserialize)]
#[serde(untagged)]
pub enum TrackDto {
    Keyframed(HashMap<String, KeyframeDto>),
    Static(KeyframeDto)
}

impl Default for TrackDto {
    fn default() -> Self {
        TrackDto::Keyframed(HashMap::new())
    }
}

impl From<TrackDto> for Spline<f32, Vec3> {
    fn from(dto: TrackDto) -> Self {
        match dto {
            TrackDto::Keyframed(keyframes) => Spline::from_vec(keyframes.into_iter().flat_map(|(t, k)| k.into_keys(t.parse().unwrap_or(0.0))).collect()),
            TrackDto::Static(keyframe) => Spline::from_vec(keyframe.into_keys(0.0))
        }
    }
}

impl From<TrackDto> for Spline<f32, Quat> {
    fn from(dto: TrackDto) -> Self {
        match dto {
            TrackDto::Keyframed(keyframes) => Spline::from_vec(keyframes.into_iter().flat_map(|(t, k)| k.into_rot_keys(t.parse().unwrap_or(0.0))).collect()),
            TrackDto::Static(keyframe) => Spline::from_vec(keyframe.into_rot_keys(0.0))
        }
    }
}