use glam::{EulerRot, Quat, Vec3};
use serde::Deserialize;
use splines::{Interpolation, Key};
use crate::animation::lerp_mode::LerpModeDto;

#[derive(Deserialize)]
#[serde(untagged)]
pub enum KeyframeDto {
    Num(f32),
    Linear(Vec3),
    Full {
        post: Vec3,
        #[serde(default)]
        pre: Option<Vec3>,
        #[serde(default)]
        lerp_mode: LerpModeDto,
    },
}

impl KeyframeDto {
    fn into_keys_with<T: PartialEq>(self, timestamp: f32, transform: impl Fn(Vec3) -> T) -> Vec<Key<f32, T>> {
        let (pre_raw, post_raw, lerp_mode_dto) = match self {
            KeyframeDto::Num(v) => (Vec3::splat(v), Vec3::splat(v), LerpModeDto::Linear),
            KeyframeDto::Linear(v) => (v, v, LerpModeDto::Linear),
            KeyframeDto::Full { post, pre, lerp_mode } => (pre.unwrap_or(post), post, lerp_mode),
        };

        let pre = transform(pre_raw);
        let post = transform(post_raw);

        let lerp_mode = match lerp_mode_dto {
            LerpModeDto::Linear => Interpolation::Linear,
            LerpModeDto::Catmullrom => Interpolation::CatmullRom,
        };

        if pre == post {
            vec![Key::new(timestamp, post, lerp_mode)]
        } else {
            vec![
                Key::new(timestamp, pre, Interpolation::Step(1.0)),
                Key::new(timestamp, post, lerp_mode),
            ]
        }
    }

    pub fn into_keys(self, timestamp: f32) -> Vec<Key<f32, Vec3>> {
        self.into_keys_with(timestamp, |v| v)
    }

    pub fn into_rot_keys(self, timestamp: f32) -> Vec<Key<f32, Quat>> {
        self.into_keys_with(timestamp, |v| {
            Quat::from_euler(EulerRot::ZYX, v.z, v.y, v.x)
        })
    }
}