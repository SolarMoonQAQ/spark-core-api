#[cfg(feature = "serde")]
use serde::{Deserialize, Serialize};

#[cfg_attr(feature = "serde", derive(Serialize, Deserialize), )]
#[derive(Debug, Clone, Copy, PartialEq, Default)]
pub enum Loop {
    #[default]
    Once,
    HoldOnLastFrame,
    True
}