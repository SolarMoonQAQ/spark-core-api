use glam::{Quat, Vec3};
use splines::Spline;

pub type KeyframeTrack = Spline<f32, Vec3>;
pub type KeyframeRotTrack = Spline<f32, Quat>;

