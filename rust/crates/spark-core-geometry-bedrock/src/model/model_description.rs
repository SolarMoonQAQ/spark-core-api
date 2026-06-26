use serde::Deserialize;

#[derive(Deserialize)]
pub struct ModelDescriptionDto {
    pub identifier: String,
    #[serde(default)]
    pub texture_width: u32,
    #[serde(default)]
    pub texture_height: u32,
}