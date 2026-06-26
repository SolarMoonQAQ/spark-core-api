use glam::{Vec2, Vec3};

#[derive(Default, Debug, Clone, Copy)]
pub struct Vertex {
    pub position: Vec3,
    pub uv: Vec2,
    pub normal: Vec3,
}