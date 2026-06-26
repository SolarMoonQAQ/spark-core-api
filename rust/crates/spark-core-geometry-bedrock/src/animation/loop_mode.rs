use serde::Deserialize;

#[derive(Deserialize)]
#[serde(untagged)]
pub enum LoopDto {
    Typed(LoopTypedDto),
    True(bool)
}

impl Default for LoopDto {
    fn default() -> Self {
        LoopDto::Typed(LoopTypedDto::default())
    }
}

#[derive(Deserialize, Default)]
#[serde(rename_all = "snake_case")]
pub enum LoopTypedDto {
    #[default]
    Once,
    HoldOnLastFrame,
}