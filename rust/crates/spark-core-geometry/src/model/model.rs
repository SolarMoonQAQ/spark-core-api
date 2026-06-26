use crate::model::bone::Bone;
use crate::model::model_description::ModelDescription;
use indexmap::IndexMap;
#[cfg(feature = "serde")]
use serde::{Deserialize, Serialize};
#[cfg(feature = "serde-bedrock")]
use spark_core_geometry_bedrock::model::model::ModelDto;

#[cfg_attr(feature = "serde", derive(Serialize, Deserialize))]
#[cfg_attr(feature = "serde-bedrock", serde(from = "ModelDto"))]
#[derive(Debug, Clone)]
pub struct Model {
    pub description: ModelDescription,
    #[cfg_attr(feature = "serde", serde(default))]
    pub bones: IndexMap<String, Bone>,
}