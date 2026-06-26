use glam::{Quat, Vec3};

#[cfg(feature = "serde")]
use serde::{Deserialize, Serialize};

#[cfg_attr(feature = "serde", derive(Serialize, Deserialize))]
#[derive(Debug, Clone)]
pub struct Locator {
    pub name: String,
    #[cfg_attr(feature = "serde", serde(default))]
    pub offset: Vec3,
    #[cfg_attr(feature = "serde", serde(default))]
    pub rotation: Quat,
}