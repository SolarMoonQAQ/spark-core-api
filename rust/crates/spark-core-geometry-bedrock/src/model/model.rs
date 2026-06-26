use serde::Deserialize;
use crate::model::bone::BoneDto;
use crate::model::model_description::ModelDescriptionDto;

#[derive(Deserialize)]
pub struct ModelDto {
    pub description: ModelDescriptionDto,
    #[serde(default)]
    pub bones: Vec<BoneDto>,
}