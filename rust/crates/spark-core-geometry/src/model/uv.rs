use enum_map::{Enum, EnumMap};
use glam::{Vec2, Vec3};
#[cfg(feature = "serde")]
use serde::{Deserialize, Serialize};

#[cfg_attr(feature = "serde", derive(Serialize, Deserialize), serde(untagged))]
#[derive(Debug, Clone, Copy)]
pub enum UVUnion {
    Face(EnumMap<Direction, FaceUV>),
    Box(Vec2)
}

#[cfg_attr(feature = "serde", derive(Serialize, Deserialize))]
#[derive(Debug, Clone, Copy, Default)]
pub struct FaceUV {
    #[cfg_attr(feature = "serde", serde(default))]
    pub uv: Vec2,
    #[cfg_attr(feature = "serde", serde(default))]
    pub uv_size: Vec2,
}

#[cfg_attr(feature = "serde", derive(Serialize, Deserialize), serde(rename_all = "lowercase"))]
#[derive(Debug, Clone, Copy, PartialEq, Eq, Enum)]
pub enum Direction {
    North,
    South,
    East,
    West,
    Up,
    Down,
}

impl Direction {
    pub const ALL: [Direction; 6] = [
        Direction::North, Direction::South, Direction::East,
        Direction::West, Direction::Up, Direction::Down
    ];

    /// 标准 Minecraft 坐标系下的法线向量
    pub fn normal(&self) -> Vec3 {
        match self {
            Direction::West => Vec3::NEG_X,
            Direction::East => Vec3::X,
            Direction::North => Vec3::NEG_Z,
            Direction::South => Vec3::Z,
            Direction::Up => Vec3::Y,
            Direction::Down => Vec3::NEG_Y,
        }
    }
}