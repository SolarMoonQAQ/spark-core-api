use crate::model::model::Model;
#[cfg(feature = "serde")]
use serde::{Deserialize, Serialize};

#[cfg_attr(feature = "serde", derive(Serialize, Deserialize))]
#[derive(Debug, Clone)]
pub struct GeometryFile {
    pub format_version: String,
    #[cfg_attr(feature = "serde", serde(rename = "minecraft:geometry"))]
    pub models: Vec<Model>
}