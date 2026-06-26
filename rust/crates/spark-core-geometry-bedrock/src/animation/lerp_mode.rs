use serde::Deserialize;

#[derive(Deserialize, Default)]
#[serde(rename_all = "snake_case")]
pub enum LerpModeDto {
    #[default]
    Linear,
    Catmullrom
}