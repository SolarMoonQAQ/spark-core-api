use glam::Vec3;
use serde::Deserialize;

#[derive(Deserialize)]
#[serde(untagged)]
pub enum LocatorDto {
    Simple(Vec3),
    Full {
        #[serde(default)]
        offset: Vec3,
        #[serde(default)]
        rotation: Vec3,
    }
}