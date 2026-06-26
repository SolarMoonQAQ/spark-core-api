#[cfg(feature = "serde")]
use serde::{Deserialize, Serialize};

#[cfg_attr(feature = "serde", derive(Deserialize, Serialize))]
#[derive(Debug, Clone)]
pub struct ModelDescription {
    pub identifier: String,
    #[cfg_attr(feature = "serde", serde(default))]
    pub texture_width: u32,
    #[cfg_attr(feature = "serde", serde(default))]
    pub texture_height: u32,
}